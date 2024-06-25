package com.tinytinkers.etcdconfigmgmt;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.TEXT_PLAIN;
import static org.slf4j.LoggerFactory.getLogger;

public class EtcdConfigVerticle extends AbstractVerticle {

    private static final Logger LOGGER = getLogger(EtcdConfigVerticle.class);

    private EtcdConfigClient etcdConfigClient;

    @Override
    public void start(Promise<Void> startPromise) {
        String etcdEndPoint = config().getString("etcd.endpoint", "localhost");
        int etcdPort = config().getInteger("etcd.port", 2379);
        int configMgmtServerPort = config().getInteger("configMgmtServer.port", 8080);

        LOGGER.info(
                "Connected to etcd endpoint --[{}]-- on port:::{}",
                etcdEndPoint, etcdPort
        );
        
        this.etcdConfigClient = new EtcdConfigClient(etcdEndPoint, etcdPort);
        Router router = createRouter();

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

    @Override
    public void stop(Promise<Void> stopPromise) {
        this.etcdConfigClient.stopExecutorService();
        stopPromise.complete();
    }

    private Router createRouter() {
        Router router = Router.router(vertx);

        // TODO: add request validations inside the handlers
        router.post("/api/config").handler(this::handlePutConfig);
        router.get("/api/config").handler(this::handleGetConfig);

        return router;
    }

    private void handlePutConfig(RoutingContext routingContext) {
        HttpServerRequest request = routingContext.request();
        String key = request.getParam("key");
        String value = request.getParam("value");

        this.etcdConfigClient.putConfig(key, value);

        routingContext
                .response()
                .putHeader(CONTENT_TYPE, TEXT_PLAIN)
                .setStatusCode(201)
                .end("Configuration added/updated to etcd");
    }

    private void handleGetConfig(RoutingContext routingContext) {
        String key = routingContext.request().getParam("key");

        String configValue = this.etcdConfigClient.getConfig(key);

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
