package com.gitMind.project.DTO;

import lombok.Data;

@Data
public class FileChangeResponse {

    private String filename;

    private String status;

    private Integer changes;

    private Integer additions;

    private Integer deletions;

    private String patch;
}
