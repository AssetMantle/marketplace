package app.assetmantle.sitemap;

import com.google.inject.AbstractModule;

class SitemapModule extends AbstractModule {

override def configure() {
        bind(SitemapConfig.class).asEagerSingleton();
        bind(SitemapProviders.class).asEagerSingleton();
        bind(SitemapJob.class).asEagerSingleton();
    }
}