package com.chinonso.university_scheduling.repository;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

        List<Schedule> findByClassSection(ClassSection classSection);

        List<Schedule> findByDayOfWeek(Schedule.DayOfWeek dayOfWeek);

        List<Schedule> findByClassSectionIn(List<ClassSection> classSections);

        @Query("""
                        SELECT s FROM Schedule s
                        WHERE s.classSection.room.roomId = :roomId
                          AND s.dayOfWeek = :day
                          AND s.startTime < :endTime
                          AND s.endTime > :startTime
                        """)
        List<Schedule> findOverlappingRoomSchedules(
                        @Param("roomId") Integer roomId,
                        @Param("day") Schedule.DayOfWeek day,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);

        @Query("""
                        SELECT s FROM Schedule s
                        WHERE s.classSection IN (
                            SELECT e.classSection FROM Enrolment e WHERE e.student.studentId = :studentId
                        )
                          AND s.dayOfWeek = :day
                          AND s.startTime < :endTime
                          AND s.endTime > :startTime
                        """)
        List<Schedule> findOverlappingStudentSchedules(
                        @Param("studentId") Integer studentId,
                        @Param("day") Schedule.DayOfWeek day,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);
}
