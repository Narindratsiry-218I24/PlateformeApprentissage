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
    private final InscriptionRepository inscriptionRepository;
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
                           InscriptionRepository inscriptionRepository,
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
        this.inscriptionRepository = inscriptionRepository;
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

        Utilisateur ana = creerUtilisateur("ana_ravelo",  "ana@etudiant.mg",  "etudiant123", "Ana",  "Ravelo",         Role.APPRENANT);
        Utilisateur solo = creerUtilisateur("solo_andria", "solo@etudiant.mg", "etudiant123", "Solo", "Andriamahefa",   Role.APPRENANT);
        Utilisateur koto = creerUtilisateur("koto_rabe",   "koto@etudiant.mg", "etudiant123", "Koto", "Rabemananjara",  Role.APPRENANT);

        Utilisateur uTsiry = creerUtilisateur("narindra_tsiry", "narindraTsiry18@gmail.com", "etudiant123", "Narindra", "Tsiry", Role.APPRENANT);
        Utilisateur uTsiriniaina = creerUtilisateur("narindra_tsiriniaina", "narindraTsiriniaina366@gmail.com", "etudiant123", "Narindra", "Tsiriniaina", Role.APPRENANT);
        Utilisateur uOnja = creerUtilisateur("onja_naldina", "onjanaldinah06@gmail.com", "etudiant123", "Onja", "Naldina", Role.APPRENANT);
        Utilisateur uTaratra = creerUtilisateur("taratra_rakoto", "taratrarakotondramanana@gmail.com", "etudiant123", "Taratra", "Rakotondramanana", Role.APPRENANT);


        // ── Admin ───────────────────────────────────────────────────────────
        creerUtilisateur("admin", "admin@erudition.mg", "admin123", "Super", "Admin", Role.ADMINISTRATEUR);

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

        // ── Inscriptions (pour peupler les dashboards professeurs) ───────────
        inscrireEtudiant(ana, coursMBA);
        inscrireEtudiant(solo, coursMBA);
        inscrireEtudiant(ana, coursWeb);
        inscrireEtudiant(koto, coursMarketing);

        Cours coursReactNative = creerCoursReactNative(prof2);
        Cours coursScrum = creerCoursAgileScrum(prof1);
        Cours coursML = creerCoursMachineLearning(prof2);

        inscrireEtudiant(uTsiry, coursReactNative);
        inscrireEtudiant(uTsiriniaina, coursReactNative);
        inscrireEtudiant(uOnja, coursScrum);
        inscrireEtudiant(uTaratra, coursML);


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
        log.info("   Admin      : admin@erudition.mg        / admin123");
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

    private void inscrireEtudiant(Utilisateur etudiant, Cours cours) {
        if (!inscriptionRepository.existsByApprenantAndCours(etudiant, cours)) {
            Inscription ins = new Inscription();
            ins.setApprenant(etudiant);
            ins.setCours(cours);
            ins.setDateInscription(LocalDateTime.now());
            ins.setTermine(false);
            inscriptionRepository.save(ins);
            
            // Mettre à jour le nombre d'apprenants du cours
            int count = (cours.getNombreApprenants() != null ? cours.getNombreApprenants() : 0) + 1;
            cours.setNombreApprenants(count);
            coursRepository.save(cours);
        }
    }

    private void ajouterImage(Chapitre chapitre, Section section, String titre, String url, int ordre) {
        ContenuItem ci = new ContenuItem();
        ci.setChapitre(chapitre);
        ci.setSection(section);
        ci.setTitre(titre);
        ci.setTypeContenu(TypeContenu.IMAGE);
        ci.setFichierUrl(url);
        ci.setOrdre(ordre);
        ci.setApercuGratuit(true);
        contenuItemRepository.save(ci);
    }

    private void ajouterVideo(Chapitre chapitre, Section section, String titre, String url, int ordre) {
        ContenuItem ci = new ContenuItem();
        ci.setChapitre(chapitre);
        ci.setSection(section);
        ci.setTitre(titre);
        ci.setTypeContenu(TypeContenu.VIDEO);
        ci.setVideoUrl(url);
        ci.setOrdre(ordre);
        ci.setApercuGratuit(true);
        contenuItemRepository.save(ci);
    }

    private void ajouterLien(Chapitre chapitre, Section section, String titre, String url, int ordre) {
        ContenuItem ci = new ContenuItem();
        ci.setChapitre(chapitre);
        ci.setSection(section);
        ci.setTitre(titre);
        ci.setTypeContenu(TypeContenu.LIEN);
        ci.setLienExterne(url);
        ci.setOrdre(ordre);
        ci.setApercuGratuit(true);
        contenuItemRepository.save(ci);
    }

    private Cours creerCoursReactNative(Professeur professeur) {
        Cours cours = creerCours(professeur, 
            "Développement Mobile avec React Native",
            "react-native-mobile",
            "Maîtrisez le développement d'applications mobiles cross-platform avec React Native et JavaScript. Ce cours couvre tous les fondamentaux jusqu'aux fonctionnalités avancées.",
            "Créez des applications mobiles natives avec React Native",
            50);
        
        Section sec1 = creerSection(cours, "Introduction à React Native", 1);
        Chapitre chap1_1 = creerChapitre(sec1, "Qu'est-ce que React Native ?", 1);
        ajouterTexte(chap1_1, sec1, "Introduction", 
            "<h2>React Native</h2><p>React Native permet de créer des applications mobiles natives en utilisant uniquement JavaScript. Il utilise le même design que React, ce qui vous permet de composer une interface utilisateur mobile riche à partir de composants déclaratifs.</p><ul><li>Cross-platform : iOS et Android</li><li>Réutilisabilité du code</li><li>Performances quasi natives</li></ul>", 1);
        
        Chapitre chap1_2 = creerChapitre(sec1, "Installation et configuration", 2);
        ajouterVideo(chap1_2, sec1, "Guide d'installation", "https://www.youtube.com/embed/7XFOD6vDJ9A", 1);
        ajouterTexte(chap1_2, sec1, "Commandes CLI", "<p>Pour démarrer un nouveau projet :</p><pre><code>npx react-native init MonProjet\ncd MonProjet\nnpx react-native run-android</code></pre>", 2);
        
        Section sec2 = creerSection(cours, "Composants et Navigation", 2);
        Chapitre chap2_1 = creerChapitre(sec2, "Composants essentiels", 1);
        ajouterTexte(chap2_1, sec2, "Core Components", "<p>React Native fournit de nombreux composants natifs de base comme View, Text, Image, ScrollView et TextInput.</p>", 1);
        ajouterImage(chap2_1, sec2, "Schéma des composants", "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=400&fit=crop", 2);
        
        Chapitre chap2_2 = creerChapitre(sec2, "Navigation entre écrans", 2);
        ajouterTexte(chap2_2, sec2, "React Navigation", "<p>React Navigation est la solution la plus populaire pour la navigation dans React Native. Elle permet de définir une architecture de navigation complexe (Stack, Tab, Drawer).</p>", 1);
        ajouterLien(chap2_2, sec2, "Documentation React Navigation", "https://reactnavigation.org/", 2);

        Section sec3 = creerSection(cours, "Gestion d'état et API", 3);
        Chapitre chap3_1 = creerChapitre(sec3, "State et Props", 1);
        ajouterTexte(chap3_1, sec3, "Hooks React", "<p>useState et useEffect sont les hooks principaux pour gérer l'état et le cycle de vie dans les composants fonctionnels.</p>", 1);
        ajouterVideo(chap3_1, sec3, "Comprendre les Hooks", "https://www.youtube.com/embed/7XFOD6vDJ9A", 2);

        Chapitre chap3_2 = creerChapitre(sec3, "Appel API avec fetch", 2);
        ajouterTexte(chap3_2, sec3, "Fetch API", "<pre><code>fetch('https://api.example.com/data')\n  .then(response => response.json())\n  .then(data => console.log(data));</code></pre>", 1);
        ajouterImage(chap3_2, sec3, "Flux de données", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&h=400&fit=crop", 2);

        // Quiz
        Quiz q1 = creerQuiz(cours, "Quiz: Fondamentaux React Native", "Testez vos connaissances sur React Native");
        ajouterQuestion(q1, "Qu'est-ce que React Native ?", new String[]{"Un framework web", "Un framework mobile", "Une base de données", "Un langage"}, 1);
        ajouterQuestion(q1, "Quel langage est principalement utilisé ?", new String[]{"Java", "Python", "JavaScript", "C++"}, 2);
        ajouterQuestion(q1, "React Native est créé par :", new String[]{"Google", "Apple", "Meta (Facebook)", "Microsoft"}, 2);
        ajouterQuestion(q1, "Peut-on utiliser du code natif (Java/Swift) dans React Native ?", new String[]{"Oui", "Non", "Seulement sur Android", "Seulement sur iOS"}, 0);
        ajouterQuestion(q1, "Quel est l'équivalent de la balise <div> en React Native ?", new String[]{"<Container>", "<View>", "<Div>", "<Section>"}, 1);
        ajouterQuestion(q1, "Quel est l'équivalent de la balise <span> ?", new String[]{"<Text>", "<Span>", "<Label>", "<Paragraph>"}, 0);
        ajouterQuestion(q1, "Comment créer un nouveau projet ?", new String[]{"npm create-react-app", "npx react-native init", "ng new", "vue create"}, 1);
        ajouterQuestion(q1, "React Native utilise le DOM web.", new String[]{"Vrai", "Faux", "Parfois", "Seulement sur iOS"}, 1);
        ajouterQuestion(q1, "Quel composant est utilisé pour les listes performantes ?", new String[]{"<ScrollView>", "<ListView>", "<FlatList>", "<List>"}, 2);
        ajouterQuestion(q1, "Qu'est-ce que le Hot Reloading ?", new String[]{"Un module de refroidissement", "Le rechargement du code sans perdre l'état", "Une fonctionnalité serveur", "Un hook React"}, 1);

        Quiz q2 = creerQuiz(cours, "Quiz: Navigation et Composants", "Questions sur la navigation et les composants");
        ajouterQuestion(q2, "Quelle bibliothèque de navigation est la plus populaire ?", new String[]{"React Router", "React Navigation", "Native Navigation", "Expo Router"}, 1);
        ajouterQuestion(q2, "Quel type de navigateur gère un historique en pile ?", new String[]{"TabNavigator", "DrawerNavigator", "StackNavigator", "SwitchNavigator"}, 2);
        ajouterQuestion(q2, "Comment récupérer un paramètre de route ?", new String[]{"route.params", "navigation.getParam()", "this.props.route", "Les deux premiers"}, 3);
        ajouterQuestion(q2, "Quel composant permet de saisir du texte ?", new String[]{"<Input>", "<TextField>", "<TextInput>", "<TextEdit>"}, 2);
        ajouterQuestion(q2, "Quel composant affiche une image ?", new String[]{"<Img>", "<Picture>", "<Image>", "<Icon>"}, 2);
        ajouterQuestion(q2, "Comment rendre une zone cliquable sans style par défaut ?", new String[]{"<Button>", "<TouchableOpacity>", "<TouchableWithoutFeedback>", "<Pressable>"}, 2);
        ajouterQuestion(q2, "Quel est l'avantage de SafeAreaView ?", new String[]{"Sécurise les données", "Évite le chevauchement avec les encoches (notch)", "Améliore les performances", "Gère les erreurs"}, 1);
        ajouterQuestion(q2, "Peut-on imbriquer des navigateurs ?", new String[]{"Oui", "Non", "Seulement des Stack", "Seulement des Tab"}, 0);

        Quiz q3 = creerQuiz(cours, "Quiz: API et Gestion d'état", "Questions sur les appels API et le state");
        ajouterQuestion(q3, "Quel hook permet de gérer l'état local ?", new String[]{"useEffect", "useState", "useContext", "useReducer"}, 1);
        ajouterQuestion(q3, "Quel hook est utilisé pour les effets de bord (API) ?", new String[]{"useEffect", "useState", "useContext", "useCallback"}, 0);
        ajouterQuestion(q3, "Comment faire une requête GET ?", new String[]{"fetch(url)", "axios.post(url)", "http.get(url)", "request(url)"}, 0);
        ajouterQuestion(q3, "Que retourne fetch() ?", new String[]{"Un objet", "Une chaîne de caractères", "Une Promise", "Un Array"}, 2);
        ajouterQuestion(q3, "Quel hook permet d'éviter les rendus inutiles ?", new String[]{"useMemo", "useLayoutEffect", "useRef", "useTransition"}, 0);
        ajouterQuestion(q3, "Redux est-il utilisable avec React Native ?", new String[]{"Oui", "Non", "Seulement avec Expo", "Seulement sur Android"}, 0);
        ajouterQuestion(q3, "Comment gérer l'état global complexe ?", new String[]{"useState", "Props drilling", "Redux/Context API", "Variables globales"}, 2);

        return cours;
    }

    private Cours creerCoursAgileScrum(Professeur professeur) {
        Cours cours = creerCours(professeur, 
            "Gestion de Projet Agile avec Scrum",
            "gestion-projet-agile-scrum",
            "Découvrez les principes et pratiques de la méthodologie Agile Scrum pour mener à bien vos projets.",
            "Maîtrisez la gestion de projet Agile avec Scrum",
            30);
        
        Section sec1 = creerSection(cours, "Introduction à Scrum", 1);
        Chapitre chap1_1 = creerChapitre(sec1, "Les rôles Scrum", 1);
        ajouterTexte(chap1_1, sec1, "Les 3 Rôles", "<h2>Les Rôles dans Scrum</h2><ul><li><strong>Product Owner</strong> : Maximise la valeur du produit</li><li><strong>Scrum Master</strong> : Garantit le cadre Scrum</li><li><strong>Développeurs</strong> : Réalisent le travail</li></ul>", 1);
        
        Chapitre chap1_2 = creerChapitre(sec1, "Les cérémonies Scrum", 2);
        ajouterTexte(chap1_2, sec1, "Les Événements", "<p>Sprint Planning, Daily Scrum, Sprint Review, Sprint Retrospective.</p>", 1);
        ajouterImage(chap1_2, sec1, "Cycle Scrum", "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?w=800&h=400&fit=crop", 2);
        
        Section sec2 = creerSection(cours, "Planning et Suivi", 2);
        Chapitre chap2_1 = creerChapitre(sec2, "Gestion du backlog", 1);
        ajouterTexte(chap2_1, sec2, "Product Backlog", "<p>Le Product Backlog est une liste ordonnée de tout ce qui est connu pour être nécessaire dans le produit.</p>", 1);
        ajouterVideo(chap2_1, sec2, "Créer un backlog", "https://www.youtube.com/embed/7XFOD6vDJ9A", 2);
        
        Chapitre chap2_2 = creerChapitre(sec2, "Suivi avec les métriques", 2);
        ajouterTexte(chap2_2, sec2, "Burndown Chart", "<p>Le Burndown Chart permet de visualiser le travail restant au fil du temps.</p>", 1);
        ajouterLien(chap2_2, sec2, "Outils de suivi", "https://www.atlassian.com/agile/metrics", 2);

        Section sec3 = creerSection(cours, "Outils et Pratiques", 3);
        Chapitre chap3_1 = creerChapitre(sec3, "Utilisation de Jira", 1);
        ajouterTexte(chap3_1, sec3, "Jira Software", "<p>Jira est l'un des outils les plus populaires pour la gestion de projet Agile.</p>", 1);
        ajouterImage(chap3_1, sec3, "Interface Jira", "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=400&fit=crop", 2);

        Chapitre chap3_2 = creerChapitre(sec3, "Bonnes pratiques Agile", 2);
        ajouterTexte(chap3_2, sec3, "Conseils", "<ul><li>Communiquer régulièrement</li><li>Adapter plutôt que suivre un plan aveuglément</li><li>Livrer de la valeur rapidement</li></ul>", 1);

        Quiz q1 = creerQuiz(cours, "Quiz: Scrum Master", "Testez vos connaissances sur Scrum");
        ajouterQuestion(q1, "Qui est responsable de maximiser la valeur du produit ?", new String[]{"Scrum Master", "Product Owner", "Développeur", "Manager"}, 1);
        ajouterQuestion(q1, "Quelle est la durée recommandée d'un Sprint ?", new String[]{"1 semaine", "2 à 4 semaines", "3 mois", "1 an"}, 1);
        ajouterQuestion(q1, "Quel événement se tient quotidiennement ?", new String[]{"Sprint Planning", "Daily Scrum", "Sprint Review", "Sprint Retrospective"}, 1);
        ajouterQuestion(q1, "Qui participe au Daily Scrum ?", new String[]{"Uniquement le Scrum Master", "Les Développeurs", "Toute l'entreprise", "Uniquement le Product Owner"}, 1);
        ajouterQuestion(q1, "Qu'est-ce que le Product Backlog ?", new String[]{"Un document technique", "Une liste ordonnée des besoins", "Un diagramme de Gantt", "Un outil logiciel"}, 1);
        ajouterQuestion(q1, "Quel est le but de la Rétrospective ?", new String[]{"Critiquer les autres", "Améliorer le processus pour le prochain Sprint", "Présenter le produit aux clients", "Planifier le budget"}, 1);
        ajouterQuestion(q1, "Qui s'assure que Scrum est compris et appliqué ?", new String[]{"Product Owner", "CEO", "Scrum Master", "Project Manager"}, 2);
        ajouterQuestion(q1, "Combien de rôles y a-t-il dans Scrum ?", new String[]{"2", "3", "4", "5"}, 1);

        return cours;
    }

    private Cours creerCoursMachineLearning(Professeur professeur) {
        Cours cours = creerCours(professeur, 
            "Machine Learning avec Python",
            "machine-learning-python",
            "Apprenez à créer des modèles d'intelligence artificielle prédictifs avec Python et scikit-learn.",
            "Devenez Data Scientist avec Python",
            60);
        
        Section sec1 = creerSection(cours, "Fondamentaux", 1);
        Chapitre chap1_1 = creerChapitre(sec1, "Introduction au ML", 1);
        ajouterTexte(chap1_1, sec1, "Qu'est-ce que le Machine Learning ?", "<p>Le ML permet aux systèmes d'apprendre et de s'améliorer à partir de données sans être explicitement programmés.</p>", 1);
        ajouterVideo(chap1_1, sec1, "Intro au ML", "https://www.youtube.com/embed/7XFOD6vDJ9A", 2);
        
        Chapitre chap1_2 = creerChapitre(sec1, "Types d'apprentissage", 2);
        ajouterTexte(chap1_2, sec1, "Supervisé vs Non Supervisé", "<ul><li><strong>Supervisé</strong> : Données étiquetées (ex: Classification)</li><li><strong>Non supervisé</strong> : Données non étiquetées (ex: Clustering)</li></ul>", 1);
        
        Section sec2 = creerSection(cours, "Algorithmes", 2);
        Chapitre chap2_1 = creerChapitre(sec2, "Régression linéaire", 1);
        ajouterTexte(chap2_1, sec2, "Prédiction de valeurs", "<p>La régression linéaire trouve la meilleure ligne d'ajustement à travers les points de données.</p>", 1);
        ajouterImage(chap2_1, sec2, "Graphe de régression", "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=800&h=400&fit=crop", 2);
        
        Chapitre chap2_2 = creerChapitre(sec2, "Classification", 2);
        ajouterTexte(chap2_2, sec2, "Arbres de décision", "<p>Permet de classer des éléments dans des catégories.</p>", 1);
        ajouterVideo(chap2_2, sec2, "Arbres de décision expliqués", "https://www.youtube.com/embed/7XFOD6vDJ9A", 2);

        Section sec3 = creerSection(cours, "Modélisation", 3);
        Chapitre chap3_1 = creerChapitre(sec3, "Entraînement et évaluation", 1);
        ajouterTexte(chap3_1, sec3, "Train/Test Split", "<p>Il est crucial de séparer ses données pour évaluer correctement le modèle.</p>", 1);
        ajouterLien(chap3_1, sec3, "Doc Scikit-Learn", "https://scikit-learn.org/", 2);

        Chapitre chap3_2 = creerChapitre(sec3, "Optimisation des modèles", 2);
        ajouterTexte(chap3_2, sec3, "Hyperparamètres", "<p>Utiliser Grid Search pour trouver les meilleurs paramètres.</p>", 1);

        Quiz q1 = creerQuiz(cours, "Quiz: Machine Learning", "Testez vos connaissances en Machine Learning");
        ajouterQuestion(q1, "Qu'est-ce que l'apprentissage supervisé ?", new String[]{"Apprendre sans données", "Apprendre avec des données étiquetées", "Apprendre avec des données non étiquetées", "Apprendre par renforcement"}, 1);
        ajouterQuestion(q1, "Laquelle n'est PAS une bibliothèque de ML en Python ?", new String[]{"Scikit-learn", "TensorFlow", "PyTorch", "React"}, 3);
        ajouterQuestion(q1, "Quel algorithme est utilisé pour prédire une valeur continue ?", new String[]{"Régression Linéaire", "K-Means", "Régression Logistique", "Arbre de décision"}, 0);
        ajouterQuestion(q1, "Qu'est-ce que l'overfitting (surapprentissage) ?", new String[]{"Le modèle est trop simple", "Le modèle apprend trop par cœur les données d'entraînement", "Le modèle n'a pas assez de données", "Le modèle est trop rapide"}, 1);
        ajouterQuestion(q1, "À quoi sert la fonction train_test_split ?", new String[]{"Fusionner des données", "Séparer les données en ensembles d'entraînement et de test", "Nettoyer les données", "Entraîner le modèle"}, 1);
        ajouterQuestion(q1, "Lequel est un algorithme de clustering ?", new String[]{"SVM", "K-Means", "Random Forest", "Réseau de neurones"}, 1);
        ajouterQuestion(q1, "Qu'est-ce qu'une matrice de confusion ?", new String[]{"Une erreur Python", "Un tableau évaluant les performances d'un modèle de classification", "Un algorithme complexe", "Une base de données"}, 1);
        ajouterQuestion(q1, "Que signifie 'Deep Learning' ?", new String[]{"Apprentissage par arbres de décision profonds", "Utilisation de réseaux de neurones artificiels profonds", "Analyse de données océanographiques", "Apprentissage très lent"}, 1);
        ajouterQuestion(q1, "Quel paramètre contrôle le pas d'apprentissage ?", new String[]{"Epochs", "Learning rate", "Batch size", "Dropout"}, 1);
        ajouterQuestion(q1, "Pandas est utilisé principalement pour :", new String[]{"Créer des sites web", "La manipulation et l'analyse de données", "Créer des graphiques 3D", "Développer des jeux"}, 1);

        return cours;
    }
}
