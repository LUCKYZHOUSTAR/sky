package lucky.sky.util.http;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Iterator;

public class SystemConfiguration {

	private static Logger log = Logger.getLogger(SystemConfiguration.class);
	
	private static final String Configuration_Directory = Thread.currentThread().getContextClassLoader().getResource("etc/").getPath();

	private static SystemConfiguration systemConfiguration = new SystemConfiguration();
	private static CompositeConfiguration compositeConfiguration = new CompositeConfiguration();
	private static boolean isRead = false;

	private static String[] files = new File(Configuration_Directory).list();
	
	private SystemConfiguration() {}

	public static SystemConfiguration getInstance() {
		if (!isRead) {
			reload();
			isRead = true;
		}
		return systemConfiguration;
	}

	public static void reload() {
		
		if (ArrayUtils.isEmpty(files)) {
			log.warn("没有配置文件，无法读取！");
			return;
		}

		compositeConfiguration.clear();
		Configuration conf = null;
		int index = 0;
		int count = 0;

		for (String file : files) {
			if (!StringUtils.isBlank(file)) {
				log.debug("开始读取系统配置文件：" + file);

				try {
					if (FilenameUtils.isExtension(file, "xml")) {
						continue;
					} else if (FilenameUtils.isExtension(file, "properties")) {
						conf = new PropertiesConfiguration(Configuration_Directory + file);
					} else {
						continue;
					}
					
					compositeConfiguration.addConfiguration(conf);
					index = 0;
					Iterator<String> it = conf.getKeys();
					while (it.hasNext()) {
						index++;
						count++;
						String key = it.next();
						log.debug("配置文件信息：[key = " + key + ", value = " + conf.getString(key) + "]");
					}
					log.debug("[" + file + "]配置文件完毕，SystemConfig总共加载了" + index + "个配置信息");
				} catch (ConfigurationException e) {
					log.error("SystemConfiguration读取系统配置出现错误！", e);
				}
			}
		}
		
		log.info("所有配置文件完毕，systemConfiguration总共加载了 " + count +" 个配置信息");
	}
	
	/** 
	 * 读取key，返回boolean结果，如果没有找到对应键值，则返回false 
	 *
	 * @param key
	 * @return boolean
	 */
	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}
	
	/** 
	 * 读取key，返回boolean结果，如果没有找到对应键值，则返回默认值  
	 *
	 * @param key
	 * @param defaultValue
	 * @return boolean 
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		return compositeConfiguration.getBoolean(key, defaultValue);
	}
	
	/** 
	 * 读取key，返回字符串结果，如果没有找到对应键值，则返回null  
	 *
	 * @param key
	 * @return String
	 */
	public String getString(String key) {
		return getString(key, null);
	}
	
	/** 
	 * 读取key，返回字符串结果，如果没有找到对应键值，则返回默认值 
	 *
	 * @param key
	 * @param defaultValue
	 * @return String
	 */
	public String getString(String key, String defaultValue) {
		return compositeConfiguration.getString(key, defaultValue);
	}
	
	/** 
	 * 读取key，返回int结果，如果没有找到对应键值，则返回0
	 *
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}
	
	/** 
	 * 读取key，返回int结果，如果没有找到对应键值，则返回默认值 
	 *
	 * @param key
	 * @param defaultValue
	 * @return int
	 */
	public int getInt(String key, int defaultValue) {
		return compositeConfiguration.getInt(key, defaultValue);
	}
	
	/** 
	 * 读取key，返回double结果，如果没有找到对应键值，则返回0f 
	 *
	 * @param key
	 * @return double
	 */
	public double getDouble(String key) {
		return getDouble(key, 0d);
	}
	
	/** 
	 * 读取key，返回double结果，如果没有找到对应键值，则返回默认值 
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public double getDouble(String key, double defaultValue) {
		return compositeConfiguration.getDouble(key, defaultValue);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回0 
	 *
	 * @param key
	 * @return byte
	 */
	public byte getByte(String key) {
		return getByte(key, (byte) 0);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回默认值  
	 *
	 * @param key
	 * @param defaultValue
	 * @return byte
	 */
	public byte getByte(String key, byte defaultValue) {
		return compositeConfiguration.getByte(key, defaultValue);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回0f  
	 *
	 * @param key
	 * @return float
	 */
	public float getFloat(String key) {
		return getFloat(key, 0f);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回默认值 
	 *
	 * @param key
	 * @param defaultValue
	 * @return float
	 */
	public float getFloat(String key, float defaultValue) {
		return compositeConfiguration.getFloat(key, defaultValue);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回0l 
	 *
	 * @param key
	 * @return long
	 */
	public long getLong(String key) {
		return getLong(key, 0l);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回默认值 
	 *
	 * @param key
	 * @param defaultValue
	 * @return long
	 */
	public long getLong(String key, long defaultValue) {
		return compositeConfiguration.getLong(key, defaultValue);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回0 
	 *
	 * @param key
	 * @return short
	 */
	public short getShort(String key) {
		return getShort(key, (short) 0);
	}
	
	/** 
	 * 读取key，返回key结果，如果没有找到对应键值，则返回默认值 
	 *
	 * @param key
	 * @param defaultValue
	 * @return short
	 */
	public static short getShort(String key, short defaultValue) {
		return compositeConfiguration.getShort(key, defaultValue);
	}
	
	public String[] getLocations() {
		return SystemConfiguration.files;
	}
	
	public void setLocations(String[] files) {
		SystemConfiguration.files = files;
	}
}
