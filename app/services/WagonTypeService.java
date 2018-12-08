package services;

import domain.WagonType;
import dto.Page;
import dto.WagonTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.WagonTypeRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class WagonTypeService {
    private static final Logger LOG = LoggerFactory.getLogger(WagonTypeService.class);

    private WagonTypeRepository repository;

    @Inject
    public WagonTypeService (WagonTypeRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getWagonTypes (Integer page, Integer size, String query) {
        return repository.getWagonTypes(page, size, query);
    }

    public CompletionStage<WagonType> createWagonType (WagonTypeDTO wagonType) {
        final WagonType data = wagonType.instanceOf();
        return repository.createWagonType(data);
    }

    public CompletionStage<WagonType> updateWagonType (WagonTypeDTO wagonType) {
        final WagonType data = wagonType.instanceOf();
        return repository.updateWagonType(data);
    }

    public CompletionStage<Boolean> deleteWagonType (Long id) {
        return repository.deleteWagonType(id);
    }

    public CompletionStage<WagonType> getWagonTypeById (Long id) {
        return repository.getWagonTypeById(id);
    }
}
