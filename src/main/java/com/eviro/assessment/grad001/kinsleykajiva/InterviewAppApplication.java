package com.eviro.assessment.grad001.kinsleykajiva;

import com.eviro.assessment.grad001.kinsleykajiva.interfaces.AccountProfileRepository;
import com.eviro.assessment.grad001.kinsleykajiva.service.CSVProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.*;
import java.io.File;

@SpringBootApplication
public class InterviewAppApplication {

    @Autowired
    private CSVProcessorService csvProcessorService;

    public static void main(String[] args) {
        SpringApplication.run(InterviewAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner acountData(AccountProfileRepository accountProfileRepository) {
        return (args) -> {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data.csv");

            if (inputStream == null) {
                System.err.println("Failed to read CSV file from resources.");
                return;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            File fileCSV = convertBufferedReaderToFile(br);
            csvProcessorService.parseCSV(fileCSV);
        };
    }

    public File convertBufferedReaderToFile(BufferedReader br) throws IOException {
        // Create a temporary file
        File tempFile = File.createTempFile("temp", ".txt");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            String line;
            while ((line = br.readLine()) != null) {
                writer.write(line + System.lineSeparator());
            }
        }

        return tempFile;
    }
}
