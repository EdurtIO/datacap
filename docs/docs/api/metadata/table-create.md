---
title: 新建数据表
---

请求地址: `/api/v1/metadata/{code}/{database}/create-table`

请求方式: `POST`

## Request

=== "Path 示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/dc1/create-table
    ```

=== "Body 示例"

    ```json
    {
        "columns": [
            {
                "name": "c1",
                "type": "TINYINT",
                "length": "123",
                "isNullable": true,
                "defaultValue": "123",
                "primaryKey": true,
                "autoIncrement": true,
                "isNullable": true,
                "comment": "123"
            }
        ],
        "name": "t4",
        "engine": "InnoDB",
        "comment": ""
    }
    ```

=== "Path 参数"

    |参数|类型|描述|
    |---|---|---|
    |`code`|String|数据源编码|
    |`database`|String|数据库名称|

=== "Body 参数"

    |参数|类型|描述|
    |---|---|---|
    |`columns`|List|字段列表|
    |`name`|String|表名称|
    |`engine`|String|表引擎|
    |`comment`|String|表注释|

=== "Column 参数"

    |参数|类型|描述|
    |---|---|---|
    |`name`|String|字段名称|
    |`type`|String|字段类型|
    |`length`|String|字段长度|
    |`isNullable`|Boolean|是否可为空|
    |`defaultValue`|String|默认值|
    |`primaryKey`|Boolean|是否主键|
    |`autoIncrement`|Boolean|是否自增|
    |`comment`|String|字段注释|

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