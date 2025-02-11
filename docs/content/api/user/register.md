---
title: 用户注册
---

请求地址：`/api/auth/signup`

请求方式：`POST`

## Body

::: tabs
=== "示例"

    ``` json
    {
        "timestamp": 1732183276000,
        "username": "aaa",
        "password": "aaaaaaaa",
        "confirmPassword": "aaaaaaaa",
        "captcha": "5"
    }
    ```

=== "参数"

    |名称|类型|必选项|描述|依赖|
    |---|---|---|---|---|
    |`timestamp`|数值|否|当前时间戳，也可以是自定义数值，启用 `captcha` 时，该值才会生效|`captcha`|
    |`username`|字符串|是|注册的用户名，需要是英文|-|
    |`password`|字符串|是|注册用户名的密码|-|
    |`confirmPassword`|字符串|是|注册用户名的密码|-|
    |`captcha`|数值|否|只有系统启用验证码的情况下才有效，该值是验证码的计算结果|`timestamp`|
:::

## Response

::: tabs
=== "示例"

    ```json
    {
      "username": "a07",
      "chatConfigure": null,
      "editorConfigure": null,
      "avatarConfigure": null
    }
    ```

=== "参数"

    | 名称                | 类型  | 描述    |
    |-------------------|-----|-------|
    | `username`        | 字符串 | 用户名   |
    | `chatConfigure`   | 字符串 | 对话配置  |
    | `editorConfigure` | 字符串 | 编辑器配置 |
    | `avatarConfigure` | 字符串 | 头像配置  |
:::