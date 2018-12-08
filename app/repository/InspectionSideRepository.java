package repository;

import domain.InspectionSide;
import dto.InspectionSideDTO;
import dto.Page;
import mappers.InspectionSideMapper;
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

public class InspectionSideRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InspectionSideRepository.class);

    private final InspectionSideMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public InspectionSideRepository(InspectionSideMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getInspectionSides(Integer page, Integer size, String query, Long inspectionId) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getInspectionSidesTotalSize");

            int totalSize = mapper.getInspectionSidesTotalSize(query);

            LOG.debug(">> selectAllInspectionSides");
            return new Page(page, size, totalSize, mapper.selectAllInspectionSides(page * size, size, query, inspectionId)
                    .stream()
                    .map(InspectionSideDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public InspectionSide createInspectionSide(InspectionSide inspectionSide) {
        LOG.debug(">> createInspectionSide");

       // return CompletableFuture.supplyAsync(() -> {
            mapper.insertInspectionSide(inspectionSide);
            return inspectionSide;
       // }, ec);

    }

    @Transactional
    public CompletionStage<InspectionSide> updateInspectionSide(InspectionSide inspectionSide) {
        LOG.debug(">> updateInspectionSide");
        Objects.requireNonNull(inspectionSide);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateInspectionSideById(inspectionSide);
            return inspectionSide;
        }, ec);
    }

    public CompletionStage<Boolean> deleteInspectionSide(Long id) {
        LOG.debug(">> deleteInspectionSide");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final InspectionSide data = mapper.selectInspectionSideById(id);
        if (data != null) {
            mapper.deleteInspectionSide(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<InspectionSide> getInspectionSideById(Long id) {
        LOG.debug(">> getInspectionSideById");
        return CompletableFuture.supplyAsync(() -> mapper.selectInspectionSideById(id));
    }

    public CompletionStage<List<InspectionSideDTO>> getFullInspectionSidesBySerial(String serial) {
        LOG.debug(">> getFullInspectionSidesBySerial");
        return CompletableFuture.supplyAsync(()->mapper.selectFullInspectionSidesBySerial(serial).stream().map(InspectionSideDTO::new).collect(Collectors.toList()));
    }

    public Long getInspectionSideByParams (Long inspectionId, Long sideId) {
        LOG.debug(">> getInspectionSideByParams");
        return mapper.selectInspectionSideByParams(inspectionId, sideId);
    }
}
