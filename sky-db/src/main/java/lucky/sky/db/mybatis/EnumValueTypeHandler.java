package lucky.sky.db.mybatis;

import lucky.sky.util.lang.EnumValueSupport;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 枚举类型值处理器。
 */
public class EnumValueTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

  private Class<E> type;
  private Map<Integer, E> map = new HashMap<>();

  public EnumValueTypeHandler(Class<E> type) {
    if (type == null) {
      throw new IllegalArgumentException("Type argument cannot be null");
    }
    this.type = type;
    E[] enums = type.getEnumConstants();
    if (enums == null) {
      throw new IllegalArgumentException(
          type.getSimpleName() + " does not represent an enum type.");
    }
    for (E e : enums) {
      EnumValueSupport valuedEnum = (EnumValueSupport) e;
      map.put(valuedEnum.value(), e);
    }
  }

  @Override
  public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType)
      throws SQLException {
    EnumValueSupport valuedEnum = (EnumValueSupport) parameter;
    ps.setInt(i, valuedEnum.value());
  }

  @Override
  public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
    int i = rs.getInt(columnName);
    if (rs.wasNull()) {
      return null;
    } else {
      return enumOf(i);
    }
  }

  @Override
  public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
    int i = rs.getInt(columnIndex);
    if (rs.wasNull()) {
      return null;
    } else {
      return enumOf(i);
    }
  }

  @Override
  public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
    int i = cs.getInt(columnIndex);
    if (cs.wasNull()) {
      return null;
    } else {
      return enumOf(i);
    }
  }

  private E enumOf(int value) {
    try {
      return map.get(value);
    } catch (Exception ex) {
      throw new IllegalArgumentException(
          "Cannot convert " + value + " to " + type.getSimpleName() + " by value.", ex);
    }
  }
}
