package mtuci.ru.textanalyzer.Utils;

import lombok.RequiredArgsConstructor;
import mtuci.ru.textanalyzer.service.TextClassificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class TrainingDataLoader implements CommandLineRunner {

    private final TextClassificationService classificationService;

    @Override
    public void run(String... args) throws Exception {
        loadTrainingData("финансы", "training/finance.txt");
        loadTrainingData("медицина", "training/medicine.txt");
        loadTrainingData("спорт", "training/sport.txt");
    }

    private void loadTrainingData(String category, String resourcePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(resourcePath);
        String text = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        classificationService.addTrainingText(category, text);
        System.out.println("Дерево для категории '" + category + "' построено.");
    }
}
