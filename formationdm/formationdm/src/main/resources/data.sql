-- Insertions dans FORMATION (pas besoin de spécifier l'id grâce à AUTO_INCREMENT)
INSERT INTO FORMATION (titre, description, duree, planifiee) VALUES
                                                                 ('Spring Boot', 'Formation Spring Boot pour débutants', 16, false),
                                                                 ('Angular', 'Développement Frontend avec Angular', 24, true);

-- Insertions dans DEMANDE_FORMATION
INSERT INTO DEMANDE_FORMATION (collaborateur, date_demande, statut, formation_id) VALUES
                                                                                      ('Jean Dupont', '2023-06-01', 'EN_ATTENTE', 1),
                                                                                      ('Marie Martin', '2023-06-05', 'APPROUVEE', 2);


-- Seulement exécuté si la table est vide
INSERT INTO formation (id, titre, description, duree, planifiee)
SELECT 1, 'Java', 'Formation Java avancée', 20, false
    WHERE NOT EXISTS (SELECT 1 FROM formation WHERE id = 1);

INSERT INTO formation (id, titre, description, duree, planifiee)
SELECT 2, 'Spring Boot', 'Développement backend avec Spring', 30, true
    WHERE NOT EXISTS (SELECT 1 FROM formation WHERE id = 2);

INSERT INTO formation (titre, description, duree, planifiee)
VALUES ('Spring Boot', 'Backend avec Java', 5, true);