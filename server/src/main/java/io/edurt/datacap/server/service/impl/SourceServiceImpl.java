package io.edurt.datacap.server.service.impl;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import io.edurt.datacap.server.adapter.PageRequestAdapter;
import io.edurt.datacap.server.body.SharedSourceBody;
import io.edurt.datacap.server.common.JSON;
import io.edurt.datacap.server.common.PluginCommon;
import io.edurt.datacap.server.common.Response;
import io.edurt.datacap.server.common.ServiceState;
import io.edurt.datacap.server.entity.PageEntity;
import io.edurt.datacap.server.entity.PluginEntity;
import io.edurt.datacap.server.entity.SourceEntity;
import io.edurt.datacap.server.entity.UserEntity;
import io.edurt.datacap.server.repository.SourceRepository;
import io.edurt.datacap.server.repository.UserRepository;
import io.edurt.datacap.server.security.UserDetailsService;
import io.edurt.datacap.server.service.SourceService;
import io.edurt.datacap.spi.FormatType;
import io.edurt.datacap.spi.Plugin;
import io.edurt.datacap.spi.PluginType;
import io.edurt.datacap.spi.model.Configure;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SourceServiceImpl
        implements SourceService
{
    private final SourceRepository sourceRepository;
    private final UserRepository userRepository;
    private final Injector injector;

    public SourceServiceImpl(SourceRepository sourceRepository, UserRepository userRepository, Injector injector)
    {
        this.sourceRepository = sourceRepository;
        this.userRepository = userRepository;
        this.injector = injector;
    }

    @Override
    public Response<SourceEntity> saveOrUpdate(SourceEntity configure)
    {
        configure.setConfigure(JSON.toJSON(configure.getConfigures()));
        configure.setUser(UserDetailsService.getUser());
        return Response.success(this.sourceRepository.save(configure));
    }

    @Override
    public Response<PageEntity<SourceEntity>> getAll(int offset, int limit)
    {
        Pageable pageable = PageRequestAdapter.of(offset, limit);
        UserEntity user = UserDetailsService.getUser();
        return Response.success(PageEntity.build(this.sourceRepository.findAllByUserOrPublishIsTrue(user, pageable)));
    }

    @Override
    public Response<Long> delete(Long id)
    {
        this.sourceRepository.deleteById(id);
        return Response.success(id);
    }

    @Override
    public Response<Object> testConnection(SourceEntity configure)
    {
        Optional<Plugin> pluginOptional = PluginCommon.getPluginByName(this.injector, configure.getType());
        if (!pluginOptional.isPresent()) {
            return Response.failure(ServiceState.PLUGIN_NOT_FOUND);
        }

        Configure _configure = new Configure();
        Plugin plugin = pluginOptional.get();
        _configure.setHost(configure.getHost());
        _configure.setPort(configure.getPort());
        _configure.setUsername(Optional.ofNullable(configure.getUsername()));
        _configure.setPassword(Optional.ofNullable(configure.getPassword()));
        Optional<String> _database = StringUtils.isNotEmpty(configure.getDatabase()) ? Optional.ofNullable(configure.getDatabase()) : Optional.empty();
        _configure.setDatabase(_database);
        _configure.setEnv(Optional.ofNullable(configure.getConfigures()));
        _configure.setSsl(Optional.ofNullable(configure.getSsl()));
        _configure.setFormat(FormatType.JSON);
        plugin.connect(_configure);
        io.edurt.datacap.spi.model.Response response = plugin.execute(plugin.validator());
        plugin.destroy();
        if (response.getIsSuccessful()) {
            return Response.success(response);
        }
        return Response.failure(ServiceState.PLUGIN_EXECUTE_FAILED, response.getMessage());
    }

    @Override
    public Response<SourceEntity> getById(Long id)
    {
        return Response.success(this.sourceRepository.findById(id));
    }

    @Override
    public Response<List<PluginEntity>> getPlugins()
    {
        List<PluginEntity> plugins = this.injector.getInstance(Key.get(new TypeLiteral<Set<Plugin>>() {}))
                .stream()
                .filter(plugin -> plugin.type().equals(PluginType.SOURCE))
                .map(plugin -> {
                    PluginEntity entity = new PluginEntity();
                    entity.setName(plugin.name());
                    entity.setDescription(plugin.description());
                    return entity;
                })
                .collect(Collectors.toList());
        return Response.success(plugins);
    }

    @Override
    public Response<Long> count()
    {
        return Response.success(this.sourceRepository.countByUserOrPublishIsTrue(UserDetailsService.getUser()));
    }

    @Override
    public Response<Object> shared(SharedSourceBody configure)
    {
        Optional<SourceEntity> sourceOptional = this.sourceRepository.findById(configure.getSourceId());
        if (!sourceOptional.isPresent()) {
            return Response.failure(ServiceState.SOURCE_NOT_FOUND);
        }

        Optional<UserEntity> userOptional = this.userRepository.findById(configure.getUserId());
        if (!userOptional.isPresent()) {
            return Response.failure(ServiceState.USER_NOT_FOUND);
        }

        SourceEntity source = sourceOptional.get();
        source.setUser(userOptional.get());
        source.setPublish(configure.getPublish());
        return Response.success(this.sourceRepository.save(source));
    }
}
