# Voxparli

Aplicativo responsivo para estudar inglês, espanhol, italiano, francês, alemão, mandarim, japonês, russo, grego, coreano, latim, norueguês, holandês, sueco e dinamarquês. O mesmo projeto roda na web e pode ser empacotado como APK Android com Capacitor. A identidade visual usa um balão de conversa com um “v” em forma de onda sonora, em roxo profundo e lavanda. A tela principal e o perfil também funcionam ao abrir `index.html` diretamente; para a IA local, use o servidor de desenvolvimento, a versão publicada ou o APK.

## O que já funciona

- Cursos independentes para até quinze idiomas, salvos no dispositivo.
- Perfil local com nome personalizado e iniciais no aplicativo.
- Exportação e importação de um arquivo JSON com perfil, cursos, níveis, sequência e exercícios concluídos para transportar ou guardar o progresso.
- Geração de lições novas por IA local com WebLLM, no navegador e no APK; depois de ativar a IA em um curso, o botão **Continuar** gera novas questões automaticamente quando a fila daquele nível acabar.
- Nivelamento por idioma: CEFR nos cursos europeus, HSK 1–9 no mandarim, JLPT N5–N1 no japonês, TOPIK 1–6 no coreano e etapas de leitura próprias para latim.
- Lições de dez questões com correção, pontuação, progresso e sequência diária.
- Revisão e trilha de unidades.
- Banco combinatório local para inglês, espanhol, italiano, francês e alemão, com até 1.440 combinações por idioma e nível.
- Interface adaptável para celular e desktop, com cache offline como PWA.
- Pronúncia automática dos enunciados por síntese de voz do dispositivo, com configuração para ativar/desativar.
- Exercícios orais opcionais com reconhecimento de fala (quando o navegador/WebView oferece suporte), inseridos nas posições 4 e 8 da lição; pausa de 15 minutos substitui temporariamente esses itens por questões comuns, preservando 10 exercícios e a pontuação.
- Workflow do GitHub Actions que gera APK debug e publica o arquivo como artefato do workflow.

## Escalas e exercícios

As lições incluem múltipla escolha e tradução ativa: em algumas questões, a pessoa recebe a frase em português e a escreve no idioma estudado. Mandarim, japonês, russo, grego e coreano também têm exercícios de leitura dos seus sistemas de escrita, com transliteração ou pronúncia em questões iniciais.

Inglês, espanhol, italiano, francês e alemão usam o banco combinatório. Mandarim, japonês, russo, grego, coreano, latim, norueguês, holandês, sueco e dinamarquês têm conteúdo inicial de frases e exercícios por nível, ainda em expansão. HSK, JLPT e TOPIK são suas próprias escalas, não equivalências oficiais do CEFR; o nivelamento usa questões do próprio idioma e reinicia se a pessoa mudar a seleção durante o teste.

## Prática local sem IA

Com a IA desligada, as lições continuam variadas e funcionam offline. Para os cinco idiomas originais, o gerador local monta até 1.440 combinações por nível e filtra combinações incompatíveis entre verbo, objeto e contexto. Nos outros cursos, o modo local usa um conjunto inicial de frases revisadas por nível; a ampliação desse banco segue em andamento. Antes de exibir uma lição, ele filtra as questões já apresentadas naquele idioma; nos níveis seguintes, inclui também questões de revisão de conteúdos anteriores. O histórico faz parte dos dados exportados no perfil. Esse banco é montado por regras e combinações, então serve como prática de protótipo e ainda precisa de revisão pedagógica para uso como currículo definitivo.

## IA local e uso offline

Em cada curso, **Ativar questões com IA** usa o modelo `Llama-3.2-1B-Instruct-q4f16_1-MLC` no próprio dispositivo via WebGPU. O modelo recebe instruções específicas do CEFR atual e pode revisar estruturas anteriores. Antes de guardar as questões, o app compara as frases normalizadas com o banco-base, com a fila e com os hashes de questões já geradas naquele nível; duplicatas são descartadas e novas tentativas são feitas. Depois da ativação, **Continuar** usa a fila de questões novas e a reabastece automaticamente. O modelo não vai dentro do APK: na primeira utilização é preciso internet para baixá-lo (centenas de MB); depois o runtime o mantém no cache local para gerar lições sem conexão. Essa escolha evita transformar o APK em um download enorme. O navegador e a WebView Android precisam oferecer WebGPU e memória gráfica suficiente. Se o dispositivo não oferecer WebGPU, o curso continua com as lições locais tradicionais. A IA pode cometer erros linguísticos; revise as traduções antes de usá-las como material didático definitivo.

## Executar localmente

Requer Node.js 20+ e npm.

```bash
npm install
npm run web:serve
```

## Gerar APK localmente

Requer Android Studio/SDK, Java 17 e ambiente Android configurado.

```bash
npm install
npx cap add android
npx cap sync android
cd android
./gradlew assembleDebug
```

O APK debug aparece em `android/app/build/outputs/apk/debug/app-debug.apk`.

## Gerar APK pelo GitHub

Envie este diretório para um repositório GitHub. Em **Actions**, execute **Build APK** com `workflow_dispatch`, ou faça push na branch `main`. Ao terminar, abra a execução e baixe `voxparli-debug-apk` na seção **Artifacts**.

## Limites deste protótipo

O conteúdo do banco é gerado por combinações de modelos de frase, não é um currículo revisado por professores. O perfil e o progresso ficam no armazenamento local do navegador; para levar os dados a outro dispositivo, use **Exportar progresso** e depois **Importar progresso** no perfil. O APK gerado é de depuração; distribuição na Play Store exige assinatura de release e configuração adicional.
