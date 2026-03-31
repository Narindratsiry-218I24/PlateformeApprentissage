/**
 * PROFESSEUR DASHBOARD - JavaScript Functions
 */

// ============================================
// DOM Elements & Global Variables
// ============================================
let currentModal = null;
let currentToast = null;
let quillEditor = null;

// Structure data
let sections = [];
let currentContentTab = 'video';

// ============================================
// Utility Functions
// ============================================
function showToast(message, type = 'success') {
    if (currentToast) currentToast.remove();
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'} mr-2"></i>${message}`;
    document.body.appendChild(toast);
    currentToast = toast;
    setTimeout(() => { toast.style.animation = 'slideOutRight 0.3s ease'; setTimeout(() => toast.remove(), 300); }, 3000);
}

function escapeHtml(str) {
    if (!str) return '';
    return str.replace(/[&<>]/g, m => m === '&' ? '&amp;' : m === '<' ? '&lt;' : '&gt;');
}

function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) { modal.classList.remove('hidden'); modal.classList.add('flex'); currentModal = modal; document.body.style.overflow = 'hidden'; }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) { modal.classList.add('hidden'); modal.classList.remove('flex'); currentModal = null; document.body.style.overflow = ''; }
}

function closeCurrentModal() {
    if (currentModal) { currentModal.classList.add('hidden'); currentModal.classList.remove('flex'); currentModal = null; document.body.style.overflow = ''; }
}

