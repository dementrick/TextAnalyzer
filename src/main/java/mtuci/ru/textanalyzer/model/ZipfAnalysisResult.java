package mtuci.ru.textanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZipfAnalysisResult {

    private List<ZipfWordFrequency> wordFrequencies; // Частоты слов
    private double naturalnessPercentage; // Процент естественности текста

    public List<WordEntry> toWordEntries() {

        List<ZipfWordFrequency> sortedFrequencies = wordFrequencies.stream()
                .sorted((a, b) -> Double.compare(b.getActualFrequency(), a.getActualFrequency()))
                .toList();

        AtomicInteger rank = new AtomicInteger(1);
        return sortedFrequencies.stream()
                .map(wf -> new WordEntry(wf.getWord(), rank.getAndIncrement()))
                .collect(Collectors.toList());
    }
}