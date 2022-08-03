package nioProxy;

import core.nio.ChannelHandler;
import core.nio.Client;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class NioProxyHandler implements ChannelHandler {

    private final Client client;

    public NioProxyHandler(Client client) {
        this.client = client;
    }

    @Override
    public CompletableFuture<byte[]> handle(byte[] request) {
        try {
            return client.doRequest(request);
        } catch (IOException e) {
            System.out.println("Could not proxy request:" + e.getMessage());
            e.printStackTrace();
            return CompletableFuture.failedFuture(e);
        }
    }
}
