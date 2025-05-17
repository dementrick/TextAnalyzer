package mtuci.ru.textanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZipfWordFrequency {
    private String word;
    private double actualFrequency;
    private double expectedZipfFrequency;
}
