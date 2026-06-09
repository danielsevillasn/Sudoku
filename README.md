# 🧩 Java Sudoku Classic

¡Bienvenido a **Java Sudoku Classic**! Este es un juego de Sudoku de escritorio desarrollado en **Java** utilizando **Swing** para la interfaz gráfica y **SQLite** para la persistencia de datos (historial de partidas, estadísticas y mejores tiempos). El diseño y las mecánicas principales están inspirados en la popular plataforma interactiva [Sudoku.com](https://sudoku.com/es).

---

## 🚀 Características del Proyecto

Para replicar la experiencia de la web de referencia, el proyecto abarca las siguientes funcionalidades organizadas por módulos:

* **Generación y Validación de Tableros:** Creación de tableros de Sudoku válidos con solución única y niveles de dificultad configurables (Fácil, Medio, Difícil, Experto).
* **Interfaz Gráfica Interactiva (Swing):**
    * Matriz de $9 \times 9$ dinámica que diferencia visualmente los números iniciales (fijos) de los introducidos por el usuario.
    * Panel numérico en pantalla y soporte para entrada por teclado.
    * **Modo Notas (Borrador):** Posibilidad de anotar posibles candidatos en pequeño dentro de cada celda.
    * Herramientas de ayuda: Botón de pista, deshacer/rehacer movimientos y borrador.
    * Cronómetro en tiempo de ejecución.
* **Persistencia con Base de Datos (SQLite):**
    * Guardado automático del estado de la partida actual para poder reanudarla más tarde.
    * Sección de estadísticas locales: historial de victorias, tiempos récord según dificultad y porcentaje de aciertos.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java (JDK 17 o superior)
* **Entorno de Desarrollo (IDE):** Visual Studio Code
* **Interfaz Gráfica:** Java Swing / AWT
* **Base de Datos:** SQLite (gestor embebido, no requiere instalación de servidor externo)
* **Control de Versiones:** Git & GitHub

---

## 📂 Estructura del Proyecto

El código fuente está organizado siguiendo el patrón arquitectónico **MVC (Modelo-Vista-Controlador)** para garantizar la separación de responsabilidades y un código limpio:

```text
├── .gitignore
├── README.md
├── schema.sql               # Script de creación inicial de la base de datos
├── lib/                     # Librerías externas (.jar)
│   └── sqlite-jdbc.jar      # Conector JDBC para SQLite
└── src/
    ├── main/
    │   └── Main.java        # Punto de entrada de la aplicación
    ├── model/               # Lógica de negocio (Tablero, Celda, Estadísticas)
    ├── view/                # Componentes de la interfaz gráfica (Ventana, TableroPanel)
    └── controller/          # Mediador entre la Vista y el Modelo (Manejadores de eventos)
