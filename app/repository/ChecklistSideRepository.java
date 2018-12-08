package repository;

import domain.ChecklistSide;
import dto.ChecklistSideDTO;
import dto.Page;
import mappers.ChecklistSideMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ChecklistSideRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ChecklistSideRepository.class);

    private final ChecklistSideMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public ChecklistSideRepository(ChecklistSideMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getChecklistSides(Integer page, Integer size, Long wagonSideId) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getChecklistSidesTotalSize");

            int totalSize = mapper.getChecklistSidesTotalSize(wagonSideId);

            LOG.debug(">> selectAllChecklistSides");
            return new Page(page, size, totalSize, mapper.selectAllChecklistSides(page * size, size, wagonSideId)
                    .stream()
                    .map(ChecklistSideDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<ChecklistSide> createChecklistSide(ChecklistSide checklistSide) {
        LOG.debug(">> createChecklistSide");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertChecklistSide(checklistSide);
            return checklistSide;
        }, ec);

    }

    @Transactional
    public CompletionStage<ChecklistSide> updateChecklistSide(ChecklistSide checklistSide) {
        LOG.debug(">> updateChecklistSide");
        Objects.requireNonNull(checklistSide);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateChecklistSideById(checklistSide);
            return checklistSide;
        }, ec);
    }

    public CompletionStage<Boolean> deleteChecklistSide(Long id) {
        LOG.debug(">> deleteChecklistSide");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final ChecklistSide data = mapper.selectChecklistSideById(id);
        if (data != null) {
            mapper.deleteChecklistSide(id);
            result = true;
        }
        return result;
    }
}
