INSERT INTO historia_clinica (documento, nombre, fecha_nacimiento, abierta_por, abierta_en) VALUES
  ('1085123456', 'María Fernanda Guerrero', '1980-05-12', 'med-01', '2026-01-10 08:00:00'),
  ('27089123',   'José Luis Pantoja',       '1955-11-03', 'med-02', '2026-01-10 08:00:00')
ON DUPLICATE KEY UPDATE documento = VALUES(documento);

INSERT INTO alergia (id, historia_documento, sustancia, severidad, reaccion, registrada_por, registrada_en) VALUES
  ('a-1', '1085123456', 'Ibuprofeno', 'LEVE',     'Urticaria',              'enf-01', '2026-01-10 08:00:00'),
  ('a-2', '1085123456', 'Penicilina', 'GRAVE',    'Anafilaxia',             'med-01', '2026-01-10 08:00:00'),
  ('a-3', '27089123',   'Látex',      'MODERADA', 'Dermatitis de contacto', 'enf-01', '2026-01-10 08:00:00')
ON DUPLICATE KEY UPDATE id = VALUES(id);

INSERT INTO antecedente (id, historia_documento, descripcion, registrado_por, registrado_en) VALUES
  ('an-1', '1085123456', 'Hipertensión arterial en tratamiento con losartán', 'med-01', '2026-01-10 08:00:00'),
  ('an-2', '1085123456', 'Diabetes mellitus tipo 2',                          'med-01', '2026-01-10 08:00:00'),
  ('an-3', '27089123',   'Infarto agudo de miocardio en 2019',                'med-02', '2026-01-10 08:00:00'),
  ('an-4', '27089123',   'Anticoagulado con warfarina',                       'med-02', '2026-01-10 08:00:00')
ON DUPLICATE KEY UPDATE id = VALUES(id);
