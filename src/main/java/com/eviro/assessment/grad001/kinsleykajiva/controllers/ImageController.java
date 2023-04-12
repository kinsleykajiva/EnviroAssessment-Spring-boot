package com.eviro.assessment.grad001.kinsleykajiva.controllers;


import com.eviro.assessment.grad001.kinsleykajiva.interfaces.FileParser;
import com.eviro.assessment.grad001.kinsleykajiva.service.CSVProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

@RestController
@RequestMapping("/v1/api/image")
public class ImageController {


    @Autowired
    CSVProcessorService csvProcessorService;

    // @GetMapping("/{name}/{surname}/{\\w\\.\\w}")    // Invalid mapping pattern detected: /{name}/{surname}/{\w\.\w}//Char '\' not allowed at start of captured variable name
    @GetMapping("/{name}/{surname}")
    public FileSystemResource gethttpImageLink(@PathVariable String name, @PathVariable String surname) {

        if (name == null || name.isEmpty()) { // pre-validation
            return null;
        }

        if (surname == null || surname.isEmpty()) {
            return null;
        }
        try { // always use try catch dealing with filesystem
            var accountProfiles = csvProcessorService.getAccountProfile(name, surname);


            if (accountProfiles != null) {
                var fileRes = csvProcessorService.convertCSVDataToImage(accountProfiles.getHttpImageLink());
                URI resLink = csvProcessorService.createImageLink(fileRes);

                String filePath = resLink.toString();

                System.out.println("fileRes     - " + fileRes.getAbsolutePath());
                System.out.println("filePath - " + filePath);
                accountProfiles.setHttpImageLink(fileRes.getAbsolutePath());
                csvProcessorService.updatePath(accountProfiles);

                // return new FileSystemResource("<a href=\"" + resLink.toString() + "\">Download File</a>");


                return new FileSystemResource(new File(fileRes.getAbsolutePath()));

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
