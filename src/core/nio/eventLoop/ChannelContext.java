package core.nio.eventLoop;

import core.nio.ChannelHandler;

public class ChannelContext {
    private final ChannelHandler handler;

    public ChannelContext(ChannelHandler handler) {
        this.handler = handler;
    }

    public ChannelHandler getHandler() {
        return handler;
    }
}
