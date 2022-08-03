package core.blocking;

import java.io.IOException;

public interface Client {
    byte[] doRequest(byte[] request) throws IOException;
}
