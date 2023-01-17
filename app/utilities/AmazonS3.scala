package utilities

import com.amazonaws.auth.AWSCredentialsProvider
import com.amazonaws.event.{ProgressEvent, ProgressListener}
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import com.amazonaws.{AmazonClientException, AmazonServiceException, SdkClientException, auth}
import exceptions.BaseException
import play.api.Logger

import java.io.{File, FileOutputStream, IOException, OutputStream}
import java.util
import java.util.{Date => JavaDate}
import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

object AmazonS3 {

  private implicit val module: String = constants.Module.UTILITIES_AMAZON_S3

  private implicit val logger: Logger = Logger(this.getClass)

  private val clientRegion: Regions = Regions.fromName(constants.CommonConfig.AmazonS3.Region)

  private val bucketName = constants.CommonConfig.AmazonS3.BucketName

  private object awsS3Credentials extends AWSCredentialsProvider with auth.AWSCredentials {
    def getAWSAccessKeyId: String = constants.CommonConfig.AmazonS3.AccessKeyID

    def getAWSSecretKey: String = constants.CommonConfig.AmazonS3.SecretKey

    def getCredentials: auth.AWSCredentials = this

    def refresh(): Unit = ()
  }

  private val s3Client = AmazonS3ClientBuilder.standard().withCredentials(awsS3Credentials).withRegion(clientRegion).build

  private val transferManager = TransferManagerBuilder.standard.withS3Client(s3Client).build

  private val maxMultiPartUploadTime: Int = constants.CommonConfig.AmazonS3.MaxMultiPartUploadTime * 24 * 60 * 60

  private val schedulerExecutionContext: ExecutionContext = actors.Service.actorSystem.dispatchers.lookup("akka.actor.scheduler-dispatcher")

  private def getPutObjectRequestWithMetaData(objectKey: String, filePath: String, metaData: Map[String, String]): PutObjectRequest = {
    try {
      val request = new PutObjectRequest(bucketName, objectKey, new File(filePath))
      if (metaData.nonEmpty) {
        val metadata = new ObjectMetadata()
        metadata.setContentType("plain/text")
        metaData.foreach { case (key, value) => metadata.addUserMetadata(key, value) }
        request.setMetadata(metadata)
      }
      request
    } catch {
      case ioException: IOException => logger.error(ioException.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_UPLOAD_FAILURE)
    }
  }

