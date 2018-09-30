package lucky.sky.db.jsd;

import java.sql.*;

/**
 * on 15/5/29.
 */
public interface DataReader {

  boolean getBoolean(int index);

  boolean getBoolean(String name);

  short getShort(int index);

  short getShort(String name);

  int getInt(int index);

  int getInt(String name);

  long getLong(int index);

  long getLong(String name);

  float getFloat(int index);

  float getFloat(String name);

  double getDouble(int index);

  double getDouble(String name);

  String getString(int index);

  String getString(String name);

  Date getDate(int index);

  Date getDate(String name);

  Time getTime(int index);

  Time getTime(String name);

  Timestamp getTimestamp(int index);

  Timestamp getTimestamp(String name);

  Object getObject(int index);

  Object getObject(String name);

  static DataReader create(ResultSet rs) {
    return new ResultSetDataReader(rs);
  }

  class ResultSetDataReader implements DataReader {

    private ResultSet rs;

    private ResultSetDataReader(ResultSet rs) {
      this.rs = rs;
    }

    @Override
    public short getShort(int index) {
      try {
        return rs.getShort(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public short getShort(String name) {
      try {
        return rs.getShort(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public int getInt(int index) {
      try {
        return rs.getInt(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public int getInt(String name) {
      try {
        return rs.getInt(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public long getLong(int index) {
      try {
        return rs.getLong(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public long getLong(String name) {
      try {
        return rs.getLong(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public String getString(int index) {
      try {
        return rs.getString(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public String getString(String name) {
      try {
        return rs.getString(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Object getObject(int index) {
      try {
        return rs.getObject(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Object getObject(String name) {
      try {
        return rs.getObject(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public boolean getBoolean(int index) {
      try {
        return rs.getBoolean(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public boolean getBoolean(String name) {
      try {
        return rs.getBoolean(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public float getFloat(int index) {
      try {
        return rs.getFloat(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public float getFloat(String name) {
      try {
        return rs.getFloat(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public double getDouble(int index) {
      try {
        return rs.getDouble(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public double getDouble(String name) {
      try {
        return rs.getDouble(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Date getDate(int index) {
      try {
        return rs.getDate(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Date getDate(String name) {
      try {
        return rs.getDate(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Time getTime(int index) {
      try {
        return rs.getTime(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Time getTime(String name) {
      try {
        return rs.getTime(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Timestamp getTimestamp(int index) {
      try {
        return rs.getTimestamp(index);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

    @Override
    public Timestamp getTimestamp(String name) {
      try {
        return rs.getTimestamp(name);
      } catch (SQLException e) {
        throw new JsdException(e);
      }
    }

  }
}
