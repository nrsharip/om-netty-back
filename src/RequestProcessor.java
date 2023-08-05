import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

public class RequestProcessor {
    public static HttpRequest request = null;
    public static HttpMethod method = null;
    public static Map<String, List<String>> params = null;
    public static ByteBuf content = null;
    public static StringBuilder response = new StringBuilder();

    static void checkParams(HttpRequest request) {

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());

        RequestProcessor.request = request;
        RequestProcessor.method = request.method();
        RequestProcessor.params = queryStringDecoder.parameters();
        RequestProcessor.response.setLength(0);

        System.out.println("method: " + RequestProcessor.method);
        System.out.println("params: " + RequestProcessor.params);
    }

    static StringBuilder checkBody(HttpContent httpContent) {

        RequestProcessor.content = httpContent.content();

        if (content.isReadable()) {

            System.out.println("content: " + RequestProcessor.content.toString(CharsetUtil.UTF_8));

            RequestProcessor.response
                    .append(RequestProcessor.content.toString(CharsetUtil.UTF_8).toUpperCase())
                    .append("\r\n");
        }

        return RequestProcessor.response;
    }

    static StringBuilder finalize(LastHttpContent trailer) {

        RequestProcessor.request = null;
        RequestProcessor.method = null;
        RequestProcessor.params = null;
        RequestProcessor.content = null;

        return RequestProcessor.response;
    }
}
