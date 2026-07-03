const fs = require('fs');
let code = fs.readFileSync('src/main/java/com/plateforme_etudiant/demo/service/CertificatService.java', 'utf8');

code = code.replace(/document\.add\(new Paragraph\("\\n\\n\\n"\)\);\s*\/\/ Espacement haut/g, 'document.add(new Paragraph("\\n\\n")); // Espacement haut');
code = code.replace(/document\.add\(new Paragraph\("\\n\\n\\n"\)\);/g, 'document.add(new Paragraph("\\n"));');
code = code.replace(/document\.add\(new Paragraph\("\\n\\n\\n\\n\\n"\)\);/g, 'document.add(new Paragraph("\\n\\n"));');

fs.writeFileSync('src/main/java/com/plateforme_etudiant/demo/service/CertificatService.java', code);
console.log('CertificatService spacing reduced.');
