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

    // KEY QUERY: Check if a room is already booked at a given day/time
    // Used for room conflict detection in Day 5
    @Query("""
                SELECT s FROM Schedule s
                WHERE s.classSection.room.roomId = :roomId
                  AND s.dayOfWeek = :day
                  AND s.startTime < :endTime
                  AND s.endTime > :startTime
                  AND (:excludeClassId IS NULL OR s.classSection.classId != :excludeClassId)
            """)
    List<Schedule> findRoomConflicts(
            @Param("roomId") Integer roomId,
            @Param("day") Schedule.DayOfWeek day,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeClassId") Integer excludeClassId);

    // KEY QUERY: Get all schedules for classes a student is enrolled in
    // Used for student time clash detection in Day 5
    @Query("""
                SELECT s FROM Schedule s
                WHERE s.classSection.classId IN (
                    SELECT e.classSection.classId FROM Enrolment e
                    WHERE e.student.studentId = :studentId
                      AND e.status = 'ENROLLED'
                )
            """)
    List<Schedule> findSchedulesByStudentId(@Param("studentId") Integer studentId);
}
