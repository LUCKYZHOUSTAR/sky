package lucky.sky.net.rpc.simple.server.service;

import io.netty.channel.group.DefaultChannelGroup;
import lucky.sky.net.rpc.simple.server.ChannelState;
import lucky.sky.net.rpc.simple.server.SimpleServer;
import lucky.sky.util.build.BuildUtil;
import lucky.sky.util.config.AppConfig;
import lucky.sky.util.config.ConfigProperties;

import java.util.ArrayList;

/**
 *
 */
public class SystemServiceImp implements SystemService {

  private SimpleServer server;

  public SystemServiceImp(SimpleServer server) {
    this.server = server;
  }

  @Override
  public boolean ping() {
    return true;
  }

  @Override
  public GetInfoResponse getInfo() {
    GetInfoResponse response = new GetInfoResponse();
    response.name = AppConfig.getDefault().getAppName();
    response.startTime = server.getStartTime().getTime();
    response.buildInfo = BuildUtil.getBuildInfo();
    response.version =
        server.getOptions().getVersion() == null ? "" : server.getOptions().getVersion();
    response.clients = server.getSessionHandler().getChannels().size();
    response.maxClients = server.getOptions().getMaxClients();
    response.profiles = ConfigProperties.activeProfiles();
    return response;
  }

  @Override
  public GetClientListResponse getClientList() {
    GetClientListResponse response = new GetClientListResponse();
    DefaultChannelGroup channels = server.getSessionHandler().getChannels();
    response.clients = new ArrayList<>(channels.size());
    channels.forEach(channel -> {
      GetClientListResponse.ClientInfo client = new GetClientListResponse.ClientInfo();
      ChannelState state = channel.attr(ChannelState.KEY).get();
      if (state == null) {
        client.id = "";
        client.createTime = System.currentTimeMillis();
      } else {
        client.id = state.getId();
        client.createTime = state.getCreateTime().getTime();
        client.activeTime = state.getActiveTime().getTime();
        client.service = state.getService();
        client.method = state.getMethod();
      }
      client.address = channel.remoteAddress().toString().substring(1);

      response.clients.add(client);
    });
    return response;
  }
}
