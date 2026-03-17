package com.chinonso.university_scheduling.service;

import org.springframework.stereotype.Service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Schedule;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.exception.SchedulingConflictException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.ScheduleRepository;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassSectionRepository classSectionRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
            ClassSectionRepository classSectionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.classSectionRepository = classSectionRepository;
    }

    // GET ALL
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    // GET BY ID
    public Schedule getScheduleById(Integer id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Schedule not found with ID: " + id));
    }

    // GET BY CLASS
    public List<Schedule> getSchedulesByClass(Integer classId) {
        return scheduleRepository.findByClassSection_ClassId(classId);
    }

    // GET BY DAY
    public List<Schedule> getSchedulesByDay(Schedule.DayOfWeek day) {
        return scheduleRepository.findByDayOfWeek(day);
    }

    // CREATE — checks for room conflicts before saving
    public Schedule createSchedule(Schedule schedule, Integer classId) {
        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));
        schedule.setClassSection(classSection);

        // Validate that end time is after start time
        if (!schedule.getEndTime().isAfter(schedule.getStartTime())) {
            throw new IllegalArgumentException(
                    "End time must be after start time");
        }

        // Check for room conflict — only if the class has a room assigned
        if (classSection.getRoom() != null) {
            Integer roomId = classSection.getRoom().getRoomId();
            List<Schedule> conflicts = scheduleRepository.findRoomConflicts(
                    roomId,
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    classId // exclude the current class itself
            );

            if (!conflicts.isEmpty()) {
                Schedule conflict = conflicts.get(0);
                throw new SchedulingConflictException(
                        "Room conflict: " + classSection.getRoom().getRoomCode() +
                                " is already booked on " + schedule.getDayOfWeek() +
                                " from " + conflict.getStartTime() + " to " + conflict.getEndTime());
            }
        }

        return scheduleRepository.save(schedule);
    }

    // UPDATE
    public Schedule updateSchedule(Integer id, Schedule updated, Integer classId) {
        Schedule existing = getScheduleById(id);

        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));
        existing.setClassSection(classSection);

        if (!updated.getEndTime().isAfter(updated.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Check room conflict — exclude this schedule's own class
        if (classSection.getRoom() != null) {
            Integer roomId = classSection.getRoom().getRoomId();
            List<Schedule> conflicts = scheduleRepository.findRoomConflicts(
                    roomId,
                    updated.getDayOfWeek(),
                    updated.getStartTime(),
                    updated.getEndTime(),
                    classId);

            if (!conflicts.isEmpty()) {
                Schedule conflict = conflicts.get(0);
                throw new SchedulingConflictException(
                        "Room conflict: " + classSection.getRoom().getRoomCode() +
                                " is already booked on " + updated.getDayOfWeek() +
                                " from " + conflict.getStartTime() + " to " + conflict.getEndTime());
            }
        }

        existing.setDayOfWeek(updated.getDayOfWeek());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setFrequency(updated.getFrequency());

        return scheduleRepository.save(existing);
    }

    // DELETE
    public void deleteSchedule(Integer id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(schedule);
    }
}