  def uploadFile(objectKey: String, filePath: String, metaData: Map[String, String] = Map()): Unit = {
    try {
      s3Client.putObject(getPutObjectRequestWithMetaData(objectKey = objectKey, filePath = filePath, metaData = metaData))
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE)
    }
  }

  def uploadString(objectKeyName: String, data: String): Unit = {
    try {
      s3Client.putObject(bucketName, objectKeyName, data)
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
      case ioException: IOException => logger.error(ioException.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_UPLOAD_FAILURE, ioException)
    }
  }

  // https://docs.aws.amazon.com/AmazonS3/latest/userguide/mpu-upload-object.html
  def highLevelMultipartUpload(objectKey: String, filePath: String, track: Boolean = false, metaData: Map[String, String] = Map()): Unit = {
    try {
      val request = getPutObjectRequestWithMetaData(objectKey = objectKey, filePath = filePath, metaData = metaData)
      if (track) {
        request.setGeneralProgressListener(new ProgressListener() {
          @Override
          def progressChanged(progressEvent: ProgressEvent): Unit = {
            println("Transferred bytes: " + progressEvent.getBytesTransferred)
          }
        })
      }
      transferManager.upload(request).waitForCompletion()
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        constants.Response.AMAZON_S3_PROCESS_FAILURE.throwBaseException(e)
      case e: AmazonClientException => logger.error(e.getLocalizedMessage)
      case e: InterruptedException => logger.error(e.getLocalizedMessage)
      case ioException: IOException => logger.error(ioException.getLocalizedMessage)
        constants.Response.AMAZON_S3_UPLOAD_FAILURE.throwBaseException(ioException)
    }
  }

  // https://docs.aws.amazon.com/AmazonS3/latest/userguide/mpu-upload-object.html
  // Default part size: 5 MB.
  def lowLevelMultipartUpload(keyName: String, filePath: String, partSize: Long = 5 * 1024 * 1024, metaData: Map[String, String] = Map()): String = {
    try {
      val file = new File(filePath)
      val contentLength = file.length
      val partETags = new util.ArrayList[PartETag]
      val initRequest = new InitiateMultipartUploadRequest(bucketName, keyName)
      if (metaData.nonEmpty) {
        val metadata = new ObjectMetadata()
        metadata.setContentType("plain/text")
        metaData.foreach { case (key, value) => metadata.addUserMetadata(key, value) }
        initRequest.setObjectMetadata(metadata)
      }
      val initResponse = s3Client.initiateMultipartUpload(initRequest)

      var filePosition: Long = 0
      var i = 1
      var currentPartSize = partSize
      while (filePosition < contentLength) {
        currentPartSize = Math.min(currentPartSize, contentLength - filePosition)
        val uploadRequest = new UploadPartRequest().withBucketName(bucketName).withKey(keyName).withUploadId(initResponse.getUploadId).withPartNumber(i).withFileOffset(filePosition).withFile(file).withPartSize(partSize)
        val uploadResult = s3Client.uploadPart(uploadRequest)
        partETags.add(uploadResult.getPartETag)
        filePosition += currentPartSize
        i += 1
      }
      val compRequest = new CompleteMultipartUploadRequest(bucketName, keyName, initResponse.getUploadId, partETags)
      s3Client.completeMultipartUpload(compRequest)
      initResponse.getUploadId
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
      case ioException: IOException => logger.error(ioException.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_UPLOAD_FAILURE, ioException)
    }
  }

  def getMultipartUploadListingLowLevel: MultipartUploadListing = {
    try {
      val allMultipartUploadsRequest = new ListMultipartUploadsRequest(bucketName)
      s3Client.listMultipartUploads(allMultipartUploadsRequest)
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  private val awsS3Runnable = new Runnable {
    def run(): Unit = {
      val lastDate = new JavaDate(System.currentTimeMillis - maxMultiPartUploadTime)
      // High level abort multipart uploads
      transferManager.abortMultipartUploads(bucketName, lastDate)
    }
  }

  actors.Service.actorSystem.scheduler.scheduleWithFixedDelay(initialDelay = 10000.second, delay = maxMultiPartUploadTime.second)(awsS3Runnable)(schedulerExecutionContext)

  def lowLevelAbortMultipartUploads(uploadID: String, objectKey: String): Unit = {
    try {
      s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, objectKey, uploadID))
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  // Only for files less than 5GB
  def copyObject(sourceKey: String, destinationKey: String, destinationBucketName: String = bucketName): CopyObjectResult = {
    try {
      val copyObjRequest = new CopyObjectRequest(bucketName, sourceKey, destinationBucketName, destinationKey)
      s3Client.copyObject(copyObjRequest)
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  def exists(objectKey: String, bucketName: String = bucketName): Boolean = {
    try {
      s3Client.doesObjectExist(bucketName, objectKey)
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  def renameObject(sourceKey: String, destinationKey: String, destinationBucketName: String = bucketName): Unit = {
    try {
      copyObject(sourceKey = sourceKey, destinationKey = destinationKey, destinationBucketName = destinationBucketName)
      deleteObject(sourceKey)
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  def getFullObject(objectKey: String, customFileName: String = ""): S3Object = {
    try {
      val request = new GetObjectRequest(bucketName, objectKey)
      if (customFileName != "") {
        val headerOverrides = new ResponseHeaderOverrides().withCacheControl("No-cache").withContentDisposition("attachment; filename=" + customFileName)
        request.setResponseHeaders(headerOverrides)
      }
      s3Client.getObject(request)
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
      case ioException: IOException => logger.error(ioException.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_UPLOAD_FAILURE, ioException)
    }
  }

  def createFileAndGetMetaData(objectKey: String, filePath: String): Map[String, String] = {
    try {
      val s3Object = s3Client.getObject(bucketName, objectKey)
      val os: OutputStream = new FileOutputStream(new File(filePath))
      os.write(s3Object.getObjectContent.readAllBytes())
      s3Object.getObjectMetadata.getUserMetadata.asScala.toMap
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
      case ioException: IOException => logger.error(ioException.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_UPLOAD_FAILURE, ioException)
    }
  }

  def deleteObjectNonVersionedBucket(objectKey: String): Unit = {
    try {
      s3Client.deleteObject(new DeleteObjectRequest(bucketName, objectKey))
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  def deleteObjectVersionedBucket(objectKey: String, versionID: String): Unit = {
    try {
      val bucketVersionStatus = s3Client.getBucketVersioningConfiguration(bucketName).getStatus
      if (bucketVersionStatus == BucketVersioningConfiguration.ENABLED) {
        s3Client.deleteVersion(new DeleteVersionRequest(bucketName, objectKey, versionID))
      } else throw new BaseException(constants.Response.AMAZON_S3_NON_VERSIONED_BUCKET)
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  def deleteObject(objectKey: String, versionID: Option[String] = None): Unit = {
    try {
      versionID.fold {
        deleteObjectNonVersionedBucket(objectKey)
      } { version =>
        deleteObjectVersionedBucket(objectKey = objectKey, versionID = version)
      }
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

  def deleteMultipleObjectsNonVersionedBucket(objectKeys: Seq[String]): Unit = {
    try {
      val multiObjectDeleteRequest = new DeleteObjectsRequest(bucketName).withKeys(objectKeys: _*).withQuiet(false)
      s3Client.deleteObjects(multiObjectDeleteRequest).getDeletedObjects.size
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
      case ioException: IOException => logger.error(ioException.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_UPLOAD_FAILURE, ioException)
    }
  }

  // TODO deleteMultipleObjectsVersionedBucket
  def listBucketObjects(): util.List[S3ObjectSummary] = {
    try {
      val listObjects = new ListObjectsRequest().withBucketName(bucketName)
      s3Client.listObjects(listObjects).getObjectSummaries
    } catch {
      case e: AmazonServiceException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_PROCESS_FAILURE, e)
      case e: SdkClientException => logger.error(e.getLocalizedMessage)
        throw new BaseException(constants.Response.AMAZON_S3_CLIENT_CONNECTION_FAILURE, e)
    }
  }

}

