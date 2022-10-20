package io.edurt.datacap.server.controller.user;

import io.edurt.datacap.server.common.Response;
import io.edurt.datacap.server.entity.PageEntity;
import io.edurt.datacap.server.entity.PluginAuditEntity;
import io.edurt.datacap.server.service.PluginAuditService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping(value = "/api/v1/audit/plugin")
public class PluginAuditController
{
    private final PluginAuditService pluginAuditService;

    public PluginAuditController(PluginAuditService pluginAuditService)
    {
        this.pluginAuditService = pluginAuditService;
    }

    @GetMapping
    public Response<PageEntity<PluginAuditEntity>> getAll(@RequestParam(value = "page", defaultValue = "1") int start,
            @RequestParam(value = "size", defaultValue = "10") int end)
    {
        return this.pluginAuditService.getAll(start, end);
    }
}
