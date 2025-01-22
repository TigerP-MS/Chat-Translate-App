package com.tigerpms.chatTranslateApp.Service;

import lombok.Getter;
import java.util.List;

@Getter
public class TextEntry {
    private List<Message> data;

    public TextEntry(List<Message> data) {
        this.data = data;
    }

    @Getter
    public static class Message {
        private Integer id;
        private String time;
        private String username;
        private String message;

        public Message(Integer id, String time, String username, String message) {
            this.id = id;
            this.time = time;
            this.username = username;
            this.message = message;
        }
    }
}
