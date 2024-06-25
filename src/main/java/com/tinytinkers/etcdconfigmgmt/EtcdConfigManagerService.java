package com.tinytinkers.etcdconfigmgmt;

import com.google.common.util.concurrent.Futures;
import com.google.protobuf.ByteString;
import com.ibm.etcd.client.EtcdClient;
import com.ibm.etcd.client.kv.KvClient;
import org.slf4j.Logger;

import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

public class EtcdConfigManagerService {
    private static final Logger LOGGER = getLogger(EtcdConfigManagerService.class);
    public static final long READ_TIMEOUT = 5000;

    private final KvClient kvClient;

    public EtcdConfigManagerService(String endpoint, int port) {
        this.kvClient = EtcdClient
                .forEndpoint(endpoint, port)
                .withPlainText()
                .build()
                .getKvClient();
    }

    public void putConfig(String key, String value) {
        this.kvClient
                .put(ByteString.copyFromUtf8(key), ByteString.copyFromUtf8(value))
                .sync();
    }

    public String getConfig(String key) {
        try {
            var listenableFuture = this.kvClient
                    .get(ByteString.copyFromUtf8(key))
                    .timeout(READ_TIMEOUT)
                    .async();

            var rangeResponse = Futures.getChecked(
                    listenableFuture,
                    TimeoutException.class,
                    READ_TIMEOUT,
                    MILLISECONDS
            );

            return rangeResponse.getKvsList().stream()
                    .findFirst()
                    .map(keyValue -> keyValue.getValue().toStringUtf8())
                    .orElseGet(() -> {
                        LOGGER.error("Config not found for key - [{}]!", key);
                        return "N/A";
                    });
        } catch (TimeoutException e) {
            LOGGER.error("Timeout while getting config for key - [{}]! Exception: {}", key, e.getMessage());
            return "N/A";
        }
    }

    // TODO: add method to watch config changes as well
}
