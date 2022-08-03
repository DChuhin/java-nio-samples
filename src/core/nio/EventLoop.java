package core.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;

public interface EventLoop {

    void assignNewChannel(SocketChannel channel, ChannelHandler handler) throws IOException;

    void start();

    void submit(Consumer<ByteBuffer> task);
}
