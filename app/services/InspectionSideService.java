package services;

import domain.InspectionSide;
import dto.InspectionSideDTO;
import dto.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.InspectionSideRepository;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;

public class InspectionSideService {
    private static final Logger LOG = LoggerFactory.getLogger(InspectionSideService.class);

    private InspectionSideRepository repository;

    @Inject
    public InspectionSideService(InspectionSideRepository repository) {
        this.repository = repository;
    }

    public CompletionStage<Page> getInspectionSides(Integer page, Integer size, String query, Long inspectionId) {
        return repository.getInspectionSides(page, size, query, inspectionId);
    }

    public InspectionSide createInspectionSide(InspectionSideDTO inspectionSide) {
        final InspectionSide data = inspectionSide.instanceOf();
        return repository.createInspectionSide(data);
    }

    public CompletionStage<InspectionSide> updateInspectionSide(InspectionSideDTO inspectionSide) {
        final InspectionSide data = inspectionSide.instanceOf();
        return repository.updateInspectionSide(data);
    }

    public CompletionStage<Boolean> deleteInspectionSide(Long id) {
        return repository.deleteInspectionSide(id);
    }

    public CompletionStage<InspectionSide> getInspectionSideById(Long id) {
        return repository.getInspectionSideById(id);
    }

    public CompletionStage<List<InspectionSideDTO>> getFullInspectionSidesBySerial(String serial) {
        return repository.getFullInspectionSidesBySerial(serial);
    }

    public Long getInspectionSideByParams (Long inspectionId, Long sideId) {
        return repository.getInspectionSideByParams(inspectionId, sideId);
    }
}
