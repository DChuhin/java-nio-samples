import nioProxy.NioProxyHandler;
import core.nio.ChannelHandler;
import core.nio.Client;
import core.nio.EventLoop;
import core.nio.EventLoopFactory;
import core.Server;
import core.ServerFactory;
import core.nio.client.DefaultNioClient;
import core.nio.eventLoop.DefaultEventLoopFactory;
import core.factory.ServerFactoryImpl;

public class NioProxyApp {

    private static final int SERVER_PORT = 5455;

    public static void main(String[] args) {
        EventLoopFactory eventLoopFactory = new DefaultEventLoopFactory();
        EventLoop eventLoop = eventLoopFactory.build();

        ChannelHandler requestHandler = requestHandler(eventLoop);

        ServerFactory serverFactory = new ServerFactoryImpl();
        Server server = serverFactory.buildNioServer(SERVER_PORT, requestHandler, eventLoop);
        server.start();
    }

    public static ChannelHandler requestHandler(EventLoop eventLoop) {
        String host = System.getenv("INTERNAL_SERVER_HOST");
        String port = System.getenv("INTERNAL_SERVER_PORT");
        if (host == null || host.isBlank()) {
            host = "localhost";
        }
        if(port == null || port.isBlank()) {
            port = "5456";
        }
        Client client = new DefaultNioClient(host, Integer.parseInt(port), eventLoop);
        return new NioProxyHandler(client);
    }
}
