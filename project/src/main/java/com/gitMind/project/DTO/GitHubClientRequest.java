package com.gitMind.project.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GitHubClientRequest {

    // this is a request from user and response to the github api url
    private String owner;
    private String repo;
    private String sha;
}
