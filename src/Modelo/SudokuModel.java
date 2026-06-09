package Modelo;

public class SudokuModel {
    private int[][] tableroSolucion = new int[9][9];
    private int[][] tableroActual = new int[9][9];
    private boolean[][] celdasIniciales = new boolean[9][9];

    private String dificultad = "Medio";
    private int errores = 0;
    private final int MAX_ERRORES = 3;
    private int puntuacion = 0;
    private int tiempoSegundos = 0;

    public SudokuModel() {
        nuevoJuego("Medio");
    }

    public void nuevoJuego(String dificultad) {
        this.dificultad = dificultad;
        this.errores = 0;
        this.puntuacion = 0;
        this.tiempoSegundos = 0;
        
        generarTableroSimulado();
    }

    private void generarTableroSimulado() {
        int[][] baseSolucion = {
            {5, 3, 4, 6, 7, 8, 9, 1, 2},
            {6, 7, 2, 1, 9, 5, 3, 4, 8},
            {1, 9, 8, 3, 4, 2, 5, 6, 7},
            {8, 5, 9, 7, 6, 1, 4, 2, 3},
            {4, 2, 6, 8, 5, 3, 7, 9, 1},
            {7, 1, 3, 9, 2, 4, 8, 5, 6},
            {9, 6, 1, 5, 3, 7, 2, 8, 4},
            {2, 8, 7, 4, 1, 9, 6, 3, 5},
            {3, 4, 5, 2, 8, 6, 1, 7, 9}
        };

        for (int r = 0; r < 9; r++) {
            System.arraycopy(baseSolucion[r], 0, this.tableroSolucion[r], 0, 9);
        }

        int celdasAOcultar = switch (dificultad) {
            case "Fácil" -> 30;
            case "Medio" -> 45;
            case "Difícil" -> 54;
            case "Experto" -> 60;
            default -> 45;
        };

        for (int r = 0; r < 9; r++) {
            System.arraycopy(tableroSolucion[r], 0, tableroActual[r], 0, 9);
            for (int c = 0; c < 9; c++) {
                celdasIniciales[r][c] = true;
            }
        }

        int ocultas = 0;
        while (ocultas < celdasAOcultar) {
            int r = (int) (Math.random() * 9);
            int c = (int) (Math.random() * 9);
            if (tableroActual[r][c] != 0) {
                tableroActual[r][c] = 0;
                celdasIniciales[r][c] = false;
                ocultas++;
            }
        }
    }

    // Comprueba si un número se ha colocado correctamente las 9 veces reglamentarias
    public boolean esNumeroCompletado(int num) {
        int contador = 0;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (tableroActual[r][c] == num) {
                    contador++;
                }
            }
        }
        return contador == 9;
    }

    public boolean proponerValor(int fila, int columna, int valor) {
        if (celdasIniciales[fila][columna]) return false;

        if (tableroSolucion[fila][columna] == valor) {
            tableroActual[fila][columna] = valor;
            puntuacion += 100; 
            return true;
        } else {
            errores++;
            puntuacion = Math.max(0, puntuacion - 50); 
            return false;
        }
    }

    public boolean solicitarPista(int fila, int columna) {
        if (fila == -1 || columna == -1 || celdasIniciales[fila][columna]) return false;
        if (tableroActual[fila][columna] == tableroSolucion[fila][columna]) return false;

        tableroActual[fila][columna] = tableroSolucion[fila][columna];
        puntuacion = Math.max(0, puntuacion - 20); 
        return true;
    }

    public boolean esPartidaGanada() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (tableroActual[r][c] != tableroSolucion[r][c]) return false;
            }
        }
        return true;
    }

    public boolean esPartidaPerdida() {
        return errores >= MAX_ERRORES;
    }

    public int[][] getTableroActual() { return tableroActual; }
    public boolean[][] getCeldasIniciales() { return celdasIniciales; }
    public String getDificultad() { return dificultad; }
    public int getErrores() { return errores; }
    public int getMAX_ERRORES() { return MAX_ERRORES; }
    public int getPuntuacion() { return puntuacion; }
    public int getTiempoSegundos() { return tiempoSegundos; }
    public void incrementarTiempo() { this.tiempoSegundos++; }
}