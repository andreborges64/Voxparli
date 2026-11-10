const fs = require('node:fs');
const path = require('node:path');
// Carrega o gerador local usado para preparar o banco do aplicativo Android.
global.window = {};
require('../local-bank.js');
// Define idiomas, faixas e agrupamentos de níveis nativos.
const languages = ['en', 'es', 'it', 'fr', 'de', 'zh', 'ja', 'ru', 'el', 'ko', 'la', 'nb', 'nl', 'sv', 'da'];
const bands = {
  zh: ['A1', 'A2', 'B1', 'B2', 'C1', 'C2', 'C2', 'C2', 'C2'],
  ja: ['A1', 'A2', 'B1', 'B2', 'C1'],
  ko: ['A1', 'A2', 'B1', 'B2', 'C1', 'C2']
};
const levels = ['A1', 'A2', 'B1', 'B2', 'C1', 'C2'];
// Gera as questões de cada curso e faixa compatível.
const bank = {};
const wordTranslations = {};
for (const language of languages) {
  for (const level of new Set(bands[language] || levels)) {
    const key = `${language}:${level}`;
    bank[key] = window.voxparliLocalBank.catalog(language, level);
    for (const exercise of bank[key]) {
      const reverse = exercise.kind === 'write';
      const translationsKey = reverse ? `${key}:reverse` : key;
      const translations = wordTranslations[translationsKey] ||= {};
      const words = exercise.prompt.match(/[\p{Script=Han}\p{Script=Hiragana}\p{Script=Katakana}\p{Script=Hangul}]+|[\p{L}\p{M}]+(?:['’\-][\p{L}\p{M}]+)*/gu) || [];
      for (const word of words) {
        const translation = window.voxparliLocalBank.translateWord(language, word, level, reverse);
        if (translation) translations[window.voxparliLocalBank.normalize(word)] = translation;
      }
    }
  }
}
// Grava o banco no diretório de recursos Android.
const outputDirectory = path.join(__dirname, '..', 'android-native', 'app', 'src', 'main', 'assets');
fs.mkdirSync(outputDirectory, { recursive: true });
const questionOutput = path.join(outputDirectory, 'question-bank.json');
const translationOutput = path.join(outputDirectory, 'word-translations.json');
fs.writeFileSync(questionOutput, `${JSON.stringify(bank)}\n`);
fs.writeFileSync(translationOutput, `${JSON.stringify(wordTranslations)}\n`);
console.log(`Exported ${Object.values(bank).reduce((sum, questions) => sum + questions.length, 0)} offline questions and ${Object.values(wordTranslations).reduce((sum, words) => sum + Object.keys(words).length, 0)} word translations.`);
