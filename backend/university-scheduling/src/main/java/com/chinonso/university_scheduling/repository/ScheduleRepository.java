package com.chinonso.university_scheduling.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.chinonso.university_scheduling.entity.Schedule;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

  // Get all schedules for a specific class
  List<Schedule> findByClassSection_ClassId(Integer classId);

  // Get all schedules for a specific day
  List<Schedule> findByDayOfWeek(Schedule.DayOfWeek dayOfWeek);

  @Query("""
          SELECT s FROM Schedule s
          WHERE s.classSection.room.roomId = :roomId
            AND s.dayOfWeek = :day
            AND s.startTime < :endTime
            AND s.endTime > :startTime
            AND s.scheduleId != :excludeScheduleId
      """)
  List<Schedule> findRoomConflicts(
      @Param("roomId") Long roomId,
      @Param("day") Schedule.DayOfWeek day,
      @Param("startTime") LocalTime startTime,
      @Param("endTime") LocalTime endTime,
      @Param("excludeScheduleId") Long excludeScheduleId);

  @Query("""
          SELECT s FROM Schedule s
          WHERE s.classSection.classId IN (
              SELECT e.classSection.classId FROM Enrolment e
              WHERE e.student.studentId = :studentId
                AND e.status = 'ENROLLED'
          )
      """)
  List<Schedule> findSchedulesByStudentId(@Param("studentId") Integer studentId);

  @Query("""
          SELECT s FROM Schedule s
          WHERE s.classSection.classId IN (
              SELECT ct.classSection.classId FROM ClassTeacher ct
              WHERE ct.teacher.teacherId = :teacherId
          )
      """)
  List<Schedule> findSchedulesByTeacherId(@Param("teacherId") Integer teacherId);
}
