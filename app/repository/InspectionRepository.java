package repository;

import domain.Inspection;
import domain.mobile.InspectionCardMobile;
import dto.InspectionDTO;
import dto.Page;
import dto.mobile.InspectionCardDTO;
import mappers.InspectionMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class InspectionRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InspectionRepository.class);

    private final InspectionMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public InspectionRepository(InspectionMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getInspections(Integer page, Integer size, String query, Long wagonId) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getInspectionsTotalSize");

            int totalSize = mapper.getInspectionsTotalSize(query);

            LOG.debug(">> selectAllInspections");
            return new Page<>(page, size, totalSize, mapper.selectAllInspections(page * size, size, query, wagonId)
                    .stream()
                    .map(InspectionDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<Page> getInspectionsCards(Integer page, Integer size){
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getInspectionsCardsTotalSize");

            int totalSize = mapper.selectInspectionsCardsTotalSize();

            LOG.debug(">> getInspectionsCards");
            return new Page<>(page, size, totalSize, mapper.selectInspectionsCards(page*size, size)
            .stream()
            .map(InspectionCardDTO::new)
            .collect(Collectors.toList()));
        }, ec);
    }

    public Inspection createInspection(Inspection inspection) {
        LOG.debug(">> createInspection");

       // return CompletableFuture.supplyAsync(() -> {
            mapper.insertInspection(inspection);
            return inspection;
       // }, ec);

    }

    @Transactional
    public CompletionStage<Inspection> updateInspection(Inspection inspection) {
        LOG.debug(">> updateInspection");
        Objects.requireNonNull(inspection);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateInspectionById(inspection);
            return inspection;
        }, ec);
    }

    public void finalizeInspection(Long id) {
        LOG.debug(">> finalizeInspection");
        mapper.finalizeInspection(id);
    }

    public CompletionStage<Boolean> deleteInspection(Long id) {
        LOG.debug(">> deleteInspection");
        return CompletableFuture.supplyAsync(() ->
                deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final Inspection data = mapper.selectInspectionById(id);
        if (data != null) {
            mapper.deleteInspection(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<Inspection> getInspectionById(Long id) {
        LOG.debug(">> getInspectionById");
        return CompletableFuture.supplyAsync(() -> mapper.selectInspectionById(id));
    }

    public Inspection getFullInspectionById(Long id) {
        LOG.debug(">> getFullInspectionById");
        return  mapper.selectFullInspectionById(id);
    }

    public CompletionStage<List<Inspection>> getFullInspectionsByWagonId(Long id) {
        LOG.debug(">> getFullInspectionsByWagonId");
        return CompletableFuture.supplyAsync(()->mapper.selectFullInspectionsByWagonId(id));
    }

    public CompletionStage<List<Inspection>> getFullInspectionsByCustomerId(Long id) {
        LOG.debug(">> getFullInspectionsByCustomerId");
        return CompletableFuture.supplyAsync(()->mapper.selectFullInspectionsByCustomerId(id));
    }

    public CompletionStage<Boolean> deleteInspectionsByIds(List<Long> ids) {
        LOG.debug(">> deleteInspectionsByIds");
        mapper.deleteInspectionsByIds(ids);
        return CompletableFuture.supplyAsync(() -> true);
    }

    public CompletionStage<InspectionCardMobile> getInspectionCardMobileById(Long id){
        return CompletableFuture.supplyAsync(()->mapper.selectInspectionCardMobile(id));
    }
}
