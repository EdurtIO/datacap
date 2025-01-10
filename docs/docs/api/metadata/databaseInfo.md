---
title: 数据库详情
---

请求地址: `/api/v1/metadata/{code}/{database}`

请求方式: `GET`

## Path

=== "示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/sys
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`code`|String|数据源编码|
    |`database`|String|数据库名称|

## Response

=== "示例"

    ```json
    {
        "object_name": "sys",
        "object_charset": "utf8mb4",
        "object_collation": "utf8mb4_0900_ai_ci",
        "object_create_time": "2024-04-05 17:10:07",
        "object_update_time": null,
        "object_data_size": 0.02,
        "object_index_size": 0,
        "object_total_size": 0.02,
        "object_table_count": 1,
        "object_column_count": 1019,
        "object_index_count": 1,
        "object_view_count": 100,
        "object_procedure_count": 26,
        "object_trigger_count": 2,
        "object_foreign_key_count": 0,
        "object_total_rows": 6
    }
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`object_name`|String|数据库名称|
    |`object_charset`|String|字符集|
    |`object_collation`|String|排序规则|
    |`object_create_time`|String|创建时间|
    |`object_update_time`|String|更新时间|
    |`object_data_size`|Number|数据大小|
    |`object_index_size`|Number|索引大小|
    |`object_total_size`|Number|总大小|
    |`object_table_count`|Number|表数量|
    |`object_column_count`|Number|列数量|
    |`object_index_count`|Number|索引数量|
    |`object_view_count`|Number|视图数量|
    |`object_procedure_count`|Number|存储过程数量|
    |`object_trigger_count`|Number|触发器数量|
    |`object_foreign_key_count`|Number|外键数量|
    |`object_total_rows`|Number|总行数|