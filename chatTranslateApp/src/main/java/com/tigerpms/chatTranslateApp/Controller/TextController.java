package com.tigerpms.chatTranslateApp.Controller;

import com.tigerpms.chatTranslateApp.Repository.ChatTranslateRepository;
import com.tigerpms.chatTranslateApp.Service.TextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class TextController {
    private final TextService textService;

    @PostMapping("/api/text/translate")
    public ResponseEntity<String> textTranslate(@RequestBody String text) {
        System.out.println("Received text to translate:");

        String json = textService.parseToJSON(textService.textToParse(text));
        String translatedData = textService.callPythonAPI(json);
        textService.saveMessage(translatedData);

        System.out.println("Translated text successfully:");
        return ResponseEntity.status(HttpStatus.OK).body(translatedData);
    }
}
