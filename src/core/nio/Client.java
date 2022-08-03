package core.nio;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public interface Client {
    CompletableFuture<byte[]> doRequest(byte[] request) throws IOException;
}
