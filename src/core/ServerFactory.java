package core;

import core.blocking.RequestHandler;
import core.nio.ChannelHandler;
import core.nio.EventLoop;

import java.util.concurrent.Executor;

public interface ServerFactory {
    Server buildNioServer(int port, ChannelHandler handler, EventLoop eventLoop);

    Server buildBlockingServer(int port, Executor threadPool, RequestHandler handler);
}
