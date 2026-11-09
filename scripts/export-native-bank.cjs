const fs = require('node:fs');
const path = require('node:path');

global.window = {};
require('../local-bank.js');

const languages = ['en', 'es', 'it', 'fr', 'de', 'zh', 'ja', 'ru', 'el', 'ko', 'la', 'nb', 'nl', 'sv', 'da'];
const bands = {
  zh: ['A1', 'A2', 'B1', 'B2', 'C1', 'C2', 'C2', 'C2', 'C2'],
  ja: ['A1', 'A2', 'B1', 'B2', 'C1'],
  ko: ['A1', 'A2', 'B1', 'B2', 'C1', 'C2']
};
const levels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
const bank = {};

for (const language of languages) {
  for (const level of new Set(bands[language] || levels)) {
    bank[`${language}:${level}`] = window.voxparliLocalBank.catalog(language, level);
  }
}

const output = path.join(__dirname, '..', 'android-native', 'app', 'src', 'main', 'assets', 'question-bank.json');
fs.mkdirSync(path.dirname(output), { recursive: true });
fs.writeFileSync(output, `${JSON.stringify(bank)}\n`);
console.log(`Exported ${Object.values(bank).reduce((sum, questions) => sum + questions.length, 0)} offline questions to ${output}`);