package lucky.sky.net.rpc.simple.server.service;

import lucky.sky.net.rpc.ServiceInfo;
import lucky.sky.net.rpc.annotation.RpcMethod;
import lucky.sky.net.rpc.annotation.RpcService;
import lucky.sky.util.lang.EnumValueSupport;
import sun.reflect.FieldInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * on 16/1/9.
 */
@RpcService(name = "$meta", description = "元数据服务")
public interface MetaService {

  @RpcMethod(description = "获取服务列表")
  ServiceList getServiceList();

  @RpcMethod(description = "获取服务信息")
  Service getService(String serviceName);

  @RpcMethod(description = "获取方法信息")
  Method getMethod(String serviceName, String methodName);

  @RpcMethod(description = "获取类型信息")
  Type getType(String typeID);

  class ServiceList {

    public List<Service> services;
  }

  class Service {

    public String name;

    public String description;

    public List<Method> methods;

    public Service() {
      // for decode
    }

    public Service(ServiceInfo s, boolean includeMethods) {
      this.name = s.getName();
      this.description = s.getDescription();
      if (includeMethods && s.getMethods() != null && !s.getMethods().isEmpty()) {
        this.methods = new ArrayList<>(s.getMethods().size());
        s.getMethods().forEach((n, m) -> this.methods.add(new Method(m)));
      }
    }
  }

  class Method {

    public String name;

    public String description;

    public List<Parameter> parameters;

    public Parameter returnType;

    public Method() {
      // for decode
    }

    public Method(ServiceInfo.MethodInfo m) {
      this.name = m.getName();
      this.description = m.getDescription();
      if (m.getParameters() != null && !m.getParameters().isEmpty()) {
        this.parameters = new ArrayList<>(m.getParameters().size());
        m.getParameters().forEach(p -> this.parameters.add(new Parameter(p)));
      }
      if (m.getReturnType() != null) {
        this.returnType = new Parameter(m.getReturnType());
      }
    }
  }

  class Parameter {

    public String name;

    public Type type;

    public String description;

    public Parameter() {
      // for decode
    }

    public Parameter(ServiceInfo.ParameterInfo p) {
      this.name = p.getName();
      this.type = new Type(p.getType(), false);
      this.description = p.getDescription();
    }
  }

  class Type {

    public String id;

    public String name;

    public int kind;

    public String description;

    public List<Field> fields;

    public List<Enum> enums;

    public Type() {
      // for decode
    }

    public Type(Class<?> clazz, boolean includeFields) {
//            this.id = clazz.getName();
//            this.name = clazz.getSimpleName();
//            this.kind = SimpleEncoder.getDataType(clazz);
//            if (this.kind == SimpleEncoder.DT_PROTOBUF) {
//                ProtoMessage annotation = clazz.getAnnotation(ProtoMessage.class);
//                if (annotation != null) {
//                    this.description = annotation.description();
//                }
//                if (includeFields) {
//                    this.fillFields(clazz);
//                }
//            }
    }

    private void fillFields(Class<?> clazz) {
//            if (clazz.isEnum()) {
//                Object[] constants = clazz.getEnumConstants();
//                this.enums = new ArrayList<>(constants.length);
//                for (int i = 0; i < constants.length; i++) {
//                    Enum e = new Enum(constants[i]);
//                    this.enums.add(e);
//                }
//            } else {
//                List<FieldInfo> infos = ProtobufProxyUtils.processDefaultValue(FieldUtils.findMatchedFields(clazz, ProtoField.class));
//                this.fields = new ArrayList<>(infos.size());
//                infos.forEach(f -> this.fields.add(new Field(f)));
//            }
    }
  }

  class Field {

    public String name;

    public Type type;

    public String description;

    public int order;

    public int modifier;

    public int kind;

    public Field() {
      // for decode
    }

    public Field(FieldInfo f) {
//            this.name = f.getField().getName();
//            Class<?> clazz = f.isList() ? f.getGenericKeyType() : f.getField().getType();
//            this.type = new Type(clazz, clazz != Type.class);   // 对 Type 类型做特殊处理, 防止死循环
//            this.description = f.getDescription();
//            this.order = f.getOrder();
//            this.kind = f.getType().getKind();
//            if (f.isList()) {
//                this.modifier = 3;
//            } else if (f.isRequired()) {
//                this.modifier = 2;
//            } else {
//                this.modifier = 1;
//            }
    }
  }

  class Enum {

    public String name;

    public int value;

    public Enum() {
      // for decode
    }

    public Enum(Object value) {
      java.lang.Enum e = (java.lang.Enum) value;
      this.name = e.name();
      this.value = ((EnumValueSupport) e).value();
    }
  }
}
