package services;

import domain.StageType;
import dto.Page;
import dto.StageTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.StageTypeRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class StageTypeService {
    private static final Logger LOG = LoggerFactory.getLogger(StageTypeService.class);

    private StageTypeRepository repository;

    @Inject
    public StageTypeService(StageTypeRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getStageTypes (Integer page, Integer size, String query) {
        return repository.getStageTypes(page, size, query);
    }

    public CompletionStage<StageType> createStageType (StageTypeDTO stage) {
        final StageType data = stage.instanceOf();
        return repository.createStageType(data);
    }

    public CompletionStage<StageType> updateStageType (StageTypeDTO stage) {
        final StageType data = stage.instanceOf();
        return repository.updateStageType(data);
    }

    public CompletionStage<Boolean> deleteStageType (Long id) {
        return repository.deleteStageType(id);
    }

    public CompletionStage<StageType> getStageTypeById (Long id) {
        return repository.getStageTypeById(id);
    }
}
