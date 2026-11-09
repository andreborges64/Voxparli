// Carrega a IA separadamente para manter o app e os cursos disponíveis offline.
// O Vite inclui esta importação dinâmica nas versões web e Capacitor.
window.voxparliLoadAI = () => import('@mlc-ai/web-llm');
