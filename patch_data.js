const fs = require('fs');
let code = fs.readFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', 'utf8');

// We need to add the new students in the students section.
const newStudents = `
        Utilisateur uTsiry = creerUtilisateur("narindra_tsiry", "narindraTsiry18@gmail.com", "etudiant123", "Narindra", "Tsiry", Role.APPRENANT);
        Utilisateur uTsiriniaina = creerUtilisateur("narindra_tsiriniaina", "narindraTsiriniaina366@gmail.com", "etudiant123", "Narindra", "Tsiriniaina", Role.APPRENANT);
        Utilisateur uOnja = creerUtilisateur("onja_naldina", "onjanaldinah06@gmail.com", "etudiant123", "Onja", "Naldina", Role.APPRENANT);
        Utilisateur uTaratra = creerUtilisateur("taratra_rakoto", "taratrarakotondramanana@gmail.com", "etudiant123", "Taratra", "Rakotondramanana", Role.APPRENANT);
`;

code = code.replace(
    'Utilisateur koto = creerUtilisateur("koto_rabe",   "koto@etudiant.mg", "etudiant123", "Koto", "Rabemananjara",  Role.APPRENANT);',
    'Utilisateur koto = creerUtilisateur("koto_rabe",   "koto@etudiant.mg", "etudiant123", "Koto", "Rabemananjara",  Role.APPRENANT);\n' + newStudents
);

// We need to add the methods: creerCoursReactNative, creerCoursAgileScrum, creerCoursMachineLearning
const callMethods = `
        Cours coursReactNative = creerCoursReactNative(prof2);
        Cours coursScrum = creerCoursAgileScrum(prof1);
        Cours coursML = creerCoursMachineLearning(prof2);

        inscrireEtudiant(uTsiry, coursReactNative);
        inscrireEtudiant(uTsiriniaina, coursReactNative);
        inscrireEtudiant(uOnja, coursScrum);
        inscrireEtudiant(uTaratra, coursML);
`;

code = code.replace(
    'inscrireEtudiant(koto, coursMarketing);',
    'inscrireEtudiant(koto, coursMarketing);\n' + callMethods
);

// Add the method definitions at the end
const newMethods = `
    private void ajouterImage(Chapitre chapitre, Section section, String titre, String url, int ordre) {
        ContenuItem ci = new ContenuItem();
        ci.setChapitre(chapitre);
        ci.setSection(section);
        ci.setTitre(titre);
        ci.setTypeContenu(TypeContenu.IMAGE);
        ci.setUrlFichier(url);
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
        ci.setUrlFichier(url);
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
        ci.setUrlFichier(url);
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
`;

code = code.replace(/}\s*$/, newMethods);

fs.writeFileSync('src/main/java/com/plateforme_etudiant/demo/config/DataInitializer.java', code);
console.log('Successfully updated DataInitializer.java');
