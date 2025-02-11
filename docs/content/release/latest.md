---
title: latest
---

**DataCap 发布！**

|    发布版本    |     发布时间     |
|:----------:|:------------:|
| `2024.4.1` | `2024-12-31` |

尊敬的 DataCap 用户：

DataCap 2024.4.1 版本现已正式发布。本次更新包含多项重要功能升级和性能优化，现将主要更新内容公布如下：

### 核心功能升级

1. **数据库功能增强 (实现功能)**
    - 新增数据库管理功能：支持创建、删除和切换数据库
    - 完善表管理功能：支持创建表、删除表、插入数据等操作

2. **MongoDB 驱动全面升级**
    - 优化查询性能：将 find 替换为 aggregate
    - 新增元数据支持
    - 支持 com.dbschema.MongoJdbcDriver 适配
    - 完善版本控制和索引获取功能

3. **工作流引擎优化**
    - 新增工作流任务提交功能
    - 支持工作流重启操作
    - 优化 SeatTunnel 执行器，支持自定义节点类型

4. **SQL 解析器增强**
    - 优化 G4 表达式结构
    - 新增 SHOW 语句支持
    - 完善 SELECT 语句格式化功能
    - 支持 CREATE DATABASE 语法

### 问题修复
- 修复查询历史记录创建问题
- 修复 LocalDateTime 类型 JSON 转换问题
- 修复历史数据获取失败问题
- 优化达梦数据库插件版本获取

### 其他优化
- 完善 Windows 平台支持
- 优化发布脚本
- 优化 CI/CD 流程

### 获取方式
- GitHub：https://github.com/devlive-community/datacap
- 官方网站：https://datacap.devlive.org/
- Docker：已更新最新镜像

感谢社区用户一直以来的支持与反馈。如有问题或建议，欢迎通过 GitHub Issues 与我们交流。
