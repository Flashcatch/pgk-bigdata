package services;

import domain.ChecklistInspection;
import dto.ChecklistInspectionDTO;
import dto.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ChecklistInspectionRepository;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class ChecklistInspectionService {
    private static final Logger LOG = LoggerFactory.getLogger(ChecklistInspectionService.class);

    private ChecklistInspectionRepository repository;

    @Inject
    public ChecklistInspectionService(ChecklistInspectionRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getChecklistInspections (Integer page, Integer size) {
        return repository.getChecklistInspections(page, size);
    }

    public CompletionStage<ChecklistInspection> createChecklistInspection (ChecklistInspectionDTO checklistInspection) {
        final ChecklistInspection data = checklistInspection.instanceOf();
        return repository.createChecklistInspection(data);
    }

    public CompletionStage<ChecklistInspection> updateChecklistInspection (ChecklistInspectionDTO checklistInspection) {
        final ChecklistInspection data = checklistInspection.instanceOf();
        return repository.updateChecklistInspection(data);
    }

    public CompletionStage<Boolean> deleteChecklistInspection (Long id) {
        return repository.deleteChecklistInspection(id);
    }
}
