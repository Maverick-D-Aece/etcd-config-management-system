package com.tinytinkers.etcdconfigmgmt;

import com.tinytinkers.etcdconfigmgmt.helpers.EnvironmentVariablesRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static org.slf4j.LoggerFactory.getLogger;

public class EtcdConfigManagerVerticle extends AbstractVerticle {

    private static final Logger LOGGER = getLogger(EtcdConfigManagerVerticle.class);

    // TODO: add constants with default values support(enums?) and cleanup the hard-coded values

    private EtcdConfigManagerService etcdConfigManagerService;

    @Override
    public void start(Promise<Void> startPromise) {
        var envConfigs = EnvironmentVariablesRetriever.getVariables();

        var etcdEndPoint = envConfigs.getString("etcd.endpoint", "localhost");
        var etcdPort = envConfigs.getInteger("etcd.port", 2379);
        var configMgmtServerPort = envConfigs.getInteger("config-mgmt-server.port", 8080);

        LOGGER.info(
                "Connected to etcd endpoint --[{}]-- on port:::{}",
                etcdEndPoint, etcdPort
        );
        
        this.etcdConfigManagerService = new EtcdConfigManagerService(etcdEndPoint, etcdPort);

        var router = createRouter();

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(configMgmtServerPort, http -> {
                    if (http.succeeded()) {
                        startPromise.complete();
                        LOGGER.info(
                                "Config Management Server started on port:::{}",
                                http.result().actualPort()
                        );
                    } else startPromise.fail(http.cause());
                });
    }

    private Router createRouter() {
        var router = Router.router(vertx);

        // TODO: add request validations inside the handlers
        //  send and handle post requests via request payload bodies
        router.post("/api/config").handler(this::handlePutConfig);
        router.get("/api/config").handler(this::handleGetConfig);

        return router;
    }

    // TODO: move handlers to different classes
    private void handlePutConfig(RoutingContext routingContext) {
        var request = routingContext.request();
        var key = request.getParam("key");
        var value = request.getParam("value");

        this.etcdConfigManagerService.putConfig(key, value);

        routingContext
                .response()
                .putHeader(CONTENT_TYPE, TEXT_PLAIN)
                .setStatusCode(201)
                .end("Configuration added/updated to etcd");
    }
    private void handleGetConfig(RoutingContext routingContext) {
        var key = routingContext.request().getParam("key");

        var configValue = this.etcdConfigManagerService.getConfig(key);

        if ("N/A".equals(configValue)) routingContext
                .response()
                .setStatusCode(404)
                .end();

        else routingContext
                .response()
                .putHeader(CONTENT_TYPE, TEXT_PLAIN)
                .setStatusCode(200)
                .end(configValue);
    }

}
