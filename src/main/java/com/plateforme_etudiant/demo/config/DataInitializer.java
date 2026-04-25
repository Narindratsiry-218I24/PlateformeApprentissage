package com.plateforme_etudiant.demo.config;

import com.plateforme_etudiant.demo.model.*;
import com.plateforme_etudiant.demo.model.enums.Role;
import com.plateforme_etudiant.demo.model.enums.TypeContenu;
import com.plateforme_etudiant.demo.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UtilisateurRepository utilisateurRepository;
    private final ProfesseurRepository professeurRepository;
    private final CoursRepository coursRepository;
    private final SectionRepository sectionRepository;
    private final ChapitreRepository chapitreRepository;
    private final ContenuItemRepository contenuItemRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final ReponseRepository reponseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UtilisateurRepository utilisateurRepository,
                           ProfesseurRepository professeurRepository,
                           CoursRepository coursRepository,
                           SectionRepository sectionRepository,
                           ChapitreRepository chapitreRepository,
                           ContenuItemRepository contenuItemRepository,
                           QuizRepository quizRepository,
                           QuestionRepository questionRepository,
                           ReponseRepository reponseRepository,
                           PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.professeurRepository = professeurRepository;
        this.coursRepository = coursRepository;
        this.sectionRepository = sectionRepository;
        this.chapitreRepository = chapitreRepository;
        this.contenuItemRepository = contenuItemRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.reponseRepository = reponseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("=== VÉRIFICATION DES DONNÉES ===");

        // ── Compte professeur de démo (backward compat) ──────────────────────
        if (!utilisateurRepository.existsByEmail("professeur@demo.com")) {
            creerCompteProf("profvalmont", "professeur@demo.com", "demo123",
                            "Jean-Pierre", "Valmont", "Mathématiques", "Professeur expert en mathématiques");
        } else {
            // S'assurer que le professeur associé existe
            utilisateurRepository.findByEmail("professeur@demo.com").ifPresent(u -> {
                if (professeurRepository.findByUtilisateur(u).isEmpty()) {
                    creerProfesseurPourUtilisateur(u, "Mathématiques", "Professeur expert");
                }
            });
        }

        // ── Si des cours existent déjà, ne pas recréer ───────────────────────
        if (coursRepository.count() > 0) {
            log.info("✅ Cours déjà présents ({}) – initialisation des cours ignorée.", coursRepository.count());
            log.info("=== INITIALISATION TERMINÉE ===");
            return;
        }

        log.info("🚀 Création des cours, quiz et données de démonstration...");

        // ── Professeurs ───────────────────────────────────────────────────────
        Utilisateur uProf1 = creerUtilisateur("prof_martin", "martin@erudition.mg",
                "prof1234", "Jean", "Martin", Role.PROFESSEUR);
        Utilisateur uProf2 = creerUtilisateur("prof_rakoto", "rakoto@erudition.mg",
                "prof1234", "Hery", "Rakoto", Role.PROFESSEUR);

        Professeur prof1 = creerProfesseurPourUtilisateur(uProf1, "Management & Stratégie",
                "Expert en stratégie d'entreprise, 15 ans d'expérience.");
        Professeur prof2 = creerProfesseurPourUtilisateur(uProf2, "Informatique & Data",
                "Développeur Full-Stack et Data Scientist.");

        // ── Étudiants ─────────────────────────────────────────────────────────
        creerUtilisateur("ana_ravelo",  "ana@etudiant.mg",  "etudiant123", "Ana",  "Ravelo",         Role.APPRENANT);
        creerUtilisateur("solo_andria", "solo@etudiant.mg", "etudiant123", "Solo", "Andriamahefa",   Role.APPRENANT);
        creerUtilisateur("koto_rabe",   "koto@etudiant.mg", "etudiant123", "Koto", "Rabemananjara",  Role.APPRENANT);

        // ═══════════════════════════════════════════════════════════════════════
        //  COURS 1 : MBA Management
        // ═══════════════════════════════════════════════════════════════════════
        Cours coursMBA = creerCours(prof1,
                "MBA Management des Organisations",
                "mba-management",
                "Maîtrisez les fondamentaux du management moderne : stratégie, leadership, finance et marketing.",
                "Formation MBA complète pour managers et futurs dirigeants.", 40);

        // --- Section 1 : Stratégie ---
        Section secStrategie = creerSection(coursMBA, "Stratégie d'Entreprise", 1);

        Chapitre chapSwot = creerChapitre(secStrategie, "Introduction à la Stratégie", 1);
        ajouterTexte(chapSwot, secStrategie, "Qu'est-ce que la stratégie ?",
                "<h2>La Stratégie d'Entreprise</h2>"
                + "<p>La stratégie définit les orientations à long terme d'une organisation.</p>"
                + "<ul><li><strong>Corporate</strong> : quels secteurs d'activité ?</li>"
                + "<li><strong>Business</strong> : comment être compétitif ?</li>"
                + "<li><strong>Fonctionnelle</strong> : comment optimiser chaque fonction ?</li></ul>", 1);
        ajouterTexte(chapSwot, secStrategie, "L'analyse SWOT",
                "<h2>L'Analyse SWOT</h2>"
                + "<p>Forces, Faiblesses, Opportunités, Menaces – un outil stratégique fondamental.</p>"
                + "<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%'>"
                + "<tr><th style='background:#003178;color:white'>Forces (internes)</th><th style='background:#003178;color:white'>Faiblesses (internes)</th></tr>"
                + "<tr><td>Avantages concurrentiels</td><td>Limitations internes</td></tr>"
                + "<tr><th style='background:#1a5276;color:white'>Opportunités (externes)</th><th style='background:#1a5276;color:white'>Menaces (externes)</th></tr>"
                + "<tr><td>Facteurs favorables</td><td>Facteurs défavorables</td></tr></table>", 2);

        Chapitre chapPorter = creerChapitre(secStrategie, "Les 5 Forces de Porter", 2);
        ajouterTexte(chapPorter, secStrategie, "Le Modèle des 5 Forces",
                "<h2>5 Forces de Porter</h2>"
                + "<ol><li>Rivalité entre concurrents</li><li>Menace de nouveaux entrants</li>"
                + "<li>Pouvoir des fournisseurs</li><li>Pouvoir des clients</li>"
                + "<li>Produits de substitution</li></ol>", 1);

        // Quiz Stratégie MBA
        Quiz qStrategie = creerQuiz(coursMBA, "Quiz : Fondamentaux de la Stratégie",
                "Testez vos connaissances sur les concepts clés de la stratégie d'entreprise.");
        ajouterQuestion(qStrategie, "Quelle analyse évalue Forces, Faiblesses, Opportunités et Menaces ?",
                new String[]{"Analyse PESTEL", "Analyse SWOT", "Modèle de Porter", "Matrice BCG"}, 1);
        ajouterQuestion(qStrategie, "Combien de forces compose le modèle de Porter ?",
                new String[]{"3 forces", "4 forces", "5 forces", "6 forces"}, 2);
        ajouterQuestion(qStrategie, "La stratégie 'corporate' concerne :",
                new String[]{"La compétitivité sur un marché donné", "Le choix des secteurs d'activité", "La gestion des ressources humaines", "Le marketing"}, 1);
        ajouterQuestion(qStrategie, "La matrice BCG classe les produits selon :",
                new String[]{"Prix et qualité", "Part de marché et taux de croissance", "Coût et rentabilité", "Risque et rendement"}, 1);
        ajouterQuestion(qStrategie, "Un avantage concurrentiel durable est (VRIN) :",
                new String[]{"Visible, Rentable, Innovant, Notable", "Valeur, Rareté, Non-imitable, Non-substituable", "Viable, Réel, Intégré, Nouveau", "Aucune des réponses"}, 1);
        ajouterQuestion(qStrategie, "Le Blue Ocean Strategy consiste à :",
                new String[]{"Copier les concurrents", "Réduire les coûts uniquement", "Créer un nouvel espace sans concurrence", "Acheter des entreprises concurrentes"}, 2);

        // --- Section 2 : Leadership ---
        Section secLeadership = creerSection(coursMBA, "Leadership & Gestion d'Équipe", 2);

        Chapitre chapLead = creerChapitre(secLeadership, "Styles de Leadership", 1);
        ajouterTexte(chapLead, secLeadership, "Les différents styles de leadership",
                "<h2>Styles de Leadership</h2>"
                + "<ul><li><strong>Autoritaire</strong> : décisions centralisées</li>"
                + "<li><strong>Démocratique</strong> : participation de l'équipe</li>"
                + "<li><strong>Laissez-faire</strong> : grande autonomie</li>"
                + "<li><strong>Transformationnel</strong> : inspiration par la vision</li>"
                + "<li><strong>Servant leader</strong> : le leader sert l'équipe</li></ul>", 1);

        Quiz qLeadership = creerQuiz(coursMBA, "Quiz : Leadership & Management d'Équipe",
                "Évaluez votre compréhension des styles de leadership.");
        ajouterQuestion(qLeadership, "Quel style accorde la plus grande autonomie à l'équipe ?",
                new String[]{"Autoritaire", "Démocratique", "Laissez-faire", "Transformationnel"}, 2);
        ajouterQuestion(qLeadership, "Maslow – Quel besoin est au sommet de sa pyramide ?",
                new String[]{"Besoins physiologiques", "Sécurité", "Appartenance", "Accomplissement de soi"}, 3);
        ajouterQuestion(qLeadership, "La théorie X de McGregor suppose que les employés :",
                new String[]{"Aiment naturellement le travail", "N'aiment pas le travail et doivent être contrôlés", "Sont toujours motivés", "N'ont pas besoin de management"}, 1);
        ajouterQuestion(qLeadership, "L'intelligence émotionnelle inclut :",
                new String[]{"Uniquement le QI", "La capacité à reconnaître et gérer ses émotions et celles des autres", "La mémoire à long terme", "La vitesse de traitement de l'information"}, 1);
        ajouterQuestion(qLeadership, "Un manager 'coach' se caractérise par :",
                new String[]{"Des ordres stricts", "Aider les collaborateurs à développer leurs compétences", "Éviter tout conflit", "Décider seul de tout"}, 1);

        // ═══════════════════════════════════════════════════════════════════════
        //  COURS 2 : Développement Web Full-Stack
        // ═══════════════════════════════════════════════════════════════════════
        Cours coursWeb = creerCours(prof2,
                "Développement Web Full-Stack",
                "dev-web-fullstack",
                "Apprenez HTML, CSS, JavaScript, Spring Boot et les bases de données.",
                "Du front-end au back-end : maîtrisez le développement web complet.", 60);

        Section secFront = creerSection(coursWeb, "Front-End : HTML & CSS", 1);
        Chapitre chapHtml = creerChapitre(secFront, "Les bases de HTML", 1);
        ajouterTexte(chapHtml, secFront, "Introduction au HTML",
                "<h2>HTML – HyperText Markup Language</h2>"
                + "<p>HTML est le langage de balisage standard pour créer des pages web. Il décrit la <strong>structure</strong> du contenu.</p>"
                + "<pre style='background:#1e293b;color:#e2e8f0;padding:16px;border-radius:8px'>"
                + "&lt;!DOCTYPE html&gt;\n&lt;html&gt;\n  &lt;head&gt;&lt;title&gt;Ma Page&lt;/title&gt;&lt;/head&gt;\n"
                + "  &lt;body&gt;\n    &lt;h1&gt;Bonjour !&lt;/h1&gt;\n  &lt;/body&gt;\n&lt;/html&gt;</pre>", 1);
        ajouterTexte(chapHtml, secFront, "Balises HTML essentielles",
                "<h2>Balises Essentielles</h2><ul>"
                + "<li><code>&lt;h1&gt;...&lt;h6&gt;</code> – Titres</li>"
                + "<li><code>&lt;p&gt;</code> – Paragraphe</li>"
                + "<li><code>&lt;a href&gt;</code> – Lien hypertexte</li>"
                + "<li><code>&lt;img src&gt;</code> – Image</li>"
                + "<li><code>&lt;form&gt;</code> – Formulaire</li>"
                + "<li><code>&lt;div&gt; / &lt;span&gt;</code> – Conteneurs</li></ul>", 2);

        Section secBack = creerSection(coursWeb, "Back-End : Java Spring Boot", 2);
        Chapitre chapSpring = creerChapitre(secBack, "Introduction à Spring Boot", 1);
        ajouterTexte(chapSpring, secBack, "Qu'est-ce que Spring Boot ?",
                "<h2>Spring Boot</h2>"
                + "<p>Framework Java qui simplifie le développement d'applications web avec configuration automatique.</p>"
                + "<h3>Avantages :</h3><ul>"
                + "<li>Démarrage rapide via Spring Initializr</li>"
                + "<li>Serveur Tomcat embarqué</li>"
                + "<li>Écosystème riche : JPA, Security, Mail...</li>"
                + "<li>Convention over Configuration</li></ul>", 1);

        Quiz qWeb = creerQuiz(coursWeb, "Quiz : Bases du Développement Web",
                "Testez vos connaissances sur HTML, CSS et le développement web.");
        ajouterQuestion(qWeb, "Que signifie HTML ?",
                new String[]{"High Text Markup Language", "HyperText Markup Language", "Hyperlink Text Model Language", "Home Tool Markup Language"}, 1);
        ajouterQuestion(qWeb, "Quelle balise HTML crée un hyperlien ?",
                new String[]{"<link>", "<href>", "<a>", "<url>"}, 2);
        ajouterQuestion(qWeb, "En CSS, quelle propriété change la couleur du texte ?",
                new String[]{"font-color", "text-color", "color", "foreground"}, 2);
        ajouterQuestion(qWeb, "Spring Boot est principalement basé sur quel langage ?",
                new String[]{"Python", "JavaScript", "PHP", "Java"}, 3);
        ajouterQuestion(qWeb, "Qu'est-ce qu'une API REST ?",
                new String[]{"Un type de base de données", "Une interface utilisant HTTP pour échanger des données", "Un framework CSS", "Un serveur web"}, 1);
        ajouterQuestion(qWeb, "Quel code HTTP signifie une requête réussie ?",
                new String[]{"404", "500", "200", "301"}, 2);

        // ═══════════════════════════════════════════════════════════════════════
        //  COURS 3 : Marketing Digital
        // ═══════════════════════════════════════════════════════════════════════
        Cours coursMarketing = creerCours(prof1,
                "Marketing Digital & Réseaux Sociaux",
                "marketing-digital",
                "Stratégies de marketing en ligne : SEO, publicité digitale, réseaux sociaux.",
                "Devenez expert du marketing digital.", 25);

        Section secSEO = creerSection(coursMarketing, "SEO & Référencement Naturel", 1);
        Chapitre chapSEO = creerChapitre(secSEO, "Fondamentaux du SEO", 1);
        ajouterTexte(chapSEO, secSEO, "Introduction au SEO",
                "<h2>SEO – Search Engine Optimization</h2>"
                + "<p>Ensemble de techniques pour améliorer le positionnement d'un site dans les résultats des moteurs de recherche.</p>"
                + "<h3>Les 3 piliers du SEO :</h3>"
                + "<ol><li><strong>Technique</strong> – vitesse, mobile-friendly, structure</li>"
                + "<li><strong>Contenu</strong> – qualité, pertinence, mots-clés</li>"
                + "<li><strong>Popularité</strong> – backlinks, autorité de domaine</li></ol>", 1);

        Section secSocial = creerSection(coursMarketing, "Réseaux Sociaux & Publicité", 2);
        Chapitre chapSocial = creerChapitre(secSocial, "Stratégie Réseaux Sociaux", 1);
        ajouterTexte(chapSocial, secSocial, "Choisir les bons réseaux sociaux",
                "<h2>Choisir Ses Réseaux Sociaux</h2>"
                + "<table border='1' cellpadding='8' style='border-collapse:collapse;width:100%'>"
                + "<tr style='background:#003178;color:white'><th>Réseau</th><th>Audience cible</th><th>Usage</th></tr>"
                + "<tr><td>LinkedIn</td><td>Professionnels, B2B</td><td>Networking, recrutement</td></tr>"
                + "<tr><td>Instagram</td><td>18-35 ans, visuels</td><td>Branding, produits</td></tr>"
                + "<tr><td>TikTok</td><td>Gen Z</td><td>Contenu viral court</td></tr>"
                + "<tr><td>Facebook</td><td>Tous âges</td><td>Communautés, pub ciblée</td></tr></table>", 1);

        Quiz qMarketing = creerQuiz(coursMarketing, "Quiz : Marketing Digital Fondamentaux",
                "Évaluez vos connaissances en marketing digital.");
        ajouterQuestion(qMarketing, "Que signifie SEO ?",
                new String[]{"Social Engine Optimization", "Search Engine Optimization", "Sales Email Output", "Search Email Operation"}, 1);
        ajouterQuestion(qMarketing, "L'objectif principal du marketing de contenu est :",
                new String[]{"Acheter de la publicité", "Attirer et fidéliser une audience par du contenu pertinent", "Spammer des emails", "Copier les concurrents"}, 1);
        ajouterQuestion(qMarketing, "Le taux de conversion mesure :",
                new String[]{"Le nombre de visites", "Le pourcentage de visiteurs réalisant l'action souhaitée", "Le coût de la publicité", "La vitesse du site"}, 1);
        ajouterQuestion(qMarketing, "KPI signifie :",
                new String[]{"Key Performance Indicator", "Kind Product Information", "Key Page Interface", "Knowledge Product Index"}, 0);
        ajouterQuestion(qMarketing, "Quel réseau social est le plus adapté au marketing B2B ?",
                new String[]{"TikTok", "Instagram", "LinkedIn", "Snapchat"}, 2);
        ajouterQuestion(qMarketing, "Le taux de rebond (bounce rate) indique :",
                new String[]{"Le nombre de partages", "Le pourcentage de visiteurs quittant le site après une seule page", "Le nombre de clics sur une pub", "La durée moyenne de visite"}, 1);

        log.info("✅ Initialisation terminée :");
        log.info("   → {} cours créés", coursRepository.count());
        log.info("   → {} utilisateurs (dont {} professeurs, {} étudiants)",
                utilisateurRepository.count(),
                professeurRepository.count(),
                utilisateurRepository.findByRole(Role.APPRENANT).size());
        log.info("   → {} quiz avec questions", quizRepository.count());
        log.info("");
        log.info("🔐 Comptes de test :");
        log.info("   Professeur : professeur@demo.com       / demo123");
        log.info("   Professeur : martin@erudition.mg       / prof1234");
        log.info("   Professeur : rakoto@erudition.mg       / prof1234");
        log.info("   Étudiant   : ana@etudiant.mg           / etudiant123");
        log.info("   Étudiant   : solo@etudiant.mg          / etudiant123");
        log.info("=== INITIALISATION TERMINÉE ===");
    }

    // ─── Méthodes utilitaires ────────────────────────────────────────────────

    private void creerCompteProf(String nomUtilisateur, String email, String motDePasse,
                                  String prenom, String nom, String specialite, String bio) {
        Utilisateur u = creerUtilisateur(nomUtilisateur, email, motDePasse, prenom, nom, Role.PROFESSEUR);
        creerProfesseurPourUtilisateur(u, specialite, bio);
    }

    private Utilisateur creerUtilisateur(String nomUtilisateur, String email, String motDePasse,
                                          String prenom, String nom, Role role) {
        if (utilisateurRepository.existsByEmail(email)) {
            return utilisateurRepository.findByEmail(email).orElseThrow();
        }
        Utilisateur u = new Utilisateur();
        u.setNomUtilisateur(nomUtilisateur);
        u.setEmail(email);
        u.setMotDePasse(passwordEncoder.encode(motDePasse));
        u.setPrenom(prenom);
        u.setNom(nom);
        u.setRole(role);
        u.setActif(true);
        return utilisateurRepository.save(u);
    }

    private Professeur creerProfesseurPourUtilisateur(Utilisateur u, String specialite, String bio) {
        return professeurRepository.findByUtilisateur(u).orElseGet(() -> {
            Professeur p = new Professeur();
            p.setUtilisateur(u);
            p.setSpecialite(specialite);
            p.setBiographie(bio);
            p.setVerifie(true);
            p.setDateCreation(LocalDateTime.now());
            return professeurRepository.save(p);
        });
    }

    private Cours creerCours(Professeur professeur, String titre, String slug,
                              String description, String descCourte, int duree) {
        Cours c = new Cours();
        c.setTitre(titre);
        c.setSlug(slug);
        c.setDescription(description);
        c.setDescriptionCourte(descCourte);
        c.setProfesseur(professeur);
        c.setPublie(true);
        c.setDureeEstimee(duree);
        c.setNombreApprenants(0);
        c.setDatePublication(LocalDateTime.now());
        return coursRepository.save(c);
    }

    private Section creerSection(Cours cours, String titre, int ordre) {
        Section s = new Section();
        s.setCours(cours);
        s.setTitre(titre);
        s.setOrdre(ordre);
        return sectionRepository.save(s);
    }

    private Chapitre creerChapitre(Section section, String titre, int ordre) {
        Chapitre ch = new Chapitre();
        ch.setSection(section);
        ch.setTitre(titre);
        ch.setOrdre(ordre);
        return chapitreRepository.save(ch);
    }

    private void ajouterTexte(Chapitre chapitre, Section section, String titre, String texte, int ordre) {
        ContenuItem ci = new ContenuItem();
        ci.setChapitre(chapitre);
        ci.setSection(section);
        ci.setTitre(titre);
        ci.setTypeContenu(TypeContenu.TEXTE);
        ci.setContenuTexte(texte);
        ci.setOrdre(ordre);
        ci.setApercuGratuit(false);
        contenuItemRepository.save(ci);
    }

    private Quiz creerQuiz(Cours cours, String titre, String description) {
        Quiz q = new Quiz();
        q.setCours(cours);
        q.setTitre(titre);
        q.setDescription(description);
        return quizRepository.save(q);
    }

    /**
     * @param bonneReponseIndex index 0-based de la bonne réponse dans choices[]
     */
    private void ajouterQuestion(Quiz quiz, String texte, String[] choices, int bonneReponseIndex) {
        Question q = new Question();
        q.setQuiz(quiz);
        q.setTexteQuestion(texte);
        q = questionRepository.save(q);

        for (int i = 0; i < choices.length; i++) {
            Reponse r = new Reponse();
            r.setQuestion(q);
            r.setTexteReponse(choices[i]);
            r.setEstCorrecte(i == bonneReponseIndex);
            reponseRepository.save(r);
        }
    }
}
