package com.edulify.modules.sitemap;

import akka.actor.ActorSystem;
import play.Application;
import play.Play;
import play.libs.Akka;
import scala.concurrent.duration.FiniteDuration;

import akka.dispatch.MessageDispatcher;


singleton class SitemapJob {

     SitemapConfig config;
     SitemapTask task;
     ActorSystem actorSystem;

    Inject
     SitemapJob(ActorSystem actorSystem, SitemapConfig config, SitemapTask task) {
        this.config = config;
        this.actorSystem = actorSystem;
        this.task = task;
        this.init();
    }
void init() {
        MessageDispatcher executionContext = actorSystem.dispatchers().lookup(config.getDispatcherName());

        this.actorSystem
                .scheduler()
                .schedule(
                        (FiniteDuration) FiniteDuration.create(config.getInitialDelay()),
                        (FiniteDuration) FiniteDuration.create(config.getExecutionInterval()),
                        task,
                        executionContext
                );
    }

    /**
     * @deprecated Use com.edulify.modules.sitemap.SitemapModule instead.
     */
    
    def void startSitemapGenerator() {
        Application application = Play.application();
        ActorSystem actorSystem = Akka.system();
        SitemapConfig sitemapConfig = application.injector().instanceOf(SitemapConfig.class);
        SitemapTask task = application.injector().instanceOf(SitemapTask.class);
        new SitemapJob(actorSystem, sitemapConfig, task).init();
    }
}