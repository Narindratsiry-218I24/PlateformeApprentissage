const fs = require('fs');

let html = fs.readFileSync('src/main/resources/templates/etudiant/quiz-resultat.html', 'utf8');

const successSectionRegex = /<!-- Case 1: SUCCESS -->[\s\S]*?(?=<!-- Case 2: FAILURE -->)/;

const newSuccessSection = `<!-- Case 1: SUCCESS -->
    <div th:if="\${score >= 100}" class="bg-green-50 border border-green-200 rounded-2xl p-8 mb-8 text-center max-w-3xl mx-auto shadow-sm certificat-section">
        <h2 class="text-3xl font-black text-green-700 mb-2">🎉 FÉLICITATIONS !</h2>
        <p class="text-green-800 text-lg mb-1">Vous avez réussi le quiz !</p>
        <p class="font-bold text-green-700 mb-6">Score: <span th:text="\${correctes}"></span>/<span th:text="\${totalQuestions}"></span> (<span th:text="\${#numbers.formatDecimal(score, 1, 0)}"></span>%)</p>
        
        <div class="bg-white rounded-xl p-6 border border-green-200 mt-6 shadow-sm email-form">
            <label class="block text-green-800 font-bold mb-4">📧 Recevez votre certificat par email</label>
            <div class="flex flex-col sm:flex-row gap-3 justify-center mb-6 max-w-lg mx-auto">
                <input type="email" id="emailInput" placeholder="Entrez votre email..." required 
                       class="flex-1 px-4 py-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500"
                       th:value="\${session.utilisateur != null ? session.utilisateur.email : ''}">
            </div>
            <div class="flex flex-wrap items-center justify-center gap-4">
                <button onclick="envoyerCertificat()" class="bg-green-600 hover:bg-green-700 text-white px-6 py-3 rounded-xl font-bold transition-colors flex items-center gap-2">
                    <span class="material-symbols-outlined text-sm">mail</span> Envoyer par email
                </button>
                <button onclick="telechargerCertificat()" class="bg-white border border-green-600 text-green-700 hover:bg-green-50 px-6 py-3 rounded-xl font-bold transition-colors flex items-center gap-2">
                    <span class="material-symbols-outlined text-sm">download</span> Télécharger PDF
                </button>
            </div>
        </div>

        <div class="mt-6">
            <a th:href="'/etudiant/cours/' + \${cours.id} + '/visionner'" class="text-green-700 font-bold hover:underline flex items-center justify-center gap-2">
                Continuer vers le cours <span class="material-symbols-outlined text-sm">arrow_forward</span>
            </a>
        </div>
    </div>
    
    <script th:inline="javascript">
        const quizId = /*[[\${quiz.id}]]*/ null;
        
        function envoyerCertificat() {
            const email = document.getElementById('emailInput').value;
            if (!email) {
                alert('Veuillez saisir votre email');
                return;
            }
            
            const btn = event.currentTarget;
            const originalText = btn.innerHTML;
            btn.innerHTML = '<span class="material-symbols-outlined text-sm">hourglass_empty</span> Envoi en cours...';
            btn.disabled = true;
            
            fetch('/api/quiz/' + quizId + '/certificat/envoyer?email=' + encodeURIComponent(email), {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            })
            .then(response => {
                if (response.ok) {
                    alert('✅ Certificat envoyé à ' + email);
                } else {
                    alert('❌ Erreur lors de l\\'envoi du certificat.');
                }
            })
            .catch(error => {
                alert('❌ Erreur: ' + error.message);
            })
            .finally(() => {
                btn.innerHTML = originalText;
                btn.disabled = false;
            });
        }

        function telechargerCertificat() {
            const email = document.getElementById('emailInput').value;
            if (!email) {
                alert('Veuillez saisir votre email avant de télécharger');
                return;
            }
            window.location.href = '/api/quiz/' + quizId + '/certificat/telecharger?email=' + encodeURIComponent(email);
        }
    </script>

    `;

html = html.replace(successSectionRegex, newSuccessSection);
fs.writeFileSync('src/main/resources/templates/etudiant/quiz-resultat.html', html);
console.log("HTML form injected.");
