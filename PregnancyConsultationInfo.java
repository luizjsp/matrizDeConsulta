package br.com.mv.clinic.domain;


import br.com.mv.clinic.domain.attendance.Attendance;
import br.com.mv.clinic.domain.attendance.AttendanceMedicalRecord;
import br.com.mv.clinic.domain.obstetric_plan.ObstetricPlan;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="pregnancy_consultation_info")
public class PregnancyConsultationInfo extends AbstractAuditingEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	//@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name = "date_info")
	private Date dateInfo;
	@Column(name = "complaint", nullable = false)
	private String complaint;
	@Column(name = "gestational_age",nullable = false)
	private Integer gestationalAge;
	@Column(name = "weight", nullable = false)
	private Double weight;
	@Column(name = "imc", nullable = false)
	private Double imc;
	@Column(name = "height", nullable = false)
	private Double height;
	@Column(name = "edema", nullable = false)
	private String edema;
	@Column(name = "blood_pressure_sistolica", nullable = false)
	private Integer bloodPressureSistolica;
	@Column(name = "blood_pressure_diastolica", nullable = false)
	private Integer bloodPressureDiastolica;
	@Column(name = "uterine_height", nullable = false)
	private Double uterineHeight;
	@Column(name = "fetal_presentation", nullable = false)
	private String fetalPresentation;
	@Column(name = "bcf", nullable = false)
	private Integer bcf;
	@Column(name = "fetal_movement", nullable = false)
	private String fetalMovement;
	@Column(name = "touch", nullable = false)
	private String touch;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "obstetric_plan_id", nullable = false, referencedColumnName = "id")
	private ObstetricPlan obstetricPlan;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "attendance_id", nullable = false, referencedColumnName = "id")
	private Attendance attendance;
	@Column(nullable = false)
	private boolean active;
	@Column(nullable = false)
	private String description;
	@Column(name = "client_key", nullable = false)
	private String clientKey;

	@Column(name = "uterine_curve_svg")
	private String uterineCurveSvg;

	@Column(name = "nutritional_chart_svg")
	private String nutritionalChartSvg;

}
