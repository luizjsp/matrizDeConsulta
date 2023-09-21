package br.com.mv.clinic.rest.pregnancy_consultation_info;

import br.com.mv.clinic.constants.AppConstants;
import br.com.mv.clinic.dto.pregnancy_consultation_info.PregnancyConsultationInfoDTO;
import br.com.mv.clinic.service.pregnancy_consultation_info_service.PregnancyConsultationInfoService;
import br.com.mv.clinic.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = AppConstants.PATH)
public class PregnancyConsultationInfoResource {
    @Autowired(required = true)
    PregnancyConsultationInfoService pregnancyConsultationInfoService;

    @Timed
    @RequestMapping(value = "/v1/obstetric-plans/{obstetricPlanId}/consultation-info",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PregnancyConsultationInfoDTO>> findAll(Pageable pageable,@PathVariable("obstetricPlanId") Long obstetricPlanId)
            throws IOException {
        Page<PregnancyConsultationInfoDTO> dtoList = this.pregnancyConsultationInfoService.findAll(pageable, obstetricPlanId);
        return new ResponseEntity<>(dtoList.getContent(), HeaderUtil.createPaginationHeader(dtoList), HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/v1/obstetric-plans/{obstetricPlanId}/consultation-info",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PregnancyConsultationInfoDTO> create(@PathVariable("obstetricPlanId") Long obstetricPlanId, @RequestBody PregnancyConsultationInfoDTO dto) throws IOException {
        PregnancyConsultationInfoDTO newPregnancy = this.pregnancyConsultationInfoService.create(dto, obstetricPlanId);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPregnancy);

    }
    @Timed
    @RequestMapping(value = "/v1/obstetric-plans/{obstetricPlanId}/consultation-info/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PregnancyConsultationInfoDTO> update(@PathVariable("obstetricPlanId") Long obstetricPlanId, @RequestBody PregnancyConsultationInfoDTO dto, @PathVariable("id") long id)
            throws IOException {
        PregnancyConsultationInfoDTO updatedPregnancyPlan = this.pregnancyConsultationInfoService.update(id, dto, obstetricPlanId);
        return ResponseEntity.status(HttpStatus.OK).body(updatedPregnancyPlan);
    }

    @Timed
    @RequestMapping(value = "/v1/obstetric-plans/{obstetricPlanId}/consultation-info/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PregnancyConsultationInfoDTO> findById(@PathVariable("obstetricPlanId") Long obstetricPlanId, @PathVariable("id") Long id) throws IOException{
        PregnancyConsultationInfoDTO persistedDTO = this.pregnancyConsultationInfoService.findById(id,obstetricPlanId);
        return new ResponseEntity<>(persistedDTO, HttpStatus.OK);
    }

    @Timed
    @RequestMapping(value = "/v1/obstetric-plans/{obstetricPlanId}/consultation-info/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<HttpStatus> delete(@PathVariable("obstetricPlanId") long obstetricPlanId,@PathVariable("id") Long id) throws IOException {
        this.pregnancyConsultationInfoService.delete(id,obstetricPlanId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

}


