package mtuci.ru.textanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimilarityResult {
    private double cosineSimilarity;  // Значение сходства в %
    private String interpretation;    // Похожие, разные, очень разные
}
