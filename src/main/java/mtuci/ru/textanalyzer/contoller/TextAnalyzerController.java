package mtuci.ru.textanalyzer.contoller;

import lombok.RequiredArgsConstructor;
import mtuci.ru.textanalyzer.model.ClassificationResult;
import mtuci.ru.textanalyzer.model.CompareRequest;
import mtuci.ru.textanalyzer.model.SimilarityResult;
import mtuci.ru.textanalyzer.model.ZipfAnalysisResult;
import mtuci.ru.textanalyzer.service.TextClassificationService;
import mtuci.ru.textanalyzer.service.TextSimilarityService;
import mtuci.ru.textanalyzer.service.ZipfAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analyze")
public class TextAnalyzerController {

    private final ZipfAnalysisService zipfAnalysisService;
    private final TextSimilarityService textSimilarityService;
    private final TextClassificationService classificationService;

    @PostMapping("/zipf")
    public ResponseEntity<ZipfAnalysisResult> analyze(@RequestBody String text) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.badRequest().body(new ZipfAnalysisResult(List.of(), 0.0));
        }
        return ResponseEntity.ok(zipfAnalysisService.analyze(text));
    }

    @PostMapping("/similarity")
    public ResponseEntity<SimilarityResult> compareTexts(@RequestBody CompareRequest request) {
        SimilarityResult result = textSimilarityService.compareTexts(request.getText1(), request.getText2());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/classify")
    public ResponseEntity<ClassificationResult> classifyText(@RequestBody String text) {
        ClassificationResult result = classificationService.classifyText(text);
        return ResponseEntity.ok(result);
    }
}