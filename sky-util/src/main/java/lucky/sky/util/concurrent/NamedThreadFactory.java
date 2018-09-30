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

package lucky.sky.util.concurrent;

import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;

import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;


public class NamedThreadFactory implements ThreadFactory {

  private static final Logger logger = LoggerManager.getLogger(NamedThreadFactory.class);

  private final AtomicInteger id = new AtomicInteger();
  private final String name;
  private final boolean daemon;
  private final int priority;
  private final ThreadGroup group;

  public NamedThreadFactory(String name) {
    this(name, false, Thread.NORM_PRIORITY);
  }

  public NamedThreadFactory(String name, boolean daemon) {
    this(name, daemon, Thread.NORM_PRIORITY);
  }

  public NamedThreadFactory(String name, int priority) {
    this(name, false, priority);
  }

  public NamedThreadFactory(String name, boolean daemon, int priority) {
    this.name = name + " #";
    this.daemon = daemon;
    this.priority = priority;
    SecurityManager s = System.getSecurityManager();
    group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
  }

  @Override
  public Thread newThread(Runnable r) {
    Objects.requireNonNull(r);

    String name2 = name + id.getAndIncrement();

    Runnable r2 = wrapRunnable(r);

    Thread t = wrapThread(group, r2, name2);

    try {
      if (t.isDaemon() != daemon) {
        t.setDaemon(daemon);
      }

      if (t.getPriority() != priority) {
        t.setPriority(priority);
      }
    } catch (Exception ignored) { /* doesn't matter even if failed to set. */ }

    logger.info("Creates new {}.", t);

    return t;
  }

  public ThreadGroup getThreadGroup() {
    return group;
  }

  protected Runnable wrapRunnable(Runnable r) {
    return r; // InternalThreadLocalRunnable.wrap(r)
  }

  protected Thread wrapThread(ThreadGroup group, Runnable r, String name) {
    return new Thread(group, r, name);
  }
}
