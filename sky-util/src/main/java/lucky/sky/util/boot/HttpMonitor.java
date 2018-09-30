package lucky.sky.util.boot;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import lucky.sky.util.convert.DateConverter;
import lucky.sky.util.mx.MXUtil;
import lucky.sky.util.lang.UncheckedException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


public class HttpMonitor {

  private static final int MAX_CLIENTS = 20;
  private InetSocketAddress address;
  private Map<String, Function<HttpExchange, String>> handlers = new HashMap<>();
  private Handler handler = new Handler(handlers);
  private LocalDateTime startTime;

  public HttpMonitor(int port, LocalDateTime startTime) {
    this.address = new InetSocketAddress(port);
    this.startTime = startTime;
    // register default handler
    this.registerHandler("/", e -> String.format("start time: %s, process: %s",
        DateConverter.toString(this.startTime),
        MXUtil.getPID()));
  }

  public void registerHandler(String url, Function<HttpExchange, String> handler) {
    handlers.put(url, handler);
  }

  public void start() {
    try {
      HttpServerProvider provider = HttpServerProvider.provider();
      HttpServer server = provider.createHttpServer(this.address, MAX_CLIENTS);
      handlers.forEach((u, h) -> server.createContext(u, this.handler));
      server.setExecutor(null);
      server.start();
    } catch (Exception e) {
      throw new UncheckedException(e);
    }
  }

  static class Handler implements HttpHandler {

    private static final String NOT_FOUND = "404 NOT FOUND";
    private Map<String, Function<HttpExchange, String>> handlers;

    public Handler(Map<String, Function<HttpExchange, String>> handlers) {
      this.handlers = handlers;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
      int code;
      String content;

      String url = exchange.getRequestURI().getPath().toLowerCase();
      Function<HttpExchange, String> handler = handlers.get(url);
      if (handler == null) {
        code = HttpURLConnection.HTTP_NOT_FOUND;
        content = NOT_FOUND;
      } else {
        code = HttpURLConnection.HTTP_OK;
        content = handler.apply(exchange);
      }

      byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
      exchange.sendResponseHeaders(code, bytes.length);
      OutputStream out = exchange.getResponseBody();
      out.write(bytes);
      out.flush();
      exchange.close();
    }
  }

}
