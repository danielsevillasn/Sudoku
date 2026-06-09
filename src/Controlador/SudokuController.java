package Controlador;

import Modelo.SudokuModel;
import Visualización.SudokuView;
import Visualización.PantallaInicio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import javax.swing.SwingUtilities;

public class SudokuController {
    private SudokuModel model;
    private SudokuView view;
    private Timer cronometroVisual;
    private static final String DB_URL = "jdbc:sqlite:sudoku.db";

    public SudokuController(SudokuModel model, SudokuView view) {
        this.model = model;
        this.view = view;

        inicializarListeners();
        comenzarNuevoJuego();
    }

    private void inicializarListeners() {
        // Volver al menú inicial sin guardar
        view.addAbandonarListener(e -> regresarAlMenuInicio());

        view.addPistaListener(e -> {
            int r = view.getFilaSeleccionada();
            int c = view.getColumnaSeleccionada();
            if (model.solicitarPista(r, c)) {
                sincronizarVistaYModelo();
                comprobarEstadoPartida();
            } else {
                JOptionPane.showMessageDialog(view, "Selecciona una celda vacía modificable para obtener la pista.",
                        "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        for (int i = 1; i <= 9; i++) {
            final int numero = i;
            view.addTecladoNumListener(numero, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    procesarEntradaNumero(numero);
                }
            });
        }
    }

    private void comenzarNuevoJuego() {
        sincronizarVistaYModelo();

        cronometroVisual = new Timer(1000, e -> {
            model.incrementarTiempo();
            view.actualizarEstado(model.getDificultad(), model.getErrores(), model.getMAX_ERRORES(),
                    model.getPuntuacion(), model.getTiempoSegundos());
        });
        cronometroVisual.start();
    }

    private void procesarEntradaNumero(int num) {
        int r = view.getFilaSeleccionada();
        int c = view.getColumnaSeleccionada();

        if (r == -1 || c == -1) {
            JOptionPane.showMessageDialog(view, "Primero selecciona una celda de la cuadrícula.", "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean esCorrecto = model.proponerValor(r, c, num);
        sincronizarVistaYModelo();

        if (!esCorrecto) {
            JOptionPane.showMessageDialog(view,
                    "¡Número incorrecto! Respeta las reglas del Sudoku (Sin repeticiones en filas, columnas ni cuadrantes).",
                    "Error de coincidencia", JOptionPane.ERROR_MESSAGE);
        }

        comprobarEstadoPartida();
    }

    private void comprobarEstadoPartida() {
        if (model.esPartidaGanada()) {
            cronometroVisual.stop();
            guardarResultadoEnBaseDatos("VICTORIA");
            JOptionPane.showMessageDialog(view,
                    "🎉 ¡Enhorabuena! Has resuelto con éxito el Sudoku.\nPuntuación final: " + model.getPuntuacion(),
                    "¡Victoria!", JOptionPane.INFORMATION_MESSAGE);
            regresarAlMenuInicio();
        } else if (model.esPartidaPerdida()) {
            cronometroVisual.stop();
            guardarResultadoEnBaseDatos("DERROTA");
            JOptionPane.showMessageDialog(view, "❌ Has cometido 3 errores. Fin de la partida.", "Game Over",
                    JOptionPane.ERROR_MESSAGE);
            regresarAlMenuInicio();
        }
    }

    private void regresarAlMenuInicio() {
        if (cronometroVisual != null) {
            cronometroVisual.stop();
        }
        view.dispose(); // Destruye la ventana de juego actual
        
        // Abre una nueva pantalla de inicio limpia
        SwingUtilities.invokeLater(() -> {
            PantallaInicio menu = new PantallaInicio();
            menu.setVisible(true);
        });
    }

    private void sincronizarVistaYModelo() {
        view.actualizarTablero(model.getTableroActual(), model.getCeldasIniciales());
        view.actualizarEstado(model.getDificultad(), model.getErrores(), model.getMAX_ERRORES(), model.getPuntuacion(),
                model.getTiempoSegundos());
        
        for (int i = 1; i <= 9; i++) {
            boolean completado = model.esNumeroCompletado(i);
            view.cambiarVisibilidadBotonNumerico(i, !completado);
        }
    }

    private void guardarResultadoEnBaseDatos(String resultado) {
        String query = "INSERT INTO estadisticas (dificultad, tiempo_segundos, puntuacion, errores, resultado) VALUES (?, ?, ?, ?, ?)";
        // CORREGIDO: Eliminada la llamada redundante .getConnection() sobre el objeto conn
        try (Connection conn = DriverManager.getConnection(DB_URL);
                PreparedStatement pstmt = conn.prepareStatement(query)) { 

            pstmt.setString(1, model.getDificultad());
            pstmt.setInt(2, model.getTiempoSegundos());
            pstmt.setInt(3, model.getPuntuacion());
            pstmt.setInt(4, model.getErrores());
            pstmt.setString(5, resultado);
            pstmt.executeUpdate();
            System.out.println("Partida guardada en el historial SQLite con éxito.");
        } catch (Exception e) {
            System.err.println("Error al registrar estadísticas en base de datos: " + e.getMessage());
        }
    }
}