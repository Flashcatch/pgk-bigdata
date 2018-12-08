package services;

import domain.Stage;
import dto.Page;
import dto.StageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.StageRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class StageService {
    private static final Logger LOG = LoggerFactory.getLogger(StageService.class);

    private StageRepository repository;

    @Inject
    public StageService(StageRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getStages (Integer page, Integer size, Long wagonId) {
        return repository.getStages(page, size, wagonId);
    }

    public CompletionStage<Stage> createStage (StageDTO stage) {
        final Stage data = stage.instanceOf();
        return repository.createStage(data);
    }

    public CompletionStage<Stage> updateStage (StageDTO stage) {
        final Stage data = stage.instanceOf();
        return repository.updateStage(data);
    }

    public CompletionStage<Boolean> deleteStage (Long id) {
        return repository.deleteStage(id);
    }

    public CompletionStage<Stage> getStageById (Long id) {
        return repository.getStageById(id);
    }
}
