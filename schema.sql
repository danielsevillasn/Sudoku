CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS estadisticas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    dificultad TEXT NOT NULL,
    tiempo_segundos INTEGER NOT NULL,
    puntuacion INTEGER NOT NULL,
    errores INTEGER NOT NULL,
    resultado TEXT CHECK(resultado IN ('VICTORIA', 'DERROTA')) NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Insertar un usuario por defecto si no existe
INSERT OR IGNORE INTO usuarios (id, nombre) VALUES (1, 'Jugador Principal');