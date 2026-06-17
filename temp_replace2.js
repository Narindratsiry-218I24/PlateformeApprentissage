const fs = require('fs');

const files = [
    'src/main/resources/templates/Etudiant/progression.html',
    'src/main/resources/templates/Etudiant/visionner-cours.html',
    'src/main/resources/templates/layout/header.html',
    'src/main/resources/templates/professeur/fragments/sidebar.html'
];

files.forEach(file => {
    try {
        let content = fs.readFileSync(file, 'utf8');
        content = content.replace(/#1e3a8a/gi, '#2563eb');
        content = content.replace(/#4318FF/gi, '#2563eb');
        content = content.replace(/#4A28A8/gi, '#2563eb');
        content = content.replace(/#4F46E5/gi, '#2563eb');
        content = content.replace(/#172554/g, '#1d4ed8');
        fs.writeFileSync(file, content);
        console.log('Updated ' + file);
    } catch(e) {
        console.error('Error on ' + file + ':', e);
    }
});