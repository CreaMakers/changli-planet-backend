package com.hayaizo.chatsystem.websocket;

// 引入必要的类和工具包

import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import java.util.Optional;

/**
 * HttpHeadersHandler 是一个 Netty 入站处理器，用于从 HTTP 请求中提取 token 和 IP 地址，
 * 并将这些信息绑定到当前的 Channel 上，方便后续的业务逻辑使用。
 */
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {

    /**
     * 当有消息读取时会触发该方法。该方法用于处理 HTTP 请求，提取相关信息（如 token 和 IP 地址），
     * 并将处理后的消息传递给下一个处理器。
     *
     * @param ctx 上下文对象，表示当前 Channel 的上下文信息。
     * @param msg 读取的消息对象，可能是 HTTP 请求或其他类型。
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            try {
                // 从请求头中获取 Token
                String token = extractTokenFromHeader(request);

                // 如果解析到 Token，则设置到 Channel 的属性中
                if (token != null) {
                    NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN, token);
                }

                // 移除当前处理器，避免重复处理
                ctx.pipeline().remove(this);

                // 将处理后的请求对象传递给下一个处理器
                ctx.fireChannelRead(request);
            } catch (Exception e) {
                // 处理异常情况
                e.printStackTrace();
                ctx.close(); // 如果需要，可以关闭连接
            }
        } else {
            // 如果不是 HTTP 请求，直接传递给下一个处理器
            ctx.fireChannelRead(msg);
        }
    }

    /**
     * 从 HTTP 请求头中提取 Bearer Token
     *
     * @param request FullHttpRequest 对象
     * @return 提取的 Token 或 null
     */
    private String extractTokenFromHeader(FullHttpRequest request) {
        // 获取请求头中的 Authorization 字段
        String authorizationHeader = request.headers().get("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return null; // 如果请求头不存在或者格式不正确，返回 null
        }

        // 提取 Bearer 后的部分
        return authorizationHeader.substring("Bearer ".length()).trim();
    }
}
