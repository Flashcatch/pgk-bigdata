package repository;

import domain.ChecklistInspection;
import dto.ChecklistInspectionDTO;
import dto.Page;
import mappers.ChecklistInspectionMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class ChecklistInspectionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(ChecklistInspectionRepository.class);

    private final ChecklistInspectionMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public ChecklistInspectionRepository(ChecklistInspectionMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getChecklistInspections(Integer page, Integer size) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getChecklistInspectionsTotalSize");

            int totalSize = mapper.getChecklistInspectionsTotalSize();

            LOG.debug(">> selectAllChecklistInspections");
            return new Page(page, size, totalSize, mapper.selectAllChecklistInspections(page * size, size)
                    .stream()
                    .map(ChecklistInspectionDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<ChecklistInspection> createChecklistInspection(ChecklistInspection checklistInspection) {
        LOG.debug(">> createChecklistInspection");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertChecklistInspection(checklistInspection);
            return checklistInspection;
        }, ec);

    }

    @Transactional
    public CompletionStage<ChecklistInspection> updateChecklistInspection(ChecklistInspection checklistInspection) {
        LOG.debug(">> updateChecklistInspection");
        Objects.requireNonNull(checklistInspection);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateChecklistInspectionById(checklistInspection);
            return checklistInspection;
        }, ec);
    }

    public CompletionStage<Boolean> deleteChecklistInspection(Long id) {
        LOG.debug(">> deleteChecklistInspection");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final ChecklistInspection data = mapper.selectChecklistInspectionById(id);
        if (data != null) {
            mapper.deleteChecklistInspection(id);
            result = true;
        }
        return result;
    }
}
