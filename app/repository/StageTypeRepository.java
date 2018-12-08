package repository;

import domain.StageType;
import dto.Page;
import dto.StageTypeDTO;
import mappers.StageTypeMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class StageTypeRepository {

    private static final Logger LOG = LoggerFactory.getLogger(StageTypeRepository.class);

    private final StageTypeMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public StageTypeRepository(StageTypeMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getStageTypes(Integer page, Integer size, String query) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getStageTypesTotalSize");

            int totalSize = mapper.getStageTypesTotalSize(query);

            LOG.debug(">> selectAllStageTypes");
            return new Page<>(page, size, totalSize, mapper.selectAllStageTypes(page * size, size, query)
                    .stream()
                    .map(StageTypeDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<StageType> createStageType(StageType stageType) {
        LOG.debug(">> createStageType");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertStageType(stageType);
            return stageType;
        }, ec);

    }

    @Transactional
    public CompletionStage<StageType> updateStageType(StageType stageType) {
        LOG.debug(">> updateStageType");
        Objects.requireNonNull(stageType);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateStageTypeById(stageType);
            return stageType;
        }, ec);
    }

    public CompletionStage<Boolean> deleteStageType(Long id) {
        LOG.debug(">> deleteStageType");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final StageType data = mapper.selectStageTypeById(id);
        if (data != null) {
            mapper.deleteStageType(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<StageType> getStageTypeById(Long id) {
        LOG.debug(">> getStageTypeById");
        return CompletableFuture.supplyAsync(() -> mapper.selectStageTypeById(id));
    }
}
