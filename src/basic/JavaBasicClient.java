package basic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JavaBasicClient {

    // Client is blocking for simplicity
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5456);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out.println("Hello from client");
        System.out.println(in.readLine());
    }
}
