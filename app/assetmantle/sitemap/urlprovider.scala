package app.assetmantle.sitemap;

import com.redfin.sitemapgenerator.WebSitemapGenerator;

public interface UrlProvider {

  /**
   * Receives a sitemap generator and add urls to it.
   *
   * @param generator the web site generator
   */
  void addUrlsTo(WebSitemapGenerator generator);
}