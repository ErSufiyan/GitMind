package com.gitMind.project.Service;

import com.gitMind.project.DTO.CommitResponse;
import com.gitMind.project.DTO.FileChangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

            // SQL
            "sql"
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

    private static final int CONTEXT_LINES = 5;

    public CommitResponse response(CommitResponse res) {

        filterFiles(res);

        return res;
    }

    private void filterFiles(CommitResponse res) {

        List<FileChangeResponse> filteredFiles =
                res.getFiles()
                        .stream()
                        .filter(file -> !shouldSkip(file))
                        .map(this::optimizeFile)
                        .toList();

        res.setFiles(filteredFiles);
    }

    private boolean shouldSkip(FileChangeResponse file) {

        if (file == null) {
            return true;
        }

        String fileName = file.getFilename();

        if (fileName == null || fileName.isBlank()) {
            return true;
        }

        fileName = fileName.toLowerCase();

        /*
         * Never send environment/configuration files to LLM
         */
        if (fileName.endsWith(".env")
                || fileName.contains(".env.")
                || fileName.endsWith(".properties")) {
            return true;
        }

        /*
         * Ignore unwanted directories
         */
        if (IGNORED_PATHS.stream().anyMatch(fileName::startsWith)) {
            return true;
        }

        /*
         * No patch means nothing useful to explain
         */
        if (file.getPatch() == null || file.getPatch().isBlank()) {
            return true;
        }

        /*
         * Only process supported source files
         */
        return !SUPPORTED_EXTENSIONS.contains(getExtension(fileName));
    }

    private FileChangeResponse optimizeFile(FileChangeResponse file) {

        String optimizedPatch = optimizePatch(file.getPatch());

        file.setPatch(optimizedPatch);

        return file;
    }

    private String optimizePatch(String patch) {

        if (patch == null || patch.isBlank()) {
            return "";
        }

        String[] lines = patch.split("\\R");

        List<Integer> changedLines = new ArrayList<>();

        /*
         * Find actual changed lines.
         *
         * + = added line
         * - = deleted line
         *
         * Ignore Git diff headers:
         * +++
         * ---
         */
        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];

            if ((line.startsWith("+") && !line.startsWith("+++"))
                    || (line.startsWith("-") && !line.startsWith("---"))) {

                changedLines.add(i);
            }
        }

        /*
         * If there are no actual changes,
         * return the original patch.
         */
        if (changedLines.isEmpty()) {
            return patch;
        }

        boolean[] keep = new boolean[lines.length];

        /*
         * Keep:
         *
         * changed lines
         * +
         * -
         *
         * and 5 lines of surrounding context.
         */
        for (int index : changedLines) {

            int start = Math.max(0, index - CONTEXT_LINES);
            int end = Math.min(
                    lines.length - 1,
                    index + CONTEXT_LINES
            );

            for (int i = start; i <= end; i++) {
                keep[i] = true;
            }
        }

        StringBuilder optimized = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {

            /*
             * Always keep GitHub hunk header
             */
            if (lines[i].startsWith("@@")) {
                optimized.append(lines[i]).append("\n");
                continue;
            }

            if (keep[i]) {
                optimized.append(lines[i]).append("\n");
            }
        }

        return optimized.toString().trim();
    }

    private String getExtension(String fileName) {

        if (fileName == null) {
            return "";
        }

        int index = fileName.lastIndexOf(".");

        if (index == -1) {
            return "";
        }

        return fileName.substring(index + 1);
    }
}