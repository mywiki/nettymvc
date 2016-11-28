package io.netty.mvc;

import io.netty.mvc.utils.RankoServer;

/**
 *
 * 文档地址:	http://localhost:8200/api/doc
 * 测试方法参见:	直接看代码{@link  io.netty.mvc.controller.DemoAction}
 */
public class ApiServer {

    private static RankoServer server;

    public static void main(String[] args) throws Exception {
        int serverPort = Integer.parseInt(args.length > 0 && args[0] != null ? args[0] : "8200");
        server = new RankoServer(serverPort);
        server.start();
    }
}