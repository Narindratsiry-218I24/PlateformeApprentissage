
document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard professeur chargé');

    // Gestion du menu mobile
    const menuToggle = document.getElementById('mobile-menu-toggle');
    if (menuToggle) {
        menuToggle.addEventListener('click', function() {
            document.querySelector('.sidebar-mobile')?.classList.toggle('open');
        });
    }

    // Confirmation de suppression
    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', function(e) {
            if (!confirm('Êtes-vous sûr de vouloir supprimer cet élément ?')) {
                e.preventDefault();
            }
        });
    });

    // Auto-fermeture des notifications
    setTimeout(() => {
        document.querySelectorAll('.toast').forEach(toast => {
            toast.style.opacity = '0';
            setTimeout(() => toast.remove(), 300);
        });
    }, 3000);
});

// Fonctions utilitaires
function showToast(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });
}