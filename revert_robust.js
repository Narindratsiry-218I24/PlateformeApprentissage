const fs = require('fs');
const path = require('path');

const templatesDir = path.join(__dirname, 'src/main/resources/templates');

function walk(dir, callback) {
    fs.readdirSync(dir).forEach(f => {
        let dirPath = path.join(dir, f);
        let isDirectory = fs.statSync(dirPath).isDirectory();
        isDirectory ? walk(dirPath, callback) : callback(path.join(dir, f));
    });
}

const replacements = [
    { regex: /(src|href)="[^"]*tailwindcss\.js(\?[^"]*)?"/g, replacement: '$1="https://cdn.tailwindcss.com$2"' },
    { regex: /(src|href)="[^"]*chart\.min\.js"/g, replacement: '$1="https://cdn.jsdelivr.net/npm/chart.js"' },
    { regex: /(src|href)="[^"]*sockjs\.js"/g, replacement: '$1="https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js"' },
    { regex: /(src|href)="[^"]*stomp\.js"/g, replacement: '$1="https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js"' },
    { regex: /(src|href)="[^"]*jquery\.min\.js"/g, replacement: '$1="https://code.jquery.com/jquery-3.7.1.min.js"' },
    { regex: /(src|href)="[^"]*uicons-bold-rounded\.css"/g, replacement: '$1="https://cdn-uicons.flaticon.com/2.6.0/uicons-bold-rounded/css/uicons-bold-rounded.css"' },
    { regex: /(src|href)="[^"]*uicons-solid-rounded\.css"/g, replacement: '$1="https://cdn-uicons.flaticon.com/2.6.0/uicons-solid-rounded/css/uicons-solid-rounded.css"' },
    { regex: /(src|href)="[^"]*uicons-regular-rounded\.css"/g, replacement: '$1="https://cdn-uicons.flaticon.com/2.6.0/uicons-regular-rounded/css/uicons-regular-rounded.css"' },
    { regex: /(src|href)="[^"]*fonts\/inter\/index\.css"/g, replacement: '$1="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800;900&display=swap"' },
    { regex: /(src|href)="[^"]*fonts\/plus-jakarta-sans\/index\.css"/g, replacement: '$1="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;500;600;700;800&display=swap"' },
    { regex: /(src|href)="[^"]*fontawesome\/all\.min\.css"/g, replacement: '$1="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"' },
    { regex: /(src|href)="[^"]*css\/aos\.css"/g, replacement: '$1="https://unpkg.com/aos@2.3.1/dist/aos.css"' },
    { regex: /(src|href)="[^"]*js\/aos\.js"/g, replacement: '$1="https://unpkg.com/aos@2.3.1/dist/aos.js"' },
    { regex: /(src|href)="[^"]*lottie\.min\.js"/g, replacement: '$1="https://cdnjs.cloudflare.com/ajax/libs/lottie-web/5.12.2/lottie.min.js"' }
];

walk(templatesDir, function(filePath) {
    if (filePath.endsWith('.html')) {
        let content = fs.readFileSync(filePath, 'utf8');
        let modified = false;
        for (let r of replacements) {
            if (content.match(r.regex)) {
                content = content.replace(r.regex, r.replacement);
                modified = true;
            }
        }
        if (modified) {
            fs.writeFileSync(filePath, content, 'utf8');
            console.log(`Reverted robustly in ${filePath}`);
        }
    }
});
