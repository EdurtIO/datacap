---
title: 安装插件
---

请求地址：`/api/v1/plugin/install`

请求方式：`POST`

## Body

::: tabs
=== "示例"
    
    ```json
    {
        "url": "https://cdn.north.devlive.org/applications/datacap/plugins/2024.4.0-SNAPSHOT/plugin/datacap-plugin-mysql-bin.tar.gz",
        "name": "datacap-plugin-mysql"
    }
    ```

=== "参数"

    | 参数名 | 类型 | 是否必须 | 描述 |
    | --- | --- | --- | --- |
    | `url` | string | 是 | 插件下载地址 |
    | `name` | string | 是 | 插件名称 |
:::
## Response

::: tabs
=== "示例"

    ```json
    true
    ```

=== "参数"

    如果返回 true 表示安装成功，否则安装失败。
:::