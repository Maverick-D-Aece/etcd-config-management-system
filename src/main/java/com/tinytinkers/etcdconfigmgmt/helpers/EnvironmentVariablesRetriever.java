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

public class EnvironmentVariablesRetriever {

    public static final String VARIABLE_PREFIX = "ecms.";

    private EnvironmentVariablesRetriever() { }

    private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentVariablesRetriever.class);
    private static JsonObject variables;

    public static Future<Void> init(Vertx vertx) {
        Promise<Void> promise = Promise.promise();
        var env = new ConfigStoreOptions()
                .setType("env");
        var configRetrieverOptions = new ConfigRetrieverOptions()
                .setScanPeriod(5000)
                .addStore(env);

        var configRetriever = ConfigRetriever.create(vertx, configRetrieverOptions);
        configRetriever.getConfig(configRetrieval -> {
            if (configRetrieval.succeeded()) {
                var envVars = configRetrieval.result()
                        .stream()
                        .filter(entry -> entry.getKey().startsWith(VARIABLE_PREFIX))
                        .collect(Collectors.toMap(entry ->
                                entry.getKey().substring(VARIABLE_PREFIX.length()),
                                Map.Entry::getValue)
                        );

                variables = new JsonObject(envVars);
                promise.complete();
            } else {
                promise.fail(configRetrieval.cause());
                LOGGER.error("Unable to configure.", configRetrieval.cause());
            }
        });

        return promise.future();
    }

    public static JsonObject getVariables() {
        return variables;
    }
}