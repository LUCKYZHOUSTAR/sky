

package lucky.sky.util.lang;


import lucky.sky.util.log.Logger;
import lucky.sky.util.log.LoggerManager;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

import static lucky.sky.util.lang.StackTraceUtil.stackTrace;


public final class UnsafeUtil {


  private static final Logger logger = LoggerManager.getLogger(UnsafeUtil.class);

  private static final Unsafe unsafe;

  static {
    Unsafe _unsafe;
    try {
      Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
      unsafeField.setAccessible(true);
      _unsafe = (Unsafe) unsafeField.get(null);
    } catch (Throwable t) {
      if (logger.isWarnEnabled()) {
        logger.warn("sun.misc.Unsafe.theUnsafe: unavailable, {}.", stackTrace(t));
      }

      _unsafe = null;
    }

    unsafe = _unsafe;
  }

  private static final MemoryAccessor memoryAccessor = new MemoryAccessor(unsafe);

  private static final long BYTE_ARRAY_BASE_OFFSET = arrayBaseOffset(byte[].class);
  // Micro-optimization: we can assume a scale of 1 and skip the multiply
  // private static final long BYTE_ARRAY_INDEX_SCALE = 1;

  private static final long BOOLEAN_ARRAY_BASE_OFFSET = arrayBaseOffset(boolean[].class);
  private static final long BOOLEAN_ARRAY_INDEX_SCALE = arrayIndexScale(boolean[].class);

  private static final long INT_ARRAY_BASE_OFFSET = arrayBaseOffset(int[].class);
  private static final long INT_ARRAY_INDEX_SCALE = arrayIndexScale(int[].class);

  private static final long LONG_ARRAY_BASE_OFFSET = arrayBaseOffset(long[].class);
  private static final long LONG_ARRAY_INDEX_SCALE = arrayIndexScale(long[].class);

  private static final long FLOAT_ARRAY_BASE_OFFSET = arrayBaseOffset(float[].class);
  private static final long FLOAT_ARRAY_INDEX_SCALE = arrayIndexScale(float[].class);

  private static final long DOUBLE_ARRAY_BASE_OFFSET = arrayBaseOffset(double[].class);
  private static final long DOUBLE_ARRAY_INDEX_SCALE = arrayIndexScale(double[].class);

  private static final long OBJECT_ARRAY_BASE_OFFSET = arrayBaseOffset(Object[].class);
  private static final long OBJECT_ARRAY_INDEX_SCALE = arrayIndexScale(Object[].class);

  private static final long BUFFER_ADDRESS_OFFSET = objectFieldOffset(bufferAddressField());

  private static final long STRING_VALUE_OFFSET = objectFieldOffset(stringValueField());

  /**
   * Returns the {@link Unsafe}'s instance.
   */
  public static Unsafe getUnsafe() {
    return unsafe;
  }

  /**
   * Get a {@link MemoryAccessor} appropriate for the platform.
   */
  public static MemoryAccessor getMemoryAccessor() {
    return memoryAccessor;
  }

  public static byte getByte(Object target, long offset) {
    return memoryAccessor.getByte(target, offset);
  }

  public static void putByte(Object target, long offset, byte value) {
    memoryAccessor.putByte(target, offset, value);
  }

  public static int getInt(Object target, long offset) {
    return memoryAccessor.getInt(target, offset);
  }

  public static void putInt(Object target, long offset, int value) {
    memoryAccessor.putInt(target, offset, value);
  }

  public static long getLong(Object target, long offset) {
    return memoryAccessor.getLong(target, offset);
  }

  public static void putLong(Object target, long offset, long value) {
    memoryAccessor.putLong(target, offset, value);
  }

  public static boolean getBoolean(Object target, long offset) {
    return memoryAccessor.getBoolean(target, offset);
  }

  public static void putBoolean(Object target, long offset, boolean value) {
    memoryAccessor.putBoolean(target, offset, value);
  }

  public static float getFloat(Object target, long offset) {
    return memoryAccessor.getFloat(target, offset);
  }

  public static void putFloat(Object target, long offset, float value) {
    memoryAccessor.putFloat(target, offset, value);
  }

  public static double getDouble(Object target, long offset) {
    return memoryAccessor.getDouble(target, offset);
  }

  public static void putDouble(Object target, long offset, double value) {
    memoryAccessor.putDouble(target, offset, value);
  }

  public static Object getObject(Object target, long offset) {
    return memoryAccessor.getObject(target, offset);
  }

  public static void putObject(Object target, long offset, Object value) {
    memoryAccessor.putObject(target, offset, value);
  }

