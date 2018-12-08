package services;

import domain.Inspection;
import domain.InspectionSide;
import domain.Wagon;
import domain.mobile.CreateIns;
import domain.mobile.InspectionCardMobile;
import dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.InspectionRepository;
import utils.exceptions.Ex;

import javax.inject.Inject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class InspectionService {
    private static final Logger LOG = LoggerFactory.getLogger(InspectionService.class);

    private InspectionRepository repository;
    private WagonService wagonService;
    private InspectionSideService inspectionSideService;
    private WagonSideService wagonSideService;

    @Inject
    public InspectionService(InspectionRepository repository,
                             WagonService wagonService,
                             InspectionSideService inspectionSideService,
                             WagonSideService wagonSideService) {
        this.repository = repository;
        this.wagonService = wagonService;
        this.inspectionSideService = inspectionSideService;
        this.wagonSideService = wagonSideService;
    }

    public CompletionStage<Page> getInspections(Integer page, Integer size, String query, Long wagonId) {
        return repository.getInspections(page, size, query, wagonId);
    }

    public CompletionStage<Page> getInspectionsCards(Integer page, Integer size) {
        return repository.getInspectionsCards(page, size);
    }

    public Inspection createInspection(InspectionDTO stage) {
        final Inspection data = stage.instanceOf();
        return repository.createInspection(data);
    }

    public Long createInspectionMobile(CreateIns data) throws IOException {
        InspectionDTO inspection = new InspectionDTO();
        String serial = data.getId().toString();

        inspection.setCreationDate(LocalDateTime.now());
        Wagon wagon = wagonService.getWagonBySerial(serial);

        if (wagon == null)
            throw Ex.createExecutionException("Вагон с таким номером не найден");

        inspection.setWagon(new WagonDTO(wagon));
        inspection.setPassed(data.getMark());
        inspection.setDraft(Boolean.TRUE);
        inspection.setCustomerId(-1L);
        inspection.setComment("");

        Inspection ins = createInspection(inspection);
        Long zz = ins.getId();

        data.getSteps().stream().forEach(step -> {
            InspectionSideDTO side = new InspectionSideDTO();
            WagonSideDTO wagonSide = new WagonSideDTO();
            wagonSide.setId(step.getId());

            side.setInspectionId(ins.getId());
            side.setWagonSide(wagonSide);
            side.setComment(step.getComment());

            InspectionSide createdSide = inspectionSideService.createInspectionSide(side);

            step.getItems().stream().forEach(createElement -> {
                InspectionSideDTO element = new InspectionSideDTO();
                WagonSideDTO wagonElement = new WagonSideDTO();
                wagonElement.setId(createElement.getId());

                element.setInspectionId(ins.getId());
                element.setWagonSide(wagonElement);
                element.setParentSideId(createdSide.getId());
                element.setValueId(wagonSideService.getValueIdByValue(createElement.getState(), wagonElement.getId()));

                InspectionSide createdElement = inspectionSideService.createInspectionSide(element);

            });
        });
       /* return createInspection(inspection).thenApply(ins -> {
            Long zz = ins.getId();
            data.getSteps().stream().forEach(step -> {
                InspectionSideDTO side = new InspectionSideDTO();
                WagonSideDTO wagonSide = new WagonSideDTO();
                wagonSide.setId(step.getId());

                side.setInspectionId(ins.getId());
                side.setWagonSide(wagonSide);
                side.setComment(step.getComment());

                InspectionSide zzz = inspectionSideService.createInspectionSide(side);

                step.getItems().stream().forEach(createElement -> {

                });
            });

            return zz;
        }).handle((data1, err) ->
        {
            if (err != null) {
                LOG.debug("err = "+err);
                throw Ex.createExecutionException("");
            } else {
                return data1;
            }
        });*/
       return ins.getId();
        }

        public CompletionStage<Inspection> updateInspection (InspectionDTO stage){
            final Inspection data = stage.instanceOf();
            return repository.updateInspection(data);
        }

        public void finalizeInspection(Long id) {
        LOG.debug(">> finalizeInspection");
        repository.finalizeInspection(id);
    }

        public CompletionStage<Boolean> deleteInspection (Long id){
            return repository.deleteInspection(id);
        }

        public CompletionStage<InspectionDTO> getInspectionById (Long id){
            return repository.getInspectionById(id).thenApply(InspectionDTO::new);
        }

        public Inspection getFullInspectionById (Long id){
            return repository.getFullInspectionById(id);
        }

        public CompletionStage<List<InspectionDTO>> getFullInspectionsByWagonId (Long id){
            return repository.getFullInspectionsByWagonId(id).thenApplyAsync(i -> i.stream().map(InspectionDTO::new).collect(Collectors.toList()));
        }

        public CompletionStage<List<InspectionDTO>> getFullInspectionsByCustomerId (Long id){
            return repository.getFullInspectionsByCustomerId(id).thenApplyAsync(i -> i.stream().map(InspectionDTO::new).collect(Collectors.toList()));
        }

        public CompletionStage<Boolean> deleteInspectionsByIds (List < Long > ids) {
            return repository.deleteInspectionsByIds(ids);
        }

        public CompletionStage<InspectionCardMobile> getInspectionCardMobileById (Long id){
            return repository.getInspectionCardMobileById(id);
        }
    }
