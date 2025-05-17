package mtuci.ru.textanalyzer.contoller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TextAnalyzerUIController {

    @GetMapping("/analyze")
    public String home() {
        return "TextAnalyzerUI";
    }
}