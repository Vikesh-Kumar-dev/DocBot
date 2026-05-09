package com.example.DocBot.repository;

import com.example.DocBot.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Appointment> findByConfirmationCode(String confirmationCode);
}
