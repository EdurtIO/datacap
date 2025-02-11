---
title: 保存仪表盘
---

请求地址：`/api/v1/dashboard`

请求方式：`POST`

## Body

::: tabs
    === "示例"
        ```json
        {
          "name": "M 1.0",
          "configure": "[...]",
          "version": "1.0",
          "description": "随意修改的仪表盘",
          "reports": [
            {
              "code": "24f38722eb6d432a9ee574e675bd0509"
            }
          ]
        }
        ```
    === "参数"
        |参数|类型| 描述                  |
        |---|---|---------------------|
        |`name`|String| 仪表盘名称               |
        |`configure`|String| 仪表盘配置               |
        |`version`|String| 仪表盘版本               |
        |`description`|String| 仪表盘描述               |
        |`reports`|Array| 报表列表，结构 `[{"code": "24f38722eb6d432a9ee574e675bd0509"}]` |
:::

## Response

::: tabs
    === "示例"
        ```json
        {
          "name": "M 1.0",
          "code": "9e78e83f55864da7a4802e0989ae1f33",
          "active": true,
          "createTime": "2024-11-25 13:48:27",
          "updateTime": "2024-11-25 13:48:27",
          "configure": "[...]",
          "description": "随意修改的仪表盘",
          "avatar": {},
          "version": "1.0",
          "reports": [
            {
              "name": null,
              "code": "24f38722eb6d432a9ee574e675bd0509",
              "active": true,
              "createTime": null,
              "updateTime": null,
              "configure": null,
              "realtime": false,
              "type": null,
              "query": null,
              "description": null,
              "source": null,
              "dataset": null
            }
          ]
        }
        ```
    === "参数"
        |参数|类型| 描述                  |
        |---|---|---------------------|
        |`name`|String| 仪表盘名称               |
        |`code`|String| 仪表盘编码               |
        |`active`|Boolean| 是否激活               |
        |`createTime`|String| 创建时间               |
        |`updateTime`|String| 更新时间               |
        |`configure`|String| 仪表盘配置               |
        |`description`|String| 仪表盘描述               |
        |`avatar`|Object| 仪表盘头像               |
        |`version`|String| 仪表盘版本               |
        |`reports`|Array| 报表列表详见[报表](../report/list.md) |
:::