package services;

import domain.Wagon;
import dto.Page;
import dto.WagonDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.WagonRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class WagonService {
    private static final Logger LOG = LoggerFactory.getLogger(WagonService.class);

    private WagonRepository repository;

    @Inject
    public WagonService(WagonRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getWagons (Integer page, Integer size, String query) {
        return repository.getWagons(page, size, query);
    }

    public CompletionStage<List<WagonDTO>> getFullWagonsByCustomerId(Long id) {
        LOG.debug(">> getFullWagonsByCustomerId");
        return repository.getFullWagonsByCustomerId(id).thenApplyAsync(w->w.stream().map(WagonDTO::new).collect(Collectors.toList()));
    }

    public CompletionStage<Wagon> createWagon (WagonDTO wagon) {
        final Wagon data = wagon.instanceOf();
        return repository.createWagon(data);
    }

    public CompletionStage<Wagon> updateWagon (WagonDTO wagonDTO) {
        final Wagon data = wagonDTO.instanceOf();
        return repository.updateWagon(data);
    }

    public CompletionStage<Boolean> deleteWagon (Long id) {
        return repository.deleteWagon(id);
    }

    public CompletionStage<Wagon> getWagonById (Long id) {
        return repository.getWagonById(id);
    }

    public Wagon getWagonBySerial(String serial) {
        LOG.debug(">> getWagonBySerial");
        return repository.getWagonBySerial(serial);
    }
}
