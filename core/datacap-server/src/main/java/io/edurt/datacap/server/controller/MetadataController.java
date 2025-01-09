package io.edurt.datacap.server.controller;

import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.service.service.MetadataService;
import io.edurt.datacap.spi.generator.definition.TableDefinition;
import io.edurt.datacap.spi.model.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "/api/v1/metadata")
public class MetadataController
{
    private final MetadataService service;

    public MetadataController(MetadataService service)
    {
        this.service = service;
    }

    @GetMapping(value = "{code}/databases")
    public CommonResponse<Response> fetchDatabases(@PathVariable String code)
    {
        return this.service.getDatabases(code);
    }

    @GetMapping(value = "{code}/{database}/tables")
    public CommonResponse<Response> fetchTables(
            @PathVariable String code,
            @PathVariable String database
    )
    {
        return this.service.getTables(code, database);
    }

    @GetMapping(value = "{code}/{database}/{table}/columns")
    public CommonResponse<Response> fetchColumns(
            @PathVariable String code,
            @PathVariable String database,
            @PathVariable String table
    )
    {
        return this.service.getColumns(code, database, table);
    }

    @GetMapping(value = "{code}/{database}")
    public CommonResponse<Response> fetchDatabase(
            @PathVariable String code,
            @PathVariable String database
    )
    {
        return this.service.getDatabase(code, database);
    }

    @GetMapping(value = "{code}/{database}/{table}")
    public CommonResponse<Response> fetchTable(
            @PathVariable String code,
            @PathVariable String database,
            @PathVariable String table
    )
    {
        return this.service.getTable(code, database, table);
    }

    @GetMapping(value = "{code}/{database}/{table}/statement")
    public CommonResponse<Response> fetchTableStatement(
            @PathVariable String code,
            @PathVariable String database,
            @PathVariable String table
    )
    {
        return this.service.getTableStatement(code, database, table);
    }

    @PutMapping(value = "{code}/{database}/{table}/auto-increment")
    public CommonResponse<Response> updateAutoIncrement(
            @PathVariable String code,
            @PathVariable String database,
            @PathVariable String table,
            @RequestBody TableDefinition definition
    )
    {
        return this.service.updateAutoIncrement(code, definition, database, table);
    }
}
