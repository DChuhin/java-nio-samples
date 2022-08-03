package core.nio.server;

import core.nio.ChannelHandler;
import core.nio.EventLoop;
import core.Server;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

public class NioServer implements Server {

    private static final Logger log = Logger.getLogger(NioServer.class.getName());

    private final Selector selector;
    private final ServerSocketChannel serverSocket;
    private final ChannelHandler handler;
    private final EventLoop eventLoop;

    private int requestCounter = 0;

    public NioServer(Selector selector, ServerSocketChannel serverSocket, EventLoop eventLoop, ChannelHandler handler) {
        if (selector == null) {
            throw new NullPointerException("Selector cannot be null");
        }
        if (serverSocket == null) {
            throw new NullPointerException("Socket cannot be null");
        }
        if (eventLoop == null) {
            throw new NullPointerException("Event loop cannot be null");
        }
        if (handler == null) {
            throw new NullPointerException("Handler cannot be null");
        }
        this.selector = selector;
        this.serverSocket = serverSocket;
        this.handler = handler;
        this.eventLoop = eventLoop;
    }

    @Override
    public void start() {
        try {
            System.out.println("Nio server is up and listening");
            while (true) {
                try {
                    int ready = selector.select();
                    if (ready == 0) {
                        continue;
                    }
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();
                    var iterator = selectionKeys.iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        if (key.isAcceptable()) {
                            addNewClient();
                        }
                        iterator.remove();
                    }
                } catch (IOException e) {
                    System.out.println("Error during processing channels: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Throwable e) {
            log.severe("Server shutdown because of error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void addNewClient() throws IOException {
        System.out.println("new client " + ++requestCounter);
        SocketChannel client = serverSocket.accept();
        if (client != null) {
            eventLoop.assignNewChannel(client, handler);
        }
    }
}
