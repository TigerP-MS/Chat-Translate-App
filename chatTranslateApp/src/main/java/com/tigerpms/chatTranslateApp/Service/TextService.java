package com.tigerpms.chatTranslateApp.Service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextService {
    public String textToJSON (String text) {
        System.out.println(text);
        text = text.replace("\\n", "\n");
        List<String[]> textList = new ArrayList<>();
        String[] lines = text.substring(1, text.length() - 1).split("\\n\\n");
        for (String line : lines) {
            if (line.trim().isEmpty())
                continue;
            int timeStart = line.indexOf("[");
            int timeEnd = line.indexOf("]");
            int colonIndex = line.substring(timeEnd + 1).indexOf(":") + timeEnd + 1;
            if (timeStart >= 0 && timeEnd > timeStart && colonIndex > timeEnd) {
                String time = line.substring(timeStart + 1, timeEnd).trim();
                String username = line.substring(timeEnd + 1, colonIndex).trim();
                String message = line.substring(colonIndex + 1).trim();
                textList.add(new String[]{time, username, message});
                System.out.println(time + "\n" + username + "\n" + message);
            }
        }
        return text;
    }
}
