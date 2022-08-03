package core.factory;

import core.blocking.RequestHandler;
import core.blocking.server.BlockingServer;
import core.nio.ChannelHandler;
import core.nio.EventLoop;
import core.Server;
import core.ServerFactory;
import core.nio.server.NioServer;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Executor;

public class ServerFactoryImpl implements ServerFactory {
    @Override
    public NioServer buildNioServer(int port, ChannelHandler handler, EventLoop eventLoop) {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocket = ServerSocketChannel.open();
            serverSocket.bind(new InetSocketAddress("0.0.0.0", port));

            serverSocket.configureBlocking(false);
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);

            return new NioServer(selector, serverSocket, eventLoop, handler);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not init server: " + e.getMessage());
        }
    }

    @Override
    public Server buildBlockingServer(int port, Executor threadPool, RequestHandler handler) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            return new BlockingServer(serverSocket, threadPool, handler);
        } catch (Exception e) {
            throw new RuntimeException("Could not init server: " + e.getMessage());
        }
    }
}
