package com.example.Qore.repository;

import com.example.Qore.model.ClassSession;
import com.example.Qore.model.Client;
import com.example.Qore.model.Discipline;
import com.example.Qore.model.Enum.EstadoSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ClassSessionRepository extends JpaRepository<ClassSession, Long> {
    boolean existsByRoomId(Long roomId);
    List<ClassSession> findByClients_Id(Long clientId);
    @Query("SELECT DISTINCT c FROM ClassSession c " +
            "LEFT JOIN FETCH c.repeatDays " +
            "LEFT JOIN FETCH c.clients " +
            "WHERE c.startDate BETWEEN :start AND :end")
    List<ClassSession> findByStartDateBetween(@Param("start") LocalDate start,
                                              @Param("end") LocalDate end);
    @Query("SELECT COUNT(c) FROM ClassSession c " +
            "WHERE c.startDate BETWEEN :start AND :end")
    long countClassesBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
           SELECT COUNT(cs)
           FROM ClassSession cs
           JOIN cs.clients c
           WHERE c.id = :clientId
             AND cs.startDate >= :start
             AND cs.startDate <= :end
           """)
    long countClassesByClientAndPeriod(@Param("clientId") Long clientId,
                                       @Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    @Query("""
    SELECT cs FROM ClassSession cs
    JOIN cs.clients c
    WHERE c.id = :clientId
    ORDER BY cs.startDate ASC, cs.startTime ASC
""")
    List<ClassSession> findAllByClientId(@Param("clientId") Long clientId);

    List<ClassSession> findByInstructorIdAndStartDateBetween(
            Long instructorId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<ClassSession> findByInstructorIdOrderByStartDateAscStartTimeAsc(Long instructorId);
    @Query("SELECT DISTINCT c FROM ClassSession c " +
            "JOIN FETCH c.discipline " +
            "JOIN FETCH c.room " +
            "LEFT JOIN FETCH c.clients " +
            "WHERE c.instructor.id = :instructorId " +
            "AND c.startDate BETWEEN :start AND :end " +
            "ORDER BY c.startDate ASC, c.startTime ASC")
    List<ClassSession> findByInstructorAndDateRange(
            @Param("instructorId") Long instructorId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
    long countByInstructorIdAndStartDateBetweenAndEstado(
            Long instructorId, LocalDate start, LocalDate end, EstadoSession estado);
    List<ClassSession> findByInstructorIdAndStartDateAndEstado(
            Long instructorId, LocalDate startDate, EstadoSession estado);


    @Query("SELECT DISTINCT c FROM ClassSession c " +
            "JOIN FETCH c.discipline " +
            "JOIN FETCH c.instructor " +
            "JOIN FETCH c.room " +
            "LEFT JOIN FETCH c.clients " +
            "WHERE c.discipline IN :disciplines " +
            "AND c.startDate BETWEEN :start AND :end")
    List<ClassSession> findByDisciplinesAndDateRange(
            @Param("disciplines") List<Discipline> disciplines,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
    @Query("SELECT c FROM ClassSession cs JOIN cs.clients c WHERE cs.id = :classId")
    List<Client> findClientsByClassId(@Param("classId") Long classId);

    @Query("SELECT s FROM ClassSession s JOIN s.clients c WHERE c.id = :clientId")
    List<ClassSession> findByClientId(@Param("clientId") Long clientId);

    @Query("SELECT c FROM ClassSession c " +
            "JOIN c.clients cl " +
            "JOIN FETCH c.instructor " +
            "JOIN FETCH c.discipline " +
            "WHERE cl.id = :clientId " +
            "AND c.startDate BETWEEN :startDate AND :endDate " +
            "ORDER BY c.startDate DESC, c.startTime DESC")
    List<ClassSession> findJoinedClassesByClientAndDateRange(
            @Param("clientId") Long clientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}
