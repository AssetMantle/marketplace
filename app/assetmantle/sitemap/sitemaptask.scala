package app.assetmantle.sitemap;

import com.redfin.sitemapgenerator.WebSitemapGenerator;
import play.inject.ApplicationLifecycle;


class SitemapTask implements Runnable {

    SitemapConfig sitemapConfig;
   SitemapProviders sitemapProviders;

    // Indicates the application is shutting down, see #22 for more details
    private boolean shuttingDown = false;

   
   inject def SitemapTask(SitemapConfig sitemapConfig, SitemapProviders providers, ApplicationLifecycle lifecycle) {
        this.sitemapConfig = sitemapConfig;
        this.sitemapProviders = providers;
        lifecycle.addStopHook(() -> {
            this.shuttingDown = true;
            return CompletableFuture.completedFuture(null);
        });
    }

    
   override def run() {
        // Akka triggers tasks also when it is shutting down
        if (shuttingDown) return;

        String baseUrl = sitemapConfig.getBaseUrl();
        try {
            WebSitemapGenerator generator = new WebSitemapGenerator(baseUrl, sitemapConfig.getBaseDir());
            List<UrlProvider> providers = sitemapProviders.getProviders();
            for (UrlProvider urlProvider : providers) {
                urlProvider.addUrlsTo(generator);
            }
            generator.write();
            try {
                generator.writeSitemapsWithIndex();
            } catch (RuntimeException ex) {
                play.Logger.warn("Could not create sitemap index", ex);
            }
        } catch(MalformedURLException ex) {
            play.Logger.error("Oops! Can't create a sitemap generator for the given baseUrl " + baseUrl, ex);
        }
    }