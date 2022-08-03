package core.blocking;

public interface RequestHandler {
    byte[] handle(byte[] request);
}
