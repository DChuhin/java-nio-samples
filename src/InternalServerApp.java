import internalServer.HttpStaticContentHandler;
import internalServer.StaticContent;
import core.nio.ChannelHandler;
import core.nio.EventLoop;
import core.nio.EventLoopFactory;
import core.Server;
import core.ServerFactory;
import core.nio.eventLoop.DefaultEventLoopFactory;
import core.factory.ServerFactoryImpl;

public class InternalServerApp {

    private static final int SERVER_PORT = 5456;

    public static void main(String[] args) {
        EventLoopFactory eventLoopFactory = new DefaultEventLoopFactory();
        EventLoop eventLoop = eventLoopFactory.build();

        ChannelHandler requestHandler = requestHandler();

        ServerFactory serverFactory = new ServerFactoryImpl();
        Server server = serverFactory.buildNioServer(SERVER_PORT, requestHandler, eventLoop);
        server.start();
    }

    public static ChannelHandler requestHandler() {
        StaticContent staticContent = new StaticContent();
        staticContent.init();
        return new HttpStaticContentHandler(staticContent);
    }
}
