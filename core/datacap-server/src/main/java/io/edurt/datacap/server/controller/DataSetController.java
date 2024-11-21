package io.edurt.datacap.server.controller;

import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.plugin.PluginMetadata;
import io.edurt.datacap.service.body.FilterBody;
import io.edurt.datacap.service.body.adhoc.Adhoc;
import io.edurt.datacap.service.entity.DataSetColumnEntity;
import io.edurt.datacap.service.entity.DataSetEntity;
import io.edurt.datacap.service.repository.DataSetRepository;
import io.edurt.datacap.service.service.DataSetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController()
@RequestMapping(value = "/api/v1/dataset")
public class DataSetController
        extends BaseController<DataSetEntity>
{
    private final DataSetRepository repository;
    private final DataSetService service;

    protected DataSetController(DataSetRepository repository, DataSetService service)
    {
        super(repository, service);
        this.repository = repository;
        this.service = service;
    }

    @RequestMapping(value = "create", method = {RequestMethod.POST, RequestMethod.PUT})
    public CommonResponse save(@RequestBody DataSetEntity configure)
    {
        return service.saveOrUpdate(configure);
    }

    @PutMapping(value = "rebuild/{id}")
    public CommonResponse rebuild(@PathVariable Long id)
    {
        return service.rebuild(id);
    }

    @GetMapping(value = "columns/{code}")
    public CommonResponse<Set<DataSetColumnEntity>> getColumnsByCode(@PathVariable String code)
    {
        return service.getColumnsByCode(code);
    }

    @PutMapping(value = "syncData/{id}")
    public CommonResponse<Boolean> syncData(@PathVariable Long id)
    {
        return service.syncData(id);
    }

    @PutMapping(value = "clearData/{code}")
    public CommonResponse<Boolean> clearData(@PathVariable String code)
    {
        return service.clearData(code);
    }

    @PostMapping(value = "adhoc/{code}")
    public CommonResponse<Object> adhoc(@PathVariable String code,
            @RequestBody Adhoc configure)
    {
        return service.adhoc(code, configure);
    }

    @PostMapping(value = "history/{code}")
    public CommonResponse<Object> history(@PathVariable String code,
            @RequestBody FilterBody filter)
    {
        return this.service.getHistory(code, filter);
    }

    @GetMapping(value = "getActuators")
    public CommonResponse<Set<PluginMetadata>> getActuators()
    {
        return service.getActuators();
    }
}
