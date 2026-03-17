package com.chinonso.university_scheduling.service;

import com.chinonso.university_scheduling.entity.ClassSection;
import com.chinonso.university_scheduling.entity.Schedule;
import com.chinonso.university_scheduling.exception.ResourceNotFoundException;
import com.chinonso.university_scheduling.repository.ClassSectionRepository;
import com.chinonso.university_scheduling.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ClassSectionRepository classSectionRepository;

    public ScheduleService(ScheduleRepository scheduleRepository,
                           ClassSectionRepository classSectionRepository) {
        this.scheduleRepository = scheduleRepository;
        this.classSectionRepository = classSectionRepository;
    }

    // ── READ ───────────────────────────────────────────────────────────────────
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    public Schedule findById(Integer id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with id: " + id));
    }

    public List<Schedule> findByClassSection(Integer classSectionId) {
        ClassSection cs = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Class section not found with id: " + classSectionId));
        return scheduleRepository.findByClassSection(cs);
    }

    public List<Schedule> findByDayOfWeek(Schedule.DayOfWeek day) {
        return scheduleRepository.findByDayOfWeek(day);
    }

    /** Returns any schedules that conflict with the given room / day / time window. */
    public List<Schedule> findRoomConflicts(Integer roomId, Schedule.DayOfWeek day,
                                            LocalTime startTime, LocalTime endTime) {
        return scheduleRepository.findOverlappingRoomSchedules(roomId, day, startTime, endTime);
    }

    /** Returns any schedules that conflict with the given student / day / time window. */
    public List<Schedule> findStudentConflicts(Integer studentId, Schedule.DayOfWeek day,
                                               LocalTime startTime, LocalTime endTime) {
        return scheduleRepository.findOverlappingStudentSchedules(studentId, day, startTime, endTime);
    }

    // ── CREATE ─────────────────────────────────────────────────────────────────
    public Schedule create(Schedule schedule) {
        // Resolve class section
        if (schedule.getClassSection() != null && schedule.getClassSection().getClassId() != null) {
            ClassSection cs = classSectionRepository.findById(schedule.getClassSection().getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Class section not found with id: " + schedule.getClassSection().getClassId()));
            schedule.setClassSection(cs);
        }
        return scheduleRepository.save(schedule);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────────
    public Schedule update(Integer id, Schedule updated) {
        Schedule existing = findById(id);

        // Resolve class section if changed
        if (updated.getClassSection() != null && updated.getClassSection().getClassId() != null) {
            ClassSection cs = classSectionRepository.findById(updated.getClassSection().getClassId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Class section not found with id: " + updated.getClassSection().getClassId()));
            existing.setClassSection(cs);
        }

        existing.setDayOfWeek(updated.getDayOfWeek());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());
        existing.setFrequency(updated.getFrequency());

        return scheduleRepository.save(existing);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────────
    public void delete(Integer id) {
        if (!scheduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Schedule not found with id: " + id);
        }
        scheduleRepository.deleteById(id);
    }
}
