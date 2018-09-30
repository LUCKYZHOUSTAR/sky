package lucky.sky.net.rpc.simple.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import lucky.sky.net.rpc.MethodExecutor;
import lucky.sky.net.rpc.RpcContext;
import lucky.sky.net.rpc.RpcError;
import lucky.sky.net.rpc.RpcException;
import lucky.sky.net.rpc.ServiceContainer;
import lucky.sky.net.rpc.serialization.Serializer;
import lucky.sky.net.rpc.serialization.kryo.KryoSerializer;
import lucky.sky.net.rpc.simple.data.JRequestPayload;
import lucky.sky.net.rpc.simple.data.JResponsePayload;
import lucky.sky.net.rpc.simple.data.SimpleRequestMessage;
import lucky.sky.net.rpc.simple.data.SimpleResponseMessage;
import lucky.sky.net.rpc.simple.server.ChannelState;
import lucky.sky.net.rpc.simple.server.SimpleServer;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;


public class ServerHandler extends SimpleChannelInboundHandler<JRequestPayload> {

  private static final Logger logger = LoggerManager.getLogger(ServerHandler.class);
  private final SimpleServer server;
  private static final Serializer SERIALIZER = new KryoSerializer();

  public ServerHandler(SimpleServer server) {
    this.server = server;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, JRequestPayload jRequestPayload)
      throws Exception {
    ChannelState state = ctx.channel().attr(ChannelState.KEY).get();
    try {
      server.getThreadPool()
          .execute(new Task(server.getContainer(), ctx.channel(), jRequestPayload, state));
    } catch (RejectedExecutionException exception) {
      logger.error("biz thread pool is full(MaxThreads:{})", server.getOptions().getMaxThreads());
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    logger.error("happen exception unknown server error:{}" + cause.toString(), cause);
    ctx.close();

  }

  private static class Task implements Runnable {

    private final ServiceContainer container;
    private final Channel channel;
    private final JRequestPayload jRequestPayload;
    private final ChannelState state;

    public Task(ServiceContainer container, Channel channel, JRequestPayload jRequestPayload,
        ChannelState state) {
      this.container = container;
      this.channel = channel;
      this.jRequestPayload = jRequestPayload;
      this.state = state;
    }

    @Override
    public void run() {
      SimpleResponseMessage responseMessage = null;
      SimpleRequestMessage requestMessage = null;
      try {
        requestMessage = SERIALIZER.readObject(jRequestPayload.bytes(), SimpleRequestMessage.class);
        if (state != null) {
          state.setActiveTime(new Date());
          state.setId(requestMessage.getClientName());
          state.setService(requestMessage.getServiceName());
          state.setMethod(requestMessage.getMethodName());
        }
        RpcContext.set(requestMessage.getContextID(), requestMessage.getMessageID());

        MethodExecutor executor = container
            .getExecutor(requestMessage.getServiceName(), requestMessage.getMethodName());
        if (executor == null) {
          throw new RpcException(RpcError.SERVER_SERVICE_NOT_FOUND, requestMessage.getServiceName(),
              requestMessage.getMethodName());
        }

        Object[] args = buildArgs(executor.getParameterTypes(), requestMessage.getParameters());
        Object result = executor.invoke(args);
        responseMessage = SimpleResponseMessage.success(result, requestMessage.getMessageID());
      } catch (Exception e) {
        responseMessage = SimpleResponseMessage.failed(e, requestMessage.getMessageID());
      } finally {
        RpcContext.remove();
      }

      JResponsePayload jResponsePayload = new JResponsePayload();
      byte[] result = SERIALIZER.writeObject(responseMessage);
      jResponsePayload.bytes(result);
      channel.writeAndFlush(jResponsePayload);
    }

    private Object[] buildArgs(Class[] types, List<Object> values) {
      if (values == null) {
        return null;
      }
      return values.toArray();
    }


  }


}
