package com.plateforme_etudiant.demo.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.ColumnText;
import com.plateforme_etudiant.demo.model.ResultatQuiz;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class CertificatService {

    public byte[] genererCertificatPDF(ResultatQuiz resultat) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            // Document A4 paysage avec petites marges
            Document document = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            PdfContentByte canvas = writer.getDirectContent();
            float width = document.getPageSize().getWidth();
            float height = document.getPageSize().getHeight();

            // Couleurs
            Color colorBlue = new Color(18, 60, 145);
            Color colorGold = new Color(215, 176, 77);
            Color colorGoldDark = new Color(197, 157, 61);
            Color colorGreyLight = new Color(208, 213, 223);
            Color text444 = new Color(68, 68, 68);
            Color text555 = new Color(85, 85, 85);
            Color text666 = new Color(102, 102, 102);
            Color text777 = new Color(119, 119, 119);

            // --- BORDURES (une seule, plus fine) ---
            canvas.setColorStroke(colorGold);
            canvas.setLineWidth(1.5f);
            canvas.rectangle(15, 15, width - 30, height - 30);
            canvas.stroke();

            // --- FORMES GÉOMÉTRIQUES (plus petites) ---
            canvas.setColorFill(colorBlue);
            
            // Triangles (réduits)
            canvas.moveTo(0, height);
            canvas.lineTo(180, height);
            canvas.lineTo(0, height - 180);
            canvas.fill();

            canvas.moveTo(width, 180);
            canvas.lineTo(width, 0);
            canvas.lineTo(width - 180, 0);
            canvas.fill();

            canvas.setColorFill(colorGold);
            canvas.moveTo(70, height);
            canvas.lineTo(180, height);
            canvas.lineTo(180, height - 110);
            canvas.fill();

            canvas.moveTo(width - 175, 20);
            canvas.lineTo(width - 70, 20);
            canvas.lineTo(width - 70, 125);
            canvas.fill();

            // Badge (plus petit)
            float badgeCenterX = width - 50 - 35f;
            float badgeCenterY = height - 40 - 35f;
            canvas.circle(badgeCenterX, badgeCenterY, 35f);
            canvas.fill();

            // --- TEXTES (polices réduites) ---
            Font fontTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 36, colorBlue);
            Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA, 18, text444);
            Font fontSchool = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, colorBlue);
            Font fontSlogan = FontFactory.getFont(FontFactory.HELVETICA, 12, text777);
            Font fontPresented = FontFactory.getFont(FontFactory.HELVETICA, 16, text555);
            Font fontName = FontFactory.getFont(FontFactory.TIMES_ITALIC, 44, colorGoldDark);
            Font fontDesc = FontFactory.getFont(FontFactory.HELVETICA, 14, text444);
            Font fontFooterText = FontFactory.getFont(FontFactory.HELVETICA, 13, text666);
            Font fontDate = FontFactory.getFont(FontFactory.HELVETICA, 13, text666);
            Font fontSign = FontFactory.getFont(FontFactory.HELVETICA, 12, text444);
            Font fontBadge = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);

            // Badge texte
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("Excellence", fontBadge), badgeCenterX, badgeCenterY + 4, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("Academique", fontBadge), badgeCenterX, badgeCenterY - 8, 0);

            // Position Y de départ
            float yPos = height - 50;

            // Titre
            Paragraph title = new Paragraph("CERTIFICAT", fontTitle);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(2);
            document.add(title);

            // Sous-titre
            Paragraph subtitle = new Paragraph("DE REUSSITE", fontSubtitle);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(5);
            document.add(subtitle);

            // École
            Paragraph school = new Paragraph("L'ERUDITION", fontSchool);
            school.setAlignment(Element.ALIGN_CENTER);
            school.setSpacingAfter(2);
            document.add(school);

            // Slogan
            Paragraph slogan = new Paragraph("Centre d'Excellence en Formation et Innovation", fontSlogan);
            slogan.setAlignment(Element.ALIGN_CENTER);
            slogan.setSpacingAfter(8);
            document.add(slogan);

            // Présentation
            Paragraph presented = new Paragraph("Ce certificat est decerne avec honneur a :", fontPresented);
            presented.setAlignment(Element.ALIGN_CENTER);
            presented.setSpacingAfter(4);
            document.add(presented);

            // Nom
            String nom = resultat.getEtudiant() != null ? resultat.getEtudiant().getNomComplet() : "Nom et Prenom";
            Paragraph name = new Paragraph(nom, fontName);
            name.setAlignment(Element.ALIGN_CENTER);
            name.setSpacingAfter(6);
            document.add(name);

            // Ligne séparatrice
            canvas.setColorStroke(colorGreyLight);
            canvas.setLineWidth(1.5f);
            canvas.moveTo(width / 2 - 180, writer.getVerticalPosition(false) + 8);
            canvas.lineTo(width / 2 + 180, writer.getVerticalPosition(false) + 8);
            canvas.stroke();

            // Description cours
            String coursTitre = (resultat.getQuiz() != null && resultat.getQuiz().getCours() != null) 
                                ? resultat.getQuiz().getCours().getTitre() : "Formation";
            Paragraph desc1 = new Paragraph(coursTitre, fontDesc);
            desc1.setAlignment(Element.ALIGN_CENTER);
            desc1.setSpacingAfter(4);
            document.add(desc1);

            // Footer
            Paragraph footer = new Paragraph("Excellence • Savoir • Innovation", fontFooterText);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingAfter(2);
            document.add(footer);

            // Date
            String date = resultat.getDatePassage() != null ? 
                         resultat.getDatePassage().format(DateTimeFormatter.ofPattern("dd / MM / yyyy")) : "____ / ____ / ______";
            Paragraph dateP = new Paragraph("Fait le : " + date, fontDate);
            dateP.setAlignment(Element.ALIGN_CENTER);
            dateP.setSpacingAfter(8);
            document.add(dateP);

            // Signatures (en position absolue)
            float signY = 70f;
            canvas.setColorStroke(text444);
            canvas.setLineWidth(1f);
            
            // Directeur Général
            canvas.moveTo(width / 2 - 160, signY);
            canvas.lineTo(width / 2 - 40, signY);
            canvas.stroke();
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("Directeur General", fontSign), width / 2 - 100, signY - 12, 0);

            // Responsable Pédagogique
            canvas.moveTo(width / 2 + 40, signY);
            canvas.lineTo(width / 2 + 160, signY);
            canvas.stroke();
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER, new Phrase("Responsable Pedagogique", fontSign), width / 2 + 100, signY - 12, 0);

            document.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return out.toByteArray();
    }
}