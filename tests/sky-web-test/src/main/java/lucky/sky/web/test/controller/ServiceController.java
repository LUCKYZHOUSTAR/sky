package lucky.sky.web.test.controller;

import com.google.common.collect.Lists;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import lucky.sky.net.rpc.registry.Provider;
import lucky.sky.net.rpc.serialization.Serializer;
import lucky.sky.net.rpc.serialization.fastjson.FastJsonSerializer;
import lucky.sky.net.rpc.simple.data.SimpleRequestMessage;
import lucky.sky.net.rpc.simple.data.SimpleResponseMessage;
import lucky.sky.net.rpc.simple.server.service.MetaService;
import lucky.sky.net.rpc.simple.server.service.SystemService;
import lucky.sky.util.encode.JsonEncoder;
import lucky.sky.util.etcd.ZkManager;
import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import lucky.sky.util.net.DefaultSocketClient;
import lucky.sky.web.result.AjaxResult;
import lucky.sky.web.test.model.ServiceInfo;
import org.apache.zookeeper.CreateMode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author:chaoqiang.zhou
 * @Description:
 * @Date:Create in 10:51 2018/3/15
 */
@Controller
@RequestMapping("service")
public class ServiceController {


  private static Logger logger = LoggerManager.getLogger(ServiceController.class);
  private static final Serializer SERIALIZER = new FastJsonSerializer();

  public void fly() {
    System.out.println();
    logger.info("sdf");

  }


  @RequestMapping("view")
  public ModelAndView list() {
    return new ModelAndView("rpc/list");

  }


  @RequestMapping("rpc")
  public ModelAndView rpc(String serviceName) {
    ModelAndView view = new ModelAndView("rpc/rpc");
    view.addObject("servicename", serviceName);
    return view;
  }

  @RequestMapping("testlist")
  @ResponseBody
  public List<ServiceInfo> serviceInfolist(String serviceName) {
    List<ServiceInfo> serviceInfos = new ArrayList<>();
    ServiceInfo serviceInfo = new ServiceInfo();
    serviceInfo.setName("test");
    serviceInfo.setNodes(3);
    serviceInfo.setPort(9090);
    serviceInfo.setType("simple");
    serviceInfo.setDeveloper("asfd");
    serviceInfo.setMark("sdf");
    serviceInfos.add(serviceInfo);
    return serviceInfos;
  }

  @RequestMapping("list")
  @ResponseBody
  public List<ServiceInfo> serviceList() {
    List<ServiceInfo> serviceInfos = new ArrayList<>();
    //获取到服务节点的信息,/service
    List<String> serviceList = ZkManager.getChildNodes(getRootPath());
    serviceList.forEach(service -> {
      ServiceInfo serviceInfo = new ServiceInfo();
      serviceInfo.setName(service);
      List<String> nodes = ZkManager.getChildNodes(getProvidersPath(service));
      serviceInfo.setNodes(nodes.size());

      if (nodes != null && nodes.size() > 0) {
        String providerJson = ZkManager.getNode(getServerPath(service, nodes.get(0)));
        Provider provider = JsonEncoder.DEFAULT.decode(providerJson, Provider.class);
        serviceInfo.setMark(provider.getNote());
        String[] parts = provider.getAddress().split(":");
        serviceInfo.setPort(Integer.valueOf(parts[1]));
        serviceInfo.setType("service");
      }
      serviceInfos.add(serviceInfo);
    });
    return serviceInfos;
  }


  @ResponseBody
  @RequestMapping(value = "testservices", method = RequestMethod.GET)
  public List<Provider> testgetServiceInfo(String serviceName) {

    List<Provider> providers = new ArrayList<>();
    Provider provider = new Provider();
    provider.setAddress("192.168.23.11:4567");
    provider.setClients(5);
    provider.setName("hotle.order.service");
    provider.setType("simple");
    provider.setNote("订单服务");
    providers.add(provider);
    return providers;
  }


