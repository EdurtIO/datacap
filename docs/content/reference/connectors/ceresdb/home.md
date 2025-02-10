---
title: CeresDB
status: new
---

#### 什么是 CeresDB ?

CeresDB 是一款高性能、分布式的云原生时序数据库。

#### 环境

!!! note

    如果你需要使用这个数据源, 您需要将 DataCap 服务升级到 >= `1.9.x`

!!!

支持时间: `2023-04-12`

#### 配置

---

!!! note

    如果您的CeresDB服务版本需要其他特殊配置，请参考修改配置文件并重启 DataCap 服务。

!!!

::: tabs
    === "配置"
        | Field  |             Required              | Default Value |
        |:------:|:---------------------------------:|:-------------:|
        | `Name` | :check{20,#3CA34F}:  |       \-       |
        | `Host` | :check{20,#3CA34F}: |  `127.0.0.1`  |
        | `Port` | :check{20,#3CA34F}: |    `5440`     |
:::

#### 版本（验证）

---

!!! warning

    服务版本尚未测试，如果您有详细的测试并发现错误，请提交 [issues](https://github.com/EdurtIO/datacap/issues/new/choose)

    - [x] 1.x

!!!