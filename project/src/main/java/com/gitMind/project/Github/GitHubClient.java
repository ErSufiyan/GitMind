package com.gitMind.project.Github;


import com.gitMind.project.DTO.GitHubClientRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@AllArgsConstructor
public class GitHubClient {

    private final RestClient restClient;
    private final GitHubProperties gitHubProperties;

    // this method is takes a GitHubClientRequest as request and generate a github api url to get commits
    public String getCommit(GitHubClientRequest request) {
        System.out.println("API URL = " + gitHubProperties.getApiUrl());
        String url = gitHubProperties.getApiUrl()
                +"/repos/"
                + request.getOwner()
                +"/"
                + request.getRepo()
                + "/commits/"
                + request.getSha();

        System.out.println("url:"+url);


        // restClient is used to request to the server and fetch data from there ( from https://api.github/...etc)
        return restClient.get()
                .uri(url)
                .header("Authorization","Bearer "+gitHubProperties.getToken())
                .header("Accept","application/vnd.github+json")
                .retrieve()
                .body(String.class);

    }
}