  @ResponseBody
  @RequestMapping(value = "onlinetestservices", method = RequestMethod.GET)
  public List<Provider> onlinetestgetServiceInfo(String serviceName) {

    List<Provider> providers = new ArrayList<>();
    Provider provider = new Provider();
    provider.setAddress("192.168.23.11:4567");
    provider.setClients(5);
    provider.setName("hotle.order.service");
    provider.setType("simple");
    provider.setNote("订单服务");
    providers.add(provider);
    return providers;
  }

  @ResponseBody
  @RequestMapping(value = "services", method = RequestMethod.GET)
  public List<Provider> getServiceInfo(String servicename) {

    List<Provider> providers = new ArrayList<>();
    List<String> serviceList = ZkManager.getChildNodes(getProvidersPath(servicename));
    serviceList.forEach(address -> {
      String providerJson = ZkManager.getNode(getServerPath(servicename, address));
      Provider provider = JsonEncoder.DEFAULT.decode(providerJson, Provider.class);
      providers.add(provider);
    });
    return providers;

  }


  /**
   * 服务下线
   */
  @ResponseBody
  @RequestMapping(value = "offline", method = RequestMethod.GET)
  public AjaxResult offLine(String serviceName, String address) {
    AjaxResult result = new AjaxResult();
    result.setSuccess(true);
    return result;
//    try {
//      String providerJson = ZkManager.getNode(getServerPath(serviceName, address));
//      ZkManager.deleteNode(getServerPath(serviceName, address));
//      if (ZkManager.isPathExists(getOfflineNodePath(serviceName, address))) {
//        ZkManager.setNode(getOfflineNodePath(serviceName, address), providerJson);
//        return result;
//      }
//      ZkManager.createPath(getOfflineNodePath(serviceName, address), CreateMode.EPHEMERAL,
//          providerJson.getBytes(Charset.forName("utf-8")));
//    } catch (Exception e) {
//      logger.error("offline servicename:{},address:{},failed,error{}", serviceName, address, e);
//      result.setSuccess(false);
//    }

//    return result;
  }


  /**
   * 服务上线
   */
  @ResponseBody
  @RequestMapping(value = "online", method = RequestMethod.GET)
  public AjaxResult onLine(String serviceName, String address) {
    AjaxResult result = new AjaxResult();
    result.setSuccess(true);
    try {
      String providerJson = ZkManager.getNode(getOfflineNodePath(serviceName, address));
      ZkManager.deleteNode(getOfflineNodePath(serviceName, address));
      if (ZkManager.isPathExists(getServerPath(serviceName, address))) {
        ZkManager.setNode(getServerPath(serviceName, address), providerJson);
        return result;
      }
      ZkManager.createPath(getServerPath(serviceName, address), CreateMode.EPHEMERAL,
          providerJson.getBytes(Charset.forName("utf-8")));
    } catch (Exception e) {
      logger.error("online servicename:{},address:{},failed,error{}", serviceName, address, e);
      result.setSuccess(false);
    }

    return result;
  }


  /**
   * 服务的状态信息
   */
  @ResponseBody
  @RequestMapping(value = "status", method = RequestMethod.GET)
  public SystemService.GetInfoResponse status(String address) {

    SimpleRequestMessage requestMessage = new SimpleRequestMessage();
    requestMessage.setServiceName("$system");
    requestMessage.setMethodName("GetInfo");
    String[] ips = address.split(":");
    byte[] result = DefaultSocketClient
        .sendReceive(ips[0], Integer.valueOf(ips[1]), SERIALIZER.writeObject(requestMessage));
    SimpleResponseMessage responseMessage = SERIALIZER
        .readObject(result, SimpleResponseMessage.class);
    return (SystemService.GetInfoResponse) responseMessage.getResult();
  }


  @ResponseBody
  @RequestMapping(value = "clients", method = RequestMethod.GET)
  public SystemService.GetClientListResponse getClientList(String address) {
    SimpleRequestMessage requestMessage = new SimpleRequestMessage();
    requestMessage.setServiceName("$system");
    requestMessage.setMethodName("GetClientList");
    String[] ips = address.split(":");
    byte[] result = DefaultSocketClient
        .sendReceive(ips[0], Integer.valueOf(ips[1]), SERIALIZER.writeObject(requestMessage));
    SimpleResponseMessage responseMessage = SERIALIZER
        .readObject(result, SimpleResponseMessage.class);
    return (SystemService.GetClientListResponse) responseMessage.getResult();
  }