  public static byte getByte(byte[] target, long index) {
    return memoryAccessor.getByte(target, BYTE_ARRAY_BASE_OFFSET + index);
  }

  public static void putByte(byte[] target, long index, byte value) {
    memoryAccessor.putByte(target, BYTE_ARRAY_BASE_OFFSET + index, value);
  }

  public static int getInt(int[] target, long index) {
    return memoryAccessor.getInt(target, INT_ARRAY_BASE_OFFSET + (index * INT_ARRAY_INDEX_SCALE));
  }

  public static void putInt(int[] target, long index, int value) {
    memoryAccessor.putInt(target, INT_ARRAY_BASE_OFFSET + (index * INT_ARRAY_INDEX_SCALE), value);
  }

  public static long getLong(long[] target, long index) {
    return memoryAccessor.getLong(
        target, LONG_ARRAY_BASE_OFFSET + (index * LONG_ARRAY_INDEX_SCALE));
  }

  public static void putLong(long[] target, long index, long value) {
    memoryAccessor.putLong(
        target, LONG_ARRAY_BASE_OFFSET + (index * LONG_ARRAY_INDEX_SCALE), value);
  }

  public static boolean getBoolean(boolean[] target, long index) {
    return memoryAccessor.getBoolean(
        target, BOOLEAN_ARRAY_BASE_OFFSET + (index * BOOLEAN_ARRAY_INDEX_SCALE));
  }

  public static void putBoolean(boolean[] target, long index, boolean value) {
    memoryAccessor.putBoolean(
        target, BOOLEAN_ARRAY_BASE_OFFSET + (index * BOOLEAN_ARRAY_INDEX_SCALE), value);
  }

  public static float getFloat(float[] target, long index) {
    return memoryAccessor.getFloat(
        target, FLOAT_ARRAY_BASE_OFFSET + (index * FLOAT_ARRAY_INDEX_SCALE));
  }

  public static void putFloat(float[] target, long index, float value) {
    memoryAccessor.putFloat(
        target, FLOAT_ARRAY_BASE_OFFSET + (index * FLOAT_ARRAY_INDEX_SCALE), value);
  }

  public static double getDouble(double[] target, long index) {
    return memoryAccessor.getDouble(
        target, DOUBLE_ARRAY_BASE_OFFSET + (index * DOUBLE_ARRAY_INDEX_SCALE));
  }

  public static void putDouble(double[] target, long index, double value) {
    memoryAccessor.putDouble(
        target, DOUBLE_ARRAY_BASE_OFFSET + (index * DOUBLE_ARRAY_INDEX_SCALE), value);
  }

  public static Object getObject(Object[] target, long index) {
    return memoryAccessor.getObject(
        target, OBJECT_ARRAY_BASE_OFFSET + (index * OBJECT_ARRAY_INDEX_SCALE));
  }

  public static void putObject(Object[] target, long index, Object value) {
    memoryAccessor.putObject(
        target, OBJECT_ARRAY_BASE_OFFSET + (index * OBJECT_ARRAY_INDEX_SCALE), value);
  }

  public static byte getByte(long address) {
    return memoryAccessor.getByte(address);
  }

  public static void putByte(long address, byte value) {
    memoryAccessor.putByte(address, value);
  }

  public static int getInt(long address) {
    return memoryAccessor.getInt(address);
  }

  public static void putInt(long address, int value) {
    memoryAccessor.putInt(address, value);
  }

  public static long getLong(long address) {
    return memoryAccessor.getLong(address);
  }

  public static void putLong(long address, long value) {
    memoryAccessor.putLong(address, value);
  }

  /**
   * Reports the offset of the first element in the storage allocation of a given array class.
   */
  public static int arrayBaseOffset(Class<?> clazz) {
    return unsafe != null ? unsafe.arrayBaseOffset(clazz) : -1;
  }

  /**
   * Reports the scale factor for addressing elements in the storage allocation of a given array
   * class.
   */
  public static int arrayIndexScale(Class<?> clazz) {
    return unsafe != null ? unsafe.arrayIndexScale(clazz) : -1;
  }

  /**
   * Returns the offset of the provided field, or {@code -1} if {@code sun.misc.Unsafe} is not
   * available.
   */
  public static long objectFieldOffset(Field field) {
    return field == null || unsafe == null ? -1 : unsafe.objectFieldOffset(field);
  }

  /**
   * Gets the offset of the {@code address} field of the given direct {@link ByteBuffer}.
   */
  public static long addressOffset(ByteBuffer buffer) {
    return unsafe.getLong(buffer, BUFFER_ADDRESS_OFFSET);
  }

