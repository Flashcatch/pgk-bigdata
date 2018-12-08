package repository;

import domain.WagonSide;
import domain.mobile.WagonTypeMobile;
import dto.Page;
import dto.WagonSideDTO;
import mappers.WagonSideMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class WagonSideRepository {

    private static final Logger LOG = LoggerFactory.getLogger(WagonSideRepository.class);

    private final WagonSideMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public WagonSideRepository(WagonSideMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getWagonSides(Integer page, Integer size, Long wagonTypeId, String query) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getWagonSidesTotalSize");

            int totalSize = mapper.getWagonSidesTotalSize(wagonTypeId, query);

            LOG.debug(">> selectAllWagonSides");
            return new Page(page, size, totalSize, mapper.selectAllWagonSides(page * size, size, wagonTypeId, query)
                    .stream()
                    .map(WagonSideDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<WagonSide> createWagonSide(WagonSide wagonSide) {
        LOG.debug(">> createWagonSide");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertWagonSide(wagonSide);
            return wagonSide;
        }, ec);

    }

    @Transactional
    public CompletionStage<WagonSide> updateWagonSide(WagonSide wagonSide) {
        LOG.debug(">> updateWagonSide");
        Objects.requireNonNull(wagonSide);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateWagonSideById(wagonSide);
            return wagonSide;
        }, ec);
    }

    public CompletionStage<Boolean> deleteWagonSide(Long id) {
        LOG.debug(">> deleteWagonSide");
        return CompletableFuture.supplyAsync(() ->
                deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final WagonSide data = mapper.selectWagonSideById(id);
        if (data != null) {
            mapper.deleteWagonSide(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<WagonSide> getWagonSideById(Long id) {
        LOG.debug(">> getWagonSideById");
        return CompletableFuture.supplyAsync(() -> mapper.selectWagonSideById(id));
    }

    public CompletionStage<Optional<WagonTypeMobile>> getWagonSidesMobileBySerial(String serial) {
        LOG.debug(">> getWagonSidesMobileBySerial");
        WagonTypeMobile zz = mapper.selectWagonSideMobileBySerial(serial);
        return CompletableFuture.supplyAsync(() -> Optional.ofNullable(mapper.selectWagonSideMobileBySerial(serial)), ec);
    }

    public CompletionStage<List<WagonTypeMobile>> getWagonSidesMobileWithTypes() {
        LOG.debug(">> getWagonSidesMobileWithTypes");
        return CompletableFuture.supplyAsync(() -> mapper.selectWagonSidesMobileWithTypes());
    }

    public Long getValueIdByValue(Long value, Long sideId) {
        LOG.debug(">> getValueIdByValue");
        return mapper.selectValueIdByValue(value, sideId);
    }
}
