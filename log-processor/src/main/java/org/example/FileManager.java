package org.example;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    private final Path logsDirectoryPath;
    private final String processedLogsDirectory;

    public FileManager(String logsDirectory, String processedLogsDirectory) {
        this.logsDirectoryPath = Paths.get(logsDirectory);
        this.processedLogsDirectory = processedLogsDirectory;
    }

    public Map<Path, Integer> getFileIndexMap() throws IOException {
        Map<Path, Integer> fileIndexMap = new HashMap<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logsDirectoryPath, "*.log")) {
            for (Path logFile : stream) {

                try (var reader = Files.newBufferedReader(logFile)) {
                    String firstLine = reader.readLine();
                    int lastIndex = Integer.parseInt(firstLine.trim());
                    fileIndexMap.put(logFile, lastIndex);
                }
            }
        }

        return fileIndexMap;
    }

    public int processFile(Path source, int lastIndex) throws IOException {
        List<String> allLines = Files.readAllLines(source);
        List<String> lines = allLines.subList(lastIndex, Math.min(lastIndex + 100, allLines.size()));

        Path dist = Paths.get(processedLogsDirectory, generateNewFileName(source, lastIndex));

        if (Files.notExists(dist.getParent())) {
            Files.createDirectories(dist.getParent());
        }

        Files.write(dist, lines, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

        return lastIndex + lines.size();
    }

    private String generateNewFileName(Path source, int batchIndex) {
        String baseName = source.getFileName().toString().replace(".log", "");
        return String.format("%s-%04d.log", baseName, (batchIndex / 100) + 1);
    }
}
