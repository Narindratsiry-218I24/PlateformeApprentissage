const fs = require('fs');
const path = require('path');

const cssDir = path.join(__dirname, 'src/main/resources/static/css');

function processDir(dir) {
    const files = fs.readdirSync(dir);
    for (const file of files) {
        const fullPath = path.join(dir, file);
        if (fs.statSync(fullPath).isDirectory()) {
            processDir(fullPath);
        } else if (fullPath.endsWith('.css') && !fullPath.includes('tailwind.min.css') && !fullPath.includes('all.min.css')) {
            let content = fs.readFileSync(fullPath, 'utf8');
            let modified = false;

            // Colors update (general)
            if (content.includes('--primary:')) {
                content = content.replace(/--primary:\s*#[a-f0-9]+;/gi, '--primary: #a855f7;');
                content = content.replace(/--primary-dark:\s*#[a-f0-9]+;/gi, '--primary-dark: #9333ea;');
                content = content.replace(/--primary-light:\s*#[a-f0-9]+;/gi, '--primary-light: #c084fc;');
                content = content.replace(/--secondary:\s*#[a-f0-9]+;/gi, '--secondary: #d946ef;');
                modified = true;
            }

            // Replace linear-gradient
            if (content.includes('linear-gradient')) {
                content = content.replace(/background:\s*linear-gradient\([^)]+\);/g, (match, offset) => {
                    return 'background: var(--primary);';
                });
                
                modified = true;
            }

            if (modified) {
                fs.writeFileSync(fullPath, content);
                console.log(`Updated ${file}`);
            }
        }
    }
}

processDir(cssDir);
console.log('Done CSS replacements');
