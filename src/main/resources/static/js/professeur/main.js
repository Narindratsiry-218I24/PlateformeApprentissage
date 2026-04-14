// Main JavaScript for professor interface

// Toast notification system
class ToastManager {
    constructor() {
        this.container = null;
        this.init();
    }

    init() {
        this.container = document.createElement('div');
        this.container.className = 'fixed bottom-4 right-4 z-50 space-y-2';
        document.body.appendChild(this.container);
    }

    show(message, type = 'success', duration = 3000) {
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;

        const icon = type === 'success' ? 'fa-check-circle' :
            type === 'error' ? 'fa-exclamation-circle' :
                type === 'warning' ? 'fa-exclamation-triangle' : 'fa-info-circle';

        toast.innerHTML = `
            <i class="fas ${icon}"></i>
            <span>${message}</span>
        `;

        this.container.appendChild(toast);

        setTimeout(() => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => toast.remove(), 300);
        }, duration);
    }
}

// Loading overlay
class LoadingManager {
    constructor() {
        this.overlay = null;
        this.init();
    }

    init() {
        this.overlay = document.createElement('div');
        this.overlay.className = 'fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center z-[9999] hidden';
        this.overlay.innerHTML = '<div class="spinner"></div>';
        document.body.appendChild(this.overlay);
    }

    show() {
        this.overlay.classList.remove('hidden');
    }

    hide() {
        this.overlay.classList.add('hidden');
    }
}

// Modal manager
class ModalManager {
    constructor() {
        this.modals = new Map();
    }

    register(id, modal) {
        this.modals.set(id, modal);
    }

    open(id) {
        const modal = this.modals.get(id);
        if (modal) {
            modal.classList.add('active');
        }
    }

    close(id) {
        const modal = this.modals.get(id);
        if (modal) {
            modal.classList.remove('active');
        }
    }

    closeAll() {
        this.modals.forEach(modal => {
            modal.classList.remove('active');
        });
    }
}

// API Service
class APIService {
    constructor() {
        this.baseUrl = '/api';
        this.csrfToken = document.querySelector('meta[name="_csrf"]')?.content;
        this.csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
    }

    async request(endpoint, options = {}) {
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        if (this.csrfToken && this.csrfHeader) {
            headers[this.csrfHeader] = this.csrfToken;
        }

        const response = await fetch(`${this.baseUrl}${endpoint}`, {
            ...options,
            headers
        });

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.message || 'Une erreur est survenue');
        }

        return response.json();
    }

    get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }

    post(endpoint, data) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    put(endpoint, data) {
        return this.request(endpoint, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    delete(endpoint) {
        return this.request(endpoint, { method: 'DELETE' });
    }
}

// Initialize managers
const toast = new ToastManager();
const loading = new LoadingManager();
const modalManager = new ModalManager();
const api = new APIService();

// Export for use in other scripts
window.toast = toast;
window.loading = loading;
window.modalManager = modalManager;
window.api = api;