package core.blocking.server;

import core.Server;
import core.blocking.RequestHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

public class BlockingServer implements Server {

    private static final Logger log = Logger.getLogger(BlockingServer.class.getName());

    private final ServerSocket serverSocket;
    private final Executor threadPool;
    private final RequestHandler handler;

    public BlockingServer(ServerSocket serverSocket, Executor threadPool, RequestHandler handler) {
        this.serverSocket = serverSocket;
        this.threadPool = threadPool;
        this.handler = handler;
    }

    @Override
    public void start() {
        try {
            System.out.println("Blocking server is up and listening");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(() -> this.handleClientSocket(clientSocket));
            }
        } catch (Throwable e) {
            log.severe("Server shutdown because of error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClientSocket(Socket clientSocket) {
        try {
            int available;
            do {
                available = clientSocket.getInputStream().available();
            } while (available == 0);
            byte[] request = clientSocket.getInputStream().readNBytes(available);
            byte[] response = handler.handle(request);
            if (response != null) {
                clientSocket.getOutputStream().write(response);
            } else {
                log.severe("Could not get response");
            }
        } catch (IOException e) {
            log.severe("Could not handle client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                log.severe("Could not close client socket");
                e.printStackTrace();
            }
        }
    }
}
