package core.nio.eventLoop;

import core.nio.ChannelHandler;
import core.nio.EventLoop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class DefaultEventLoop implements EventLoop {

    private static final Logger log = Logger.getLogger(DefaultEventLoop.class.getName());

    private final Selector selector;
    private final Executor executor = Executors.newSingleThreadExecutor(EventLoopThreadFactory.INSTANCE);

    private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    private int readCounter = 0;
    private int writeCounter = 0;

    public DefaultEventLoop(Selector selector) {
        this.selector = selector;
    }

    @Override
    public void assignNewChannel(SocketChannel channel, ChannelHandler handler) throws IOException {
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, new ChannelContext(handler));
    }

    @Override
    public void start() {
        executor.execute(this::run);
    }

    @Override
    public void submit(Consumer<ByteBuffer> task) {
        executor.execute(() -> task.accept(buffer));
    }

    private void run() {
        try {
            // without timeout it will block executing callback, as it's executed on the same executor
            int ready = selector.select(10);
            if (ready == 0) {
                return;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            var iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isReadable()) {
                    handleChannelRead(buffer, key);
                }
                iterator.remove();
            }
        } catch (IOException e) {
            log.info("Error during processing channels: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.execute(this::run);
        }
    }

    private void handleChannelRead(ByteBuffer buffer, SelectionKey key) throws IOException {
        System.out.println(Thread.currentThread().getName() + " read " + ++readCounter);

        // write to buffer (channel)
        buffer.clear();
        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);
        buffer.flip();

        if (buffer.limit() == 0) {
            System.out.println(Thread.currentThread().getName() + " " + readCounter + " disrupted");
            client.close();
            return;
        }

        // read from buffer (app)
        byte[] request = new byte[buffer.limit()];
        buffer.get(request);
        buffer.clear();

        ChannelContext context = (ChannelContext) key.attachment();
        context.getHandler().handle(request)
            .orTimeout(60, TimeUnit.SECONDS)
            .whenCompleteAsync((response, e) -> handleChannelWrite(client, response, e), executor);
    }

    private void handleChannelWrite(SocketChannel channel, byte[] response, Throwable e) {
        System.out.println(Thread.currentThread().getName() + " write " + ++writeCounter);
        buffer.clear();
        if (e != null) {
            log.info("Error handling read: " + e.getMessage() + " " + Thread.currentThread().getName());
            e.printStackTrace();
            return;
        }
        if (!channel.isOpen()) {
            System.out.println(Thread.currentThread().getName() + " write error " + writeCounter);
            return;
        }
        // write to buffer (app)
        buffer.put(response);
        buffer.flip();
        try {
            // read from buffer (channel)
            int write = channel.write(buffer);
            if (write == 0) {
                log.warning("Could not write to channel. Full");
            }
            channel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            log.info("Could not write to channel: " + ex.getMessage());
        } finally {
            buffer.clear();
        }
    }

}
