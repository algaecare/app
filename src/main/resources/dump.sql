CREATE TABLE settings (
    name VARCHAR(255) NOT NULL PRIMARY KEY,
    value TEXT NOT NULL
);

INSERT INTO settings (name, value) VALUES
('DEFAULT_LANGUAGE', 'DE');

CREATE TABLE text_layers (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    DE TEXT NOT NULL,
    FR TEXT NOT NULL,
    IT TEXT NOT NULL
);

INSERT INTO text_layers (id, DE, FR, IT) VALUES
('TITLE', 'ALGAE CARE', 'ALGAE CARE', 'ALGAE CARE'),
('SUBTITLE', 'axolotl einwerfen', 'jeter axolotl', 'lancio di axolotl');
