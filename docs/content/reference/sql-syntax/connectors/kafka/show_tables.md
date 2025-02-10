---
title: SHOW TABLES
---

### Synopsis

---

```sql
SHOW TABLES
SHOW TABLES FROM `database`
```

> `database` kafka topic name

### Description

---

Returns a list of all defined consumers in the current cluster (if a topic is specified, a list of consumers for the specified topic will be returned). Returns NULL for any information that is not populated or unavailable on the data source.

The list data is returned as a row of each column, and the default header is `*`.

| Column | Description            | Notes                           |
|--------|------------------------|---------------------------------|
| `*`    | The name of the column | `NULL` in the table summary row |
