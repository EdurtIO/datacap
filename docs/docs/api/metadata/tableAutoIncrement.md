---
title: 更新表自增键
---

请求地址: `/api/v1/metadata/{code}/{database}/{table}/auto-increment`

请求方式: `PUT`

## Request

=== "示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/dc1/datacap_menu/auto-increment
    ```

=== "Path 参数"

    |参数|类型|描述|
    |---|---|---|
    |`code`|String|数据源编码|
    |`database`|String|数据库名称|
    |`table`|String|数据表名称|

=== "Body 参数"

    |参数|类型|描述|
    |---|---|---|
    |`value`|Long|自增键|

## Response

=== "示例"

    ```json
    {
        "result": 0
    }
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`result`|Integer|操作结果|