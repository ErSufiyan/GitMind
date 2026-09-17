package com.gitMind.project.Service;

import com.gitMind.project.DTO.CommitResponse;
import com.gitMind.project.DTO.FileChangeResponse;
import com.gitMind.project.DTO.GitHubClientRequest;
import com.gitMind.project.Github.GitHubCommitResponse;
import com.gitMind.project.Github.GitHubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GitHubService {

    private final GitHubClient gitHubClient;
    private final ObjectMapper objectMapper;
    private final CommitProcessService commitProcessService;

    public CommitResponse getCommit(GitHubClientRequest request) {

        var response = gitHubClient.getCommit(request);

        try {
            GitHubCommitResponse gitHubCommitResponse =
                    objectMapper.readValue(response, GitHubCommitResponse.class);


            CommitResponse commitResponse = new CommitResponse();

            commitResponse.setSha(gitHubCommitResponse.getSha());
            commitResponse.setMessage(gitHubCommitResponse.getCommit().getMessage());
            commitResponse.setAuthorName(gitHubCommitResponse.getCommit().getAuthor().getName());
            commitResponse.setAuthorEmail(gitHubCommitResponse.getCommit().getAuthor().getEmail());
            commitResponse.setCommitDate(gitHubCommitResponse.getCommit().getAuthor().getDate());
            commitResponse.setGithubUsername(gitHubCommitResponse.getAuthor().getLogin());

            // this is used to map the raw files data into a filechange response (structure response)
            List<FileChangeResponse> files =
                    gitHubCommitResponse.getFiles()
                            .stream()
                            .map(
                                    file -> {

                                        FileChangeResponse fileChange = new FileChangeResponse();

                                        fileChange.setAdditions(file.getAdditions());
                                        fileChange.setDeletions(file.getDeletions());
                                        fileChange.setFilename(file.getFilename());
                                        fileChange.setStatus(file.getStatus());
                                        fileChange.setPatch(file.getPatch());
                                        fileChange.setChanges(file.getChanges());


                                        return fileChange;
                                    }
                            ).toList();

            commitResponse.setFiles(files);

            System.out.println("Processing commit...");
            return commitProcessService.response(commitResponse);

        }

        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
