package repository;

import domain.WagonType;
import dto.Page;
import dto.WagonTypeDTO;
import mappers.WagonTypeMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class WagonTypeRepository {

    private static final Logger LOG = LoggerFactory.getLogger(WagonTypeRepository.class);

    private final WagonTypeMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public WagonTypeRepository(WagonTypeMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getWagonTypes(Integer page, Integer size, String query) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getWagonTypesTotalSize");

            int totalSize = mapper.getWagonTypesTotalSize(query);

            LOG.debug(">> selectAllWagonTypes");
            return new Page<>(page, size, totalSize, mapper.selectAllWagonTypes(page * size, size, query)
                    .stream()
                    .map(WagonTypeDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<WagonType> createWagonType(WagonType wagonType) {
        LOG.debug(">> createWagonType");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertWagonType(wagonType);
            return wagonType;
        }, ec);

    }

    @Transactional
    public CompletionStage<WagonType> updateWagonType(WagonType wagonType) {
        LOG.debug(">> updateWagonType");
        Objects.requireNonNull(wagonType);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateWagonTypeById(wagonType);
            return wagonType;
        }, ec);
    }

    public CompletionStage<Boolean> deleteWagonType(Long id) {
        LOG.debug(">> deleteWagonType");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final WagonType data = mapper.selectWagonTypeById(id);
        if (data != null) {
            mapper.deleteWagonType(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<WagonType> getWagonTypeById(Long id) {
        LOG.debug(">> getWagonTypeById");
        return CompletableFuture.supplyAsync(() -> mapper.selectWagonTypeById(id));
    }
}
