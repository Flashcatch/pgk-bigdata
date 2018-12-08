package services;

import domain.WagonSide;
import domain.mobile.WagonTypeMobile;
import dto.Page;
import dto.WagonSideDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.WagonSideRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

public class WagonSideService {
    private static final Logger LOG = LoggerFactory.getLogger(WagonSideService.class);

    private WagonSideRepository repository;

    @Inject
    public WagonSideService(WagonSideRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getWagonSides (Integer page, Integer size, Long wagonTypeId, String query) {
        return repository.getWagonSides(page, size, wagonTypeId, query);
    }

    public CompletionStage<WagonSide> createWagonSide (WagonSideDTO wagonSide) {
        final WagonSide data = wagonSide.instanceOf();
        return repository.createWagonSide(data);
    }

    public CompletionStage<WagonSide> updateWagonSide (WagonSideDTO wagonSide) {
        final WagonSide data = wagonSide.instanceOf();
        return repository.updateWagonSide(data);
    }

    public CompletionStage<Boolean> deleteWagonSide (Long id) {
        return repository.deleteWagonSide(id);
    }

    public CompletionStage<WagonSide> getWagonSideById (Long id) {
        return repository.getWagonSideById(id);
    }

    public CompletionStage<Optional<WagonTypeMobile>> getWagonSidesMobileBySerial (String serial) {return repository.getWagonSidesMobileBySerial(serial);}

    public CompletionStage<List<WagonTypeMobile>> getWagonSidesMobileWithTypes(){
        return repository.getWagonSidesMobileWithTypes();
    }

    public Long getValueIdByValue(Long value, Long sideId) {
        return repository.getValueIdByValue(value, sideId);
    }
}
