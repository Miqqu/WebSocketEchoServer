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
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;

/**
 * Server initializer class. 
 * @version 28.4.2016
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    
    private static final String webSocketPath = "/websocket";
    private final String tag;
    private final SslContext sslCtx; 
    
    /**
     * Constructor 
     * @param sslCtx SSL
     * @param tagi server identification tag
     */
    public WebSocketServerInitializer(SslContext sslCtx, String tagi){  
        this.sslCtx = sslCtx;
        this.tag = tagi;
    }
    
    
    @Override
    public void initChannel(SocketChannel chnl) throws Exception{
        ChannelPipeline pipeline = chnl.pipeline();
        
        if (sslCtx != null) {  
            pipeline.addLast(sslCtx.newHandler(chnl.alloc()));
        }
        
        pipeline.addLast(new HttpServerCodec());  
        pipeline.addLast(new HttpObjectAggregator(65536));  
        pipeline.addLast(new WebSocketServerProtocolHandler(webSocketPath, null, true)); 
        pipeline.addLast(new WebSocketFrameHandler(tag));  //server tag passed to frame handler
        
    }

}
