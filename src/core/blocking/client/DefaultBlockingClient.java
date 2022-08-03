package core.blocking.client;

import core.blocking.Client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultBlockingClient implements Client {

    private final String host;
    private final int port;

    private AtomicInteger counter = new AtomicInteger(0);

    public DefaultBlockingClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public byte[] doRequest(byte[] request) throws IOException {
        Socket socket = new Socket(host, port);

        OutputStream out = socket.getOutputStream();
        System.out.println(request.length);
        out.write(request);
        out.flush();
        System.out.println(Thread.currentThread().getName() + " Sent request " + counter.incrementAndGet());
        return socket.getInputStream().readAllBytes();
    }
}