  /**
   * Returns a new {@link String} backed by the given {@code chars}. The char array should not be
   * mutated any more after calling this function.
   */
  public static String moveToString(char[] chars) {
    if (STRING_VALUE_OFFSET == -1) {
      // In the off-chance that this JDK does not implement String as we'd expect, just do a copy.
      return new String(chars);
    }
    final String str;
    try {
      str = (String) unsafe.allocateInstance(String.class);
    } catch (InstantiationException e) {
      // This should never happen, but return a copy as a fallback just in case.
      return new String(chars);
    }
    unsafe.putObject(str, STRING_VALUE_OFFSET, chars);
    return str;
  }

  /**
   * Returns the system {@link ClassLoader}.
   */
  public static ClassLoader getSystemClassLoader() {
    if (System.getSecurityManager() == null) {
      return ClassLoader.getSystemClassLoader();
    } else {
      return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {

        @Override
        public ClassLoader run() {
          return ClassLoader.getSystemClassLoader();
        }
      });
    }
  }

  /**
   * Finds the address field within a direct {@link Buffer}.
   */
  private static Field bufferAddressField() {
    return field(Buffer.class, "address", long.class);
  }

  /**
   * Finds the value field within a {@link String}.
   */
  private static Field stringValueField() {
    return field(String.class, "value", char[].class);
  }

  /**
   * Gets the field with the given name within the class, or {@code null} if not found. If found,
   * the field is made accessible.
   */
  private static Field field(Class<?> clazz, String fieldName, Class<?> expectedType) {
    Field field;
    try {
      field = clazz.getDeclaredField(fieldName);
      field.setAccessible(true);
      if (!field.getType().equals(expectedType)) {
        return null;
      }
    } catch (Throwable t) {
      // Failed to access the fields.
      field = null;
    }
    return field;
  }

  public static class MemoryAccessor {

    Unsafe unsafe;

    MemoryAccessor(Unsafe unsafe) {
      this.unsafe = unsafe;
    }

    public byte getByte(Object target, long offset) {
      return unsafe.getByte(target, offset);
    }

    public void putByte(Object target, long offset, byte value) {
      unsafe.putByte(target, offset, value);
    }

    public short getShort(Object target, long offset) {
      return unsafe.getShort(target, offset);
    }

    public void putShort(Object target, long offset, short value) {
      unsafe.putShort(target, offset, value);
    }

    public int getInt(Object target, long offset) {
      return unsafe.getInt(target, offset);
    }

    public void putInt(Object target, long offset, int value) {
      unsafe.putInt(target, offset, value);
    }

    public long getLong(Object target, long offset) {
      return unsafe.getLong(target, offset);
    }

    public void putLong(Object target, long offset, long value) {
      unsafe.putLong(target, offset, value);
    }

    public boolean getBoolean(Object target, long offset) {
      return unsafe.getBoolean(target, offset);
    }

    public void putBoolean(Object target, long offset, boolean value) {
      unsafe.putBoolean(target, offset, value);
    }

    public float getFloat(Object target, long offset) {
      return unsafe.getFloat(target, offset);
    }

    public void putFloat(Object target, long offset, float value) {
      unsafe.putFloat(target, offset, value);
    }

    public double getDouble(Object target, long offset) {
      return unsafe.getDouble(target, offset);
    }

    public void putDouble(Object target, long offset, double value) {
      unsafe.putDouble(target, offset, value);
    }

    public Object getObject(Object target, long offset) {
      return unsafe.getObject(target, offset);
    }

    public void putObject(Object target, long offset, Object value) {
      unsafe.putObject(target, offset, value);
    }

    public byte getByte(long address) {
      return unsafe.getByte(address);
    }

    public void putByte(long address, byte value) {
      unsafe.putByte(address, value);
    }

    public short getShort(long address) {
      return unsafe.getShort(address);
    }

    public void putShort(long address, short value) {
      unsafe.putShort(address, value);
    }

    public int getInt(long address) {
      return unsafe.getInt(address);
    }

    public void putInt(long address, int value) {
      unsafe.putInt(address, value);
    }

    public long getLong(long address) {
      return unsafe.getLong(address);
    }

    public void putLong(long address, long value) {
      unsafe.putLong(address, value);
    }

    public void copyMemory(Object srcBase, long srcOffset, Object dstBase, long dstOffset,
        long bytes) {
      unsafe.copyMemory(srcBase, srcOffset, dstBase, dstOffset, bytes);
    }

    public void copyMemory(long srcAddress, long dstAddress, long bytes) {
      unsafe.copyMemory(srcAddress, dstAddress, bytes);
    }
  }
}
