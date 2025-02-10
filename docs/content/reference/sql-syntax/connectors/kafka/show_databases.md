---
title: SHOW DATABASES
---

### Synopsis

---

```sql
SHOW DATABASES
```

### Description

---

Returns a list of all defined topics in the current cluster. Returns NULL for any information that is not populated or unavailable on the data source.

The list data is returned as a row of each column, and the default header is `*`.

| Column | Description            | Notes                           |
|--------|------------------------|---------------------------------|
| `*`    | The name of the column | `NULL` in the table summary row |
