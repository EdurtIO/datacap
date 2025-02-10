package io.edurt.datacap.service.service.impl;

import io.edurt.datacap.common.response.CommonResponse;
import io.edurt.datacap.service.adapter.PageRequestAdapter;
import io.edurt.datacap.service.body.FilterBody;
import io.edurt.datacap.service.entity.PageEntity;
import io.edurt.datacap.service.entity.ReportEntity;
import io.edurt.datacap.service.repository.BaseRepository;
import io.edurt.datacap.service.repository.DataSetRepository;
import io.edurt.datacap.service.repository.ReportRepository;
import io.edurt.datacap.service.repository.SourceRepository;
import io.edurt.datacap.service.security.UserDetailsService;
import io.edurt.datacap.service.service.ReportService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ReportServiceImpl
        implements ReportService
{
    private final ReportRepository repository;
    private final SourceRepository sourceRepository;
    private final DataSetRepository dataSetRepository;

    public ReportServiceImpl(ReportRepository repository, SourceRepository sourceRepository, DataSetRepository dataSetRepository)
    {
        this.repository = repository;
        this.sourceRepository = sourceRepository;
        this.dataSetRepository = dataSetRepository;
    }

    @Override
    public CommonResponse<PageEntity<ReportEntity>> getAll(FilterBody filter)
    {
        Pageable pageable = PageRequestAdapter.of(filter);
        return CommonResponse.success(PageEntity.build(repository.findAllByUser(UserDetailsService.getUser(), pageable)));
    }

    @Override
    public CommonResponse<ReportEntity> saveOrUpdate(BaseRepository<ReportEntity, Long> repository, ReportEntity configure)
    {
        if (configure.getSource() != null) {
            return sourceRepository.findByCode(configure.getSource().getCode())
                    .map(value -> {
                        configure.setUser(UserDetailsService.getUser());
                        configure.setSource(value);
                        return CommonResponse.success(repository.save(configure));
                    })
                    .orElseGet(() -> CommonResponse.failure(String.format("Source [ %s ] not found", configure.getSource().getCode())));
        }
        else {
            return dataSetRepository.findByCode(configure.getDataset().getCode())
                    .map(value -> {
                        configure.setUser(UserDetailsService.getUser());
                        configure.setDataset(value);
                        return CommonResponse.success(repository.save(configure));
                    })
                    .orElseGet(() -> CommonResponse.failure(String.format("Source [ %s ] not found", configure.getSource().getCode())));
        }
    }
}
