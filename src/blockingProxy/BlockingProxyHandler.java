package blockingProxy;

import core.blocking.Client;
import core.blocking.RequestHandler;

import java.io.IOException;

public class BlockingProxyHandler implements RequestHandler {

    private final Client client;

    public BlockingProxyHandler(Client client) {
        this.client = client;
    }

    @Override
    public byte[] handle(byte[] request) {
        try {
            return client.doRequest(request);
        } catch (IOException e) {
            System.out.println("Could not proxy request:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
