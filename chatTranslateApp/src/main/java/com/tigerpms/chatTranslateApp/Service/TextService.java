package com.tigerpms.chatTranslateApp.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tigerpms.chatTranslateApp.Entity.ChatTranslate;
import com.tigerpms.chatTranslateApp.Repository.ChatTranslateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class TextService {
    private final ObjectMapper objectMapper;
    private final ChatTranslateRepository chatTranslateRepository;
    private final TextEntry textEntry;

    public List<TextEntry.Message> textToParse (String text) {
        text = text.replace("\\n", "\n");
        List<TextEntry.Message> textList = new ArrayList<>();
        String[] lines = new String[0];
        if (text.contains("["))
            lines = text.substring(text.indexOf("[")).split("\\[");
        for (int i = 0; i < lines.length; i++) {
            lines[i] = "[" + lines[i];
        }

        for (int id = 1; id < lines.length; id++) {
            String line = lines[id];
            System.out.println(line);
            if (line.trim().isEmpty())
                continue;

            int timeStart = line.indexOf("[");
            int timeEnd = line.indexOf("]");
            int colonIndex = line.substring(timeEnd + 1).indexOf(":") + timeEnd + 1;

            if (timeStart >= 0 && timeEnd > timeStart && colonIndex > timeEnd) {
                String time = line.substring(timeStart + 1, timeEnd).trim();
                String username = line.substring(timeEnd + 1, colonIndex).trim();
                if (username.trim().isEmpty())
                    continue;
                String message = line.substring(colonIndex + 1).trim();
                textList.add(new TextEntry.Message(id, time, username, message, 0));
            }
        }

        System.out.println("Parsed text:");
        for (TextEntry.Message message : textList) {
            System.out.println(message.getId() + " " + message.getTime() + " " + message.getUsername() + " " + message.getMessage() + " " + message.getIsTranslated());
        }
        return textList;
    }


    public String parseToJSON (List<TextEntry.Message> textList) {
        try {
            setTranslatedStatus(textList);
            textEntry.setData(textList);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(textEntry);
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing to JSON:");
        }
    }

    // 리스트 형식의 String를 JSON으로 변환
    public List<TextEntry.Message> parseFromJSON(String translatedData) {
        try {
            if (translatedData == null || translatedData.trim().isEmpty()) {
                throw new IllegalArgumentException("Input JSON is null or empty.");
            }

            System.out.println("Parsing JSON: " + translatedData);

            TextEntry textEntryObj = objectMapper.readValue(translatedData, TextEntry.class);
            return textEntryObj.getData();

        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input: " + e.getMessage());
            throw e;

        } catch (Exception e) {
            System.err.println("Error while parsing JSON: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("Error while parsing JSON to TextEntry: " + e.getMessage());
        }
    }

    public String callPythonAPI(String json) {
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:5000").build();

        System.out.println("Calling Python API with:");

        String response = webClient.post()
                .uri("/api/text/process")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        clientResponse -> {
                            System.out.println("Error: " + clientResponse.statusCode());
                            return clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("API Error: " + errorBody)));
                        }
                )
                .bodyToMono(String.class)
                .block();

        System.out.println("Response from Python API:" + response);

        return response;
    }

    public void saveMessage(String translatedData) {
        List<String> messages = new ArrayList<>();
        List<String> translatedMessages = new ArrayList<>();
        List<Integer> isTranslated = new ArrayList<>();
        String originalMessage;
        String translatedMessage;


        for (TextEntry.Message message : textEntry.getData()) {
            messages.add(message.getMessage());
            isTranslated.add(message.getIsTranslated());
        }
        for (TextEntry.Message message : parseFromJSON(translatedData))
            translatedMessages.add(message.getMessage());
        for (int i = 0; i < messages.size(); i++) {
            originalMessage = messages.get(i);
            translatedMessage = translatedMessages.get(i);
            if (isTranslated.get(i) == 0 && checkIfTranslated(originalMessage) == 0) {
                ChatTranslate chatTranslate = new ChatTranslate();
                chatTranslate.setMessage(originalMessage);
                chatTranslate.setTranslatedMessage(translatedMessage);

                chatTranslateRepository.save(chatTranslate);
            }
        }
    }

    public Integer checkIfTranslated(String message) {
        Optional<ChatTranslate> existing = chatTranslateRepository.findByMessage(message);
        if (existing.isPresent())
            return 1;
        else
            return 0;
    }

    public void setTranslatedStatus (List<TextEntry.Message> textList) {
        for (TextEntry.Message i : textList) {
            if (checkIfTranslated(i.getMessage()) == 1) {
                i.setIsTranslated(1);
                i.setMessage(chatTranslateRepository.findByMessage(i.getMessage()).get().getTranslatedMessage());
            }
            else {
                i.setIsTranslated(0);
            }
        }
    }
}
