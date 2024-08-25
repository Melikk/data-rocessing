package org.example;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public class LogProcessor {
    private static final String LOG_DIRECTORY = "logs";
    private static final String PROCESSED_LOG_DIRECTORY = "processed_logs";
    private static final String STATE_DIRECTORY = "state";


    public static void main(String[] args) throws IOException, InterruptedException {
        FileManager fileManager = new FileManager(LOG_DIRECTORY, PROCESSED_LOG_DIRECTORY);
        StateManager stateManager = new StateManager(STATE_DIRECTORY);

        while (true){
            Map<Path, Integer> existFilesIndex = fileManager.getFileIndexMap();
            Map<Path, Integer> filesToProcess = stateManager.getFilesToProcess(existFilesIndex);

            for (Map.Entry<Path, Integer> entry : filesToProcess.entrySet()) {
                int newLastIndex = fileManager.processFile(entry.getKey(), entry.getValue());
                stateManager.updateState(entry.getKey(), newLastIndex);
            }

            Thread.sleep(3000);
        }

    }


}

