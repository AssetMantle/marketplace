package app.assetmantle.sitemap;
import com.redfin.sitemapgenerator.ChangeFreq;
 @interface SitemapItem {

  
  ChangeFreq changefreq() default ChangeFreq.DAILY;
  double priority() default 0.5;
}