### 什么是 RPC？

定义：RPC（Remote Procedure Call）即远程过程调用，是一种计算机通信协议，它允许程序在不同的计算机之间进行通信和交互，就像本地调用一样。

服务消费方只需要实现同样的 common 接口 ，无需关心服务如何实现，就能调用远程的服务，得到服务结果。

参考框架：[Apache Dubbo](https://cn.dubbo.apache.org/zh-cn/)

## 架构设计
![My Image](../assets/1720062646739.png)

