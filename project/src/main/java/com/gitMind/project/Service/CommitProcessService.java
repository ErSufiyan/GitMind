package com.gitMind.project.Service;

import com.gitMind.project.DTO.CommitResponse;
import com.gitMind.project.DTO.FileChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommitProcessService {

    private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
            // Java
            "java", "groovy",

            // JavaScript / TypeScript
            "js", "jsx", "ts", "tsx",

            // Python
            "py",

            // Go
            "go",

            // Rust
            "rs",

            // C / C++
            "c", "cpp", "cc", "h", "hpp",

            // C#
            "cs",

            // PHP
            "php",

            // Ruby
            "rb",

            // Swift
            "swift",

            // Kotlin
            "kt",

            // Scala
            "scala",

            // Config
            "xml", "yml", "yaml", "properties", "json",

            // SQL
            "sql",

            // Markdown
            "md"
    );

    private static final List<String> IGNORED_PATHS = List.of(
            ".idea/",
            ".git/",
            ".github/",
            ".mvn/",
            "target/",
            "build/",
            "node_modules/"
    );

    public CommitResponse response(CommitResponse res) {

        filterFiles(res);

        return res;
    }

    private void filterFiles(CommitResponse res) {
        List<FileChangeResponse> filterFiles =
                res.getFiles()
                        .stream()
                        .filter(file -> ! shouldSkip(file))
                        .toList();


        res.setFiles(filterFiles);
    }
    private boolean shouldSkip(FileChangeResponse file) {

        if(file.getPatch() == null || file.getPatch().isEmpty()) {
            return true;
        }

        String fileName = file.getFilename().toLowerCase();

        if (IGNORED_PATHS.stream().anyMatch(fileName::startsWith)) {
            return true;
        }

        return !SUPPORTED_EXTENSIONS.contains(getExtension(fileName));

    }

    private String getExtension(String fileName) {

        if(fileName == null) {
            return "";
        }

        int index = fileName.lastIndexOf(".");

        if(index == -1) {
            return "";
        }
        return fileName.substring(index + 1);
    }
}
