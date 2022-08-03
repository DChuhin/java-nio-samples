package core.nio;

import java.util.concurrent.CompletableFuture;

public interface ChannelHandler {
    CompletableFuture<byte[]> handle(byte[] request);
}