  /**
   * 获取该元服务下面的服务列表
   */
  @ResponseBody
  @RequestMapping(value = "serviceList", method = RequestMethod.GET)
  public MetaService.ServiceList getServiceList(String address) {
    SimpleRequestMessage requestMessage = new SimpleRequestMessage();
    requestMessage.setServiceName("$meta");
    requestMessage.setMethodName("GetServiceList");
    String[] ips = address.split(":");
    byte[] result = DefaultSocketClient
        .sendReceive(ips[0], Integer.valueOf(ips[1]), SERIALIZER.writeObject(requestMessage));
    SimpleResponseMessage responseMessage = SERIALIZER
        .readObject(result, SimpleResponseMessage.class);
    return (MetaService.ServiceList) responseMessage.getResult();
  }


  @ResponseBody
  @RequestMapping(value = "getService", method = RequestMethod.GET)
  public MetaService.Service getService(String address, String serviceName) {
    SimpleRequestMessage requestMessage = new SimpleRequestMessage();
    requestMessage.setServiceName("$meta");
    requestMessage.setMethodName("GetService");
    List<Object> parameters = new ArrayList<>();
    parameters.add(serviceName);
    requestMessage.setParameters(parameters);
    String[] ips = address.split(":");
    byte[] result = DefaultSocketClient
        .sendReceive(ips[0], Integer.valueOf(ips[1]), SERIALIZER.writeObject(requestMessage));
    SimpleResponseMessage responseMessage = SERIALIZER
        .readObject(result, SimpleResponseMessage.class);
    return (MetaService.Service) responseMessage.getResult();
  }


  @ResponseBody
  @RequestMapping(value = "getMethod", method = RequestMethod.GET)
  public MetaService.Method getMethod(String address, String serviceName, String methodName) {
    SimpleRequestMessage requestMessage = new SimpleRequestMessage();
    requestMessage.setServiceName("$meta");
    requestMessage.setMethodName("GetMethod");
    List<Object> parameters = new ArrayList<>();
    parameters.add(serviceName);
    parameters.add(methodName);
    requestMessage.setParameters(parameters);
    String[] ips = address.split(":");
    byte[] result = DefaultSocketClient
        .sendReceive(ips[0], Integer.valueOf(ips[1]), SERIALIZER.writeObject(requestMessage));
    SimpleResponseMessage responseMessage = SERIALIZER
        .readObject(result, SimpleResponseMessage.class);
    return (MetaService.Method) responseMessage.getResult();
  }

  @ResponseBody
  @RequestMapping(value = "invoke", method = RequestMethod.GET)
  public Object invoke(String address, String serviceName, String methodName, Object[] parameter) {
    SimpleRequestMessage requestMessage = new SimpleRequestMessage();
    requestMessage.setServiceName(serviceName);
    requestMessage.setMethodName(methodName);
    requestMessage.setParameters(Lists.asList(Object.class, parameter));
//        requestMessage.setParameters(parameter);
    String[] ips = address.split(":");
    byte[] result = DefaultSocketClient
        .sendReceive(ips[0], Integer.valueOf(ips[1]), SERIALIZER.writeObject(requestMessage));
    SimpleResponseMessage responseMessage = SERIALIZER
        .readObject(result, SimpleResponseMessage.class);
    return responseMessage.getResult();
  }

  private String getRootPath() {
    return String.format("/service");
  }

  private String getServerPath(String name, String server) {
    return String.format("/service/%s/providers/%s", name, server);

  }


  private String getProvidersPath(String name) {
    return String.format("/service/%s/providers", name);

  }

  // 下线节点路径
  private static String getOfflineNodePath(String name, String address) {
    return String.format("/service/%s/offlines/%s", name, address);
  }


}
