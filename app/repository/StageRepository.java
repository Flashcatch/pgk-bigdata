package repository;

import domain.Stage;
import dto.Page;
import dto.StageDTO;
import mappers.StageMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class StageRepository {

    private static final Logger LOG = LoggerFactory.getLogger(StageRepository.class);

    private final StageMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public StageRepository(StageMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getStages(Integer page, Integer size, Long wagonId) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getStagesTotalSize");

            int totalSize = mapper.getStagesTotalSize();

            LOG.debug(">> selectAllStages");
            return new Page<>(page, size, totalSize, mapper.selectAllStages(page * size, size, wagonId)
                    .stream()
                    .map(StageDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<Stage> createStage(Stage stage) {
        LOG.debug(">> createStage");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertStage(stage);
            return stage;
        }, ec);

    }

    @Transactional
    public CompletionStage<Stage> updateStage(Stage stage) {
        LOG.debug(">> updateStage");
        Objects.requireNonNull(stage);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateStageById(stage);
            return stage;
        }, ec);
    }

    public CompletionStage<Boolean> deleteStage(Long id) {
        LOG.debug(">> deleteStage");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final Stage data = mapper.selectStageById(id);
        if (data != null) {
            mapper.deleteStage(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<Stage> getStageById(Long id) {
        LOG.debug(">> getStageById");
        return CompletableFuture.supplyAsync(() -> mapper.selectStageById(id));
    }
}
