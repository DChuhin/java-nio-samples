package core.nio.eventLoop;

import java.util.concurrent.ThreadFactory;

public class EventLoopThreadFactory implements ThreadFactory {

    public static ThreadFactory INSTANCE = new EventLoopThreadFactory();

    private int counter = 0;

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, "Event-loop-" + ++counter);
    }
}
