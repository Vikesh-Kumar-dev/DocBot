package com.example.DocBot.repository;

import com.example.DocBot.model.ConversationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationLogRepository extends JpaRepository<ConversationLog, Long> {

    List<ConversationLog> findBySessionIdOrderByTimestampAsc(String sessionId);
}
