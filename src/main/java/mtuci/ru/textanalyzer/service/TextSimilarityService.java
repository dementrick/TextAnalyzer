package mtuci.ru.textanalyzer.service;

import lombok.RequiredArgsConstructor;
import mtuci.ru.textanalyzer.Utils.StopWordsLoader;
import mtuci.ru.textanalyzer.model.SimilarityResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TextSimilarityService {

    private final Set<String> stopWords = StopWordsLoader.loadStopWords("stop-words.txt");

    @Value("${mystem.path}")
    private String myStemPath;

    public SimilarityResult compareTexts(String text1, String text2) {
        text1 = preprocess(text1);
        text2 = preprocess(text2);

        String[] words1 = text1.split("\\s+");
        String[] words2 = text2.split("\\s+");

        Map<String, Double> vector1 = buildWordFrequencyVector(words1);
        Map<String, Double> vector2 = buildWordFrequencyVector(words2);

        double similarity = calculateCosineSimilarity(vector1, vector2);
        String interpretation = interpret(similarity);

        return new SimilarityResult(similarity * 100, interpretation);
    }

    private String preprocess(String text) {
        text = lemmatizeText(text);
        text = text.toLowerCase().replaceAll("[^a-zA-Zа-яА-Я\\s]", "");
        return text;
    }

    private Map<String, Double> buildWordFrequencyVector(String[] words) {
        Map<String, Double> wordFrequency = new HashMap<>();
        for (String word : words) {
            if (!stopWords.contains(word) && !word.isBlank()) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0.0) + 1);
            }
        }
        return wordFrequency;
    }

    private double calculateCosineSimilarity(Map<String, Double> vector1, Map<String, Double> vector2) {
        double normA = Math.sqrt(vector1.values().stream().mapToDouble(v -> v * v).sum());
        double normB = Math.sqrt(vector2.values().stream().mapToDouble(v -> v * v).sum());

        double dotProduct = vector1.entrySet().stream()
                .filter(e -> vector2.containsKey(e.getKey()))
                .mapToDouble(e -> e.getValue() * vector2.get(e.getKey()))
                .sum();

        if (normA == 0 || normB == 0) return 0.0;

        return dotProduct / (normA * normB);
    }

    private String lemmatizeText(String text) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(myStemPath, "-n", "-l", "-e", "utf-8");
            // НЕ сливаем потоки ошибок и вывода
            // processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            // Поток для чтения stdout (результат лемматизации)
            StringBuilder lemmatizedText = new StringBuilder();
            Thread stdoutThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("[mystem stdout] " + line);
                        if (!line.trim().isEmpty() && !line.startsWith("{")) {
                            synchronized (lemmatizedText) {
                                lemmatizedText.append(line.split("\\|")[0].split("\\?")[0].trim()).append(" ");
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stdoutThread.start();

            // Поток для чтения stderr (ошибки)
            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println("[mystem error] " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            stderrThread.start();

            // Записываем входной текст в процесс
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write(text);
                writer.flush();
            }

            // Ждем завершения процесса и потоков чтения
            int exitCode = process.waitFor();
            stdoutThread.join();
            stderrThread.join();

            if (exitCode != 0) {
                System.err.println("mystem завершился с кодом " + exitCode);
            }

            return lemmatizedText.toString().trim();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return text;  // Возвращаем исходный текст при ошибке
        }
    }

    private String interpret(double similarity) {
        if (similarity > 0.8) return "Очень похожие тексты";
        if (similarity > 0.5) return "Похожие тексты";
        if (similarity > 0.3) return "Сходство низкое";
        return "Очень разные тексты";
    }
}

