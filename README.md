multi pinyin4j (mpinyin4j) based on pinyin4j
========

A copy of http://sourceforge.net/projects/pinyin4j, then deploy it to maven central repository.

### Download ###
Download the jar via maven:
```xml
<dependency>
    <groupId>com.belerweb</groupId>
    <artifactId>pinyin4j</artifactId>
    <version>2.5.0</version>
</dependency>
```

#外挂多音词库

用户配置的外挂词库会覆盖系统中相同词的读音,可用于纠错

配置方式很简单,只需要配置路径即可 
```
MultiPinyinConfig.multiPinyinPath="/Users/yiboliu/my_multi_pinyin.txt"
```

格式同系统的多音词库,如: 
```
吸血鬼日记 (xi1,xue4,gui3,ri4,ji4)
```