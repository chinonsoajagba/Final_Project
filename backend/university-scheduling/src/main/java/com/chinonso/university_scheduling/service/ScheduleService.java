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

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Schedule getScheduleById(Integer id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Schedule not found with ID: " + id));
    }

    public List<Schedule> getSchedulesByClass(Integer classId) {
        return scheduleRepository.findByClassSection_ClassId(classId);
    }

    public List<Schedule> getSchedulesByDay(Schedule.DayOfWeek day) {
        return scheduleRepository.findByDayOfWeek(day);
    }

    public Schedule createSchedule(Schedule schedule, Integer classId) {
        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));
        schedule.setClassSection(classSection);

        if (!schedule.getEndTime().isAfter(schedule.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (classSection.getRoom() != null) {
            Integer roomId = classSection.getRoom().getRoomId();
            List<Schedule> conflicts = scheduleRepository.findRoomConflicts(
                    roomId.longValue(),
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    0L);

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

    public Schedule updateSchedule(Integer id, Schedule updated, Integer classId) {
        Schedule existing = getScheduleById(id);

        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class not found with ID: " + classId));
        existing.setClassSection(classSection);

        if (!updated.getEndTime().isAfter(updated.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        if (classSection.getRoom() != null) {
            Integer roomId = classSection.getRoom().getRoomId();
            List<Schedule> conflicts = scheduleRepository.findRoomConflicts(
                    roomId.longValue(),
                    updated.getDayOfWeek(),
                    updated.getStartTime(),
                    updated.getEndTime(),
                    existing.getScheduleId().longValue());

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

    public void deleteSchedule(Integer id) {
        Schedule schedule = getScheduleById(id);
        scheduleRepository.delete(schedule);
    }
}
