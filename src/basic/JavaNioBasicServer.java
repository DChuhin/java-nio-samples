package basic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class JavaNioBasicServer {

    public static void main(String[] args) throws IOException {

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(5456));
        serverSocket.configureBlocking(false);

        Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        System.out.println("Server started");
        while (true) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            var iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    var clientSocket = serverSocket.accept();
                    clientSocket.configureBlocking(false);
                    clientSocket.register(selector, SelectionKey.OP_READ);
                }
                if (key.isReadable()) {
                    handleRead(key, buffer);
                }
                iterator.remove();
            }
        }
    }

    private static void handleRead(SelectionKey selectionKey, ByteBuffer buffer) throws IOException {
        // Read from socket to buffer
        SocketChannel channel = (SocketChannel) selectionKey.channel();
        channel.read(buffer);

        // Read from buffer to app
        buffer.flip();
        byte[] requestBytes = new byte[buffer.limit()];
        buffer.get(requestBytes);
        String requestString = new String(requestBytes);
        System.out.println("Request receiver: " + requestString);
        String responseString = "Echo from server: " + requestString;
        byte[] responseBytes = responseString.getBytes(StandardCharsets.UTF_8);

        // Write from app to buffer
        buffer.clear();
        buffer.put(responseBytes);

        // Write from buffer to app
        buffer.flip();
        channel.write(buffer);
        buffer.clear();
        channel.close();
    }

}
