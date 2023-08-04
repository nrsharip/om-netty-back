import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.Map;

public class RequestUtils {

    static StringBuilder formatParams(HttpRequest request) {

        StringBuilder responseData = new StringBuilder();

        System.out.println("uri: " + request.uri());
        System.out.println("method: " + request.method());

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());

        Map<String, List<String>> params = queryStringDecoder.parameters();

        if (!params.isEmpty()) {

            for (Map.Entry<String, List<String>> entry : params.entrySet()) {

                String key = entry.getKey();
                List<String> vals = entry.getValue();

                for (String val : vals) {
                    responseData.append("Parameter: ")
                            .append(key.toUpperCase())
                            .append(" = ")
                            .append(val.toUpperCase())
                            .append("\r\n");
                }
            }

            responseData.append("\r\n");
        }

        return responseData;
    }

    static StringBuilder formatBody(HttpContent httpContent) {

        StringBuilder responseData = new StringBuilder();

        ByteBuf content = httpContent.content();

        if (content.isReadable()) {

            System.out.println("content: " + content.toString(CharsetUtil.UTF_8));

            responseData
                    .append(content.toString(CharsetUtil.UTF_8).toUpperCase())
                    .append("\r\n");
        }

        return responseData;
    }

    static StringBuilder prepareLastResponse(HttpRequest request, LastHttpContent trailer) {

        StringBuilder responseData = new StringBuilder();

        responseData.append("Good Bye!\r\n");

        if (!trailer.trailingHeaders().isEmpty()) {

            responseData.append("\r\n");

            for (CharSequence name : trailer.trailingHeaders().names()) {

                for (CharSequence value : trailer.trailingHeaders().getAll(name)) {

                    responseData
                            .append("P.S. Trailing Header: ")
                            .append(name)
                            .append(" = ")
                            .append(value)
                            .append("\r\n");
                }
            }

            responseData.append("\r\n");
        }

        return responseData;

    }

    public static boolean evaluateDecoderResult(HttpRequest request) {
        return true;
    }
}
