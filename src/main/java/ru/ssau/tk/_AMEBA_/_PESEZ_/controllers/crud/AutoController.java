package ru.ssau.tk._AMEBA_._PESEZ_.controllers.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class AutoController {
    @GetMapping
    public String welcome() {
        return "Welcome!";
    }
}
