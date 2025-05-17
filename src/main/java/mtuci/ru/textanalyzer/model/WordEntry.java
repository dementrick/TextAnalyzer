package mtuci.ru.textanalyzer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"word"})
public class WordEntry implements Comparable<WordEntry> {
    private String word;
    private int rank;

    @Override
    public int compareTo(WordEntry other) {
        int cmp = Integer.compare(this.rank, other.rank);
        if (cmp != 0) return cmp;
        return this.word.compareTo(other.word);
    }
}