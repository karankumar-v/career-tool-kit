// src/main/java/com/careertoolkit/controller/ResumeBuilderController.java
package com.careertoolkit.controller;

import com.careertoolkit.model.ResumeData;
import com.careertoolkit.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume")
@CrossOrigin(origins = "*")
public class ResumeBuilderController {
    
    @Autowired
    private PdfGeneratorService pdfGeneratorService;
    
    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateResume(@RequestBody ResumeData resumeData) {
        try {
            byte[] pdfBytes = pdfGeneratorService.generateResumePdf(resumeData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                sanitizeFilename(resumeData.getFullName()) + "_Resume.pdf");
            headers.setContentLength(pdfBytes.length);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/preview")
    public ResponseEntity<byte[]> previewResume(@RequestBody ResumeData resumeData) {
        try {
            byte[] pdfBytes = pdfGeneratorService.generateResumePdf(resumeData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "preview.pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private String sanitizeFilename(String name) {
        if (name == null || name.isEmpty()) {
            return "Resume";
        }
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }
}
