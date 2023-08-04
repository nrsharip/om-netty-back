import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class CustomHttpServerHandler extends SimpleChannelInboundHandler {

    private HttpRequest request;

    StringBuilder responseData = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

        System.out.println("1");

        if (msg instanceof HttpRequest) {

            HttpRequest request = this.request = (HttpRequest) msg;

            if (HttpUtil.is100ContinueExpected(request)) {
                writeResponse(ctx);
            }

            responseData.setLength(0);
            responseData.append(RequestUtils.formatParams(request));
        }

        // responseData.append(RequestUtils.evaluateDecoderResult(request));

        System.out.println("2");

        if (msg instanceof HttpContent) {

            HttpContent httpContent = (HttpContent) msg;

            responseData.append(RequestUtils.formatBody(httpContent));

            System.out.println("5");

            // responseData.append(RequestUtils.evaluateDecoderResult(request));

            if (msg instanceof LastHttpContent) {

                System.out.println("6");

                LastHttpContent trailer = (LastHttpContent) msg;
                responseData.append(RequestUtils.prepareLastResponse(request, trailer));
                writeResponse(ctx, trailer, responseData);
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    private void writeResponse(ChannelHandlerContext ctx) {

        System.out.println("3");

        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, CONTINUE, Unpooled.EMPTY_BUFFER);

        ctx.write(response);
    }

    private void writeResponse(ChannelHandlerContext ctx, LastHttpContent trailer,
                               StringBuilder responseData) {

        boolean keepAlive = HttpUtil.isKeepAlive(request);

        System.out.println("4");

        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                HTTP_1_1,
                trailer.decoderResult().isSuccess() ? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(responseData.toString(), CharsetUtil.UTF_8));

        httpResponse.headers().set(
                HttpHeaderNames.CONTENT_TYPE,
                "text/plain; charset=UTF-8");

        if (keepAlive) {
            httpResponse.headers().setInt(
                    HttpHeaderNames.CONTENT_LENGTH,
                    httpResponse.content().readableBytes());
            httpResponse.headers().set(
                    HttpHeaderNames.CONNECTION,
                    HttpHeaderValues.KEEP_ALIVE);
        }

        ctx.write(httpResponse);

        if (!keepAlive) {
            ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }
}