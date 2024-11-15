package io.edurt.datacap.server.controller;

import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.service.body.ExportBody;
import io.edurt.datacap.service.body.TableBody;
import io.edurt.datacap.service.body.TableFilter;
import io.edurt.datacap.service.entity.TableEntity;
import io.edurt.datacap.service.repository.metadata.TableRepository;
import io.edurt.datacap.service.service.TableService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(value = "/api/v1/table")
public class TableController
        extends BaseController<TableEntity>
{
    private final TableRepository repository;
    private final TableService service;

    public TableController(TableRepository repository, TableService service)
    {
        super(repository, service);
        this.repository = repository;
        this.service = service;
    }

    @PostMapping(value = "database/{code}")
    public CommonResponse<List<TableEntity>> fetchByDatabase(@PathVariable String code)
    {
        return this.service.getAllByDatabase(code);
    }

    @RequestMapping(value = "{code}", method = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public CommonResponse<Object> fetchDataById(@PathVariable String code, @RequestBody TableFilter configure)
    {
        return this.service.fetchData(code, configure);
    }

    @PostMapping(value = "export/{code}")
    public CommonResponse exportDataByCode(@PathVariable String code, @RequestBody ExportBody configure)
    {
        return this.service.exportDataByCode(code, configure);
    }

    @GetMapping(value = "dataDownload/{username}/{filename}")
    public Object dataDownload(@PathVariable String username, @PathVariable String filename)
    {
        return this.service.dataDownload(username, filename);
    }

    @PostMapping(value = "createTable/{id}")
    public CommonResponse<Object> createTable(@PathVariable Long id, @RequestBody TableBody configure)
    {
        return this.service.createTable(id, configure);
    }

    @PostMapping(value = "manageColumn/{code}")
    public CommonResponse<Object> manageColumn(@PathVariable String code, @RequestBody TableBody configure)
    {
        return this.service.manageColumn(code, configure);
    }
}
