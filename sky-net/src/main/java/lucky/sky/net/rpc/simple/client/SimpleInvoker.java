package lucky.sky.net.rpc.simple.client;

import static lucky.sky.net.rpc.RpcExceptions.putProfile;
import static lucky.sky.net.rpc.RpcExceptions.putRemoteError;
import static lucky.sky.net.rpc.RpcExceptions.putRemoteServer;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lucky.sky.net.rpc.Invoker;
import lucky.sky.net.rpc.RpcContext;
import lucky.sky.net.rpc.RpcError;
import lucky.sky.net.rpc.RpcException;
import lucky.sky.net.rpc.serialization.Serializer;
import lucky.sky.net.rpc.serialization.kryo.KryoSerializer;
import lucky.sky.net.rpc.simple.client.future.DefaultInvokeFuture;
import lucky.sky.net.rpc.simple.data.JRequestPayload;
import lucky.sky.net.rpc.simple.data.SimpleRequestMessage;
import lucky.sky.net.rpc.simple.data.SimpleResponseMessage;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.data.Guid;
import lucky.sky.util.lang.FaultException;
import lucky.sky.util.lang.StrKit;


public class SimpleInvoker implements Invoker {

  private static final Serializer SERIALIZER = new KryoSerializer();
  private static String clientName;
  private SimpleClientOptions options;
  private ChannelPool pool;

  private static final AtomicLong index = new AtomicLong();


  static {
    clientName = AppConfig.getDefault().getAppName();
    if (clientName == null) {
      clientName = "";
    }
  }

  SimpleInvoker(SimpleClientOptions options, ChannelPool pool) {
    this.options = options;
    this.pool = pool;
  }

  @Override
  public DefaultInvokeFuture invoke(String service, String method, Object[] args,
      Class<?> returnType) {
    SimpleRequestMessage requestMessage;
    JRequestPayload jRequestPayload;
    try {
      requestMessage = createRequestMessage(service, method, args);
      jRequestPayload = new JRequestPayload();
      //在业务线程中进行序列化操作
      byte[] bytes = SERIALIZER.writeObject(requestMessage);
      jRequestPayload.bytes(bytes);
    } catch (Exception e) {
      throw fault(RpcError.CLIENT_UNKNOWN_ERROR, e);
    }
    return invoke(jRequestPayload, requestMessage);

  }

  public DefaultInvokeFuture invoke(final JRequestPayload jRequestPayload,
      final SimpleRequestMessage simpleRequestMessage) {
    SimpleClientChannel channel = null;
    //发送请求操作
    final DefaultInvokeFuture invokeFuture = DefaultInvokeFuture
        .with(simpleRequestMessage.getMessageID(), options.getReadTimeout());
    try {
      // 获取连接
      Future<Channel> future = pool.acquire().awaitUninterruptibly();
      if (!future.isSuccess()) {
        throw fault(RpcError.CLIENT_ACQUIRE_FAILED, future.cause());
      }
      channel = (SimpleClientChannel) future.getNow();

      channel.writeAndFlush(jRequestPayload).awaitUninterruptibly()
          .addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
              if (future.isSuccess()) {
                // 标记已发送
                invokeFuture.markSent();
              } else {
                SimpleResponseMessage responseMessage = SimpleResponseMessage
                    .failed(RpcError.CLIENT_ERROR, simpleRequestMessage.getMessageID());
                DefaultInvokeFuture.fakeReceived(responseMessage);
              }
            }
          });
    } finally {
      if (channel != null) {
        pool.release(channel);
      }
    }
    return invokeFuture;

  }

  private SimpleRequestMessage createRequestMessage(String service, String method, Object[] args) {
    SimpleRequestMessage requestMessage = new SimpleRequestMessage();
    requestMessage.setClientName(clientName);
    requestMessage.setServiceName(service);
    requestMessage.setMethodName(method);
    requestMessage.setContextID(RpcContext.getContextID());
    requestMessage.setMessageID(Guid.get());
    if (args != null && args.length > 0) {
      List<Object> parameters = new ArrayList<>(args.length);
      for (Object arg : args) {
        parameters.add(arg);
      }
      requestMessage.setParameters(parameters);
    }
    return requestMessage;
  }

  private FaultException fault(SimpleResponseMessage m) {
    FaultException fe = new FaultException(m.getErrorCode(), m.getErrorInfo());
    if (!StrKit.isBlank(m.getErrorDetail())) {
      putRemoteError(fe, m.getErrorDetail());
    }
    putRemoteServer(fe, options);
    putProfile(fe);
    return fe;
  }

  private FaultException fault(RpcError error, Throwable e) {
    int code;
    String msg;
    if (e instanceof RpcException) {
      RpcException re = (RpcException) e;
      code = re.getErrorCode();
      msg = re.getMessage();
    } else {
      code = error.value();
      msg = String.format(error.description(), e.getMessage());
    }
    FaultException fe = new FaultException(code, msg, e);
    putRemoteServer(fe, options);
    putProfile(fe);
    return fe;
  }


}
