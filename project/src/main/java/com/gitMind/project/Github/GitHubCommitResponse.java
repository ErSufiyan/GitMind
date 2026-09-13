package com.gitMind.project.Github;

import com.gitMind.project.DTO.FileChangeResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


// this class is represents the data return by the github when we call it
@Data
@Getter
@Setter
public class GitHubCommitResponse {

    private String sha;

    private Commit commit;

    private Author author;

    private List<FileChangeResponse> files;

    @Data
    public static class Commit {

        private AuthorDetails author;

        private String message;
    }


    @Data
    public static class AuthorDetails {

        private String name;

        private String email;

        private String date;
    }


    @Data
    public static class Author {

        private String login;
    }
}
