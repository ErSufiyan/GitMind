package com.gitMind.project.Github;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Getter
@Setter
@ConfigurationProperties(prefix = "github")
@Component
public class GitHubProperties {

    // properties from user to use github apis functionality
    private String apiUrl;
    private String token;
}
