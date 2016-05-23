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
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Echoes back time stamp, ports used, server identification (tag), and received text.
 * @version 28.4.2016
 *
 */
public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final String tag;
    
    /**
     * Constructor
     * @param tagi server indetification tag
     */
    public WebSocketFrameHandler(String tagi){
        this.tag = tagi;
    }
    
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        
        if (frame instanceof TextWebSocketFrame){
            String inboundMessage = ((TextWebSocketFrame) frame).text();
            Timestamp currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
            
            //Message to be sent.
            String Echo = "Timestamp: " + currentTimestamp + " "
                    + ctx.channel().toString() + "\n" + "Server tag: " + tag  
                    + "Server tag: " + tag + " -- Text: " + inboundMessage;
           
            
            System.out.println(Echo);  //same message to server's out
            ctx.channel().writeAndFlush(new TextWebSocketFrame(Echo));
            
        } else {
            String message = "Unsupported frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }
        
    }
    
}
