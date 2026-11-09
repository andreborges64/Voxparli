// Kept separate from the classic app script so profile and course features work
// even when someone opens index.html directly from disk. Vite bundles this
// dynamic import for hosted web and Capacitor builds.
window.voxparliLoadAI = () => import('@mlc-ai/web-llm');
