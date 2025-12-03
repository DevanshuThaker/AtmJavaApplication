// theme.js - toggles light/dark and persists in localStorage
(function(){
    const btn = document.getElementById('themeToggle') || document.querySelector('.theme-btn');
    const root = document.documentElement;
    const body = document.body;

    function applyTheme(theme){
        if(theme === 'light') body.classList.add('light');
        else body.classList.remove('light');
        if(btn) btn.textContent = theme === 'light' ? '🌙' : '☀️';
    }

    const saved = localStorage.getItem('theme') || 'dark';
    applyTheme(saved);

    if(btn){
        btn.addEventListener('click', () => {
            const now = body.classList.contains('light') ? 'dark' : 'light';
            localStorage.setItem('theme', now);
            applyTheme(now);
        });
    }
})();
