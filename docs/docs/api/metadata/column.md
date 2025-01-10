---
title: 数据列列表
---

请求地址: `/api/v1/metadata/{code}/{database}/{table}/columns`

请求方式: `GET`

## Path

=== "示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/datacap/datacap_chat/columns
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`code`|String|数据源编码|
    |`database`|String|数据库名称|
    |`table`|String|数据表名称|

## Response

=== "示例"

    ```json
    {
        "type_name": "column",
        "object_name": "id",
        "object_data_type": "bigint",
        "object_nullable": "NO",
        "object_default_value": null,
        "object_comment": "",
        "object_position": 1,
        "object_definition": ""
    }
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`type_name`|String|对象类型名称|
    |`object_name`|String|对象名称|
    |`object_data_type`|String|对象数据类型|
    |`object_nullable`|String|对象是否允许为空|
    |`object_default_value`|String|对象默认值|
    |`object_comment`|String|对象注释|
    |`object_position`|String|对象位置|
    |`object_definition`|String|对象定义|
    