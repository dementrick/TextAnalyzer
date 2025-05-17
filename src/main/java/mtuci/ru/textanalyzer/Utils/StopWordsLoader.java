package mtuci.ru.textanalyzer.Utils;

import org.springframework.core.io.ClassPathResource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class StopWordsLoader {

    public static Set<String> loadStopWords(String filename) {
        Set<String> stopWords = new HashSet<>();
        try {
            var resource = new ClassPathResource(filename);
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    stopWords.add(line.trim().toLowerCase());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось загрузить слова с файла: " + filename, e);
        }
        return stopWords;
    }
}
