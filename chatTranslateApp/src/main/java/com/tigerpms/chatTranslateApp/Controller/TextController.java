package com.tigerpms.chatTranslateApp.Controller;

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
    public ResponseEntity<String> test(@RequestBody String text) {
        textService.textToJSON(text);
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }
}
