---
title: 通用实体
---

本文档主要是用于描述通用配置，所有 API 都是基于该实体返回。

## Response

::: tabs
    === "请求成功示例"
        ```json
        {
          "status": true,
          "code": 200,
          "message": "Query successful",
          "data": {}
        }
        ```
    === "请求失败示例"
        ```json
        {
          "status": true,
          "code": 400,
          "message": "Query successful",
          "data": null
        }
        ```
    === "参数"
        |参数|类型|描述|
        |---|---|---|
        |`status`|请求状态|请求状态，如果是 true 标记请求成功，false 请求出现异常。该异常是系统定制返回的不是开放性的|
        |`code`|编码|请求发送后返回的编码，不同编码有不同的含义|
        |`message`|异常信息|如果请求失败，这里会返回具体的异常信息|
        |`data`|数据|请求成功且未发生异常情况下，返回的具体数据|
:::