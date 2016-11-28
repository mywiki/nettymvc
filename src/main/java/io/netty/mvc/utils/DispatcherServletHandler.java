package io.netty.mvc.utils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedStream;


/**
 * 逻辑入口
 * @author 老佛爷 mywiki95@gmail.com
 *
 */
public class DispatcherServletHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

    private final DispatcherServlet dispatcherServlet;
    private final String url_encoding = "UTF-8";

    public DispatcherServletHandler(DispatcherServlet dispatcherServlet) {
        this.dispatcherServlet = dispatcherServlet;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {
        boolean flag = HttpMethod.POST.equals(fullHttpRequest.method()) || HttpMethod.GET.equals(fullHttpRequest.method());
        if(flag){
            //HTTP请求、GET/POST
            if(channelHandlerContext.channel().isActive()){
                MockHttpServletResponse servletResponse = new MockHttpServletResponse();
                MockHttpServletRequest servletRequest =new MockHttpServletRequest(ApplicationContextHolder.getMvcContext().getServletContext());

                // headers
                for (String name : fullHttpRequest.headers().names()) {
                    for (String value : fullHttpRequest.headers().getAll(name)) {
                        servletRequest.addHeader(name, value);
                    }
                }

                String uri = fullHttpRequest.uri();
                uri = new String(uri.getBytes("ISO8859-1"), url_encoding);
                uri = URLDecoder.decode(uri, url_encoding);
                UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri)
                        .build();
                String path = uriComponents.getPath();
                path = URLDecoder.decode(path, url_encoding);
                servletRequest.setRequestURI(path);
                servletRequest.setServletPath(path);
                servletRequest.setMethod(fullHttpRequest.method().name());

                if (uriComponents.getScheme() != null) {
                    servletRequest.setScheme(uriComponents.getScheme());
                }
                if (uriComponents.getHost() != null) {
                    servletRequest.setServerName(uriComponents.getHost());
                }
                if (uriComponents.getPort() != -1) {
                    servletRequest.setServerPort(uriComponents.getPort());
                }

                ByteBuf content = fullHttpRequest.content();
                content.readerIndex(0);
                byte[] data = new byte[content.readableBytes()];
                content.readBytes(data);
                servletRequest.setContent(data);

                try {
                    if (uriComponents.getQuery() != null) {
                        String query = UriUtils.decode(uriComponents.getQuery(), url_encoding);
                        servletRequest.setQueryString(query);
                    }
                    for (Map.Entry<String, List<String>> entry : uriComponents.getQueryParams().entrySet()) {
                        for (String value : entry.getValue()) {servletRequest.addParameter(
                        	UriUtils.decode(entry.getKey(), url_encoding), UriUtils.decode(value == null ? "" : value, url_encoding)
                        	);
                        }
                    }
                } catch (UnsupportedEncodingException ex) {
                }

                this.dispatcherServlet.service(servletRequest,servletResponse);

                HttpResponseStatus status = HttpResponseStatus.valueOf(servletResponse.getStatus());
                HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);

                channelHandlerContext.write(response);
                InputStream contentStream = new ByteArrayInputStream(servletResponse.getContentAsByteArray());
                ChannelFuture writeFuture = channelHandlerContext.writeAndFlush(new ChunkedStream(contentStream));
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }
}