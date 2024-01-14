package cn.zurish.snow.rpc.transport.netty.client;

import cn.zurish.snow.rpc.common.entity.SnowResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 2024/1/13 20:36
 */
public class UnprocessedRequests {

    private static ConcurrentHashMap<String, CompletableFuture<SnowResponse<?>>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<SnowResponse<?>> future) {
        unprocessedResponseFutures.put(requestId, future);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(SnowResponse<?> response) {
        CompletableFuture<SnowResponse<?>> future = unprocessedResponseFutures.remove(response.getRequestId());
        if ( future != null) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }
}
