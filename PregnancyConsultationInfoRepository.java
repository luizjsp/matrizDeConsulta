package br.com.mv.clinic.repository.pregnancy_consultation_info;

import br.com.mv.clinic.domain.PregnancyConsultationInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PregnancyConsultationInfoRepository extends JpaRepository <PregnancyConsultationInfo, Long>, JpaSpecificationExecutor<PregnancyConsultationInfo> {

    public Optional<PregnancyConsultationInfo> findByIdAndClientKeyAndObstetricPlanIdAndDeletedIsFalse(Long id, String clientKey,Long obstetricPlanId);
    public Page<PregnancyConsultationInfo> findAllByClientKeyAndObstetricPlanIdAndDeletedIsFalse(Pageable pageable, String clientKey, Long obstetricPlanId);

    public Optional<PregnancyConsultationInfo> findFirstByDateInfoAndClientKeyAndObstetricPlanIdAndDeletedIsFalse(Date dateInfo, String clientKey, Long obstetricPLanId);

    List<PregnancyConsultationInfo> findAllByAttendanceIdAndClientKeyAndDeletedIsFalse(Long attendanceId, String clientKey);

    @Query(value = "SELECT * FROM pregnancy_consultation_info ci " +
            "WHERE ci.attendance_id = :attendanceId " +
            "ORDER BY ci.date_info DESC LIMIT 1", nativeQuery = true)
    Optional<PregnancyConsultationInfo> getCurrentConsultationInfo(@Param("attendanceId") Long attendanceId);

    @Modifying
    @Query(value = "UPDATE pregnancy_consultation_info ci " +
            "SET ci.attendance_medical_record_id = :medicalRecordId " +
            "WHERE ci.id = :id", nativeQuery = true)
    void updateMedicalRecordId(@Param("medicalRecordId") Long medicalRecordId, @Param("id") Long id);

    @Query(value = "SELECT * \n" +
            "FROM pregnancy_consultation_info ci\n" +
            "WHERE ci.obstetric_plan_id = :obstetricPlanId\n" +
            "AND ci.active = true AND ci.deleted = false\n" +
            "ORDER BY ci.date_info DESC\n" +
            "LIMIT 1", nativeQuery = true)
    Optional<PregnancyConsultationInfo> getLatestConsultationInfoByObstetricPlanId(@Param("obstetricPlanId") Long obstetricPlanId);

    @Query(value = "SELECT * \n" +
            "FROM pregnancy_consultation_info ci\n" +
            "WHERE ci.obstetric_plan_id = :obstetricPlanId\n" +
            "AND ci.deleted = false\n" +
            "ORDER BY ci.date_info DESC\n" +
            "LIMIT 1", nativeQuery = true)
    Optional<PregnancyConsultationInfo> getLatestConsultationInfoByObstetricPlanIdWithAnyStatusActiveOrNotExecptDeleted(@Param("obstetricPlanId") Long obstetricPlanId);

    List<PregnancyConsultationInfo> findAllByObstetricPlanIdAndDeletedIsFalseAndActiveIsTrue(Long obstetricPlanId);

    List<PregnancyConsultationInfo> findAllByObstetricPlanIdAndDeletedIsFalse(Long obstetricPlanId);

}
