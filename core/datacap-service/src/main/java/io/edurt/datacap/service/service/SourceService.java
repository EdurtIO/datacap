package io.edurt.datacap.service.service;

import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.service.body.FilterBody;
import io.edurt.datacap.service.body.SharedSourceBody;
import io.edurt.datacap.service.body.SourceBody;
import io.edurt.datacap.service.entity.PageEntity;
import io.edurt.datacap.service.entity.PluginEntity;
import io.edurt.datacap.service.entity.ScheduledHistoryEntity;
import io.edurt.datacap.service.entity.SourceEntity;
import io.edurt.datacap.service.wrapper.ResponseWrapper;

import java.util.List;

public interface SourceService
        extends BaseService<SourceEntity>
{
    CommonResponse<Long> delete(Long id);

    CommonResponse<List<PluginEntity>> getPlugins();

    CommonResponse<Long> count();

    CommonResponse<Object> shared(SharedSourceBody configure);

    CommonResponse<ResponseWrapper> testConnection(SourceBody configure);

    CommonResponse<PageEntity<ScheduledHistoryEntity>> getHistory(String code, FilterBody filter);
}
