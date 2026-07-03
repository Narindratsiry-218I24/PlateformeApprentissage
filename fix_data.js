const fs = require('fs');

let code = fs.readFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', 'utf8');

code = code.replace(/ci\.setUrlFichier\(url\);/g, function(match, offset, str) {
    let context = str.substring(offset - 50, offset + 50);
    if (context.includes('TypeContenu.IMAGE')) {
        return 'ci.setFichierUrl(url);';
    } else if (context.includes('TypeContenu.VIDEO')) {
        return 'ci.setVideoUrl(url);';
    } else if (context.includes('TypeContenu.LIEN')) {
        return 'ci.setLienExterne(url);';
    }
    return match;
});

fs.writeFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', code);
console.log('Fixed setUrlFichier');