function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    if (sidebar) sidebar.classList.toggle('open');
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => { clearTimeout(timeout); func(...args); };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// ============================================
// Rich Text Editor
// ============================================
function initQuillEditor() {
    if (!quillEditor && document.getElementById('quillEditor')) {
        quillEditor = new Quill('#quillEditor', {
            theme: 'snow',
            placeholder: 'Rédigez votre contenu ici...',
            modules: {
                toolbar: [
                    [{ 'header': [1, 2, 3, false] }],
                    ['bold', 'italic', 'underline'],
                    ['blockquote', 'code-block'],
                    [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                    ['link', 'image'],
                    ['clean']
                ]
            }
        });
    }
}

// ============================================
// Course Structure Management
// ============================================
function renderSections() {
    const container = document.getElementById('sectionsContainer');
    if (!container) return;
    if (sections.length === 0) {
        container.innerHTML = `<div class="text-center py-12 text-gray-500 border-2 border-dashed border-gray-300 rounded-lg">
                                <i class="fas fa-layer-group text-4xl mb-3"></i>
                                <p>Cliquez sur "Ajouter une section" pour commencer</p>
                            </div>`;
        return;
    }
    container.innerHTML = sections.map(section => `
        <div class="border border-gray-200 rounded-lg overflow-hidden">
            <div class="bg-gray-50 p-4 flex justify-between items-center">
                <div class="flex items-center gap-3 flex-1">
                    <i class="fas fa-folder-open text-blue-500"></i>
                    <input type="text" value="${escapeHtml(section.titre)}" data-id="${section.id}"
                           onchange="updateSectionTitle(${section.id}, this.value)"
                           class="flex-1 px-3 py-2 border border-gray-300 rounded-lg font-semibold">
                </div>
                <div class="flex gap-2">
                    <button onclick="openAddChapitreModal('section', ${section.id})" class="text-green-600 hover:bg-green-50 px-3 py-1 rounded text-sm">
                        <i class="fas fa-plus"></i> Chapitre
                    </button>
                    <button onclick="deleteSection(${section.id})" class="text-red-600 hover:bg-red-50 px-2 py-1 rounded">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            <div class="p-4">${renderChapitres(section.chapitres, section.id)}</div>
        </div>
    `).join('');
}

function renderChapitres(chapitresList, sectionId) {
    if (!chapitresList || chapitresList.length === 0) {
        return `<div class="text-center py-4 text-gray-500 text-sm border-2 border-dashed border-gray-200 rounded-lg">
                    <i class="fas fa-folder-open"></i> Aucun chapitre
                    <button onclick="openAddChapitreModal('section', ${sectionId})" class="ml-2 text-blue-600">Ajouter</button>
                </div>`;
    }
    return chapitresList.map(chapitre => `
        <div class="border border-gray-200 rounded-lg mb-3">
            <div class="bg-blue-50 p-3 flex justify-between items-center">
                <div class="flex items-center gap-3 flex-1">
                    <i class="fas fa-chevron-right cursor-pointer text-gray-500" onclick="toggleChapitre(${chapitre.id})"></i>
                    <i class="fas fa-book text-blue-500"></i>
                    <input type="text" value="${escapeHtml(chapitre.titre)}" data-id="${chapitre.id}"
                           onchange="updateChapitreTitle(${chapitre.id}, this.value)"
                           class="flex-1 px-2 py-1 border border-gray-300 rounded text-sm">
                </div>
                <div class="flex gap-1">
                    <button onclick="openAddSousChapitreModal('chapitre', ${chapitre.id})" class="text-green-600 text-xs hover:bg-green-50 px-2 py-1 rounded">
                        <i class="fas fa-level-down-alt"></i> Sous-chapitre
                    </button>
                    <button onclick="openAddLeconModal('chapitre', ${chapitre.id})" class="text-blue-600 text-xs hover:bg-blue-50 px-2 py-1 rounded">
                        <i class="fas fa-plus"></i> Leçon
                    </button>
                    <button onclick="deleteChapitre(${chapitre.id})" class="text-red-600 text-xs hover:bg-red-50 px-2 py-1 rounded">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            <div id="chapitre-${chapitre.id}" class="p-3 hidden">
                ${renderSousChapitres(chapitre.sousChapitres || [], chapitre.id)}
                ${renderLeconsList(chapitre.lecons || [], chapitre.id)}
            </div>
        </div>
    `).join('');
}

function renderSousChapitres(sousChapitres, parentId) {
    if (!sousChapitres.length) return '';
    return sousChapitres.map(sous => `
        <div class="border-l-2 border-green-300 ml-4 pl-3 mb-2">
            <div class="bg-green-50 p-2 rounded flex justify-between items-center">
                <div class="flex items-center gap-2 flex-1">
                    <i class="fas fa-level-down-alt text-green-600"></i>
                    <input type="text" value="${escapeHtml(sous.titre)}" data-id="${sous.id}"
                           onchange="updateChapitreTitle(${sous.id}, this.value)"
                           class="flex-1 px-2 py-1 border border-gray-300 rounded text-sm">
                </div>
                <div class="flex gap-1">
                    <button onclick="openAddLeconModal('sousChapitre', ${sous.id})" class="text-blue-600 text-xs hover:bg-blue-50 px-2 py-1 rounded">
                        <i class="fas fa-plus"></i> Leçon
                    </button>
                    <button onclick="deleteChapitre(${sous.id})" class="text-red-600 text-xs hover:bg-red-50 px-2 py-1 rounded">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
            <div id="chapitre-${sous.id}" class="pl-4 mt-2">${renderLeconsList(sous.lecons || [], sous.id)}</div>
        </div>
    `).join('');
}

function renderLeconsList(leconsList, parentId) {
    if (!leconsList || leconsList.length === 0) {
        return `<div class="text-center py-2 text-gray-400 text-sm"><i class="fas fa-plus-circle"></i> Cliquez sur + Leçon pour ajouter du contenu</div>`;
    }
    return leconsList.map(lecon => `
        <div class="bg-purple-50 p-2 rounded flex justify-between items-center mb-1">
            <div class="flex items-center gap-2 flex-1">
                <i class="fas ${lecon.type === 'VIDEO' ? 'fa-video' : lecon.type === 'TEXTE' ? 'fa-file-alt' : 'fa-file-pdf'}"></i>
                <span class="text-sm font-medium">${escapeHtml(lecon.titre)}</span>
                ${lecon.apercuGratuit ? '<span class="text-xs bg-green-100 text-green-700 px-1 rounded">Aperçu</span>' : ''}
            </div>
            <button onclick="editLecon(${lecon.id})" class="text-blue-600 text-xs px-2"><i class="fas fa-edit"></i></button>
        </div>
    `).join('');
}

function renderLeconsContainer() {
    const container = document.getElementById('leconsContainer');
    if (!container) return;
    if (sections.length === 0) {
        container.innerHTML = `<div class="text-center py-12 text-gray-500 border-2 border-dashed border-gray-300 rounded-lg">
                                <i class="fas fa-book-open text-4xl mb-3"></i>
                                <p>Créez d'abord une section et des chapitres</p>
                            </div>`;
        return;
    }
    let html = '<div class="space-y-4">';
    sections.forEach(section => {
        html += `<div class="border border-gray-200 rounded-lg"><div class="bg-gray-50 p-3"><h3 class="font-semibold"><i class="fas fa-folder-open text-blue-500 mr-2"></i>${escapeHtml(section.titre)}</h3></div><div class="p-3">`;
        section.chapitres.forEach(chapitre => {
            html += `<div class="mb-3 border-l-4 border-blue-300 pl-3"><div class="font-medium text-blue-700"><i class="fas fa-book mr-2"></i>${escapeHtml(chapitre.titre)}</div>`;
            html += renderLeconsForParent(chapitre.lecons);
            chapitre.sousChapitres?.forEach(sous => {
                html += `<div class="ml-4 mt-2 border-l-2 border-green-300 pl-3"><div class="text-sm text-green-600"><i class="fas fa-level-down-alt mr-1"></i>${escapeHtml(sous.titre)}</div>`;
                html += renderLeconsForParent(sous.lecons);
                html += `</div>`;
            });
            html += `</div>`;
        });
        html += `</div></div>`;
    });
    html += '</div>';
    container.innerHTML = html;
}

function renderLeconsForParent(leconsList) {
    if (!leconsList || leconsList.length === 0) return '<div class="text-gray-400 text-sm ml-4">Aucune leçon</div>';
    return leconsList.map(l => `
        <div class="ml-4 p-2 bg-purple-50 rounded mb-1 flex justify-between items-center">
            <span><i class="fas ${l.type === 'VIDEO' ? 'fa-video' : l.type === 'TEXTE' ? 'fa-file-alt' : 'fa-file-pdf'} mr-2"></i>${escapeHtml(l.titre)}</span>
            ${l.apercuGratuit ? '<span class="text-xs bg-green-100 text-green-700 px-1 rounded">Gratuit</span>' : ''}
        </div>
    `).join('');
}

function toggleChapitre(id) {
    const el = document.getElementById(`chapitre-${id}`);
    if (el) el.classList.toggle('hidden');
}

function updateSectionTitle(id, value) {
    const section = sections.find(s => s.id === id);
    if (section) section.titre = value;
    showToast('Titre de section mis à jour');
}

function updateChapitreTitle(id, value) {
    function findAndUpdate(list) {
        for (let section of sections) {
            for (let chap of section.chapitres) {
                if (chap.id === id) { chap.titre = value; return true; }
                for (let sous of chap.sousChapitres || []) {
                    if (sous.id === id) { sous.titre = value; return true; }
                }
            }
        }
        return false;
    }
    findAndUpdate();
    showToast('Titre mis à jour');
}

function deleteSection(id) {
    if (confirm('Supprimer cette section ?')) {
        sections = sections.filter(s => s.id !== id);
        renderSections();
        showToast('Section supprimée');
    }
}

function deleteChapitre(id) {
    if (confirm('Supprimer ce chapitre ?')) {
        function findAndDelete(list) {
            for (let section of sections) {
                for (let i = 0; i < section.chapitres.length; i++) {
                    if (section.chapitres[i].id === id) { section.chapitres.splice(i, 1); return true; }
                    for (let j = 0; j < (section.chapitres[i].sousChapitres || []).length; j++) {
                        if (section.chapitres[i].sousChapitres[j].id === id) { section.chapitres[i].sousChapitres.splice(j, 1); return true; }
                    }
                }
            }
            return false;
        }
        findAndDelete();
        renderSections();
        showToast('Chapitre supprimé');
    }
}

// ============================================
// Section Management
// ============================================
function openAddSectionModal() {
    document.getElementById('sectionTitre').value = '';
    document.getElementById('sectionDescription').value = '';
    document.getElementById('sectionTitreError')?.classList.add('hidden');
    openModal('sectionModal');
}

function addSection() {
    const titre = document.getElementById('sectionTitre').value.trim();
    if (!titre) {
        document.getElementById('sectionTitreError').textContent = 'Le titre est obligatoire';
        document.getElementById('sectionTitreError').classList.remove('hidden');
        document.getElementById('sectionTitre').classList.add('error-border');
        return;
    }
    sections.push({
        id: Date.now(),
        titre: titre,
        description: document.getElementById('sectionDescription').value,
        chapitres: []
    });
    renderSections();
    closeModal('sectionModal');
    showToast('Section ajoutée');
}

// ============================================
// Chapter Management
// ============================================
function openAddChapitreModal(parentType, parentId) {
    document.getElementById('chapitreModalTitle').innerText = parentType === 'section' ? 'Ajouter un chapitre' : 'Ajouter un sous-chapitre';
    document.getElementById('chapitreParentType').value = parentType;
    document.getElementById('chapitreParentId').value = parentId;
    document.getElementById('chapitreTitre').value = '';
    document.getElementById('chapitreDescription').value = '';
    document.getElementById('chapitreTitreError')?.classList.add('hidden');
    openModal('chapitreModal');
}

function openAddSousChapitreModal(parentType, parentId) {
    openAddChapitreModal(parentType, parentId);
}

function addChapitre() {
    const titre = document.getElementById('chapitreTitre').value.trim();
    if (!titre) {
        document.getElementById('chapitreTitreError').textContent = 'Le titre est obligatoire';
        document.getElementById('chapitreTitreError').classList.remove('hidden');
        document.getElementById('chapitreTitre').classList.add('error-border');
        return;
    }
    const parentType = document.getElementById('chapitreParentType').value;
    const parentId = parseInt(document.getElementById('chapitreParentId').value);
    const chapitre = {
        id: Date.now(),
        titre: titre,
        description: document.getElementById('chapitreDescription').value,
        lecons: [],
        sousChapitres: []
    };
    if (parentType === 'section') {
        const section = sections.find(s => s.id === parentId);
        if (section) section.chapitres.push(chapitre);
    } else {
        function addToChapitre() {
            for (let section of sections) {
                for (let chap of section.chapitres) {
                    if (chap.id === parentId) { chap.sousChapitres.push(chapitre); return true; }
                    for (let sous of chap.sousChapitres || []) {
                        if (sous.id === parentId) { sous.sousChapitres.push(chapitre); return true; }
                    }
                }
            }
            return false;
        }
        addToChapitre();
    }
    renderSections();
    closeModal('chapitreModal');
    showToast(parentType === 'section' ? 'Chapitre ajouté' : 'Sous-chapitre ajouté');
}

// ============================================
// Lesson Management
// ============================================
function openAddLeconModal(parentType, parentId) {
    document.getElementById('leconParentId').value = parentId;
    document.getElementById('leconParentType').value = parentType;
    document.getElementById('leconTitre').value = '';
    document.getElementById('leconObjectifs').value = '';
    document.getElementById('leconVideoUrl').value = '';
    document.getElementById('leconDuree').value = '';
    document.getElementById('leconOrdre').value = '0';
    document.getElementById('leconApercuGratuit').checked = false;
    document.getElementById('videoPreview')?.classList.add('hidden');
    if (quillEditor) quillEditor.root.innerHTML = '';
    switchContentTab('video');
    initQuillEditor();
    openModal('leconModal');
}

function switchContentTab(tab) {
    currentContentTab = tab;
    document.querySelectorAll('.content-type-tab').forEach(t => {
        t.classList.remove('active', 'border-blue-500', 'text-blue-600');
        t.classList.add('text-gray-600');
    });
    document.querySelectorAll('.content-panel').forEach(p => p.classList.add('hidden'));
    const activeTab = document.getElementById(`tab${tab.charAt(0).toUpperCase() + tab.slice(1)}`);
    if (activeTab) activeTab.classList.add('active', 'border-blue-500', 'text-blue-600');
    document.getElementById(`${tab}Content`).classList.remove('hidden');
}

function addLecon() {
    const titre = document.getElementById('leconTitre').value.trim();
    if (!titre) {
        document.getElementById('leconTitreError').textContent = 'Le titre est obligatoire';
        document.getElementById('leconTitreError').classList.remove('hidden');
        document.getElementById('leconTitre').classList.add('error-border');
        return;
    }
    document.getElementById('leconTitreError').classList.add('hidden');
    document.getElementById('leconTitre').classList.remove('error-border');

    const parentId = parseInt(document.getElementById('leconParentId').value);
    let contenuTexte = '';
    if (quillEditor) contenuTexte = quillEditor.root.innerHTML;

    const lecon = {
        id: Date.now(),
        titre: titre,
        objectifs: document.getElementById('leconObjectifs').value,
        type: currentContentTab.toUpperCase(),
        videoUrl: document.getElementById('leconVideoUrl').value,
        contenuTexte: contenuTexte,
        duree: document.getElementById('leconDuree').value,
        ordre: document.getElementById('leconOrdre').value,
        apercuGratuit: document.getElementById('leconApercuGratuit').checked
    };

    function addToParent() {
        for (let section of sections) {
            for (let chap of section.chapitres) {
                if (chap.id === parentId) { chap.lecons.push(lecon); return true; }
                for (let sous of chap.sousChapitres || []) {
                    if (sous.id === parentId) { sous.lecons.push(lecon); return true; }
                }
            }
        }
        return false;
    }
    addToParent();
    renderSections();
    renderLeconsContainer();
    closeModal('leconModal');
    showToast('Leçon ajoutée');
}

// ============================================
// Dashboard Functions
// ============================================
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    const hours = Math.floor(diff / 3600000);
    if (hours < 1) return 'Il y a quelques minutes';
    if (hours < 24) return `Il y a ${hours}h`;
    return date.toLocaleDateString('fr-FR');
}

