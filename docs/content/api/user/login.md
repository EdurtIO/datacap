---
title: 用户登录
---

请求地址：`/api/auth/signin`

请求方式：`POST`

## Body

::: tabs
=== "示例"

    ``` json
    {
        "timestamp": 1732189045000,
        "username": "xxxx",
        "password": "xxxx",
        "captcha": "1"
    }
    ```

=== "参数"

    |名称|类型|必选项|描述|依赖|
    |---|---|---|---|---|
    |`timestamp`|数值|否|当前时间戳，也可以是自定义数值，启用 `captcha` 时，该值才会生效|`captcha`|
    |`username`|字符串|是|用户名，需要是英文|-|
    |`password`|字符串|是|用户名密码|-|
    |`captcha`|数值|否|只有系统启用验证码的情况下才有效，该值是验证码的计算结果|`timestamp`|
:::

## Response

::: tabs
=== "示例"

    ```json
    {
        "type": "Bearer",
        "token": "ccc",
        "id": 1,
        "username": "admin",
        "roles": [
            "1"
        ],
        "avatar": ""
    }
    ```

=== "参数"

    | 名称                | 类型  | 描述                    |
    |-------------------|-----|-----------------------|
    | `type`        | 字符串 | 账号授权类型，目前仅支持 `Bearer` |
    | `token`   | 字符串 | 登录成功后的授权密钥            |
    | `id` | 字符串 | 当前登录用户 ID             |
    | `username` | 字符串 | 当前登录用户名               |
    |`roles`|字符串数组|权限列表|
    |`avatar`|字符串|用户头像 base64 编码|
:::