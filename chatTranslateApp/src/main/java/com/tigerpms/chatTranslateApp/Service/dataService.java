package com.tigerpms.chatTranslateApp.Service;

import com.tigerpms.chatTranslateApp.Entity.ChatTranslate;
import com.tigerpms.chatTranslateApp.Repository.ChatTranslateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class dataService {
    private final ChatTranslateRepository chatTranslateRepository;

    public Integer checkWriteData(ChatTranslate chatTranslate) {
        if (chatTranslate.getMessage() == null || chatTranslate.getMessage().isEmpty() ||
        chatTranslate.getTranslatedMessage() == null || chatTranslate.getTranslatedMessage().isEmpty() ||
        checkDupes(chatTranslate) == 1) {
            System.out.println("Invalid data");
            return 0;
        }
        return 1;
    }

    public Integer checkDupes(ChatTranslate chatTranslate) {
        Optional<ChatTranslate> res = chatTranslateRepository.findByMessage(chatTranslate.getMessage());
        if (res.isPresent())
            return 1;
        return 0;
    }

    public Integer checkIdIsPresent(Integer id) {
        if (chatTranslateRepository.findById(id).isEmpty() || id < 1)
            return 0;
        return 1;
    }

    public Integer checkModifyData(Integer id, ChatTranslate chatTranslate) {
        if (chatTranslate.getId() == null || chatTranslate.getId() < 1
                || chatTranslate.getMessage() == null || chatTranslate.getMessage().isEmpty()
                || chatTranslate.getTranslatedMessage() == null || chatTranslate.getTranslatedMessage().isEmpty()) {
            System.out.println("Invalid data");
            return 0;
        }
        ChatTranslate existingData = chatTranslateRepository.findById(id).orElse(null);
        if (existingData == null) {
            System.out.println("Data not found");
            return 0;
        }
        if (existingData.getMessage().equals(chatTranslate.getMessage())) {
            return 1;
        }
        if (checkDupes(chatTranslate) == 1) {
            System.out.println("Duplicate message detected");
            return 0;
        }

        return 1;
    }
}
