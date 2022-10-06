package utilities

import controllers.routes
import play.api.mvc.Call

import scala.xml.Elem

case class Sitemap(call: Call, priority: Double = 0.5, changeFrequency: String = "weekly")

object Sitemap {

  val allElements: Seq[Sitemap] = Seq(
    Sitemap(routes.CollectionController.viewCollections("art"), 1.0)
  )


  def generate: Elem = {
    val urlElements = allElements.map { sitemapElement =>
      <url>
        <loc>{sitemapElement.call.url}</loc>
        <changefreq>{sitemapElement.changeFrequency}</changefreq>
        <priority>{sitemapElement.priority}</priority>
      </url>
    }
    <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">
      <url>
        <loc>{constants.CommonConfig.WebAppUrl}/</loc>
        <lastmod>2005-01-01</lastmod>
        <changefreq>weekly</changefreq>
        <priority>1.0</priority>
      </url>
      {urlElements}
    </urlset>
  }
}
