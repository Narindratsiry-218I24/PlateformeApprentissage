-- Script d'insertion de cours supplémentaires (PostgreSQL)

-- 1. Récupération d'un professeur existant
-- Remplacez l'email par l'email de votre professeur
DO $$
DECLARE
    prof_id bigint;
    cours_id bigint;
    section_id bigint;
    quiz_id bigint;
    question_id bigint;
BEGIN
    SELECT p.id INTO prof_id
    FROM professeur p
    JOIN utilisateur u ON p.utilisateur_id = u.id
    WHERE u.email = 'professeur@demo.com' LIMIT 1;

    IF prof_id IS NULL THEN
        RAISE NOTICE 'Professeur non trouvé. Veuillez vérifier l''email.';
        RETURN;
    END IF;

    -- Insertion du Cours 1
    INSERT INTO cours (titre, slug, description, details, duree_estimee, image_couverture, date_creation, est_publie, professeur_id)
    VALUES (
        'Introduction à l''Intelligence Artificielle', 
        'intro-intelligence-artificielle', 
        'Découvrez les bases de l''IA, le Machine Learning et les réseaux de neurones.', 
        'Ce cours couvre les concepts fondamentaux de l''intelligence artificielle, de l''historique jusqu''aux applications modernes telles que le traitement du langage naturel.', 
        20, 
        'https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&q=80&w=800', 
        CURRENT_TIMESTAMP, 
        true, 
        prof_id
    ) RETURNING id INTO cours_id;

    -- Insertion Section 1
    INSERT INTO section (cours_id, titre, ordre)
    VALUES (cours_id, 'Fondamentaux de l''IA', 1) RETURNING id INTO section_id;

    -- Insertion Quiz 1
    INSERT INTO quiz (cours_id, titre, description, date_creation)
    VALUES (cours_id, 'Quiz de l''IA', 'Testez vos connaissances sur l''IA', CURRENT_TIMESTAMP) RETURNING id INTO quiz_id;

    -- Insertion Question 1
    INSERT INTO question (quiz_id, texte_question)
    VALUES (quiz_id, 'Que signifie l''acronyme IA ?') RETURNING id INTO question_id;
    
    INSERT INTO reponse (question_id, texte_reponse, est_correcte) VALUES (question_id, 'Intelligence Artificielle', true);
    INSERT INTO reponse (question_id, texte_reponse, est_correcte) VALUES (question_id, 'Interface Avancée', false);

    -- Insertion du Cours 2
    INSERT INTO cours (titre, slug, description, details, duree_estimee, image_couverture, date_creation, est_publie, professeur_id)
    VALUES (
        'Maîtriser le Design UI/UX', 
        'maitriser-design-ui-ux', 
        'Apprenez à concevoir des interfaces utilisateur esthétiques et fonctionnelles.', 
        'Formation pratique sur Figma, les principes du design UI/UX, et l''expérience utilisateur.', 
        15, 
        'https://images.unsplash.com/photo-1561070791-2526d30994b5?auto=format&fit=crop&q=80&w=800', 
        CURRENT_TIMESTAMP, 
        true, 
        prof_id
    );

    RAISE NOTICE 'Cours insérés avec succès.';
END $$;
