package com.eviro.assessment.grad001.kinsleykajiva.service;

import com.eviro.assessment.grad001.kinsleykajiva.interfaces.AccountProfileRepository;
import com.eviro.assessment.grad001.kinsleykajiva.interfaces.FileParser;
import com.eviro.assessment.grad001.kinsleykajiva.models.AccountProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;
import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

@Service
public class CSVProcessorService implements FileParser {


    @Autowired
    private AccountProfileRepository accountProfileRepository;


    private List<AccountProfile> accountProfiles = new ArrayList<AccountProfile>();


    public AccountProfile getAccountProfile(String name, String Surname) {
        return accountProfileRepository.findByNameAndSurname(name, Surname);
    }
    public void updatePath(AccountProfile profile) {
         accountProfileRepository.save(profile);
    }

    public Optional<AccountProfile> getAccountProfileImage(String name, String Surname) {
        return accountProfiles.stream().filter(profile -> profile.getName().equals(name) && profile.getSurname().equals(Surname)).findFirst();
    }

    @Override
    public void parseCSV(File csvFile) {

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                if (lineCount > 0) {

                    String[] fields = line.split(","); // Assuming fields are separated by commas
                    if (fields.length == 4) { // Assuming exactly 4 fields in each row
                        AccountProfile accountProfile = new AccountProfile();
                        accountProfile.setName(fields[0]);
                        accountProfile.setSurname(fields[1]);
                        String fullIameBase64 = fields[2] + ";base64," + fields[3]; // data:image/image/png;base64,iVxxxxxx
                        accountProfile.setHttpImageLink(fullIameBase64);
                        accountProfiles.add(accountProfile);

                        accountProfileRepository.save(accountProfile);
                    } else {
                        System.out.println("Invalid CSV row: " + line);
                    }
                }
                lineCount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * This convert the base 64 string to a flat file but no direct file path was specified as much to use ,had to use temp folders
     */
    @Override

    public File convertCSVDataToImage(String base64ImageData) {

        if (base64ImageData == null) {
            return null;
        }
        try {
            // Extract the file type from the base64 string
            String fileType = base64ImageData.substring(base64ImageData.indexOf("/") + 1, base64ImageData.indexOf(";"));
            String fileExtension = switch (fileType) {
                case "jpeg", "jpg" -> "jpg";
                case "png" -> "png";
                case "gif" -> "gif";
                // Add more cases for other supported file types
                default -> throw new IllegalArgumentException("Unsupported file type: " + fileType);
            };

            // Generate a random file name for the temporary image file
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;

            // Create a temporary directory to store the image file
            Path tempDir = Files.createTempDirectory("temp");
            File imageFile = new File(tempDir.toFile(), fileName);
            System.out.println("Creating temporary directory" + imageFile);

            base64ImageData =base64ImageData.split(";base64,")[1];
            // Write the decoded image bytes to the temporary file
            try (OutputStream outputStream = new FileOutputStream(imageFile)) {
                byte[] imageBytes = Base64.getDecoder().decode(base64ImageData);
                outputStream.write(imageBytes);
            }

            return imageFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public URI createImageLink(File fileImage) {
        try {
            // Get the absolute path of the image file
            String absolutePath = fileImage.getAbsolutePath();

            // Create a URI with the file path

            return Paths.get(absolutePath).toUri();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