// ============================================
// Search Functionality
// ============================================
const searchCourses = debounce(function(searchTerm) {
    const courseCards = document.querySelectorAll('.course-card');
    let visibleCount = 0;
    courseCards.forEach(card => {
        const title = card.querySelector('.course-title')?.textContent.toLowerCase() || '';
        if (title.includes(searchTerm)) { card.style.display = ''; visibleCount++; }
        else { card.style.display = 'none'; }
    });
    const noResults = document.getElementById('noResults');
    if (noResults) noResults.style.display = visibleCount === 0 ? 'block' : 'none';
}, 300);

// ============================================
// Initialization
// ============================================
document.addEventListener('DOMContentLoaded', function() {
    // Search input listener
    const searchInput = document.getElementById('searchCourses');
    if (searchInput) searchInput.addEventListener('input', (e) => searchCourses(e.target.value.toLowerCase()));

    // Close modals on escape key
    document.addEventListener('keydown', (e) => { if (e.key === 'Escape' && currentModal) closeCurrentModal(); });

    // Close modals on outside click
    document.addEventListener('click', (e) => { if (currentModal && e.target === currentModal) closeCurrentModal(); });

    // Auto-hide alerts
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => setTimeout(() => { alert.style.opacity = '0'; setTimeout(() => alert.remove(), 300); }, 5000));

    // Slug auto-generation
    const titreInput = document.getElementById('courseTitre');
    const slugInput = document.getElementById('courseSlug');
    if (titreInput && slugInput) {
        titreInput.addEventListener('input', function() {
            const slug = this.value.toLowerCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '');
            slugInput.value = slug;
        });
    }

    // Char counter
    const descCourte = document.getElementById('courseDescriptionCourte');
    const charCount = document.getElementById('charCount');
    if (descCourte && charCount) {
        descCourte.addEventListener('input', () => charCount.innerText = descCourte.value.length);
    }

    // Image preview
    const courseImage = document.getElementById('courseImage');
    const imagePreview = document.getElementById('imagePreview');
    const previewImg = document.getElementById('previewImg');
    if (courseImage) {
        courseImage.addEventListener('change', function(e) {
            if (e.target.files && e.target.files[0]) {
                const reader = new FileReader();
                reader.onload = ev => { previewImg.src = ev.target.result; imagePreview.classList.remove('hidden'); };
                reader.readAsDataURL(e.target.files[0]);
            }
        });
    }

    // Video URL preview
    const leconVideoUrl = document.getElementById('leconVideoUrl');
    const videoPreview = document.getElementById('videoPreview');
    const videoPreviewText = document.getElementById('videoPreviewText');
    if (leconVideoUrl) {
        leconVideoUrl.addEventListener('input', function() {
            if (this.value.trim()) { videoPreview.classList.remove('hidden'); videoPreviewText.innerText = this.value; }
            else { videoPreview.classList.add('hidden'); }
        });
    }
});