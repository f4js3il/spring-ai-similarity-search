package org.hola.com.springaiholaguy.controller;

import org.hola.com.springaiholaguy.service.SimilarityService;
import org.hola.com.springaiholaguy.utils.MatchResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/similarity")
public class SimilarityController {
    private final SimilarityService similarityService;

    public SimilarityController(SimilarityService similarityService) {
        this.similarityService = similarityService;
    }

    @PostMapping("/match")
    public ResponseEntity<MatchResult> getBestMatch(@RequestParam String query, @RequestBody List<String> options) {
        return ResponseEntity.status(HttpStatus.OK).body(similarityService.findBestMatch(query, options));
    }

}
