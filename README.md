# Voxparli

Voxparli reúne o curso web e um aplicativo Android nativo em Kotlin para estudar inglês, espanhol, italiano, francês, alemão, mandarim, japonês, russo, grego, coreano, latim, norueguês, holandês, sueco e dinamarquês. A versão Android fica em `android-native/` e usa Jetpack Compose; a aplicação web continua na raiz do repositório. A identidade visual usa um balão de conversa com um “v” em forma de onda sonora.

## O que já funciona

- Cursos independentes para até quinze idiomas, salvos no dispositivo.
- Perfil local com nome personalizado e iniciais no aplicativo.
- Exportação e importação de um arquivo JSON com perfil, cursos, níveis, sequência e exercícios concluídos para transportar ou guardar o progresso.
- Geração de lições novas por IA local com WebLLM na versão web; depois de ativar a IA em um curso, o botão **Continuar** gera novas questões automaticamente quando a fila daquele nível acabar.
- Nivelamento por idioma: CEFR nos cursos europeus, HSK 1–9 no mandarim, JLPT N5–N1 no japonês, TOPIK 1–6 no coreano e etapas de leitura próprias para latim.
- Testes de nivelamento com o dobro de questões: cada questão no idioma-alvo é seguida por uma nova questão com enunciado em português e alternativas no idioma estudado.
- Lições de dez questões com correção, pontuação, progresso e sequência diária.
- Palavras clicáveis nos enunciados, com balão de tradução no curso web e no aplicativo Android.
- Revisão e trilha de unidades.
- Banco combinatório local para inglês, espanhol, italiano, francês e alemão, com até 1.440 combinações por idioma e nível.
- Interface adaptável para celular e desktop na web, com cache offline como PWA.
- Pronúncia automática dos enunciados por síntese de voz do dispositivo, com configuração para ativar/desativar.
- Exercícios orais opcionais com reconhecimento de fala nas posições 4 e 8 da lição.
- Aplicativo Android nativo em Jetpack Compose com navegação inferior, interface para celular, cursos, lições, revisão, teste de nível e ícone de launcher.
- Banco offline nativo com exercícios exportados do gerador compartilhado; progresso local e importação de arquivos de progresso da versão web.
- Síntese de voz e reconhecimento de fala pelas APIs do Android.
- Workflow do GitHub Actions que compila o projeto Kotlin e publica o APK debug como artefato.

## Escalas e exercícios

As lições incluem múltipla escolha e tradução ativa: em algumas questões, a pessoa recebe a frase em português e a escreve no idioma estudado. Mandarim, japonês, russo, grego e coreano também têm exercícios de leitura dos seus sistemas de escrita, com transliteração ou pronúncia em questões iniciais.

Inglês, espanhol, italiano, francês e alemão usam o banco combinatório. Mandarim, japonês, russo, grego, coreano, latim, norueguês, holandês, sueco e dinamarquês têm conteúdo inicial de frases e exercícios por nível, ainda em expansão. HSK, JLPT e TOPIK são suas próprias escalas, não equivalências oficiais do CEFR; o nivelamento alterna questões no idioma-alvo com questões de enunciado em português e reinicia se a pessoa mudar a seleção durante o teste.

O app Kotlin empacota o catálogo local para uso offline. Execute `node scripts/export-native-bank.cjs` depois de alterar o gerador para atualizar esse catálogo.

## Prática local sem IA

As lições locais funcionam offline. Para os cinco idiomas originais, o gerador monta até 1.440 combinações por nível e filtra combinações incompatíveis entre verbo, objeto e contexto. Nos outros cursos, usa frases iniciais por nível e exercícios de leitura. O app Kotlin inclui o catálogo exportado e mantém progresso e histórico no dispositivo. Esse banco é montado por regras, combinações e frases iniciais; ainda precisa de revisão pedagógica para uso como currículo definitivo.

## IA local e uso offline

Na versão web, **Ativar questões com IA** usa o modelo `Llama-3.2-1B-Instruct-q4f16_1-MLC` no próprio dispositivo via WebGPU. O modelo não vai dentro do APK. A versão Android nativa usa o catálogo local e a síntese/reconhecimento de voz do sistema; não inclui geração por IA. A IA pode cometer erros linguísticos; revise as traduções antes de usá-las como material didático definitivo.

## Executar localmente

Requer Node.js 20+ e npm.

```bash
npm install
npm run web:serve
```

## Gerar APK Kotlin localmente

Requer Node.js 20, JDK 17 ou superior e Android SDK 35 configurado. O Gradle 8.9 é baixado pelo Wrapper do projeto.

```bash
node scripts/export-native-bank.cjs
android-native/gradlew -p android-native :app:packageVoxparliDebugApk
```

O APK debug aparece como `Voxparli.apk` em `android-native/app/build/outputs/apk/debug/`.

## Gerar APK pelo GitHub

Envie este diretório para um repositório GitHub. Em **Actions**, execute **Build Kotlin APK** com `workflow_dispatch`, ou faça push na branch `main`. Ao terminar, abra a execução e baixe `voxparli-apk` na seção **Artifacts**; o arquivo dentro do artefato se chama `Voxparli.apk`.

## Limites deste protótipo

O conteúdo do banco é gerado por combinações de modelos de frase, não é um currículo revisado por professores. O perfil e o progresso ficam no armazenamento local do navegador; para levar os dados a outro dispositivo, use **Exportar progresso** e depois **Importar progresso** no perfil. O APK gerado é de depuração; distribuição na Play Store exige assinatura de release e configuração adicional.
