package com.tigerpms.chatTranslateApp.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class TextService {
    private final ObjectMapper objectMapper;


    public List<TextEntry.Message> textToParse (String text) {
        text = text.replace("\\n", "\n");
        List<TextEntry.Message> textList = new ArrayList<>();
        String[] lines = text.substring(1, text.length() - 1).split("\\n\\n");

        for (int id = 0; id < lines.length; id++) {
            String line = lines[id];
            if (line.trim().isEmpty())
                continue;

            int timeStart = line.indexOf("[");
            int timeEnd = line.indexOf("]");
            int colonIndex = line.substring(timeEnd + 1).indexOf(":") + timeEnd + 1;

            if (timeStart >= 0 && timeEnd > timeStart && colonIndex > timeEnd) {
                String time = line.substring(timeStart + 1, timeEnd).trim();
                String username = line.substring(timeEnd + 1, colonIndex).trim();
                String message = line.substring(colonIndex + 1).trim();
                textList.add(new TextEntry.Message(id, time, username, message));
            }
        }

        return textList;
    }

    public String parseToJSON (List<TextEntry.Message> textList) {
        try {
            TextEntry textEntry = new TextEntry(textList);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(textEntry);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing to JSON:");
        }
    }

    public String callPythonAPI(String json) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:5000").build();

        Mono<String> response = webClient.post()
                .uri("/api/text/process")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .bodyToMono(String.class);
        return response.block();
    }
}
