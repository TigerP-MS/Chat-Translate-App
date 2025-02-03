package com.tigerpms.chatTranslateApp.Service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
public class TextEntry {
    private List<Message> data;

    public TextEntry(List<Message> data) {
        this.data = data;
    }
    public TextEntry() {}

    @Getter
    @Setter
    public static class Message {
        private Integer id;
        private String time;
        private String username;
        private String message;
        private Integer isTranslated;

        public Message(Integer id, String time, String username, String message, Integer isTranslated) {
            this.id = id;
            this.time = time;
            this.username = username;
            this.message = message;
            this.isTranslated = isTranslated;
        }
        public Message() {}
    }
}
