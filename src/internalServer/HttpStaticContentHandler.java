package internalServer;

import core.nio.ChannelHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class HttpStaticContentHandler implements ChannelHandler {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(12);

    private final StaticContent staticContent;

    public HttpStaticContentHandler(StaticContent staticContent) {
        this.staticContent = staticContent;
    }

    @Override
    public CompletableFuture<byte[]> handle(byte[] request) {
        String requestString = new String(request);/*
            // there should be no 2 line breaks in message so we remove it from incoming message and add in the end of response later
            .replace("\n", "");*/

        CompletableFuture<byte[]> promise = new CompletableFuture<>();

        // intentional delay
        scheduler.schedule(() -> {
            try {
                // String path = HttpUtils.getPathFromRequest(requestString);
                String path = "/";
                String resource = staticContent.get(path);
                String responseString = HttpUtils.buildResponse(resource);
                byte[] responseBytes = responseString.getBytes(StandardCharsets.UTF_8);
                promise.complete(responseBytes);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Exception: " + e.getMessage());
            }
        }, 200, MILLISECONDS);
        return promise;
    }
}
