/*
package com.cambofreelance.websiteservice.controllers;

import com.cambofreelance.websiteservice.logger.contants.ErrorCode;
import com.cambofreelance.websiteservice.logger.exceptions.MessageResponse;
import com.cambofreelance.websiteservice.model.dto.base.ApiPageResponse;
import com.cambofreelance.websiteservice.model.dto.request.ArticleRequest;
import com.cambofreelance.websiteservice.model.dto.request.BaseFilterRequest;
import com.cambofreelance.websiteservice.model.dto.response.ArticleResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<MessageResponse> getArticles(BaseFilterRequest request) {
        ApiPageResponse<ArticleResponse> response = articleService.getArticles(request);
        return ResponseEntity.ok(new MessageResponse(response, ErrorCode.SUCCESS));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // Handled by Keycloak Token
    public ResponseEntity<MessageResponse> createArticle(@RequestBody @Valid ArticleRequest request) {
        ArticleResponse response = articleService.createArticle(request);
        return new ResponseEntity<>(new MessageResponse(response, ErrorCode.SUCCESS), HttpStatus.CREATED);
    }
}*/
