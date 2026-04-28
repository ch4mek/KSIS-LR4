import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Http {

    private final String method;
    private final String fullUrl;
    private final String host;
    private final int port;
    private final String path;
    private final String httpVersion;
    private final List<String> headers;
    private final int contentLength;

    private Http(String method, String fullUrl, String host, int port,
                 String path, String httpVersion, List<String> headers,
                 int contentLength) {
        this.method = method;
        this.fullUrl = fullUrl;
        this.host = host;
        this.port = port;
        this.path = path;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.contentLength = contentLength;
    }

    public static Http parse(String requestLine, List<String> headerLines) {
        String[] parts = requestLine.split(" ", 3);
        if (parts.length < 2) {
            return null;
        }

        String method = parts[0];
        String url = parts[1];
        String httpVersion = parts.length >= 3 ? parts[2] : "HTTP/1.1";

        String host;
        int port;
        String path;

        try {
            URI uri = new URI(url);
            host = uri.getHost();
            port = uri.getPort();
            path = uri.getPath();
            if (path == null || path.isEmpty()) {
                path = "/";
            }
            if (uri.getQuery() != null) {
                path += "?" + uri.getQuery();
            }
            if (port == -1) {
                port = 80;
            }
        } catch (URISyntaxException e) {
            return null;
        }

        if (host == null || host.isEmpty()) {
            return null;
        }

        int contentLength = 0;
        for (String header : headerLines) {
            String lower = header.toLowerCase();
            if (lower.startsWith("content-length:")) {
                try {
                    contentLength = Integer.parseInt(header.substring(15).trim());
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return new Http(method, url, host, port, path, httpVersion,
                headerLines, contentLength);
    }

    public String getMethod() { return method; }
    public String getFullUrl() { return fullUrl; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getPath() { return path; }
    public String getHttpVersion() { return httpVersion; }
    public List<String> getHeaders() { return headers; }
    public int getContentLength() { return contentLength; }
}
