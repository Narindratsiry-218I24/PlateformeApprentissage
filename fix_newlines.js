const fs = require('fs');

let code = fs.readFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', 'utf8');

code = code.replace(/npx react-native init MonProjet\r?\ncd MonProjet\r?\nnpx react-native run-android/g, 'npx react-native init MonProjet\\ncd MonProjet\\nnpx react-native run-android');
code = code.replace(/fetch\('https:\/\/api\.example\.com\/data'\)\r?\n\s*\.then\(response => response\.json\(\)\)\r?\n\s*\.then\(data => console\.log\(data\)\);/g, "fetch('https://api.example.com/data')\\n  .then(response => response.json())\\n  .then(data => console.log(data));");

fs.writeFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', code);
console.log('Fixed multiline strings properly');