/*
 * Copyright (c) 2015 The Jupiter Project
 *
 * Licensed under the Apache License, version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lucky.sky.net.rpc.simple.client.task;

import lucky.sky.net.rpc.RpcError;
import lucky.sky.net.rpc.serialization.Serializer;
import lucky.sky.net.rpc.serialization.kryo.KryoSerializer;
import lucky.sky.net.rpc.simple.client.future.DefaultInvokeFuture;
import lucky.sky.net.rpc.simple.data.JResponsePayload;
import lucky.sky.net.rpc.simple.data.SimpleResponseMessage;
import lucky.sky.util.lang.FaultException;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;


public class MessageTask implements Runnable {

  public static final Serializer serializer = new KryoSerializer();
  private static final Logger logger = LoggerManager.getLogger(MessageTask.class);

  private final JResponsePayload response;

  public MessageTask(JResponsePayload response) {
    this.response = response;
  }

  @Override
  public void run() {
    // stack copy
    final JResponsePayload _response = response;
    final byte[] response = _response.bytes();
    SimpleResponseMessage result;
    try {
      result = serializer.readObject(response, SimpleResponseMessage.class);
      _response.clear();
    } catch (Throwable t) {
      logger.error("Deserialize object failed:  {}.", t);
      result = SimpleResponseMessage
          .failed(new FaultException(RpcError.DESERIALIZATION_FAIL.value(), "反序列化出错"),
              _response.getInvokeId());
    }
    DefaultInvokeFuture.received(result);
  }
}
