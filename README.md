# AirLight
一个多线程实体更新的魔改服务端，基于catserver和我的旧项目，目前服务端兼容性较差（因为多线程实体导致了一堆安全问题）
# Build
 1. 打开你的终端
 2. 运行 ./gradlew setup 然后运行 ./gradlew build(如果你没有修改minecraft)
 3. 一切执行完后，你就能在 build/distributions 里看到jar包了
# Unsupported Mod Type
目前已知不支持的mod类型：
    1.修改了AI中PathFinder和PathHeap以及Path的模组（会出现转换错误）
    2.对世界加载方式修改的模组
    3.修改了EntityTracker的模组
    4.事件强制要求像是Map、List、Set等集合类型的模组(会出现转换错误)
