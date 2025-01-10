---
title: 删除数据表
---

请求地址: `/api/v1/metadata/{code}/{database}/{table}/drop-table`

请求方式: `DELETE`

## Request

=== "Path 示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/dc1/datacap_menu/drop-table
    ```

=== "Body 示例"

    ```json
    {
        "preview": true
    }
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
    |`preview`|Boolean|是否预览要执行的 SQL|

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