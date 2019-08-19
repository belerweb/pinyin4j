pinyin4j
========

A copy of https://github.com/belerweb/pinyin4j, then fix a bug

### bug修复以及新增功能 ###
1.修复 "一日千里" 调用PinyinHelper.toHanYuPinyinString输出 "yi,ri,qianli" 末尾分隔符丢失的问题

2.自定义挂载的pinyindb除了可以使用绝对路径，还新支持放在resources下被打入jar包的文件。
如我有个module，在它resources下放个资源文件 resources/pinyindb/multi_pinyin_extends.txt
这个module被打成jar包后，依然可以通过MultiPinyinConfig.multiPinyinPath = "/pinyindb/multi_pinyin_extends.txt"来设置挂载的pinyindb

### 使用方法 ###
好像原作者已经好久不维护了，提交了merge request也没响应，没拒绝也没通过。我也没发到maven中央仓库，所以需要deploy到自己到私服上使用。
