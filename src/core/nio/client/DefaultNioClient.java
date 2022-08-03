package core.nio.client;

import core.nio.Client;
import core.nio.EventLoop;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CompletableFuture;

public class DefaultNioClient implements Client {

    private final String host;
    private final int port;
    private final EventLoop eventLoop;

    public DefaultNioClient(String host, int port, EventLoop eventLoop) {
        this.host = host;
        this.port = port;
        this.eventLoop = eventLoop;
    }

    @Override
    public CompletableFuture<byte[]> doRequest(byte[] request) throws IOException {
        CompletableFuture<byte[]> promise = new CompletableFuture<>();
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(host, port));
        eventLoop.submit((buffer) -> {
            buffer.clear();
            buffer.put(request);
            buffer.flip();
            try {
                channel.write(buffer);
            } catch (IOException e) {
                System.out.println("Could not proxy request");
            } finally {
                buffer.clear();
            }
        });

        eventLoop.assignNewChannel(channel, (response) -> {
            promise.complete(response);
            try {
                channel.close();
            } catch (IOException e) {
                System.out.println("Could not close client channel: " + e.getMessage());
                e.printStackTrace();
            }
            return CompletableFuture.completedFuture(null);
        });
        return promise;
    }
}
