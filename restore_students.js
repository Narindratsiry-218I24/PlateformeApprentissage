const fs = require('fs');
let code = fs.readFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', 'utf8');

const missingStudents = `
        Utilisateur ana = creerUtilisateur("ana_ravelo",  "ana@etudiant.mg",  "etudiant123", "Ana",  "Ravelo",         Role.APPRENANT);
        Utilisateur solo = creerUtilisateur("solo_andria", "solo@etudiant.mg", "etudiant123", "Solo", "Andriamahefa",   Role.APPRENANT);
        Utilisateur koto = creerUtilisateur("koto_rabe",   "koto@etudiant.mg", "etudiant123", "Koto", "Rabemananjara",  Role.APPRENANT);
`;

code = code.replace('// ── Étudiants ─────────────────────────────────────────────────────────', '// ── Étudiants ─────────────────────────────────────────────────────────\n' + missingStudents);

fs.writeFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', code);
console.log('Restored ana, solo, koto');