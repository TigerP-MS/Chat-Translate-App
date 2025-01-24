package com.tigerpms.chatTranslateApp.Repository;

import com.tigerpms.chatTranslateApp.Entity.ChatTranslate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatTranslateRepository extends JpaRepository<ChatTranslate, Integer> {
    Optional<ChatTranslate> findByMessage(String message);
}
