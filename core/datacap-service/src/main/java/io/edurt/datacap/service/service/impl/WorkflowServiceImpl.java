package io.edurt.datacap.service.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.common.utils.NullAwareBeanUtils;
import io.edurt.datacap.executor.ExecutorService;
import io.edurt.datacap.executor.common.RunEngine;
import io.edurt.datacap.executor.common.RunMode;
import io.edurt.datacap.executor.common.RunState;
import io.edurt.datacap.executor.common.RunWay;
import io.edurt.datacap.executor.configure.ExecutorConfigure;
import io.edurt.datacap.executor.configure.ExecutorRequest;
import io.edurt.datacap.executor.configure.ExecutorResponse;
import io.edurt.datacap.plugin.PluginManager;
import io.edurt.datacap.service.common.FolderUtils;
import io.edurt.datacap.service.entity.UserEntity;
import io.edurt.datacap.service.entity.WorkflowEntity;
import io.edurt.datacap.service.initializer.InitializerConfigure;
import io.edurt.datacap.service.itransient.configuration.NodeConfiguration;
import io.edurt.datacap.service.repository.BaseRepository;
import io.edurt.datacap.service.repository.WorkflowRepository;
import io.edurt.datacap.service.security.UserDetailsService;
import io.edurt.datacap.service.service.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"})
public class WorkflowServiceImpl
        implements WorkflowService
{
    private final InitializerConfigure initializer;
    private final PluginManager pluginManager;
    private final Environment environment;
    private final WorkflowRepository repository;

    public WorkflowServiceImpl(InitializerConfigure initializer, PluginManager pluginManager, Environment environment, WorkflowRepository repository)
    {
        this.initializer = initializer;
        this.pluginManager = pluginManager;
        this.environment = environment;
        this.repository = repository;
    }

    @Override
    public CommonResponse<WorkflowEntity> saveOrUpdate(BaseRepository<WorkflowEntity, Long> repository, WorkflowEntity configure)
    {
        log.info("Starting workflow save/update process for executor: {}, code: {}",
                configure.getExecutor(), configure.getCode());
        repository.findByCode(configure.getCode())
                .ifPresent(value -> NullAwareBeanUtils.copyNullProperties(value, configure));

        if (configure.getState() != null && configure.getState().equals(RunState.RUNNING)) {
            return CommonResponse.failure("Workflow is already running");
        }

        UserEntity user = UserDetailsService.getUser();
        String workHome = FolderUtils.getWorkHome(
                initializer.getDataHome(),
                user.getUsername(),
                String.join(File.separator, "workflow", configure.getExecutor().toLowerCase(), configure.getCode())
        );
        log.debug("Created work directory: {}", workHome);

        configure.setState(RunState.CREATED);
        configure.setUser(user);
        configure.setWork(workHome);
        configure.setFrom(null);
        configure.setTo(null);
        WorkflowEntity saved = repository.save(configure);
        log.info("Workflow initialized with state CREATED, code: {}", saved.getCode());

        configure.setState(RunState.RUNNING);
        saved = repository.save(configure);
        log.info("Workflow updated to RUNNING state, code: {}", saved.getCode());

        pluginManager.getPlugin(configure.getExecutor())
                .ifPresentOrElse(
                        plugin -> {
                            log.info("Found plugin for executor: {}", configure.getExecutor());
                            final java.util.concurrent.ExecutorService executorService = Executors.newSingleThreadExecutor();
                            try {
                                Files.createDirectories(Path.of(configure.getWork()));
                                log.debug("Created work directories at: {}", configure.getWork());

                                Optional<NodeConfiguration> inputNode = configure.getConfigure()
                                        .getNodes()
                                        .stream()
                                        .filter(v -> v.getCategory().equalsIgnoreCase("source"))
                                        .findFirst();
                                checkArgument(inputNode.isPresent(), "Source node must not be null");
                                log.debug("Found source node: {}", inputNode.get().getKey());

                                Optional<NodeConfiguration> outputNode = configure.getConfigure()
                                        .getNodes()
                                        .stream()
                                        .filter(v -> v.getCategory().equalsIgnoreCase("sink"))
                                        .findFirst();
                                checkArgument(outputNode.isPresent(), "Sink node must not be null");
                                log.debug("Found sink node: {}", outputNode.get().getKey());

                                Optional<NodeConfiguration> transformNode = configure.getConfigure()
                                        .getNodes()
                                        .stream()
                                        .filter(v -> v.getCategory().equalsIgnoreCase("transform"))
                                        .findFirst();

                                ExecutorConfigure form = new ExecutorConfigure(
                                        inputNode.get().getKey(),
                                        MapUtils.toProperties(inputNode.get().getData()),
                                        Sets.newHashSet()
                                );

                                ExecutorConfigure transform = null;
                                if (transformNode.isPresent()) {
                                    transform = new ExecutorConfigure(
                                            transformNode.get().getKey(),
                                            MapUtils.toProperties(transformNode.get().getData()),
                                            Sets.newHashSet()
                                    );
                                }

                                ExecutorConfigure to = new ExecutorConfigure(
                                        outputNode.get().getKey(),
                                        MapUtils.toProperties(outputNode.get().getData()),
                                        Sets.newHashSet()
                                );

                                String executorHome = environment.getProperty(
                                        String.format("datacap.executor.%s.home",
                                                configure.getExecutor().toLowerCase())
                                );
                                log.debug("Executor home directory: {}", executorHome);

                                ExecutorRequest request = new ExecutorRequest(
                                        configure.getWork(),
                                        executorHome,
                                        configure.getCode(),
                                        user.getUsername(),
                                        form,
                                        to,
                                        RunMode.valueOf(requireNonNull(environment.getProperty("datacap.executor.mode"))),
                                        RunWay.valueOf(requireNonNull(environment.getProperty("datacap.executor.way"))),
                                        environment.getProperty("datacap.executor.startScript"),
                                        RunEngine.valueOf(requireNonNull(environment.getProperty("datacap.executor.engine"))),
                                        transform
                                );
                                log.info("Created executor request for workflow: {}", configure.getCode());

                                Timestamp start = new Timestamp(System.currentTimeMillis());
                                executorService.submit(() -> {
                                    try {
                                        log.info("Starting async execution for workflow: {}", configure.getCode());
                                        ExecutorService pluginService = plugin.getService(ExecutorService.class);

                                        repository.findByCode(configure.getCode())
                                                .ifPresent(finalConfigure -> {
                                                    log.debug("Found workflow by code: {}", configure.getCode());
                                                    initializer.getTaskExecutors().put(finalConfigure.getCode(), executorService);

                                                    log.info("Executing workflow: {}", finalConfigure.getCode());
                                                    ExecutorResponse response = pluginService.start(request);
                                                    log.info("Execution completed with state: {}", response.getState());

                                                    finalConfigure.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                                                    finalConfigure.setState(response.getState());
                                                    finalConfigure.setMessage(response.getMessage());
                                                    finalConfigure.setElapsed(finalConfigure.getUpdateTime().getTime() - start.getTime());

                                                    WorkflowEntity updated = repository.save(finalConfigure);
                                                    log.info("Workflow updated with final state: {}, elapsed time: {} ms",
                                                            updated.getState(), updated.getElapsed());
                                                });
                                    }
                                    catch (Exception e) {
                                        log.error("Error during workflow execution: {}", configure.getCode(), e);
                                    }
                                    finally {
                                        log.info("Cleaning up resources for workflow: {}", configure.getCode());
                                        initializer.getTaskExecutors().remove(configure.getCode());
                                        executorService.shutdownNow();
                                    }
                                });
                            }
                            catch (IOException e) {
                                log.error("Failed to create work directory for workflow: {}", configure.getCode(), e);
                                configure.setState(RunState.FAILURE);
                                configure.setMessage(ExceptionUtils.getStackTrace(e));
                                repository.save(configure);
                                throw new RuntimeException(e);
                            }
                        },
                        () -> {
                            String errorMsg = String.format("Executor [ %s ] not found", configure.getExecutor());
                            log.error(errorMsg);
                            configure.setState(RunState.FAILURE);
                            configure.setMessage(errorMsg);
                            repository.save(configure);
                            throw new RuntimeException(errorMsg);
                        }
                );

        log.info("Workflow process initiated successfully for: {}", configure.getCode());
        return CommonResponse.success(configure);
    }

    @Override
    public CommonResponse<Boolean> stop(String code)
    {
        return (CommonResponse) repository.findByCode(code)
                .map(entity -> {
                    if (entity.getState().equals(RunState.STOPPED)
                            || entity.getState().equals(RunState.FAILURE)
                            || entity.getState().equals(RunState.SUCCESS)
                            || entity.getState().equals(RunState.TIMEOUT)) {
                        return CommonResponse.failure(String.format("Workflow [ %s ] is already stopped", entity.getName()));
                    }

                    java.util.concurrent.ExecutorService service = initializer.getTaskExecutors()
                            .get(entity.getName());
                    if (service != null) {
                        service.shutdownNow();
                    }
                    entity.setState(RunState.STOPPED);
                    entity.setMessage(null);
                    this.repository.save(entity);
                    return CommonResponse.success(true);
                })
                .orElseGet(() -> CommonResponse.failure(String.format("Workflow [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<List<String>> log(String code)
    {
        return repository.findByCode(code)
                .map(entity -> {
                    if (entity.getState().equals(RunState.QUEUE)
                            || entity.getState().equals(RunState.CREATED)) {
                        return CommonResponse.<List<String>>failure(String.format("Workflow [ %s ] is not running", entity.getName()));
                    }

                    List<String> lines = Lists.newArrayList();
                    try (FileInputStream stream = new FileInputStream(String.format("%s/%s.log", entity.getWork(), entity.getCode()))) {
                        lines.addAll(IOUtils.readLines(stream, "UTF-8"));
                        return CommonResponse.success(lines);
                    }
                    catch (IOException e) {
                        log.error("Failed to read pipeline [ {} ] log ", entity.getName(), e);
                        return CommonResponse.<List<String>>failure("Failed to read log file");
                    }
                })
                .orElse(CommonResponse.failure(String.format("Workflow [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<String> deleteByCode(BaseRepository<WorkflowEntity, Long> repository, String code)
    {
        return repository.findByCode(code)
                .map(entity -> {
                    if (entity.getState().equals(RunState.RUNNING)) {
                        return CommonResponse.<String>failure(String.format("Workflow [ %s ] is already running", entity.getName()));
                    }

                    repository.delete(entity);
                    try {
                        FileUtils.deleteDirectory(new File(entity.getWork()));
                    }
                    catch (IOException e) {
                        log.warn("Failed to delete work directory", e);
                    }
                    return CommonResponse.success(code);
                })
                .orElseGet(() -> CommonResponse.failure(String.format("Resource [ %s ] not found", code)));
    }

    @Override
    public CommonResponse<String> restart(String code)
    {
        return repository.findByCode(code)
                .map(entity -> {
                    if (entity.getState().equals(RunState.RUNNING)) {
                        return CommonResponse.<String>failure(String.format("Workflow [ %s ] is already running", entity.getName()));
                    }

                    CommonResponse<WorkflowEntity> startResponse = saveOrUpdate(repository, entity);
                    if (startResponse.getStatus()) {
                        return CommonResponse.success(code);
                    }
                    else {
                        return CommonResponse.<String>failure(startResponse.getMessage().toString());
                    }
                })
                .orElse(CommonResponse.failure(String.format("Workflow [ %s ] not found", code)));
    }
}
