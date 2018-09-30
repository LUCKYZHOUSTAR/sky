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

package lucky.sky.net.rpc.serialization;


public abstract class Serializer {

  public static final int MAX_CACHED_BUF_SIZE = 256 * 1024;
  public static final int DEFAULT_BUF_SIZE = 512;


  public abstract <T> byte[] writeObject(T obj);


  public abstract <T> T readObject(byte[] body, Class<T> clazz);


}
