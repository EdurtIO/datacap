package io.edurt.datacap.service.service.impl;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.plugin.PluginManager;
import io.edurt.datacap.service.repository.SourceRepository;
import io.edurt.datacap.service.service.MetadataService;
import io.edurt.datacap.spi.PluginService;
import io.edurt.datacap.spi.generator.definition.TableDefinition;
import io.edurt.datacap.spi.model.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"})
public class MetadataServiceImpl
        implements MetadataService
{
    private final PluginManager pluginManager;
    private final SourceRepository repository;

    public MetadataServiceImpl(PluginManager pluginManager, SourceRepository repository)
    {
        this.pluginManager = pluginManager;
        this.repository = repository;
    }

    @Override
    public CommonResponse<Response> getEngines(String code)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            return CommonResponse.success(service.getEngines());
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> getDataTypes(String code)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            return CommonResponse.success(service.getDataTypes());
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> getDatabases(String code)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            return CommonResponse.success(service.getDatabases(entity.toConfigure(pluginManager, plugin)));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> getTables(String code, String database)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            return CommonResponse.success(service.getTablesForDatabase(entity.toConfigure(pluginManager, plugin), database));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> getColumns(String code, String database, String table)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            return CommonResponse.success(service.getColumnsForTable(entity.toConfigure(pluginManager, plugin), database, table));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> getDatabase(String code, String database)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            return CommonResponse.success(service.getDatabaseInfo(entity.toConfigure(pluginManager, plugin), database));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> getTable(String code, String database, String table)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            return CommonResponse.success(service.getTableInfo(entity.toConfigure(pluginManager, plugin), database, table));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> getTableStatement(String code, String database, String table)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            TableDefinition definition = TableDefinition.create(database, table);
                            return CommonResponse.success(service.getTableStatement(entity.toConfigure(pluginManager, plugin), definition));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> updateAutoIncrement(String code, TableDefinition configure, String database, String table)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            TableDefinition definition = TableDefinition.create(database, table)
                                    .autoIncrement(configure.getAutoIncrement());
                            return CommonResponse.success(service.updateAutoIncrement(entity.toConfigure(pluginManager, plugin), definition));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> createTable(String code, String database, TableDefinition configure)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            configure.setDatabase(database);
                            return CommonResponse.success(service.createTable(entity.toConfigure(pluginManager, plugin), configure));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> dropTable(String code, String database, String table, TableDefinition configure)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            TableDefinition definition = TableDefinition.create(database, table);
                            definition.setPreview(configure.isPreview());
                            return CommonResponse.success(service.dropTable(entity.toConfigure(pluginManager, plugin), definition));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<Response> truncateTable(String code, String database, String table, TableDefinition configure)
    {
        return repository.findByCode(code)
                .map(entity -> pluginManager.getPlugin(entity.getType())
                        .map(plugin -> {
                            PluginService service = plugin.getService(PluginService.class);
                            TableDefinition definition = TableDefinition.create(database, table);
                            definition.setPreview(configure.isPreview());
                            return CommonResponse.success(service.truncateTable(entity.toConfigure(pluginManager, plugin), definition));
                        })
                        .orElseGet(() -> CommonResponse.failure(String.format("Plugin [ %s ] not found", entity.getType()))))
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }
}
