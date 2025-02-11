---
title: 卸载插件
---

请求地址：`/api/v1/plugin/ininstall/{pluginName}`

请求方式：`DELETE`

## Path

::: tabs
=== "示例"
    
    ```json
    /MySQL
    ```

=== "参数"

    | 参数名 | 类型 | 是否必须 | 描述 |
    | --- | --- | --- | --- |
    | `pluginName` | string | 是 | 插件名称 |
:::

## Response

::: tabs
=== "示例"

    ```json
    true
    ```

=== "参数"

    如果返回 true 表示卸载成功，返回 false 表示卸载失败
:::