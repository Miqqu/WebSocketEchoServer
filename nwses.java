/**
* MIT License
* 
* Copyright (c) [2016] [Mikko Hänninen]
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.ssl.SslContextBuilder;

/**
 * Simple netty.io version 4.0.32 WebSocket echo server. 
 * If port 443 is used, use SSL with self-signed certificate.
 * Echoes back time stamp, ports used, server identification (tag),
 * and received text. 
 * @version 28.4.2016
 */
public final class nwses {
    

    /**
     * Test if valid port number is specified
     * @param arg port number
     * @return True if valid port number
     */
    private static boolean isValidPortNumber(String arg){
        int port;
        try {
            port = Integer.parseInt(arg);    
            if (port < 1 || port > 65535){
                System.out.println("Invalid port number (1 - 65535) For input string " + port);
                return false;
            }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number (1 - 65535) " + e.getMessage());
                return false;
            }
        return true;
    }
    
    
    /**
     * Main
     * @param args Optional command-line parameters for server port and tag
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        final int port;
        final String tag;
        
        boolean SSL = false; 
        
        if (args.length == 0){
            port = 80;
            tag = "server1";
        } else if (args.length == 1){
            if (isValidPortNumber(args[0])){
                port = Integer.parseInt(args[0]);
                tag = "server1";
            } else {
                return;
            }
        } else {
            if (isValidPortNumber(args[0])){
                port = Integer.parseInt(args[0]);
                tag = args[1];
            } else {
                return;
            }
            
        }
        
        // If port 443 is specified to be used, use SSL.
        if (port == 443){
            SSL = true;
            System.out.println("Websocket secure connection server initialized.");
        }
        
        final SslContext sslCtx;
            if (SSL) {
                SelfSignedCertificate ssc = new SelfSignedCertificate();
                sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
                } else {
                    sslCtx = null;
                }
        
        
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.TRACE))
            .childHandler(new WebSocketServerInitializer(sslCtx, tag));
            
            System.out.println("Server: " + tag + " started at port: " + port + " \n");
            
            Channel chnl = b.bind(port).sync().channel();
            chnl.closeFuture().sync();
            
        } 
        catch (Exception e){       //Catch exceptions e.g. trying to open second server on same port
            System.err.println("Caught exception: " + e.getMessage());
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("Server closed..");
        }
    }
}
