package core.nio.eventLoop;

import core.nio.ChannelHandler;
import core.nio.EventLoop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

public class LoadBalancedEventLoop implements EventLoop {

    private final List<EventLoop> eventLoops;
    private Iterator<EventLoop> eventLoopIterator;

    public LoadBalancedEventLoop(List<EventLoop> eventLoops) {
        this.eventLoops = eventLoops;
        eventLoopIterator = eventLoops.iterator();
    }

    @Override
    public synchronized void assignNewChannel(SocketChannel channel, ChannelHandler handler) throws IOException {
        EventLoop delegate = pickEventLoop();
        delegate.assignNewChannel(channel, handler);
    }

    @Override
    public void start() {
        eventLoops.forEach(EventLoop::start);
    }

    @Override
    public synchronized void submit(Consumer<ByteBuffer> task) {
        EventLoop delegate = pickEventLoop();
        delegate.submit(task);
    }

    private EventLoop pickEventLoop() {
        if (!eventLoopIterator.hasNext()) {
            eventLoopIterator = eventLoops.iterator();
        }
        return eventLoopIterator.next();
    }
}
