---
title: 获取建表语句
---

请求地址: `/api/v1/metadata/{code}/{database}/{table}/statement`

请求方式: `GET`

## Path

=== "示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/dc1/datacap_menu/statement
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
        "create_table_sql": "CREATE TABLE `datacap_chat_user_relation` (\n  `chat_id` bigint,\n  `user_id` bigint\n) ENGINE=InnoDB ROW_FORMAT=Dynamic DEFAULT CHARSET=utf8mb4_0900_ai_ci;"
    }
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`create_table_sql`|String|建表语句|