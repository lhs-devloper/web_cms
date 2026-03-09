const fs = require('fs');
const path = require('path');

const cssDir = path.join(__dirname, 'src');

function getAllFiles(dirPath, arrayOfFiles) {
    const files = fs.readdirSync(dirPath);
    arrayOfFiles = arrayOfFiles || [];
    files.forEach(function (file) {
        if (fs.statSync(dirPath + "/" + file).isDirectory()) {
            arrayOfFiles = getAllFiles(dirPath + "/" + file, arrayOfFiles);
        } else if (file.endsWith('.css')) {
            arrayOfFiles.push(path.join(__dirname, dirPath, "/", file));
        }
    });
    return arrayOfFiles;
}

const colorMap = [
    { regex: /#0b0d10/gi, replace: 'var(--bg-darker)' },
    { regex: /#0d0f12/gi, replace: 'var(--bg-color)' },
    { regex: /#12151a/gi, replace: 'var(--bg-lighter)' },
    { regex: /#14171d/gi, replace: 'var(--bg-panel)' },
    { regex: /#1a1d24/gi, replace: 'var(--bg-panel-header)' },
    { regex: /#0a0a0c/gi, replace: 'var(--bg-card)' },
    { regex: /#0f1115/gi, replace: 'var(--bg-settings)' },
    { regex: /#f1f3f5/gi, replace: 'var(--text-color)' },
    { regex: /#f8fafc/gi, replace: 'var(--text-light)' },
    { regex: /#e2e8f0/gi, replace: 'var(--text-light-alt)' },
    { regex: /#cbd5e0/gi, replace: 'var(--text-muted-light)' },
    { regex: /#cbd5e1/gi, replace: 'var(--text-muted-light)' },
    { regex: /#94a3b8/gi, replace: 'var(--text-muted)' },
    { regex: /#a0aec0/gi, replace: 'var(--text-muted)' },
    { regex: /#888888|#888(?![0-9a-f])/gi, replace: 'var(--text-disabled)' },
    { regex: /#777777|#777(?![0-9a-f])/gi, replace: 'var(--text-disabled)' },
    { regex: /#aaaaaa|#aaa(?![0-9a-f])/gi, replace: 'var(--text-muted-alt)' },
    { regex: /#cccccc|#ccc(?![0-9a-f])/gi, replace: 'var(--text-muted-alt)' },
    { regex: /rgba\(255,\s*255,\s*255,\s*0\.1\)/g, replace: 'var(--border-light)' },
    { regex: /rgba\(255,\s*255,\s*255,\s*0\.08\)/g, replace: 'var(--border-color)' },
    { regex: /rgba\(255,\s*255,\s*255,\s*0\.05\)/g, replace: 'var(--border-faint)' },
    { regex: /rgba\(255,\s*255,\s*255,\s*0\.15\)/g, replace: 'var(--border-medium)' },
    { regex: /rgba\(255,\s*255,\s*255,\s*0\.2\)/g, replace: 'var(--border-strong)' },
    { regex: /rgba\(255,\s*255,\s*255,\s*0\.3\)/g, replace: 'var(--border-stronger)' },
    { regex: /rgba\(255,\s*255,\s*255,\s*0\.03\)/g, replace: 'var(--glass-bg)' },
    { regex: /rgba\(0,\s*0,\s*0,\s*0\.2\)/g, replace: 'var(--glass-dark-bg)' },
    { regex: /rgba\(0,\s*0,\s*0,\s*0\.3\)/g, replace: 'var(--glass-darker-bg)' },
    { regex: /rgba\(0,\s*0,\s*0,\s*0\.4\)/g, replace: 'var(--glass-darkest-bg)' }
];

const keepWhiteBlackMap = [
    { regex: /#ffffff|#fff(?![0-9a-f])/gi, replace: 'var(--text-primary)' },
    { regex: /#000000|#000(?![0-9a-f])/gi, replace: 'var(--bg-black)' }
];

const files = getAllFiles('src');

files.forEach(file => {
    let content = fs.readFileSync(file, 'utf8');

    // First safely replace color tokens
    colorMap.forEach(rule => {
        content = content.replace(rule.regex, rule.replace);
    });

    keepWhiteBlackMap.forEach(rule => {
        content = content.replace(rule.regex, rule.replace);
    });

    fs.writeFileSync(file, content);
});

console.log('Colors replaced with CSS variables.');
