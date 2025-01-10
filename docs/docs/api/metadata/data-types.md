---
title: 数据列类型
---

请求地址: `/api/v1/metadata/{code}/data-types`

请求方式: `GET`

## Path

=== "示例"

    ```
    /api/v1/metadata/e98ef92476a94ce5ae28115d2eb7ff40/data-types
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`code`|String|数据源编码|

## Response

=== "示例"

    ```json
    [
        "INT",
        "VARCHAR",
        "TIMESTAMP",
        "DECIMAL"
    ]
    ```