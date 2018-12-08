package repository;

import domain.Wagon;
import dto.Page;
import dto.WagonDTO;
import mappers.WagonMapper;
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

public class WagonRepository {

    private static final Logger LOG = LoggerFactory.getLogger(WagonRepository.class);

    private final WagonMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public WagonRepository(WagonMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getWagons(Integer page, Integer size, String query) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getWagonsTotalSize");

            int totalSize = mapper.getWagonsTotalSize(query);

            LOG.debug(">> getWagons");
            return new Page<>(page, size, totalSize, mapper.selectAllWagons(page * size, size, query)
                    .stream()
                    .map(WagonDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<List<Wagon>> getFullWagonsByCustomerId(Long id) {
        LOG.debug(">> getFullWagonsByCustomerId");

        return CompletableFuture.supplyAsync(() -> mapper.selectFullWagonsByCustomerId(id));

    }

    public CompletionStage<Wagon> createWagon(Wagon wagon) {
        LOG.debug(">> createWagon");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertWagon(wagon);
            return wagon;
        }, ec);

    }

    @Transactional
    public CompletionStage<Wagon> updateWagon(Wagon wagon) {
        LOG.debug(">> updateWagon");
        Objects.requireNonNull(wagon);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateWagonById(wagon);
            return wagon;
        }, ec);
    }

    public CompletionStage<Boolean> deleteWagon(Long id) {
        LOG.debug(">> deleteWagon");
        return CompletableFuture.supplyAsync(() ->
                deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final Wagon data = mapper.selectWagonById(id);
        if (data != null) {
            mapper.deleteWagon(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<Wagon> getWagonById(Long id) {
        LOG.debug(">> getWagonById");
        return CompletableFuture.supplyAsync(() -> mapper.selectWagonById(id));
    }

    public Wagon getWagonBySerial(String serial) {
        LOG.debug(">> getWagonBySerial");
        return mapper.selectWagonBySerial(serial);
    }
}
