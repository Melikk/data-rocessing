package com.example.xml2json.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

@Service
public class FileService {

    public void updateLogFile(String directoryPath, String fileName, String json) throws IOException {
        File file = findOrCreateFile(directoryPath, fileName);
        StringBuilder content = new StringBuilder();
        int recordCount = 0;

        // Используем Scanner для чтения файла построчно
        try (Scanner scanner = new Scanner(file)) {
            if (scanner.hasNextLine()) {
                String firstLine = scanner.nextLine();
                try {
                    recordCount = Integer.parseInt(firstLine.trim());
                } catch (NumberFormatException e) {
                    recordCount = 0; // Если первая строка не число, начинаем с 0
                }
                recordCount++;
            } else {
                recordCount = 1;
            }

            while (scanner.hasNextLine()) {
                content.append(scanner.nextLine()).append(System.lineSeparator());
            }
        }

        // Открываем файл для перезаписи
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(recordCount + System.lineSeparator());
            fileWriter.write(content.toString());
            fileWriter.write(json + System.lineSeparator());
        }
    }

    private File findOrCreateFile(String directoryPath, String fileName) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directoryPath, fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }
}
