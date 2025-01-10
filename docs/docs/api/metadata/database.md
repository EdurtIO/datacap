---
title: 数据库列表
---

请求地址: `/api/v1/metadata/{code}/databases`

请求方式: `GET`

## Path

=== "示例"

    ```
    /api/v1/metadata/8ee2171b5d014779a45901fb9c2428c9/databases
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`code`|String|数据源编码|

## Response

=== "示例"

    ```json
    {
      "object_name": "authx",
      "object_type": "DATABASE",
      "object_comment": ""
    }
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`object_name`|String|对象名称|
    |`object_type`|String|对象类型|
    |`object_comment`|String|对象描述|