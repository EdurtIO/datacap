---
title: 用户菜单
---

请求地址: `/api/v1/user/menus`

请求方式: `GET`

## Response

::: tabs
=== "示例"

    ```json
    [
      {
        "id": 1,
        "title": "全局 - 首页",
        "url": "/home",
        "sorted": 1,
        "code": "HOME",
        "i18nKey": "common.home",
        "icon": "Home",
        "isNew": false,
        "checked": true,
        "selected": true,
        "parent": 0,
        "new": false
      }
    ]
    ```

=== "参数"

    | 参数名 | 类型 | 描述 |
    | --- |----| --- |
    | `id` | 数值 | ID |
    | `title` | 字符串 | 标题 |
    | `url` | 字符串 | URL |
    | `sorted` | 数值 | 排序 |
    | `code` | 字符串 | 代码 |
    | `i18nKey` | 字符串 | 国际化 |
    | `icon` | 字符串 | 图标 |
    | `isNew` | 布尔 | 是否新建 |
    | `checked` | 布尔 | 是否选中 |
    | `selected` | 布尔 | 是否选中 |
    | `parent` | 数值 | 父菜单 |
    | `new` | 布尔 | 是否新建|
:::