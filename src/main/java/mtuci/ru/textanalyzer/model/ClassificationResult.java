package mtuci.ru.textanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificationResult {
    private String category;       // Категория текста
    private String interpretation;  // Интерпретация результата
}
