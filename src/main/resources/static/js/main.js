
    // Добавляем отступ к body для фиксированной навигации
    document.addEventListener('DOMContentLoaded', function() {
    const navbar = document.querySelector('.navbar');
    document.body.style.paddingTop = navbar.offsetHeight + 'px';

    // Активация ссылок в навигации
    const currentPath = window.location.pathname;
    document.querySelectorAll('.navbar-nav .nav-link').forEach(link => {
    if (link.getAttribute('href') === currentPath) {
    link.classList.add('active');
}
});
});
