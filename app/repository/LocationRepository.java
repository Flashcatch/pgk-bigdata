package repository;

import domain.Location;
import dto.LocationDTO;
import dto.Page;
import mappers.LocationMapper;
import org.mybatis.guice.transactional.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class LocationRepository {

    private static final Logger LOG = LoggerFactory.getLogger(LocationRepository.class);

    private final LocationMapper mapper;
    private final ExecutionContextExecutor ec;

    @Inject
    public LocationRepository(LocationMapper mapper, ExecutionContextExecutor ec) {
        this.mapper = mapper;
        this.ec = ec;
    }

    public CompletionStage<Page> getLocations(Integer page, Integer size, String query, Long wagonId) {
        return CompletableFuture.supplyAsync(() -> {
            LOG.debug(">> getLocationsTotalSize");

            int totalSize = mapper.getLocationsTotalSize(query);

            LOG.debug(">> getLocations");
            return new Page<>(page, size, totalSize, mapper.selectAllLocations(page * size, size, query, wagonId)
                    .stream()
                    .map(LocationDTO::new)
                    .collect(Collectors.toList()));
        }, ec);
    }

    public CompletionStage<Location> createLocation(Location location) {
        LOG.debug(">> createLocation");

        return CompletableFuture.supplyAsync(() -> {
            mapper.insertLocation(location);
            return location;
        }, ec);

    }

    @Transactional
    public CompletionStage<Location> updateLocation(Location location) {
        LOG.debug(">> updateLocation");
        Objects.requireNonNull(location);
        return CompletableFuture.supplyAsync(() -> {
            mapper.updateLocationById(location);
            return location;
        }, ec);
    }

    public CompletionStage<Boolean> deleteLocation(Long id) {
        LOG.debug(">> deleteLocation");
        return CompletableFuture.supplyAsync(() ->
                        deleteMapper(id), ec);
    }

    private Boolean deleteMapper(Long id) {
        LOG.debug(">> deleteMapper");
        Boolean result = false;
        final Location data = mapper.selectLocationById(id);
        if (data != null) {
            mapper.deleteLocation(id);
            result = true;
        }
        return result;
    }

    public CompletionStage<Location> getLocationById(Long id) {
        LOG.debug(">> getLocationById");
        return CompletableFuture.supplyAsync(() -> mapper.selectLocationById(id));
    }
}
