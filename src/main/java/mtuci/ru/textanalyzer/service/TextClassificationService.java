package mtuci.ru.textanalyzer.service;

import lombok.RequiredArgsConstructor;
import mtuci.ru.textanalyzer.model.ClassificationResult;
import mtuci.ru.textanalyzer.model.WordEntry;
import mtuci.ru.textanalyzer.model.ZipfAnalysisResult;
import mtuci.ru.textanalyzer.model.ZipfWordFrequency;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextClassificationService {

    private final ZipfAnalysisService zipfAnalysisService;

    private final Map<String, TreeSet<WordEntry>> categoryTrees = new HashMap<>();

    public void addTrainingText(String category, String text) {
        ZipfAnalysisResult result = zipfAnalysisService.analyze(text);
        TreeSet<WordEntry> tree = new TreeSet<>(result.toWordEntries());

        // Если дерево для категории уже существует, то добавляем новые элементы, иначе создаем новое дерево
        categoryTrees.merge(category, tree, (existingTree, newTree) -> {
            existingTree.addAll(newTree); // Добавляем все новые элементы в существующее дерево
            return existingTree; // Возвращаем обновленное дерево
        });
    }

    public ClassificationResult classifyText(String text) {
        if (categoryTrees.isEmpty()) {
            return new ClassificationResult("Категории не заданы", "");
        }

        ZipfAnalysisResult result = zipfAnalysisService.analyze(text);
        Set<String> inputWords = result.getWordFrequencies().stream()
                .map(ZipfWordFrequency::getWord)
                .collect(Collectors.toSet());

        String bestCategory = null;
        double bestMatchPercentage = 0.0;
        int maxMatches = 0;
        int totalWords = inputWords.size();

        for (Map.Entry<String, TreeSet<WordEntry>> entry : categoryTrees.entrySet()) {
            String category = entry.getKey();
            Set<String> knownWords = entry.getValue().stream()
                    .map(WordEntry::getWord)
                    .collect(Collectors.toSet());

            int matches = 0;
            for (String word : inputWords) {
                if (knownWords.contains(word)) {
                    matches++;
                }
            }

            double matchPercentage = (double) matches / totalWords;

            if (matchPercentage > bestMatchPercentage) {
                bestMatchPercentage = matchPercentage;
                bestCategory = category;
            }
        }

        if (bestCategory == null || bestMatchPercentage < 0.3) {
            return new ClassificationResult("Не определено", "Категория не определена");
        }

        String interpretation = interpret(bestCategory, bestMatchPercentage);
        return new ClassificationResult(bestCategory, interpretation);
    }

    private String interpret(String category, double matchPercentage) {
        if (matchPercentage > 0.8) {
            return "Текст точно относится к категории " + category ;
        }
        if (matchPercentage > 0.6) {
            return "Скорее всего текст относится к категории " + category;
        }
        if (matchPercentage > 0.3) {
            return "Возможно текст относится к категории " + category;
        }
        return "Категория не определена";
    }
}
