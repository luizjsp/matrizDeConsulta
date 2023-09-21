package br.com.mv.clinic.dto.pregnancy_consultation_info;

import br.com.mv.clinic.dto.attendance.AttendanceMedicalRecordDTO;
import br.com.mv.clinic.util.DateDefaultDeserializer;
import br.com.mv.clinic.util.DateDefaultSerializer;
import br.com.mv.clinic.util.DateTimeDefaultDeserializer;
import br.com.mv.clinic.util.DateTimeDefaultSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PregnancyConsultationInfoDTO {
    private Long id;
    //@JsonDeserialize(using = DateTimeDefaultDeserializer.class, as = DateTime.class)
    //@JsonSerialize(using = DateTimeDefaultSerializer.class)
    @JsonDeserialize(using = DateDefaultDeserializer.class, as = Date.class)
    @JsonSerialize(using = DateDefaultSerializer.class)
    private Date dateInfo;
    private String complaint;
    private Integer gestationalAge;
    private Double weight;
    private Double imc;
    private Double height;
    private String edema;
    private Integer bloodPressureSistolica;
    private Integer bloodPressureDiastolica;
    private Double uterineHeight;
    private String fetalPresentation;
    private Integer bcf;
    private String fetalMovement;
    private String touch;
    private PatientInfoForPregnancyConsultationDTO obstetricPlan;
    private AttendanceInfoForPregnancyConsultation attendance;
    private boolean active;
    private boolean deleted;
    private String description;
    private String clientKey;
    private String createdBy;
    @JsonDeserialize(using = DateTimeDefaultDeserializer.class, as = DateTime.class)
    @JsonSerialize(using = DateTimeDefaultSerializer.class)
    private DateTime createdDate;

    private String uterineCurveSvg;

    private String nutritionalChartSvg;

}
