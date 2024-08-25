package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class StateManager {

    private final Path stateFilePath;
    private Map<Path, Integer> currentStates;

    public StateManager(String stateDirectory) throws IOException {
        this.stateFilePath = Paths.get(stateDirectory, "state.properties");
        this.currentStates = readState();
    }

    private Map<Path, Integer> readState() throws IOException {
        Map<Path, Integer> stateMap = new HashMap<>();
        Properties properties = new Properties();

        if (Files.exists(stateFilePath)) {
            try (BufferedReader reader = Files.newBufferedReader(stateFilePath)) {
                properties.load(reader);
            }

            for (String key : properties.stringPropertyNames()) {
                Path path = Paths.get(key);
                Integer index = Integer.parseInt(properties.getProperty(key));
                stateMap.put(path, index);
            }
        }

        return stateMap;
    }

    public Map<Path, Integer> getFilesToProcess(Map<Path, Integer> existFilesIndex) {
        Map<Path, Integer> filesToProcess = new HashMap<>();

        for (Map.Entry<Path, Integer> entry : existFilesIndex.entrySet()) {
            Integer currentIndex = currentStates.getOrDefault(entry.getKey(), 0);

            if (currentIndex < entry.getValue()) {
                filesToProcess.put(entry.getKey(), currentIndex > 0 ? currentIndex : 1);
            }
        }

        return filesToProcess;
    }

    public void updateState(Path source, int newLastIndex) throws IOException {
        currentStates.put(source, newLastIndex);
        Properties properties = new Properties();

        for (Map.Entry<Path, Integer> entry : currentStates.entrySet()) {
            properties.setProperty(entry.getKey().toString(), entry.getValue().toString());
        }

        if (Files.notExists(stateFilePath.getParent())) {
            Files.createDirectories(stateFilePath.getParent());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(stateFilePath)) {
            properties.store(writer, "State data");
        }
    }
}