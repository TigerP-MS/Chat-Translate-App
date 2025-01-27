package com.tigerpms.chatTranslateApp.Controller;

import com.tigerpms.chatTranslateApp.Entity.ChatTranslate;
import com.tigerpms.chatTranslateApp.Repository.ChatTranslateRepository;
import com.tigerpms.chatTranslateApp.Service.dataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class dataController {
    private final ChatTranslateRepository chatTranslateRepository;
    private final dataService dataService;

    @GetMapping("/api/data")
    public String data(Model model) {
        var res = chatTranslateRepository.findAll();
        model.addAttribute("items", res);
        return "data";
    }

    @GetMapping("/api/data/write")
    public String write() {
        return "write";
    }

    @PostMapping("/api/data/write")
    public String write(@ModelAttribute ChatTranslate chatTranslate) {
        if (dataService.checkWriteData(chatTranslate) == 1) {
            chatTranslateRepository.save(chatTranslate);
            System.out.println("Data saved successfully");
            return "redirect:/api/data";
        }
        return "redirect:/api/write";
    }

    @DeleteMapping("/api/data/delete/{id}")
    public ResponseEntity<Object> delete(@PathVariable Integer id) {
        if (dataService.checkIdIsPresent(id) == 1) {
            chatTranslateRepository.deleteById(id);
            System.out.println("Data deleted successfully: id=" + id);
            return ResponseEntity.status(HttpStatus.OK).body("deleted");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("id not found");
    }

    @GetMapping("/api/data/modify/{id}")
    public String modify(@PathVariable Integer id, Model model) {
        if (dataService.checkIdIsPresent(id) == 1)
            model.addAttribute("item", chatTranslateRepository.findById(id).get());
        return "modify";
    }

    @PostMapping("/api/data/modify/{id}")
    public String modify(@PathVariable Integer id, @ModelAttribute ChatTranslate chatTranslate) {
        if (dataService.checkModifyData(id, chatTranslate) == 1) {
            chatTranslateRepository.save(chatTranslate);
            System.out.println("Data updated successfully: id=" + id);
            return "redirect:/api/data";
        }
        return "redirect:/api/data/modify/" + id;
    }
}
