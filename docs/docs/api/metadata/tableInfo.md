---
title: 数据表详情
---

请求地址: `/api/v1/metadata/{code}/{database}/{table}`

请求方式: `GET`

## Path

=== "示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/datacap/datacap_chat
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
        "object_type": "table",
        "object_name": "datacap_function",
        "object_engine": "InnoDB",
        "object_collation": "utf8mb4_0900_ai_ci",
        "object_comment": "Plug-in correlation function",
        "object_create_time": "2024-04-13 00:18:20",
        "object_update_time": null,
        "object_data_size": 0.02,
        "object_index_size": 0,
        "object_rows": 2,
        "object_column_count": 11,
        "object_index_count": 1,
        "type_name": "table",
        "object_format": "Dynamic",
        "object_avg_row_length": 8192,
        "object_auto_increment": 3
    }
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`object_type`|String|数据表类型|
    |`object_name`|String|数据表名称|
    |`object_engine`|String|数据库引擎|
    |`object_collation`|String|排序规则|
    |`object_comment`|String|注释|
    |`object_create_time`|String|创建时间|
    |`object_update_time`|String|更新时间|
    |`object_data_size`|Number|数据大小|
    |`object_index_size`|Number|索引大小|
    |`object_rows`|Number|行数|
    |`object_column_count`|Number|列数量|
    |`object_index_count`|Number|索引数量|
    |`type_name`|String|数据表类型|
    |`object_format`|String|数据表格式|
    |`object_avg_row_length`|Number|平均行长度|
    |`object_auto_increment`|Number|自增列|