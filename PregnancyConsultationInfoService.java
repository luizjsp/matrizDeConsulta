package br.com.mv.clinic.service.pregnancy_consultation_info_service;

import br.com.mv.clinic.domain.Measurement;
import br.com.mv.clinic.domain.PregnancyConsultationInfo;
import br.com.mv.clinic.domain.attendance.Attendance;
import br.com.mv.clinic.domain.obstetric_plan.ObstetricPlan;
import br.com.mv.clinic.domain.patient.Patient;
import br.com.mv.clinic.dto.obstetric_card.ObstetricCardConsultationInfoDTO;
import br.com.mv.clinic.dto.obstetric_info.ObstetricInfoDTO;
import br.com.mv.clinic.dto.personal.measurement.MeasurementDTO;
import br.com.mv.clinic.dto.personal.measurement.MeasurementsDTO;
import br.com.mv.clinic.dto.pregnancy_consultation_info.PatientInfoForPregnancyConsultationDTO;
import br.com.mv.clinic.dto.pregnancy_consultation_info.PregnancyConsultationInfoDTO;
import br.com.mv.clinic.enums.MeasurementSourceEnum;
import br.com.mv.clinic.enums.MeasurementTypeEnum;
import br.com.mv.clinic.repository.MeasurementRepository;
import br.com.mv.clinic.repository.attendance.AttendanceRepository;
import br.com.mv.clinic.domain.attendance.AttendanceMedicalRecord;
import br.com.mv.clinic.domain.ehr.ComponentPropertyValues;
import br.com.mv.clinic.domain.ehr.Segment;
import br.com.mv.clinic.domain.obstetric_plan.ObstetricPlan;
import br.com.mv.clinic.dto.attendance.AttendanceMedicalRecordDTO;
import br.com.mv.clinic.dto.pregnancy_consultation_info.PatientInfoForPregnancyConsultationDTO;
import br.com.mv.clinic.dto.pregnancy_consultation_info.PregnancyConsultationInfoDTO;
import br.com.mv.clinic.repository.attendance.AttendanceRepository;
import br.com.mv.clinic.repository.ehr.ComponentPropertyValuesRepository;
import br.com.mv.clinic.repository.ehr.SegmentRepository;
import br.com.mv.clinic.repository.obstetric_plan.ObstetricPlanRepository;
import br.com.mv.clinic.repository.pregnancy_consultation_info.PregnancyConsultationInfoRepository;
import br.com.mv.clinic.service.AbstractMessage;
import br.com.mv.clinic.service.MeasurementPushService;
import br.com.mv.clinic.util.DateUtils;
import br.com.mv.clinic.util.HeaderUtil;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PregnancyConsultationInfoService extends AbstractMessage {
    private final PregnancyConsultationInfoRepository pregnancyConsultationInfoRepository;

    private final ObstetricPlanRepository obstetricPlanRepository;

    private final AttendanceRepository attendanceRepository;

    private final SegmentRepository segmentRepository;

    private final ComponentPropertyValuesRepository componentPropertyValuesRepository;

    @Autowired
    private MeasurementPushService measurementPushService;

    @Autowired
    private MeasurementRepository measurementRepository;

    public PregnancyConsultationInfoService(PregnancyConsultationInfoRepository pregnancyConsultationInfoRepository, ObstetricPlanRepository obstetricPlanRepository, AttendanceRepository attendanceRepository, SegmentRepository segmentRepository, ComponentPropertyValuesRepository componentPropertyValuesRepository) {
        this.pregnancyConsultationInfoRepository = pregnancyConsultationInfoRepository;
        this.obstetricPlanRepository = obstetricPlanRepository;
        this.attendanceRepository = attendanceRepository;
        this.segmentRepository = segmentRepository;
        this.componentPropertyValuesRepository = componentPropertyValuesRepository;
    }

    @Transactional
    public PregnancyConsultationInfoDTO create(PregnancyConsultationInfoDTO dto, Long obstetricPlanId) throws IOException {

        this.validateDtoFields(dto, obstetricPlanId);

        if (dto.getImc() != null) {
            DecimalFormat format = new DecimalFormat("#.##");

            Double parseStringForDouble = Double.parseDouble(format.format(dto.getImc()).replace(",", "."));

            dto.setImc(parseStringForDouble);
        }

        PregnancyConsultationInfo pregnancyConsultationInfo = mapper.map(dto, PregnancyConsultationInfo.class);

        pregnancyConsultationInfo.setObstetricPlan(this.validObstetricPLan(obstetricPlanId));

        this.send(pregnancyConsultationInfo);

        this.onCreationLogic(pregnancyConsultationInfo);

        mapper.map(pregnancyConsultationInfoRepository.save(pregnancyConsultationInfo), dto);

        return dto;
    }

    @Transactional
    public PregnancyConsultationInfoDTO update(Long id, PregnancyConsultationInfoDTO dto, Long obstetricPlanId) throws IOException {

        this.validFildsUpdate(dto, obstetricPlanId);

        if (dto.getImc() != null) {
            DecimalFormat format = new DecimalFormat("#.##");

            Double parseStringForDouble = Double.parseDouble(format.format(dto.getImc()).replace(",", "."));

            dto.setImc(parseStringForDouble);
        }

        PregnancyConsultationInfo pregnancyConsultationInfo = this.checkUpdateConsistence(id, dto, obstetricPlanId);

        pregnancyConsultationInfo.setObstetricPlan(this.validObstetricPLan(obstetricPlanId));

        pregnancyConsultationInfo.setClientKey(HeaderUtil.getClientKey());

        this.send(pregnancyConsultationInfo);

        pregnancyConsultationInfo = pregnancyConsultationInfoRepository.save(pregnancyConsultationInfo);

        return mapper.map(pregnancyConsultationInfo, PregnancyConsultationInfoDTO.class);
    }

    @Transactional(readOnly = true)
    public Page<PregnancyConsultationInfoDTO> findAll(Pageable pageable, Long obstetricPlanId) throws IOException {

        Page<PregnancyConsultationInfo> pregnancyConsultation = this.pregnancyConsultationInfoRepository.findAllByClientKeyAndObstetricPlanIdAndDeletedIsFalse(pageable, HeaderUtil.getClientKey(), obstetricPlanId);

        return pregnancyConsultation.map(pregnancyConsultationInfo -> {
            PregnancyConsultationInfoDTO dto = mapper.map(pregnancyConsultationInfo, PregnancyConsultationInfoDTO.class);

            if (dto.getImc() != null) {
                DecimalFormat format = new DecimalFormat("#.##");

                Double parseStringForDouble = Double.parseDouble(format.format(dto.getImc()).replace(",", "."));

                dto.setImc(parseStringForDouble);
            }

            return dto;
        });
    }

    @Transactional(readOnly = true)
    public PregnancyConsultationInfoDTO findById(Long id, Long obstetricPlanId) throws IOException {

        PregnancyConsultationInfo pregnancyConsultationInfo = this.getValidPregnancyConsultInfo(id, HeaderUtil.getClientKey(), obstetricPlanId);


        if (pregnancyConsultationInfo.getImc() != null) {
            DecimalFormat format = new DecimalFormat("#.##");

            Double parseStringForDouble = Double.parseDouble(format.format(pregnancyConsultationInfo.getImc()).replace(",", "."));

            pregnancyConsultationInfo.setImc(parseStringForDouble);
        }

        return mapper.map(pregnancyConsultationInfo, PregnancyConsultationInfoDTO.class);
    }

    @Transactional
    public void delete(Long id, Long obstetricPlanId) throws IOException {

        PregnancyConsultationInfo pregnancyConsultationInfo = this.getValidPregnancyConsultInfo(id, HeaderUtil.getClientKey(), obstetricPlanId);
        this.deleteLogic(pregnancyConsultationInfo);
        pregnancyConsultationInfoRepository.save(pregnancyConsultationInfo);
    }

    private PregnancyConsultationInfo getValidPregnancyConsultInfo(long id, String clientKey, Long obstetricPlanId) throws IOException {
        Optional<PregnancyConsultationInfo> pregnancyPlanOption = pregnancyConsultationInfoRepository.findByIdAndClientKeyAndObstetricPlanIdAndDeletedIsFalse(id, clientKey, obstetricPlanId);
        if (!pregnancyPlanOption.isPresent()) {
            throwsException("error.pregnancyPlanConsultation.resource_not_found");
        }
        return pregnancyPlanOption.get();
    }

    private void deleteLogic(PregnancyConsultationInfo pregnancyConsultationInfo) {
        pregnancyConsultationInfo.setActive(Boolean.FALSE);
        pregnancyConsultationInfo.setDeleted(Boolean.TRUE);
    }

    private void onCreationLogic(PregnancyConsultationInfo pregnancyConsultationInfo) {
        pregnancyConsultationInfo.setClientKey(HeaderUtil.getClientKey());
        pregnancyConsultationInfo.setActive(Boolean.TRUE);
        pregnancyConsultationInfo.setDeleted(Boolean.FALSE);
    }

    private void validateDtoFields(PregnancyConsultationInfoDTO postDto, Long obstetricPLanId) throws IOException {

        PatientInfoForPregnancyConsultationDTO obstetricPlanDTO = new PatientInfoForPregnancyConsultationDTO();

        obstetricPlanDTO.setId(obstetricPLanId);

        postDto.setObstetricPlan(obstetricPlanDTO);

        int maxChars = 200;

        if (postDto.getBcf() != null) {
            if ((postDto.getBcf() < 90) || (postDto.getBcf() > 220)) {
                throwsException("error.pregnancyPlanConsultation.bcf.out_of_range");
            }
        }

        if (postDto.getDateInfo() == null ) {
            throwsException("error.pregnancyConsultationInfo.fielddate.not.null");
        }

        if (postDto.getComplaint() == null ) {
            throwsException("error.pregnancyConsultationInfo.fieldcomplaint.not.null");
        }

        String complaint = postDto.getComplaint().toString();
        if (complaint.length() > maxChars) {
            throwsException("error.pregnancyPlanConsultation.complaint.maxlength.exceeded");
        }

        //String edema = postDto.getEdema().toString();
        //if (postDto.getEdema() != null){
        //    if(edema.length() > maxChars) {
        //        throwsException("error.pregnancyPlanConsultation.edema.maxlength.exceeded");
        //    }
        //}

        if (new Date().compareTo(postDto.getDateInfo()) == -1) {
            throwsException("error.pregnancyConsultationInfo.field.date.later");
        }

        if (postDto.getUterineHeight() != null) {
                if ((postDto.getUterineHeight() < 3) || (postDto.getUterineHeight() > 49.9)) {
                throwsException("error.pregnancyPlanConsultation.uterineheight.out_of_range");
            }
        }
        if (postDto.getWeight() != null) {
            if ((postDto.getWeight() < 0 || postDto.getWeight() > 250)) {
                throwsException("error.pregnancyPlanConsultation.weight.out.expectations");
            }
        }
        if (postDto.getHeight() != null) {
            if ((postDto.getHeight() < 0 || postDto.getHeight() > 215)) {
                throwsException("error.pregnancyPlanConsultation.height.out.expectations");
            }
        }
        if (postDto.getBloodPressureDiastolica() != null && postDto.getBloodPressureSistolica() != null) {
            if ((postDto.getBloodPressureDiastolica() < 0 || postDto.getBloodPressureDiastolica() > 300)) {
                throwsException("error.pregnancyPlanConsultation.diastolicpressure.outside.expectations");
            }
            if ((postDto.getBloodPressureSistolica() < 0 || postDto.getBloodPressureSistolica() > 300)) {
                throwsException("error.pregnancyPlanConsultation.sistolicpressure.outside.expectations");
            }
            if ((postDto.getBloodPressureDiastolica() > postDto.getBloodPressureSistolica())) {
                throwsException("error.pregnancyPlanConsultation.diastolicpressure.higher.than.systolicpressure");
            }
        }
        if ((postDto.getGestationalAge() != null)) {
            if ((postDto.getGestationalAge() < 1 || postDto.getGestationalAge() > 42)) {
                throwsException("error.pregnancyPlanConsultation.gestationalage.outside.expectations");
            }
        }

        if (postDto.getHeight() != null && postDto.getWeight() != null) {
            postDto.setImc(postDto.getWeight() / ((postDto.getHeight() * postDto.getHeight()) / 10000));
        }

    }

    public PregnancyConsultationInfo checkUpdateConsistence(Long id, PregnancyConsultationInfoDTO toUpdateEntity, Long obstetricPlanId) throws IOException {

        PregnancyConsultationInfo persistedEntity = this.getValidPregnancyConsultInfo(id, HeaderUtil.getClientKey(), obstetricPlanId);

        toUpdateEntity.setId(id);
        toUpdateEntity.setClientKey(persistedEntity.getClientKey());
        toUpdateEntity.getObstetricPlan().setId(obstetricPlanId);
        toUpdateEntity.setCreatedBy(persistedEntity.getCreatedBy());
        toUpdateEntity.setCreatedDate(persistedEntity.getCreatedDate());
        toUpdateEntity.setActive(persistedEntity.isActive());
        ObstetricPlan obstetricPlanDTO = new ObstetricPlan();
        persistedEntity.setObstetricPlan(obstetricPlanDTO);
        mapper.map(toUpdateEntity, persistedEntity);

        Date date = DateUtils.addDays(persistedEntity.getCreatedDate().toDate(), 1);

        if (date.compareTo(new Date()) == -1) {
            throwsException("error.pregnancyPlanConsultation.query.not.changed");
        }
        persistedEntity.setLastModifiedBy(null);
        return persistedEntity;
    }

    private ObstetricPlan validObstetricPLan(Long obstetricPlanId) throws IOException {

        Optional<ObstetricPlan> obstetricPlanOptional = this.obstetricPlanRepository.findByIdAndClientKeyAndActive(obstetricPlanId, HeaderUtil.getClientKey(), true);

        if (!obstetricPlanOptional.isPresent()) {
            throwsException("error.pregnancyPlanConsultation.obstetric.plan.not.found");
        }

        return obstetricPlanOptional.get();
    }

    private void validFildsUpdate(PregnancyConsultationInfoDTO updateDto, Long obstetricPlanId) throws IOException {

        PatientInfoForPregnancyConsultationDTO obstetricPlanDTO = new PatientInfoForPregnancyConsultationDTO();

        obstetricPlanDTO.setId(obstetricPlanId);

        updateDto.setObstetricPlan(obstetricPlanDTO);

        int maxChars = 200;

        if (updateDto.getDateInfo() == null) {
            throwsException("error.pregnancyPlanConsultation.date.not.null");
        }

        if (new Date().compareTo(updateDto.getDateInfo()) == -1) {
            throwsException("error.pregnancyConsultationInfo.field.date.later");
        }

        if (updateDto.getComplaint() == null) {
            throwsException("error.pregnancyPlanConsultation.complaint.not.null");
        }

        String complaint = updateDto.getComplaint().toString();
        if (complaint.length() > maxChars) {
            throwsException("error.pregnancyPlanConsultation.ccomplaint.maxlength.exceeded");
        }

        if (updateDto.getBcf() != null) {
            if ((updateDto.getBcf() < 90) || (updateDto.getBcf() > 220)) {
                throwsException("error.pregnancyPlanConsultation.bcf.out_of_range");
            }
        }

        //String edema = updateDto.getEdema().toString();
        //if (updateDto.getEdema() != null) {
        //    if (edema.length() > maxChars) {
        //        throwsException("error.pregnancyPlanConsultation.edema.maxlength.exceeded");
        //    }
        //}

        if ((updateDto.getGestationalAge() != null)) {
            if ((updateDto.getGestationalAge() < 1 || updateDto.getGestationalAge() > 42)) {
                throwsException("error.pregnancyPlanConsultation.gestationalage.outside.expectations");
            }
        }

        if (updateDto.getHeight() != null) {
            if ((updateDto.getHeight() < 0 || updateDto.getHeight() > 215)) {
                throwsException("error.pregnancyPlanConsultation.height.out.expectations");
            }
        }
        if (updateDto.getWeight() != null) {
            if ((updateDto.getWeight() < 0 || updateDto.getWeight() > 250)) {
                throwsException("error.pregnancyPlanConsultation.weight.out.expectations");
            }
        }
        if (updateDto.getBloodPressureDiastolica() != null && updateDto.getBloodPressureSistolica() != null) {
            if ((updateDto.getBloodPressureDiastolica() < 0 || updateDto.getBloodPressureDiastolica() > 300)) {
                throwsException("error.pregnancyPlanConsultation.diastolicpressure.outside.expectations");
            }
            if ((updateDto.getBloodPressureSistolica() < 0 || updateDto.getBloodPressureSistolica() > 300)) {
                throwsException("error.pregnancyPlanConsultation.sistolicpressure.outside.expectations");
            }

            if ((updateDto.getBloodPressureDiastolica() > updateDto.getBloodPressureSistolica())) {
                throwsException("error.pregnancyPlanConsultation.diastolicpressure.higher.than.systolicpressure");
            }

            //String fetalPresentation = updateDto.getFetalPresentation().toString();
            //if (updateDto.getFetalPresentation() != null) {
            //    if (fetalPresentation.length() > maxChars) {
            //        throwsException("error.pregnancyPlanConsultation.fetalPresenttation.maxlength.exceeded");
            //    }
            //}

            if (updateDto.getUterineHeight() != null) {
                if ((updateDto.getUterineHeight() < 3) || (updateDto.getUterineHeight() > 49.9)) {
                    throwsException("error.pregnancyPlanConsultation.uterineheight.out_of_range");
                }
            }
            if (updateDto.getHeight() != null && updateDto.getWeight() != null) {
                updateDto.setImc(updateDto.getWeight() / ((updateDto.getHeight() * updateDto.getHeight()) / 10000));
            }



        }

    }

    public ObstetricCardConsultationInfoDTO getLatestConsultationInfoByObstetricPlanIdForObstetricCard(Long obstetricPlanId) throws IOException {

        Optional<PregnancyConsultationInfo> consultationInfoOptional = this.pregnancyConsultationInfoRepository
                .getLatestConsultationInfoByObstetricPlanIdWithAnyStatusActiveOrNotExecptDeleted(obstetricPlanId);

        return consultationInfoOptional.map(pregnancyConsultationInfo ->
                mapper.map(pregnancyConsultationInfo, ObstetricCardConsultationInfoDTO.class))
                .orElse(null);

    }

    public List<ObstetricCardConsultationInfoDTO> findAllByObstetricPlanIdAndDeletedIsFalseAndActiveIsTrue(Long obstetricPlanId){

         List<PregnancyConsultationInfo>  consultationInfos = this.pregnancyConsultationInfoRepository
                 .findAllByObstetricPlanIdAndDeletedIsFalseAndActiveIsTrue(obstetricPlanId);

        List<ObstetricCardConsultationInfoDTO> consultationInfoDTOList = consultationInfos.stream().map(consultationInfo -> {
            return mapper.map(consultationInfo, ObstetricCardConsultationInfoDTO.class);
        }).collect(Collectors.toList());

         return consultationInfoDTOList;
    }

    public List<ObstetricCardConsultationInfoDTO> findAllByObstetricPlanIdAndDeletedIsFalseForObstetricCard(Long obstetricPlanId){

        List<PregnancyConsultationInfo>  consultationInfos = this.pregnancyConsultationInfoRepository
                .findAllByObstetricPlanIdAndDeletedIsFalse(obstetricPlanId);

        List<ObstetricCardConsultationInfoDTO> consultationInfoDTOList = consultationInfos.stream().map(consultationInfo -> {
            return mapper.map(consultationInfo, ObstetricCardConsultationInfoDTO.class);
        }).collect(Collectors.toList());

        return consultationInfoDTOList;
    }

    private void send(PregnancyConsultationInfo pregnancyConsultationInfo){
        List<MeasurementDTO> measurementDTOS= new ArrayList<>();
        List<Measurement> measurementList = new ArrayList<>();
        Patient patient = pregnancyConsultationInfo.getObstetricPlan().getPatient();
        try {
            if (Objects.nonNull(pregnancyConsultationInfo.getWeight())){
                measurementDTOS.add(new MeasurementDTO(patient.getLoginPersonalHealth(), patient.getIdentificationNumber(), patient.getIdentificationType().getType(), "WEIGHT", "kg", new DateTime(pregnancyConsultationInfo.getDateInfo()), pregnancyConsultationInfo.getWeight().floatValue()));
            }
            if (Objects.nonNull(pregnancyConsultationInfo.getHeight())){
                measurementDTOS.add(new MeasurementDTO(patient.getLoginPersonalHealth(), patient.getIdentificationNumber(), patient.getIdentificationType().getType(), "HEIGHT", "cm", new DateTime(pregnancyConsultationInfo.getDateInfo()), pregnancyConsultationInfo.getHeight().floatValue()));
            }
            if (!measurementDTOS.isEmpty()){
                measurementPushService.send(new MeasurementsDTO(patient.getLoginPersonalHealth(), measurementDTOS));
            }
        }catch (Exception e){
            log.error("=========================================================================");
            log.error("ERROR AO MANDAR A MENSAGEM PARA FILA TOMONITORINGMEASUREMENT ");
            log.error(e.getMessage());
            log.error("=========================================================================");
        }

        try {
            if (Objects.nonNull(pregnancyConsultationInfo.getWeight())){
                measurementList.add(new Measurement(patient, MeasurementSourceEnum.EMPLOYEE_CLINIC, MeasurementTypeEnum.WEIGHT, "kg", new DateTime(pregnancyConsultationInfo.getDateInfo()), pregnancyConsultationInfo.getWeight().floatValue()));
            }
            if (Objects.nonNull(pregnancyConsultationInfo.getHeight())){
                measurementList.add(new Measurement(patient, MeasurementSourceEnum.EMPLOYEE_CLINIC, MeasurementTypeEnum.HEIGHT, "cm", new DateTime(pregnancyConsultationInfo.getDateInfo()), pregnancyConsultationInfo.getHeight().floatValue()));
            }
           if (!measurementList.isEmpty()){
               measurementRepository.save(measurementList);
           }
        }catch (Exception e){
            log.error("=========================================================================");
            log.error("ERROR AO SALVAR AS MEDIÇÕES DA INFORMAÇÃO DE CONSULTA GESTANTE ");
            log.error(e.getMessage());
            log.error("=========================================================================");
        }
    }

}