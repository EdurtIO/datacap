---
title: 保存菜单 (管理员)
---

请求地址：`/api/v1/menu`

请求方式：`POST`

## Body

::: tabs
    === "示例"
        ```json
        {
            "name": "测试菜单",
            "description": "",
            "url": "/test",
            "sorted": "01",
            "type": "VIEW",
            "active": true,
            "i18nKey": "common.home",
            "icon": "",
            "new": false
        }
        ```
    === "参数"
        |参数|类型| 描述                  |
        |---|---|---------------------|
        |`name`|String| 菜单名称               |
        |`description`|String| 菜单描述               |
        |`url`|String| 菜单路径               |
        |`sorted`|String| 菜单排序               |
        |`type`|String| 菜单类型               |
        |`active`|Boolean| 是否激活               |
        |`i18nKey`|String| 国际化key               |
        |`icon`|String| 图标               |
        |`new`|Boolean| 是否为新菜单               |
:::

## Response

::: tabs
    === "示例"
        ```json
        {
            "name": "测试菜单",
            "code": "10ae626195704b96b353d00aeb6ab1e5",
            "createTime": null,
            "updateTime": "2024-11-25 20:32:53",
            "description": "",
            "url": "/test",
            "group": null,
            "sorted": 1,
            "type": "VIEW",
            "parent": 0,
            "i18nKey": "common.home",
            "icon": "",
            "redirect": 0,
            "isNew": false,
            "new": false
        }
        ```
    === "参数"
        |参数|类型| 描述                  |
        |---|---|---------------------|
        |`name`|String| 菜单名称               |
        |`code`|String| 菜单code               |
        |`createTime`|String| 创建时间               |
        |`updateTime`|String| 更新时间               |
        |`description`|String| 菜单描述               |
        |`url`|String| 菜单路径               |
        |`group`|String| 菜单分组               |
        |`sorted`|Number| 菜单排序               |
        |`type`|String| 菜单类型               |
        |`parent`|Number| 父菜单               |
        |`i18nKey`|String| 国际化key               |
        |`icon`|String| 图标               |
        |`redirect`|Number| 跳转地址               |
        |`isNew`|Boolean| 是否为新菜单               |
:::