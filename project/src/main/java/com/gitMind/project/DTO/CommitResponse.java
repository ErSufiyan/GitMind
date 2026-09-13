package com.gitMind.project.DTO;

import lombok.Data;

import java.util.List;


//this class represent the response we return to the user
@Data
public class CommitResponse {

    private String sha;

    private List<FileChangeResponse> files;

    private String authorName;

    private String authorEmail;

    private String message;

    private String commitDate;

    private String githubUsername;
}
