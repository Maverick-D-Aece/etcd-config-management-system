package com.tinytinkers.etcdconfigmgmt.helpers;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.stream.Collectors;

public class EnvConfigRetriever {

    public static final String CONFIG_PREFIX = "ecms.";

    private EnvConfigRetriever() { }

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvConfigRetriever.class);
    private static JsonObject configs;

    public static Future<Void> init(Vertx vertx) {
        Promise<Void> promise = Promise.promise();
        ConfigStoreOptions env = new ConfigStoreOptions()
                .setType("env");
        ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions()
                .setScanPeriod(5000)
                .addStore(env);
        ConfigRetriever configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);
        configRetriever.getConfig(config -> {
            if (config.succeeded()) {
                var envConfigs = config.result()
                        .stream()
                        .filter(entry -> entry.getKey().startsWith(CONFIG_PREFIX))
                        .collect(Collectors.toMap(entry -> entry.getKey().substring(CONFIG_PREFIX.length()), Map.Entry::getValue));

                configs = new JsonObject(envConfigs);
                promise.complete();
            } else {
                promise.fail(config.cause());
                LOGGER.error("Unable to configure.", config.cause());
            }
        });

        return promise.future();
    }

    public static JsonObject getConfigs() {
        return configs;
    }
}