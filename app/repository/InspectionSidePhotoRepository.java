package repository;

import domain.InspectionSidePhoto;
import dto.InspectionSidePhotoDTO;
import dto.Page;
import mappers.InspectionSidePhotoMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class InspectionSidePhotoRepository {

    private static final Logger LOG = LoggerFactory.getLogger(InspectionSidePhotoRepository.class);

    private final InspectionSidePhotoMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public InspectionSidePhotoRepository(InspectionSidePhotoMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getInspectionSidePhotos(Integer page, Integer size) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getInspectionSidePhotosTotalSize");

            int totalSize = mapper.getInspectionSidePhotosTotalSize();

            LOG.debug(">> selectAllInspectionSidePhotos");
            return new Page(page, size, totalSize, mapper.selectAllInspectionSidePhotos(page * size, size)
                    .stream()
                    .map(InspectionSidePhotoDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public void createInspectionSidePhoto(InspectionSidePhoto inspectionSidePhoto) {
        LOG.debug(">> createInspectionSidePhoto");

            mapper.insertInspectionSidePhoto(inspectionSidePhoto);

    }

    @Transactional
    public CompletionStage<InspectionSidePhoto> updateInspectionSidePhoto(InspectionSidePhoto inspectionSidePhoto) {
        LOG.debug(">> updateInspectionSidePhoto");
        Objects.requireNonNull(inspectionSidePhoto);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateInspectionSidePhotoById(inspectionSidePhoto);
            return inspectionSidePhoto;
        }, ec);
    }

    public CompletionStage<Boolean> deleteInspectionSidePhoto(Long id) {
        LOG.debug(">> deleteInspectionSidePhoto");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final InspectionSidePhoto data = mapper.selectInspectionSidePhotoById(id);
        if (data != null) {
            mapper.deleteInspectionSidePhoto(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<InspectionSidePhoto> getInspectionSidePhotoById(Long id) {
        LOG.debug(">> getInspectionSidePhotoById");
        return CompletableFuture.supplyAsync(() -> mapper.selectInspectionSidePhotoById(id));
    }
}
