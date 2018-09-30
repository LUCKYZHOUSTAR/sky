# config 包注意事项

## 加载顺序

1. ConfigProperties
2. ConfigParser
3. AppConfig & GlobalConfig
4. Others

> 注意: 由于 LoggerManager 依赖 GlobalConfig, 故在上面第三阶段之前是无法使用 LoggerManager 的