import blockingProxy.BlockingProxyHandler;
import core.Server;
import core.ServerFactory;
import core.blocking.Client;
import core.blocking.RequestHandler;
import core.blocking.client.DefaultBlockingClient;
import core.factory.ServerFactoryImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BlockingProxyApp {

    private static final int SERVER_PORT = 5454;

    public static void main(String[] args) {
        int threads = threadsCount();
        Executor threadPool = Executors.newFixedThreadPool(threads);
        System.out.println("Running " + threads + " threads");
        RequestHandler requestHandler = requestHandler();

        ServerFactory serverFactory = new ServerFactoryImpl();
        Server server = serverFactory.buildBlockingServer(SERVER_PORT, threadPool, requestHandler);
        server.start();
    }

    private static RequestHandler requestHandler() {
        String host = System.getenv("INTERNAL_SERVER_HOST");
        String port = System.getenv("INTERNAL_SERVER_PORT");
        if (host == null || host.isBlank()) {
            host = "localhost";
        }
        if (port == null || port.isBlank()) {
            port = "5456";
        }
        Client client = new DefaultBlockingClient(host, Integer.parseInt(port));
        return new BlockingProxyHandler(client);
    }

    private static int threadsCount() {
        String nThreadsString = System.getenv("JAVA_THREADS");
        if (nThreadsString != null && !nThreadsString.isBlank()) {
            return Integer.parseInt(nThreadsString);
        }
        return Runtime.getRuntime().availableProcessors();
    }
}
