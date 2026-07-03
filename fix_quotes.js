const fs = require('fs');

let code = fs.readFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', 'utf8');

code = code.replace(/inscrireEtudiant\('ana', coursMBA\);/g, 'inscrireEtudiant(ana, coursMBA);');
code = code.replace(/inscrireEtudiant\('solo', coursMBA\);/g, 'inscrireEtudiant(solo, coursMBA);');
code = code.replace(/inscrireEtudiant\('ana', coursWeb\);/g, 'inscrireEtudiant(ana, coursWeb);');
code = code.replace(/inscrireEtudiant\('koto', coursMarketing\);/g, 'inscrireEtudiant(koto, coursMarketing);');

fs.writeFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', code);
console.log('Fixed single quotes');