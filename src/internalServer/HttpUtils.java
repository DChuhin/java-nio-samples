package internalServer;

public class HttpUtils {

    private static final String successResponse = "" +
        "HTTP/1.1 200 OK\r\n" +
        "Content-Length: %d\r\n" +
        "Content-Type: text/html\r\n" +
        "\r\n" +
        "%s";

    private static final String notFoundResponse = "HTTP/1.1 404 notFound";

    public static String getPathFromRequest(String request) {
        String[] split = request.split(" ");
        if (split.length > 1) {
            return split[1];
        }
        throw new IllegalArgumentException("Could not extract path. Wrong http request string: " + request);
    }

    public static String buildResponse(String responseBody) {
        return responseBody != null ?
            String.format(successResponse, responseBody.length(), responseBody) :
            notFoundResponse;
    }
}
