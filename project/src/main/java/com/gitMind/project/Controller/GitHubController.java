package com.gitMind.project.Controller;

import com.gitMind.project.DTO.CommitResponse;
import com.gitMind.project.DTO.GitHubClientRequest;
import com.gitMind.project.Github.GitHubClient;
import com.gitMind.project.Github.GitHubProperties;
import com.gitMind.project.Service.GitHubService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/github")
@AllArgsConstructor
public class GitHubController {

    private final GitHubService gitHubService;

    @PostMapping("/commit")
    public CommitResponse commits(@RequestBody GitHubClientRequest request) {
        return gitHubService.getCommit(request);
    }

}
