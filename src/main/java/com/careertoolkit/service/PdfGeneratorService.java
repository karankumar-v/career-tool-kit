// src/main/java/com/careertoolkit/service/PdfGeneratorService.java
package com.careertoolkit.service;

import com.careertoolkit.model.ResumeData;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfGeneratorService {
    
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb SECONDARY_COLOR = new DeviceRgb(44, 62, 80);
    
    public byte[] generateResumePdf(ResumeData data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Fonts
        PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        
        // Header - Name
        Paragraph name = new Paragraph(data.getFullName())
                .setFont(boldFont)
                .setFontSize(24)
                .setFontColor(SECONDARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(name);
        
        // Contact Information
        StringBuilder contactInfo = new StringBuilder();
        if (data.getEmail() != null) contactInfo.append(data.getEmail());
        if (data.getPhone() != null) contactInfo.append(" | ").append(data.getPhone());
        if (data.getLocation() != null) contactInfo.append(" | ").append(data.getLocation());
        
        Paragraph contact = new Paragraph(contactInfo.toString())
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(contact);
        
        // Links
        StringBuilder links = new StringBuilder();
        if (data.getLinkedIn() != null && !data.getLinkedIn().isEmpty()) {
            links.append("LinkedIn: ").append(data.getLinkedIn());
        }
        if (data.getGithub() != null && !data.getGithub().isEmpty()) {
            if (links.length() > 0) links.append(" | ");
            links.append("GitHub: ").append(data.getGithub());
        }
        if (links.length() > 0) {
            document.add(new Paragraph(links.toString())
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(PRIMARY_COLOR));
        }
        
        addSectionSeparator(document);
        
        // Summary
        if (data.getSummary() != null && !data.getSummary().isEmpty()) {
            addSectionHeader(document, "PROFESSIONAL SUMMARY", boldFont);
            document.add(new Paragraph(data.getSummary())
                    .setFont(regularFont)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.JUSTIFIED));
            addSectionSeparator(document);
        }
        
        // Skills
        if (data.getSkills() != null && !data.getSkills().isEmpty()) {
            addSectionHeader(document, "TECHNICAL SKILLS", boldFont);
            document.add(new Paragraph(String.join(" • ", data.getSkills()))
                    .setFont(regularFont)
                    .setFontSize(11));
            addSectionSeparator(document);
        }
        
        // Experience
        if (data.getExperience() != null && !data.getExperience().isEmpty()) {
            addSectionHeader(document, "WORK EXPERIENCE", boldFont);
            for (ResumeData.Experience exp : data.getExperience()) {
                Paragraph jobTitle = new Paragraph(exp.getJobTitle())
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(SECONDARY_COLOR)
                        .setMarginBottom(0);
                document.add(jobTitle);
                
                Paragraph companyDuration = new Paragraph(exp.getCompany() + " | " + exp.getDuration())
                        .setFont(regularFont)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.GRAY)
                        .setMarginTop(0);
                document.add(companyDuration);
                if (exp.getResponsibilities() != null) {

    List responsibilities = new List()
            .setSymbolIndent(12)
            .setListSymbol("•");

    for (String resp : exp.getResponsibilities()) {
        ListItem item = new ListItem();
        item.add(new Paragraph(resp)
                .setFont(regularFont)
                .setFontSize(11));

        responsibilities.add(item);
    }

    document.add(responsibilities);
}
                document.add(new Paragraph("").setMarginBottom(10));
            }
            addSectionSeparator(document);
        }
        
        // Education
        if (data.getEducation() != null && !data.getEducation().isEmpty()) {
            addSectionHeader(document, "EDUCATION", boldFont);
            for (ResumeData.Education edu : data.getEducation()) {
                Paragraph degree = new Paragraph(edu.getDegree())
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(SECONDARY_COLOR)
                        .setMarginBottom(0);
                document.add(degree);
                
                String eduDetails = edu.getInstitution() + " | " + edu.getYear();
                if (edu.getGpa() != null && !edu.getGpa().isEmpty()) {
                    eduDetails += " | GPA: " + edu.getGpa();
                }
                document.add(new Paragraph(eduDetails)
                        .setFont(regularFont)
                        .setFontSize(10)
                        .setFontColor(ColorConstants.GRAY)
                        .setMarginTop(0));
            }
            addSectionSeparator(document);
        }
        
        // Projects
        if (data.getProjects() != null && !data.getProjects().isEmpty()) {
            addSectionHeader(document, "PROJECTS", boldFont);
            for (ResumeData.Project project : data.getProjects()) {
                Paragraph projectName = new Paragraph(project.getName())
                        .setFont(boldFont)
                        .setFontSize(12)
                        .setFontColor(SECONDARY_COLOR)
                        .setMarginBottom(0);
                document.add(projectName);
                
                if (project.getTechnologies() != null) {
                    document.add(new Paragraph("Technologies: " + project.getTechnologies())
                            .setFont(regularFont)
                            .setFontSize(10)
                            .setFontColor(PRIMARY_COLOR)
                            .setMarginTop(0)
                            .setMarginBottom(0));
                }
                
                if (project.getDescription() != null) {
                    document.add(new Paragraph(project.getDescription())
                            .setFont(regularFont)
                            .setFontSize(11)
                            .setMarginTop(2));
                }
                document.add(new Paragraph("").setMarginBottom(8));
            }
        }
        
        document.close();
        return baos.toByteArray();
    }
    
    private void addSectionHeader(Document document, String title, PdfFont font) {
        document.add(new Paragraph(title)
                .setFont(font)
                .setFontSize(13)
                .setFontColor(PRIMARY_COLOR)
                .setMarginTop(10)
                .setMarginBottom(5));
    }
    
    private void addSectionSeparator(Document document) {
        SolidLine line = new SolidLine(0.5f);
        line.setColor(ColorConstants.LIGHT_GRAY);
        document.add(new LineSeparator(line).setMarginTop(5).setMarginBottom(10));
    }
}
