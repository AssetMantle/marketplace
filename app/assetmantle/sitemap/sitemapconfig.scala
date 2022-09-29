package app.assetmantle.sitemap;
import play.Configuration;
import play.Environment;


@Singleton
public class SitemapConfig {

    private Configuration configuration;
    private Environment environment;


    @Inject
    public SitemapConfig(Configuration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }
