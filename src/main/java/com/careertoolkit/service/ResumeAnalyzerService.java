// src/main/java/com/careertoolkit/service/ResumeAnalyzerService.java
package com.careertoolkit.service;

import com.careertoolkit.model.AnalysisResult;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;



@Service
public class ResumeAnalyzerService {

    // ===== SKILLS DATABASE =====
    private static final Map<String, List<String>> SKILL_CATEGORIES = new HashMap<>();

    static {
        SKILL_CATEGORIES.put("Programming Languages", Arrays.asList(
                "java", "python", "javascript", "typescript", "c++", "c#", "go", "kotlin", "php"
        ));
        SKILL_CATEGORIES.put("Frontend", Arrays.asList(
                "html", "css", "react", "angular", "vue", "bootstrap", "tailwind"
        ));
        SKILL_CATEGORIES.put("Backend", Arrays.asList(
                "spring boot", "spring", "node.js", "express", "django"
        ));
        SKILL_CATEGORIES.put("Database", Arrays.asList(
                "mysql", "postgresql", "mongodb", "sqlite"
        ));
        SKILL_CATEGORIES.put("Cloud & DevOps", Arrays.asList(
                "aws", "docker", "kubernetes", "jenkins"
        ));
        SKILL_CATEGORIES.put("Tools", Arrays.asList(
                "git", "github", "jira", "postman", "maven"
        ));
    }

    private static final List<String> POWER_WORDS = Arrays.asList(
            "developed", "built", "designed", "implemented", "created", "led", "improved"
            
    );

    // ===== MAIN METHOD =====
    public AnalysisResult analyzeResume(MultipartFile file, String jobDescription) throws IOException {
      String content = extractText(file);

// ✅ VALIDATION
if (!isResumeContent(content)) {
    AnalysisResult error = new AnalysisResult();
    error.setAtsScore(0);
    error.setDetectedSkills(new ArrayList<>());
    error.setMissingSkills(new ArrayList<>());
    error.setSuggestions(Arrays.asList("Uploaded file does not appear to be a resume."));
    error.setOverallFeedback("Invalid resume content");

    return error;
}

return analyzeContent(content, jobDescription);
    }

    // ===== TEXT EXTRACTION =====
    private String extractText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();

if (fileName == null) {
    throw new IOException("Invalid file: filename is missing");
}

fileName = fileName.toLowerCase();

        if (fileName.endsWith(".pdf")) {
            try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
                return new PDFTextStripper().getText(doc);
            }
        } else if (fileName.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
     XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {

    return extractor.getText();
}
        } else {
            return new String(file.getBytes());
        }
    }

    // ===== ANALYSIS =====
    private AnalysisResult analyzeContent(String content, String jobDescription) {

        AnalysisResult result = new AnalysisResult();

        String lowerContent = content.toLowerCase();
        String lowerJobDesc = (jobDescription != null && !jobDescription.trim().isEmpty())
                ? jobDescription.toLowerCase()
                : "";
                long powerWordCount = POWER_WORDS.stream()
        .filter(lowerContent::contains)
        .count();



        // ===== DETECT SKILLS =====
        List<String> detectedSkills = detectSkills(lowerContent);
        result.setDetectedSkills(detectedSkills);

        // ===== JOB SKILLS =====
        List<String> jobSkills = detectSkills(lowerJobDesc);

        // fallback if nothing detected
        if (jobSkills.isEmpty() && !lowerJobDesc.isEmpty()) {
            jobSkills = Arrays.asList(lowerJobDesc.split("\\s+"));
        }

        // ===== MISSING SKILLS =====
        List<String> missingSkills = jobSkills.stream()
                .filter(skill -> detectedSkills.stream()
                        .noneMatch(d -> d.equalsIgnoreCase(skill)))
                .distinct()
                .limit(10)
                .collect(Collectors.toList());

        result.setMissingSkills(missingSkills);

        // ===== SECTION SCORES =====
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Projects", content.contains("project") ? 80 : 30);
        scores.put("Experience", content.contains("experience") ? 80 : 40);
        scores.put("Education", content.contains("education") ? 80 : 40);
        scores.put("Skills", detectedSkills.size() * 10);
        scores.put("Keywords", lowerJobDesc.isEmpty() ? 70 : 80);

        result.setSectionScores(scores);

        // ===== ATS SCORE =====
        int atsScore = (int) scores.values().stream().mapToInt(i -> i).average().orElse(50);
        result.setAtsScore(atsScore);

        // ===== SUGGESTIONS =====
        List<String> suggestions = new ArrayList<>();

// ✅ NOW it's safe to use suggestions


if (powerWordCount < 3) {
    suggestions.add("Use more action words like: " + String.join(", ", POWER_WORDS));
}

        if (!missingSkills.isEmpty()) {
            suggestions.add("Missing important skills: " + String.join(", ", missingSkills));
        }

        if (!content.contains("experience")) {
            suggestions.add("Add a proper work experience section.");
        }

        if (!content.contains("project")) {
            suggestions.add("Include projects to strengthen your resume.");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("Your resume looks good. Add more measurable achievements to improve further.");
        }

        result.setSuggestions(suggestions);

        // ===== FEEDBACK =====
        if (atsScore > 80) {
            result.setOverallFeedback("Excellent resume!");
        } else if (atsScore > 60) {
            result.setOverallFeedback("Good resume, improve a few areas.");
        } else {
            result.setOverallFeedback("Needs improvement.");
        }

        // ===== DEBUG LOGS =====
        System.out.println("Detected Skills: " + detectedSkills);
        System.out.println("Job Skills: " + jobSkills);
        System.out.println("Missing Skills: " + missingSkills);

        return result;
    }

    // ===== SKILL DETECTION =====
    private List<String> detectSkills(String content) {
        Set<String> found = new HashSet<>();

        for (List<String> skills : SKILL_CATEGORIES.values()) {
            for (String skill : skills) {
                String pattern = "\\b" + Pattern.quote(skill) + "\\b";
                if (Pattern.compile(pattern, Pattern.CASE_INSENSITIVE).matcher(content).find()) {
                    found.add(skill.substring(0, 1).toUpperCase() + skill.substring(1));
                }
            }
        }

        return new ArrayList<>(found);
    }
    private boolean isResumeContent(String content) {
    String lower = content.toLowerCase();

    return lower.contains("experience") ||
           lower.contains("education") ||
           lower.contains("skills") ||
           lower.contains("project") ||
           lower.contains("summary") ||
           lower.contains("profile");
}
}