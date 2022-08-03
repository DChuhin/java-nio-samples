package core.nio.eventLoop;

import core.nio.EventLoop;
import core.nio.EventLoopFactory;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;

public class DefaultEventLoopFactory implements EventLoopFactory {

    @Override
    public EventLoop build() {
        int threads = threadsCount();
        System.out.println("Running " + threads + " threads");
        if (threads < 1) {
            throw new IllegalArgumentException("Wrong thread count number: " + threads);
        }
        if (threads == 1) {
            EventLoop singleThreadedEventLoop = buildSingleThreaded();
            singleThreadedEventLoop.start();
            return singleThreadedEventLoop;
        }
        List<EventLoop> eventLoops = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            eventLoops.add(buildSingleThreaded());
        }
        EventLoop eventLoop = new LoadBalancedEventLoop(eventLoops);
        eventLoop.start();
        return eventLoop;
    }

    private int threadsCount() {
        String nThreadsString = System.getenv("JAVA_THREADS");
        if (nThreadsString != null && !nThreadsString.isBlank()) {
            return Integer.parseInt(nThreadsString);
        }
        return Runtime.getRuntime().availableProcessors();
    }

    public EventLoop buildSingleThreaded() {
        try {
            Selector selector = Selector.open();
            return new DefaultEventLoop(selector);
        } catch (IOException e) {
            throw new RuntimeException("Could not start event loop");
        }
    }
}
