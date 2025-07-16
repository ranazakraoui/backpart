-- Supprime les tables si elles existent déjà (optionnel)
DROP TABLE IF EXISTS DEMANDE_FORMATION;
DROP TABLE IF EXISTS FORMATION;

-- Crée la table FORMATION avec auto-incrément
CREATE TABLE FORMATION (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           titre VARCHAR(255),
                           description VARCHAR(255),
                           duree INT,
                           planifiee BOOLEAN
);

-- Crée la table DEMANDE_FORMATION avec contrainte de clé étrangère
CREATE TABLE DEMANDE_FORMATION (
                                   id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   collaborateur VARCHAR(255),
                                   date_demande DATE,
                                   statut VARCHAR(50),
                                   formation_id BIGINT,
                                   FOREIGN KEY (formation_id) REFERENCES FORMATION(id)
);