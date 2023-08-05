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

    static void checkBody(HttpContent httpContent) {

        if (httpContent.content().isReadable()) {

            RequestProcessor.content = httpContent.content();

            System.out.println("content: " + RequestProcessor.content.toString(CharsetUtil.UTF_8));

        }

    }

    static StringBuilder finalize(LastHttpContent trailer) {

        switch (RequestProcessor.method.name()) {
            case "GET":
                long millis = RequestProcessor.params.containsKey("millis") ?
                        Long.parseLong(RequestProcessor.params.get("millis").get(0)) : 0;

                RequestProcessor.response
                        .append("{ \"items\": ")
                        .append(ChatMessageStorage.INSTANCE.getMessages(millis))
                        .append(" }");

                break;
            case "POST":
                ChatMessageStorage.INSTANCE.addMessage(
                        RequestProcessor.content.toString(CharsetUtil.UTF_8)
                );
                break;
        }

        RequestProcessor.request = null;
        RequestProcessor.method = null;
        RequestProcessor.params = null;
        RequestProcessor.content = null;

        return RequestProcessor.response;
    }
}
