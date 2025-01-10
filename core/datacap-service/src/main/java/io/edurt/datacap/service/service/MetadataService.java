package io.edurt.datacap.service.service;

import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.spi.generator.definition.TableDefinition;
import io.edurt.datacap.spi.model.Response;

public interface MetadataService
{
    CommonResponse<Response> getEngines(String code);

    CommonResponse<Response> getDataTypes(String code);

    CommonResponse<Response> getDatabases(String code);

    CommonResponse<Response> getTables(String code, String database);

    CommonResponse<Response> getColumns(String code, String database, String table);

    CommonResponse<Response> getDatabase(String code, String database);

    CommonResponse<Response> getTable(String code, String database, String table);

    CommonResponse<Response> getTableStatement(String code, String database, String table);

    CommonResponse<Response> updateAutoIncrement(String code, TableDefinition configure, String database, String table);

    CommonResponse<Response> createTable(String code, String database, TableDefinition configure);

    CommonResponse<Response> dropTable(String code, String database, String table, TableDefinition configure);

    CommonResponse<Response> truncateTable(String code, String database, String table, TableDefinition configure);
}
