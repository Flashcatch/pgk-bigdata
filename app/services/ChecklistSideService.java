package services;

import domain.ChecklistSide;
import dto.ChecklistSideDTO;
import dto.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ChecklistSideRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class ChecklistSideService {
    private static final Logger LOG = LoggerFactory.getLogger(ChecklistSideService.class);

    private ChecklistSideRepository repository;

    @Inject
    public ChecklistSideService(ChecklistSideRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getChecklistSides (Integer page, Integer size, Long wagonSideId) {
        return repository.getChecklistSides(page, size, wagonSideId);
    }

    public CompletionStage<ChecklistSide> createChecklistSide (ChecklistSideDTO checklistSide) {
        final ChecklistSide data = checklistSide.instanceOf();
        return repository.createChecklistSide(data);
    }

    public CompletionStage<ChecklistSide> updateChecklistSide (ChecklistSideDTO checklistSide) {
        final ChecklistSide data = checklistSide.instanceOf();
        return repository.updateChecklistSide(data);
    }

    public CompletionStage<Boolean> deleteChecklistSide (Long id) {
        return repository.deleteChecklistSide(id);
    }
}
