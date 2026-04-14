// src/main/java/com/careertoolkit/model/AnalysisResult.java
package com.careertoolkit.model;

import java.util.List;
import java.util.Map;

public class AnalysisResult {
    private int atsScore;
    private List<String> detectedSkills;
    private List<String> missingSkills;
    private List<String> suggestions;
    private Map<String, Integer> sectionScores;
    private String overallFeedback;
    
    // Getters and Setters
    public int getAtsScore() { return atsScore; }
    public void setAtsScore(int atsScore) { this.atsScore = atsScore; }
    public List<String> getDetectedSkills() { return detectedSkills; }
    public void setDetectedSkills(List<String> detectedSkills) { this.detectedSkills = detectedSkills; }
    public List<String> getMissingSkills() { return missingSkills; }
    public void setMissingSkills(List<String> missingSkills) { this.missingSkills = missingSkills; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
    public Map<String, Integer> getSectionScores() { return sectionScores; }
    public void setSectionScores(Map<String, Integer> sectionScores) { this.sectionScores = sectionScores; }
    public String getOverallFeedback() { return overallFeedback; }
    public void setOverallFeedback(String overallFeedback) { this.overallFeedback = overallFeedback; }
}
