package com.tinytinkers.etcdconfigmgmt;

import com.tinytinkers.etcdconfigmgmt.helpers.EnvironmentVariablesRetriever;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

public class MainVerticle {

    public static Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

    public static void main(String[] args) {
        var vertx = Vertx.vertx();

        EnvironmentVariablesRetriever.init(vertx)
                .onSuccess(success -> vertx.deployVerticle(new EtcdConfigManagerVerticle())
                        .onSuccess(deployed -> LOGGER.info("Successfully deployed Etcd Config Manager Verticle"))
                        .onFailure(LOGGER::error)
                )
                .onFailure(LOGGER::error);
    }
}
