---
title: 删除工作流
---

请求地址：`/api/v1/workflow/{code}`

请求方式：`DELETE`

## Path

::: tabs
=== "示例"

    ```json
    /api/v1/workflow/8ee2171b5d014779a45901fb9c2428c9
    ```

=== "参数"

    |参数|类型|描述|
    |---|---|---|
    |`code`|String|仪表盘编码|
:::

## Response

::: tabs
=== "示例"

    ```json
    8ee2171b5d014779a45901fb9c2428c9
    ```

=== "参数"

    删除成功后返回仪表盘编码。
:::