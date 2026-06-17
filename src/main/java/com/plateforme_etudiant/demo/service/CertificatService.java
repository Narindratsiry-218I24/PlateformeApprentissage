package com.plateforme_etudiant.demo.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.plateforme_etudiant.demo.model.ResultatQuiz;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class CertificatService {

    public byte[] genererCertificatPDF(ResultatQuiz resultat) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            // Document en format portrait A4
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // Couleurs et Polices
            Color colorPrimary = new Color(30, 58, 138); // #1e3a8a
            Font fontTitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 48, Font.BOLD, colorPrimary);
            Font fontSubtitle = FontFactory.getFont(FontFactory.TIMES_ROMAN, 16, Color.GRAY);
            Font fontName = FontFactory.getFont(FontFactory.TIMES_ITALIC, 32, Color.BLACK);
            Font fontText = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14, Color.DARK_GRAY);
            Font fontFooter = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12, colorPrimary);

            // Cadre principal
            document.add(new Paragraph("\n\n\n\n")); // Espacement haut

            // 2. Titre
            Paragraph title = new Paragraph("CERTIFICAT", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            LineSeparator titleLine = new LineSeparator();
            titleLine.setOffset(-5);
            titleLine.setLineColor(colorPrimary);
            titleLine.setLineWidth(2);
            titleLine.setPercentage(60);
            document.add(new Chunk(titleLine));
            
            document.add(new Paragraph("\n\n\n"));

            // 3. Corps du texte
            Paragraph p1 = new Paragraph("Ce certificat est fièrement décerné à", fontSubtitle);
            p1.setAlignment(Element.ALIGN_CENTER);
            document.add(p1);
            document.add(new Paragraph("\n"));

            // 4. Nom de l'étudiant
            String nom = resultat.getEtudiant() != null ? resultat.getEtudiant().getNomComplet() : "Étudiant";
            Paragraph name = new Paragraph(nom, fontName);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);
            
            LineSeparator nameLine = new LineSeparator();
            nameLine.setOffset(-5);
            nameLine.setLineColor(Color.GRAY);
            nameLine.setLineWidth(1);
            nameLine.setPercentage(50);
            document.add(new Chunk(nameLine));
            
            document.add(new Paragraph("\n\n"));

            // 5. Détails du cours
            String coursTitre = (resultat.getQuiz() != null && resultat.getQuiz().getCours() != null) 
                                ? resultat.getQuiz().getCours().getTitre() : "Cours";
            String quizTitre = (resultat.getQuiz() != null) ? resultat.getQuiz().getTitre() : "Quiz";
            
            Paragraph detail = new Paragraph("Pour avoir réussi avec succès le quiz \"" + quizTitre + "\"\n" +
                                            "dans le cadre du cours :", fontText);
            detail.setAlignment(Element.ALIGN_CENTER);
            document.add(detail);
            document.add(new Paragraph("\n"));
            
            Paragraph course = new Paragraph(coursTitre, FontFactory.getFont(FontFactory.TIMES_BOLD, 18, colorPrimary));
            course.setAlignment(Element.ALIGN_CENTER);
            document.add(course);
            document.add(new Paragraph("\n\n"));

            // 6. Score et Date
            String date = resultat.getDatePassage() != null ? 
                         resultat.getDatePassage().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) : "-";
            Paragraph score = new Paragraph("Score obtenu : " + String.format("%.2f", resultat.getScore()) + "%", fontText);
            score.setAlignment(Element.ALIGN_CENTER);
            document.add(score);
            document.add(new Paragraph("\n"));
            
            Paragraph dateP = new Paragraph("Fait le : " + date, fontSubtitle);
            dateP.setAlignment(Element.ALIGN_CENTER);
            document.add(dateP);

            // 7. Signatures et Sceau
            document.add(new Paragraph("\n\n\n\n\n"));
            
            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1, 1, 1});
            
            // President Director
            com.lowagie.text.pdf.PdfPCell cellLeft = new com.lowagie.text.pdf.PdfPCell();
            cellLeft.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cellLeft.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellLeft.addElement(new Paragraph("____________________", fontFooter));
            cellLeft.addElement(new Paragraph("Directeur Général", fontFooter));
            table.addCell(cellLeft);
            
            // Sceau central (simulé par un texte rouge)
            com.lowagie.text.pdf.PdfPCell cellCenter = new com.lowagie.text.pdf.PdfPCell();
            cellCenter.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cellCenter.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellCenter.setVerticalAlignment(Element.ALIGN_MIDDLE);
            Font sealFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 40, Color.RED);
            Paragraph seal = new Paragraph("●", sealFont);
            seal.setAlignment(Element.ALIGN_CENTER);
            cellCenter.addElement(seal);
            table.addCell(cellCenter);
            
            // General Manager
            com.lowagie.text.pdf.PdfPCell cellRight = new com.lowagie.text.pdf.PdfPCell();
            cellRight.setBorder(com.lowagie.text.Rectangle.NO_BORDER);
            cellRight.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellRight.addElement(new Paragraph("____________________", fontFooter));
            cellRight.addElement(new Paragraph("Responsable Pédagogique", fontFooter));
            table.addCell(cellRight);
            
            document.add(table);

            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return out.toByteArray();
    }
}
