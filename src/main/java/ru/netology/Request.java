package ru.netology;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URLEncodedUtils;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public class Request {

    String method;
    String path;
    List<NameValuePair> queryParams;

    public Request(String method, String path) {
        this.method = method;

        URI uri;

        try {
            uri = new URI(path);
            this.path = uri.getPath();
            this.queryParams = URLEncodedUtils.parse(path, StandardCharsets.UTF_8);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public List<NameValuePair> getQueryParam(String name) {
        return queryParams
                .stream()
                .filter(n -> n.getName().equals(name))
                .collect(Collectors.toList());
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }
}