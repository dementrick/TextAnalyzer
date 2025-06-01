package mtuci.ru.textanalyzer.service;

import lombok.RequiredArgsConstructor;
import mtuci.ru.textanalyzer.Utils.StopWordsLoader;
import mtuci.ru.textanalyzer.model.ZipfAnalysisResult;
import mtuci.ru.textanalyzer.model.ZipfWordFrequency;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ZipfAnalysisService {

    private final Set<String> stopWords;
    
    public ZipfAnalysisService() {
        this.stopWords = StopWordsLoader.loadStopWords("stop-words.txt");
    }

    public ZipfAnalysisResult analyze(String text) {
        text = text.toLowerCase().replaceAll("[^a-zA-Zа-яА-Я\\s]", "");
        String[] words = text.split("\\s+");

        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String word : words) {
            if (!stopWords.contains(word)) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wordFrequency.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        List<String> labels = new ArrayList<>();
        List<Double> actualFreq = new ArrayList<>();
        List<Double> zipfFreq = new ArrayList<>();

        int limit = 100;
        double baseFreq = sorted.get(0).getValue();
        double totalZipfError = 0;
        double totalWords = 0;
        int rank = 1;

        for (int i = 0; i < Math.min(sorted.size(), limit); i++) {
            String word = sorted.get(i).getKey();
            double freq = sorted.get(i).getValue();
            double zipf = baseFreq / rank;

            labels.add(word);
            actualFreq.add(freq);
            zipfFreq.add(zipf);

            totalZipfError += Math.abs(freq - zipf);
            totalWords += freq;
            rank++;
        }

        // Построение списка частот
        List<ZipfWordFrequency> wordFrequencies = new ArrayList<>();
        for (int i = 0; i < labels.size(); i++) {
            wordFrequencies.add(new ZipfWordFrequency(labels.get(i), actualFreq.get(i), zipfFreq.get(i)));
        }

        double naturalnessPercentage = getNatutalnessPercentage(text);
        return new ZipfAnalysisResult(wordFrequencies, naturalnessPercentage);
    }

    public double getNatutalnessPercentage(String text) {
        text = text.toLowerCase().replaceAll("[^a-zA-Zа-яА-Я\\s]", "");
        String[] words = text.split("\\s+");

        Map<String, Integer> wordFrequency = new HashMap<>();
        for (String word : words) {
            if (!word.isEmpty()) {
                wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(wordFrequency.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int limit = 100;
        double baseFreq = sorted.get(0).getValue();
        double totalZipfError = 0;
        double totalWords = 0;
        int rank = 1;

        for (int i = 0; i < Math.min(sorted.size(), limit); i++) {
            double freq = sorted.get(i).getValue();
            double zipf = baseFreq / rank;

            totalZipfError += Math.abs(freq - zipf);
            totalWords += freq;
            rank++;
        }

        return  100 - (totalZipfError / totalWords * 100);
    }
}
