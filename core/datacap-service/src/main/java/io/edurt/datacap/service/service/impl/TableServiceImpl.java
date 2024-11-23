package io.edurt.datacap.service.service.impl;

import com.google.common.collect.Lists;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.common.enums.ServiceState;
import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.common.sql.SqlBuilder;
import io.edurt.datacap.common.sql.configure.SqlBody;
import io.edurt.datacap.common.sql.configure.SqlColumn;
import io.edurt.datacap.common.sql.configure.SqlOperator;
import io.edurt.datacap.common.sql.configure.SqlOrder;
import io.edurt.datacap.common.sql.configure.SqlType;
import io.edurt.datacap.common.utils.CSVUtils;
import io.edurt.datacap.fs.FsRequest;
import io.edurt.datacap.fs.FsResponse;
import io.edurt.datacap.fs.FsService;
import io.edurt.datacap.plugin.Plugin;
import io.edurt.datacap.plugin.PluginManager;
import io.edurt.datacap.service.body.ExportBody;
import io.edurt.datacap.service.body.TableBody;
import io.edurt.datacap.service.body.TableFilter;
import io.edurt.datacap.service.entity.BaseEntity;
import io.edurt.datacap.service.entity.ColumnEntity;
import io.edurt.datacap.service.entity.DatabaseEntity;
import io.edurt.datacap.service.entity.SourceEntity;
import io.edurt.datacap.service.entity.TableEntity;
import io.edurt.datacap.service.entity.UserEntity;
import io.edurt.datacap.service.initializer.InitializerConfigure;
import io.edurt.datacap.service.repository.metadata.ColumnRepository;
import io.edurt.datacap.service.repository.metadata.DatabaseRepository;
import io.edurt.datacap.service.repository.metadata.TableRepository;
import io.edurt.datacap.service.security.UserDetailsService;
import io.edurt.datacap.service.service.TableService;
import io.edurt.datacap.spi.PluginService;
import io.edurt.datacap.spi.model.Configure;
import io.edurt.datacap.spi.model.Pagination;
import io.edurt.datacap.spi.model.Response;
import io.edurt.datacap.sql.builder.ColumnBuilder;
import io.edurt.datacap.sql.builder.TableBuilder;
import io.edurt.datacap.sql.model.Column;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"})
public class TableServiceImpl
        implements TableService
{
    private final PluginManager pluginManager;
    private final TableRepository repository;
    private final DatabaseRepository databaseRepository;
    private final ColumnRepository columnRepository;
    private final InitializerConfigure initializerConfigure;
    private final HttpServletRequest request;

    public TableServiceImpl(PluginManager pluginManager, TableRepository repository, DatabaseRepository databaseRepository, ColumnRepository columnRepository, InitializerConfigure initializerConfigure, HttpServletRequest request)
    {
        this.pluginManager = pluginManager;
        this.repository = repository;
        this.databaseRepository = databaseRepository;
        this.columnRepository = columnRepository;
        this.initializerConfigure = initializerConfigure;
        this.request = request;
    }

    @Override
    public CommonResponse<List<TableEntity>> getAllByDatabase(String code)
    {
        return databaseRepository.findByCode(code)
                .map(value -> CommonResponse.success(this.repository.findAllByDatabase(value)))
                .orElseGet(() -> CommonResponse.failure(String.format("Database [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Object> fetchData(String code, TableFilter configure)
    {
        return repository.findByCode(code)
                .map(table -> {
                    SourceEntity source = table.getDatabase().getSource();
                    pluginManager.getPlugin(source.getType())
                            .map(plugin -> {
                                PluginService pluginService = plugin.getService(PluginService.class);
                                if (configure.getType().equals(SqlType.SELECT)) {
                                    return this.fetchSelect(plugin, pluginService, table, source, configure);
                                }
                                else if (configure.getType().equals(SqlType.INSERT)) {
                                    return this.fetchInsert(plugin, pluginService, table, source, configure);
                                }
                                else if (configure.getType().equals(SqlType.UPDATE)) {
                                    return this.fetchUpdate(plugin, pluginService, table, source, configure);
                                }
                                else if (configure.getType().equals(SqlType.DELETE)) {
                                    return this.fetchDelete(plugin, pluginService, table, source, configure);
                                }
                                else if (configure.getType().equals(SqlType.ALTER)) {
                                    return this.fetchAlter(plugin, pluginService, table, source, configure);
                                }
                                else if (configure.getType().equals(SqlType.SHOW)) {
                                    return this.fetchShowCreateTable(plugin, pluginService, table, source, configure);
                                }
                                else if (configure.getType().equals(SqlType.TRUNCATE)) {
                                    return this.fetchTruncateTable(plugin, pluginService, table, source, configure);
                                }
                                else if (configure.getType().equals(SqlType.DROP)) {
                                    return this.fetchDropTable(plugin, pluginService, table, source, configure);
                                }
                                return CommonResponse.failure(String.format("Not implemented yet [ %s ]", configure.getType()));
                            })
                            .orElseGet(() -> CommonResponse.failure(ServiceState.PLUGIN_NOT_FOUND));
                    return CommonResponse.failure(ServiceState.PLUGIN_NOT_FOUND);
                })
                .orElse(CommonResponse.failure(String.format("Table [ %s ] not found", code)));
    }

    @Override
    public CommonResponse exportDataByCode(String code, ExportBody configure)
    {
        TableEntity table = this.repository.findByCode(code)
                .orElse(null);
        if (table == null) {
            return CommonResponse.failure(String.format("Table [ %s ] not found", code));
        }

        SourceEntity source = table.getDatabase().getSource();
        return pluginManager.getPlugin(source.getType())
                .map(plugin -> {
                    PluginService pluginService = plugin.getService(PluginService.class);
                    return pluginManager.getPlugin(initializerConfigure.getFsConfigure().getType())
                            .map(fsPlugin -> {
                                FsService fsService = fsPlugin.getService(FsService.class);
                                UserEntity user = UserDetailsService.getUser();
                                String endpoint = String.join(File.separator, initializerConfigure.getDataHome(), user.getUsername(), "export");
                                log.info("Export data user [ {} ] home [ {} ]", user.getUsername(), endpoint);
                                String fileName = String.join(".", UUID.randomUUID().toString(), "csv");
                                log.info("Export file name [ {} ]", fileName);
                                Integer count = Integer.MAX_VALUE;
                                if (configure.getCount() > 0) {
                                    count = configure.getCount().intValue();
                                }
                                Pagination pagination = Pagination.newInstance(count.intValue(), 1, count.intValue());
                                TableFilter tableFilter = TableFilter.builder()
                                        .type(SqlType.SELECT)
                                        .pagination(pagination)
                                        .build();
                                CommonResponse<Object> tempResponse = this.fetchSelect(plugin, pluginService, table, source, tableFilter);
                                if (tempResponse.getStatus()) {
                                    Response pluginResponse = (Response) tempResponse.getData();
                                    try {
                                        File tempFile = CSVUtils.makeTempCSV(endpoint, fileName, pluginResponse.getHeaders(), pluginResponse.getColumns());
                                        log.info("Export temp file [ {} ]", tempFile.getAbsolutePath());
                                        FsRequest fsRequest = FsRequest.builder()
                                                .access(initializerConfigure.getFsConfigure().getAccess())
                                                .secret(initializerConfigure.getFsConfigure().getSecret())
                                                .endpoint(endpoint)
                                                .bucket(initializerConfigure.getFsConfigure().getBucket())
                                                .stream(Files.newInputStream(tempFile.toPath()))
                                                .fileName(fileName)
                                                .build();
                                        fsService.writer(fsRequest);
                                        if (initializerConfigure.getFsConfigure().getType().equals("Local")) {
                                            String address = request.getRequestURL()
                                                    .toString()
                                                    .replace(request.getServletPath().trim(), "");
                                            String remote = String.join("/", address, "api/v1/table/dataDownload", user.getUsername(), fileName);
                                            return CommonResponse.success(remote);
                                        }
                                    }
                                    catch (Exception ex) {
                                        return CommonResponse.failure(ex.getMessage());
                                    }
                                }
                                return CommonResponse.failure(ServiceState.REQUEST_EXCEPTION);
                            })
                            .orElseGet(() -> CommonResponse.failure(String.format("Not found Fs [ %s ]", initializerConfigure.getFsConfigure().getType())));
                })
                .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", source.getType())));
    }

    @Override
    public Object dataDownload(String username, String filename)
    {
        return pluginManager.getPlugin(initializerConfigure.getFsConfigure().getType())
                .map(plugin -> {
                    String endpoint = String.join(File.separator, initializerConfigure.getDataHome(), username, "export");
                    log.info("Download data user [ {} ] home [ {} ]", username, endpoint);
                    String filePath = String.join(File.separator, endpoint, filename);
                    log.info("Download file path [ {} ]", filePath);
                    FsRequest fsRequest = FsRequest.builder()
                            .access(initializerConfigure.getFsConfigure().getAccess())
                            .secret(initializerConfigure.getFsConfigure().getSecret())
                            .endpoint(endpoint)
                            .bucket(initializerConfigure.getFsConfigure().getBucket())
                            .fileName(filename)
                            .build();

                    FsService fsService = plugin.getService(FsService.class);
                    FsResponse response = fsService.reader(fsRequest);
                    try {
                        Resource resource = new FileSystemResource(response.getRemote());
                        HttpHeaders headers = new HttpHeaders();
                        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename());
                        return ResponseEntity.ok()
                                .headers(headers)
                                .contentLength(resource.contentLength())
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(resource);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Not found plugin"));
    }

    @Override
    public CommonResponse<Response> createTable(Long databaseId, TableBody configure)
    {
        Optional<DatabaseEntity> optionalDatabase = this.databaseRepository.findById(databaseId);
        if (!optionalDatabase.isPresent()) {
            return CommonResponse.failure(String.format("Database [ %s ] not found", databaseId));
        }

        DatabaseEntity database = optionalDatabase.get();
        SourceEntity source = database.getSource();
        return pluginManager.getPlugin(source.getType())
                .map(plugin -> {
                    PluginService pluginService = plugin.getService(PluginService.class);
                    TableBuilder.Companion.BEGIN();
                    TableBuilder.Companion.CREATE_TABLE(String.format("`%s`.`%s`", database.getName(), configure.getName()));
                    TableBuilder.Companion.COLUMNS(configure.getColumns().stream().map(item -> item.toColumnVar()).collect(Collectors.toList()));
                    String sql = TableBuilder.Companion.SQL();
                    log.info("Create table sql \n {} \n on database [ {} ]", sql, database.getName());
                    Configure pConfigure = source.toConfigure(pluginManager, plugin);
                    pConfigure.setPluginManager(pluginManager);
                    Response response = pluginService.execute(pConfigure, sql);
                    response.setContent(sql);
                    return CommonResponse.success(response);
                })
                .orElseGet(() -> CommonResponse.failure("Not found plugin"));
    }

    @Override
    public CommonResponse<Object> manageColumn(String code, TableBody configure)
    {
        return repository.findByCode(code)
                .map(table -> {
                    SourceEntity source = table.getDatabase().getSource();
                    pluginManager.getPlugin(source.getType())
                            .map(plugin -> {
                                AtomicReference<String> atomicReference = new AtomicReference<>(null);
                                if (configure.getType().equals(SqlType.CREATE)) {
                                    ColumnBuilder.Companion.BEGIN();
                                    ColumnBuilder.Companion.CREATE_COLUMN(String.format("`%s`.`%s`", table.getDatabase().getName(), table.getName()));
                                    ColumnBuilder.Companion.COLUMNS(configure.getColumns().stream().map(Column::toColumnVar).collect(Collectors.toList()));
                                    atomicReference.set(ColumnBuilder.Companion.SQL());
                                    log.info("Create column sql \n {} \n on table [ {} ]", atomicReference.get(), table.getName());
                                }
                                else if (configure.getType().equals(SqlType.DROP)) {
                                    columnRepository.findById(configure.getColumnId())
                                            .ifPresent(column -> {
                                                ColumnBuilder.Companion.BEGIN();
                                                ColumnBuilder.Companion.DROP_COLUMN(String.format("`%s`.`%s`", table.getDatabase().getName(), table.getName()));
                                                ColumnBuilder.Companion.COLUMNS(Lists.newArrayList(column.getName()));
                                                atomicReference.set(ColumnBuilder.Companion.SQL());
                                            });
                                    log.info("Drop column sql \n {} \n on table [ {} ]", atomicReference.get(), table.getName());
                                }
                                else if (configure.getType().equals(SqlType.MODIFY)) {
                                    ColumnBuilder.Companion.BEGIN();
                                    ColumnBuilder.Companion.MODIFY_COLUMN(String.format("`%s`.`%s`", table.getDatabase().getName(), table.getName()));
                                    ColumnBuilder.Companion.COLUMNS(configure.getColumns().stream().map(Column::toColumnVar).collect(Collectors.toList()));
                                    atomicReference.set(ColumnBuilder.Companion.SQL());
                                    log.info("Modify column sql \n {} \n on table [ {} ]", atomicReference.get(), table.getName());
                                }
                                Response response;
                                if (configure.isPreview()) {
                                    response = Response.builder()
                                            .isSuccessful(true)
                                            .isConnected(true)
                                            .headers(Lists.newArrayList())
                                            .columns(Lists.newArrayList())
                                            .types(Lists.newArrayList())
                                            .content(atomicReference.get())
                                            .build();
                                }
                                else {
                                    Configure pConfigure = source.toConfigure(pluginManager, plugin);
                                    pConfigure.setPluginManager(pluginManager);
                                    PluginService pluginService = plugin.getService(PluginService.class);
                                    response = pluginService.execute(pConfigure, atomicReference.get());
                                    response.setContent(atomicReference.get());
                                }
                                return CommonResponse.success(response);
                            })
                            .orElseGet(() -> CommonResponse.failure(""));
                    return CommonResponse.failure("");
                })
                .orElse(CommonResponse.failure(String.format("Table [ %s ] not found", code)));
    }

    /**
     * Fetches the selected data from the specified table in a database.
     *
     * @param plugin the plugin instance
     * @param table the table entity
     * @param source the source entity
     * @param configure the table filter configuration
     * @return the common response object containing the fetched data
     */
    private CommonResponse<Object> fetchSelect(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            List<SqlColumn> columns = Lists.newArrayList();
            int totalRows = Integer.parseInt(table.getRows());
            Configure countConfigure = source.toConfigure(pluginManager, plugin);
            countConfigure.setFormat("None");
            SqlBody countBody = SqlBody.builder()
                    .type(SqlType.SELECT)
                    .database(table.getDatabase().getName())
                    .table(table.getName())
                    .columns(Collections.singletonList(SqlColumn.builder()
                            .column("COUNT(1)")
                            .build()))
                    .build();
            if (configure.getFilter() != null) {
                countBody.setCondition(configure.getFilter().getCondition());
                countBody.setWhere(configure.getFilter().getFilters());
            }
            SqlBuilder countBuilder = new SqlBuilder(countBody);
            String countSql = countBuilder.getSql();
            Response countResponse = pluginService.execute(
                    countConfigure,
                    countSql
            );
            if (countResponse.getIsSuccessful() && !countResponse.getColumns().isEmpty()) {
                List<Object> applyResponse = (ArrayList) countResponse.getColumns().get(0);
                int applyTotal = Integer.parseInt(String.valueOf(applyResponse.get(0)));
                if (totalRows != applyTotal) {
                    totalRows = applyTotal;
                    table.setRows(String.valueOf(totalRows));
                    this.repository.save(table);
                }
            }

            // If the columns of the query are not passed, they are obtained through metadata
            if (configure.getColumns() == null || configure.getColumns().size() == 0) {
                table.getColumns()
                        .stream()
                        .sorted(Comparator.comparing(item -> Integer.parseInt(item.getPosition())))
                        .forEach(column -> columns.add(SqlColumn.builder()
                                .column(String.format("`%s`", column.getName()))
                                .build()));
            }
            else {
                configure.getColumns()
                        .forEach(column -> columns.add(SqlColumn.builder()
                                .column(String.format("`%s`", column.getColumn()))
                                .build()));
            }
            int offset = configure.getPagination().getPageSize() * (configure.getPagination().getCurrentPage() - 1);
            SqlBody body = SqlBody.builder()
                    .type(SqlType.SELECT)
                    .database(table.getDatabase().getName())
                    .table(table.getName())
                    .columns(columns)
                    .limit(configure.getPagination().getPageSize())
                    .offset(offset)
                    .build();

            if (configure.getOrders() != null) {
                List<SqlColumn> orderColumns = Lists.newArrayList();
                configure.getOrders()
                        .stream()
                        .filter(item -> StringUtils.isNotEmpty(item.getOrder()))
                        .forEach(item -> orderColumns.add(SqlColumn.builder()
                                .column(item.getColumn())
                                .order(SqlOrder.valueOf(item.getOrder().toUpperCase()))
                                .build()));
                body.setOrders(orderColumns);
            }

            if (configure.getFilter() != null) {
                body.setCondition(configure.getFilter().getCondition());
                body.setWhere(configure.getFilter().getFilters());
            }

            SqlBuilder builder = new SqlBuilder(body);
            String sql = builder.getSql();
            Configure pConfigure = source.toConfigure(pluginManager, plugin);
            Response response = pluginService.execute(
                    pConfigure,
                    sql
            );
            Pagination pagination = Pagination.newInstance(configure.getPagination().getPageSize(), configure.getPagination().getCurrentPage(), totalRows);
            response.setPagination(pagination);
            return CommonResponse.success(response);
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Fetches and inserts data into the database table.
     *
     * @param plugin the plugin object
     * @param table the table entity object
     * @param source the source entity object
     * @param configure the table filter object
     * @return the common response object containing the result of the operation
     */
    private CommonResponse<Object> fetchInsert(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            Configure updateConfigure = source.toConfigure(pluginManager, plugin);
            updateConfigure.setFormat("None");
            List<String> allSql = Lists.newArrayList();
            // Gets the auto-increment column for the current row
            List<String> autoIncrementColumns = table.getColumns()
                    .stream()
                    .filter(v -> v.getExtra().toLowerCase().contains("auto_increment"))
                    .map(BaseEntity::getName)
                    .collect(Collectors.toList());
            configure.getNewColumns().forEach(v -> {
                List<SqlColumn> columns = Lists.newArrayList();
                v.entrySet().forEach(entry -> {
                    SqlColumn column = SqlColumn.builder()
                            .column(String.format("`%s`", entry.getKey()))
                            .build();
                    if (entry.getValue() == null) {
                        column.setValue(null);
                    }
                    else {
                        if (autoIncrementColumns.contains(entry.getKey())) {
                            column.setValue(null);
                        }
                        else {
                            column.setValue(String.format("'%s'", StringEscapeUtils.escapeSql(entry.getValue())));
                        }
                    }
                    columns.add(column);
                });
                SqlBody body = SqlBody.builder()
                        .type(SqlType.INSERT)
                        .database(table.getDatabase().getName())
                        .table(table.getName())
                        .columns(columns)
                        .build();
                allSql.add(new SqlBuilder(body).getSql());
            });
            String sql = String.join("\n\n", allSql);
            return CommonResponse.success(getResponse(configure, pluginService, sql, updateConfigure));
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Fetches and updates data from a table using the specified plugin, table, source, and configure.
     *
     * @param plugin the plugin to be used for fetching and updating data
     * @param table the table from which the data will be fetched and updated
     * @param source the source entity containing the data source configuration
     * @param configure the table filter configuration for fetching and updating the data
     * @return the response containing the fetched and updated data
     */
    private CommonResponse<Object> fetchUpdate(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            Configure updateConfigure = source.toConfigure(pluginManager, plugin);
            updateConfigure.setFormat("None");
            List<String> allSql = Lists.newArrayList();
            configure.getColumns().forEach(v -> {
                SqlBody body = SqlBody.builder()
                        .type(SqlType.UPDATE)
                        .database(table.getDatabase().getName())
                        .table(table.getName())
                        .columns(Arrays.asList(v))
                        .where(applyColumns(table, configure, v)
                                .stream()
                                .distinct()
                                .collect(Collectors.toList()))
                        .build();
                allSql.add(new SqlBuilder(body).getSql());
            });
            String sql = String.join("\n\n", allSql);
            return CommonResponse.success(getResponse(configure, pluginService, sql, updateConfigure));
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Fetches and deletes data from the database.
     *
     * @param plugin the plugin to use for connecting to the database
     * @param table the table from which to delete the data
     * @param source the source entity to use for configuring the connection
     * @param configure the table filter to use for filtering the data to be deleted
     * @return the response containing the result of the delete operation
     */
    private CommonResponse<Object> fetchDelete(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            Configure updateConfigure = source.toConfigure(pluginManager, plugin);
            updateConfigure.setFormat("None");
            List<String> allSql = Lists.newArrayList();
            configure.getColumns().forEach(v -> {
                SqlBody body = SqlBody.builder()
                        .type(SqlType.DELETE)
                        .database(table.getDatabase().getName())
                        .table(table.getName())
                        .where(applyColumns(table, configure, v))
                        .build();
                allSql.add(new SqlBuilder(body).getSql());
            });
            String sql = String.join("\n\n", allSql);
            return CommonResponse.success(getResponse(configure, pluginService, sql, updateConfigure));
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Fetches an alter operation for a given plugin, table, source, and table filter.
     *
     * @param plugin the plugin to use for the alter operation
     * @param table the table entity to perform the alter operation on
     * @param source the source entity to configure the alter operation
     * @param configure the table filter to apply to the alter operation
     * @return a CommonResponse object containing the result of the alter operation
     */
    private CommonResponse<Object> fetchAlter(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            Configure alterConfigure = source.toConfigure(pluginManager, plugin);
            alterConfigure.setFormat("None");
            SqlBody body = SqlBody.builder()
                    .type(SqlType.ALTER)
                    .database(table.getDatabase().getName())
                    .table(table.getName())
                    .value(configure.getValue())
                    .build();
            return CommonResponse.success(getResponse(configure, pluginService, new SqlBuilder(body).getSql(), alterConfigure));
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Fetches the "SHOW CREATE TABLE" query for a given table.
     *
     * @param plugin the plugin to use for connecting to the database
     * @param table the table entity for which to fetch the query
     * @param source the source entity containing the database configuration
     * @param configure the table filter configuration
     * @return the common response object containing the result of the query
     */
    private CommonResponse<Object> fetchShowCreateTable(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            Configure alterConfigure = source.toConfigure(pluginManager, plugin);
            alterConfigure.setFormat("None");
            SqlBody body = SqlBody.builder()
                    .type(SqlType.SHOW)
                    .database(table.getDatabase().getName())
                    .table(table.getName())
                    .build();
            return CommonResponse.success(getResponse(configure, pluginService, new SqlBuilder(body).getSql(), alterConfigure));
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Fetches and truncates a table.
     *
     * @param plugin the plugin associated with the table
     * @param table the table entity to fetch and truncate
     * @param source the source entity associated with the table
     * @param configure the table filter configuration
     * @return the common response object containing the fetch and truncate result
     */
    private CommonResponse<Object> fetchTruncateTable(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            Configure alterConfigure = source.toConfigure(pluginManager, plugin);
            alterConfigure.setFormat("None");
            SqlBody body = SqlBody.builder()
                    .type(SqlType.TRUNCATE)
                    .database(table.getDatabase().getName())
                    .table(table.getName())
                    .build();
            return CommonResponse.success(getResponse(configure, pluginService, new SqlBuilder(body).getSql(), alterConfigure));
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Fetches the drop table operation for the given table and source.
     *
     * @param plugin the plugin instance
     * @param table the table entity
     * @param source the source entity
     * @param configure the table filter configuration
     * @return the common response object containing the fetched result
     */
    private CommonResponse<Object> fetchDropTable(Plugin plugin, PluginService pluginService, TableEntity table, SourceEntity source, TableFilter configure)
    {
        try {
            Configure alterConfigure = source.toConfigure(pluginManager, plugin);
            alterConfigure.setFormat("None");
            SqlBody body = SqlBody.builder()
                    .type(SqlType.DROP)
                    .database(table.getDatabase().getName())
                    .table(table.getName())
                    .value(configure.getValue())
                    .build();
            Response response = getResponse(configure, pluginService, new SqlBuilder(body).getSql(), alterConfigure);
            if (!configure.isPreview() && response.getIsSuccessful()) {
                repository.delete(table);
            }
            return CommonResponse.success(response);
        }
        catch (Exception ex) {
            return CommonResponse.failure(ExceptionUtils.getMessage(ex));
        }
    }

    /**
     * Applies the columns of a table to a given configuration.
     *
     * @param table the table entity containing the columns
     * @param configure the table filter configuration to apply the columns to
     * @param column the SQL column to apply
     * @return a list of SQL columns that match the applied configuration
     */
    private List<SqlColumn> applyColumns(TableEntity table, TableFilter configure, SqlColumn column)
    {
        List<SqlColumn> wheres = Lists.newArrayList();
        List<ColumnEntity> originColumns = table.getColumns()
                .stream()
                .filter(item -> item.getIsKey().equals("PRI"))
                .collect(Collectors.toList());
        if (!originColumns.isEmpty()) {
            // If the table contains a primary key, update the data using the primary key as a condition
            originColumns.forEach(item -> wheres.add(SqlColumn.builder()
                    .column(item.getName())
                    .operator(SqlOperator.EQ)
                    .value(String.valueOf(column.getOriginal().get(item.getName())))
                    .build()));
        }
        else {
            // If the current data does not have a primary key, it is updated based on all data columns
            table.getColumns()
                    .forEach(item -> wheres.add(SqlColumn.builder()
                            .column(item.getName())
                            .operator(SqlOperator.EQ)
                            .value(String.valueOf(column.getOriginal().get(item.getName())))
                            .build()));
        }
        return wheres;
    }

    /**
     * Retrieves a response based on the provided table filter, plugin, and SQL query.
     *
     * @param configure the table filter to configure the response
     * @param plugin the plugin to execute the SQL query
     * @param sql the SQL query to execute
     * @return the response containing the result of the SQL query
     */
    private Response getResponse(TableFilter filter, PluginService plugin, String sql, Configure configure)
    {
        Response response;
        if (filter.isPreview()) {
            response = Response.builder()
                    .isSuccessful(true)
                    .isConnected(true)
                    .headers(Lists.newArrayList())
                    .columns(Lists.newArrayList())
                    .types(Lists.newArrayList())
                    .content(sql)
                    .build();
        }
        else {
            response = plugin.execute(configure, sql);
            response.setContent(sql);
        }
        return response;
    }
}
