// Main User Page Logic
document.addEventListener('DOMContentLoaded', () => {
    console.log('User page initialized');
    
    // Simple scroll animation for navbar
    window.addEventListener('scroll', () => {
        const nav = document.getElementById('main-nav');
        if (window.scrollY > 50) {
            nav.style.boxShadow = '0 10px 15px -3px rgb(0 0 0 / 0.1)';
        } else {
            nav.style.boxShadow = 'none';
        }
    });

    // Handle button clicks or other interactions here
});
