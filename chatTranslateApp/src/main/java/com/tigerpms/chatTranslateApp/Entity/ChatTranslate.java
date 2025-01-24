package com.tigerpms.chatTranslateApp.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatTranslate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String message;
    private String translatedMessage;
}
