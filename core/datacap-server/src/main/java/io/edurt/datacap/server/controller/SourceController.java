package io.edurt.datacap.server.controller;

import com.fasterxml.jackson.annotation.JsonView;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.common.view.EntityView;
import io.edurt.datacap.service.body.FilterBody;
import io.edurt.datacap.service.body.SourceBody;
import io.edurt.datacap.service.entity.PageEntity;
import io.edurt.datacap.service.entity.PluginEntity;
import io.edurt.datacap.service.entity.ScheduledHistoryEntity;
import io.edurt.datacap.service.entity.SourceEntity;
import io.edurt.datacap.service.repository.SourceRepository;
import io.edurt.datacap.service.service.SourceService;
import io.edurt.datacap.service.validation.ValidationGroup;
import io.edurt.datacap.service.wrapper.ResponseWrapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController(value = "sourceControllerV2")
@RequestMapping(value = "/api/v2/source")
@SuppressFBWarnings(value = {"EI_EXPOSE_REP2"})
public class SourceController
        extends BaseController<SourceEntity>
{
    private final SourceRepository repository;
    private final SourceService service;

    public SourceController(SourceRepository repository, SourceService service)
    {
        super(repository, service);
        this.repository = repository;
        this.service = service;
    }

    @PostMapping(value = "test")
    @JsonView(value = {EntityView.UserView.class})
    public CommonResponse<ResponseWrapper> testConnection(@RequestBody @Validated(ValidationGroup.Crud.Create.class) SourceBody configure)
    {
        return this.service.testConnection(configure);
    }

    @PostMapping(value = "getHistory/{code}")
    public CommonResponse<PageEntity<ScheduledHistoryEntity>> getHistory(@PathVariable(value = "code") String code, @RequestBody FilterBody filter)
    {
        return this.service.getHistory(code, filter);
    }

    @GetMapping(value = "plugins")
    public CommonResponse<List<PluginEntity>> getPlugins()
    {
        return service.getPlugins();
    }
}
