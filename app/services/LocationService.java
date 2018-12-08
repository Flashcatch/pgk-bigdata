package services;

import domain.Location;
import dto.LocationDTO;
import dto.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.LocationRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class LocationService {
    private static final Logger LOG = LoggerFactory.getLogger(LocationService.class);

    private LocationRepository repository;

    @Inject
    public LocationService(LocationRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getLocations (Integer page, Integer size, String query, Long wagonId) {
        return repository.getLocations(page, size, query, wagonId);
    }

    public CompletionStage<Location> createLocation (LocationDTO location) {
        final Location data = location.instanceOf();
        return repository.createLocation(data);
    }

    public CompletionStage<Location> updateLocation (LocationDTO dto) {
        final Location data = dto.instanceOf();
        return repository.updateLocation(data);
    }

    public CompletionStage<Boolean> deleteLocation (Long id) {
        return repository.deleteLocation(id);
    }

    public CompletionStage<Location> getLocationById (Long id) {
        return repository.getLocationById(id);
    }
}
