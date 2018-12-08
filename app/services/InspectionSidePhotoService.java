package services;

import com.fasterxml.jackson.databind.JsonNode;
import domain.Inspection;
import domain.InspectionSidePhoto;
import dto.InspectionSidePhotoDTO;
import dto.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.InspectionSidePhotoRepository;
import utils.exceptions.Ex;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class InspectionSidePhotoService {
    private static final Logger LOG = LoggerFactory.getLogger(InspectionSidePhotoService.class);

    private InspectionSidePhotoRepository repository;
    private InspectionSideService inspectionSideService;
    private InspectionService inspectionService;

    @Inject
    public InspectionSidePhotoService(InspectionSidePhotoRepository repository,
                                      InspectionSideService inspectionSideService,
                                      InspectionService inspectionService) {
        this.repository = repository;
        this.inspectionSideService = inspectionSideService;
        this.inspectionService = inspectionService;
    }

    public CompletionStage<Page> getInspectionSidePhotos (Integer page, Integer size) {
        return repository.getInspectionSidePhotos(page, size);
    }

    public void createInspectionSidePhoto (JsonNode data) {

        Long itemId = data.get("itemId").asLong();
        Long inspectionId = data.get("inspectionId").asLong();
        Long photoServiceId = data.get("photoserviceId").asLong();
        String photoUrl = data.get("photoURL").asText();

        Long inspectionSideId = inspectionSideService.getInspectionSideByParams(inspectionId, itemId);

        Inspection ins = inspectionService.getFullInspectionById(inspectionId);

        if (ins == null)
            throw Ex.createExecutionException("Инспекция не найдена");

        if (!ins.getDraft())
            throw Ex.createExecutionException("Фотографии не могут быть добавлены к инспекции");

        if (inspectionSideId == null)
            throw Ex.createExecutionException("Не найдена часть вагона для добавления фотографии");

        InspectionSidePhoto inspectionSidePhoto = new InspectionSidePhoto(null, photoServiceId, inspectionSideId, photoUrl);

        repository.createInspectionSidePhoto(inspectionSidePhoto);
    }

    public CompletionStage<InspectionSidePhoto> updateInspectionSidePhoto (InspectionSidePhotoDTO inspectionSidePhoto) {
        final InspectionSidePhoto data = inspectionSidePhoto.instanceOf();
        return repository.updateInspectionSidePhoto(data);
    }

    public CompletionStage<Boolean> deleteInspectionSidePhoto (Long id) {
        return repository.deleteInspectionSidePhoto(id);
    }

    public CompletionStage<InspectionSidePhoto> getInspectionSidePhotoById (Long id) {
        return repository.getInspectionSidePhotoById(id);
    }
}
