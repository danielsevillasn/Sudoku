package Controlador;

import Modelo.SudokuModel;
import Visualización.SudokuView;
import Visualización.PantallaInicio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
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
        view.addAbandonarListener(e -> {
            // 1. Pausamos el cronómetro visual para que no siga contando mientras el
            // usuario decide
            if (cronometroVisual != null) {
                cronometroVisual.stop();
            }

            // 2. Lanzamos la pregunta
            int opcion = JOptionPane.showConfirmDialog(view,
                    "¿Deseas guardar tu progreso actual antes de salir?",
                    "Guardar Partida",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (opcion == JOptionPane.YES_OPTION) {
                // Guardamos el modelo en binario y salimos
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("partida_guardada.dat"))) {
                    oos.writeObject(model);
                } catch (Exception ex) {
                    System.err.println("Error al guardar la partida: " + ex.getMessage());
                }
                regresarAlMenuInicio();

            } else if (opcion == JOptionPane.NO_OPTION) {
                // Salimos directamente sin guardar nada
                regresarAlMenuInicio();

            } else {
                // Si el usuario cancela (o cierra la ventana), reanudamos el tiempo y sigue
                // jugando
                if (cronometroVisual != null) {
                    cronometroVisual.start();
                }
            }
        });

        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                procesarSalida(true);
            }
        });

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
                    "¡Enhorabuena! Has resuelto con éxito el Sudoku.\nPuntuación final: " + model.getPuntuacion(),
                    "¡Victoria!", JOptionPane.INFORMATION_MESSAGE);
            regresarAlMenuInicio();
        } else if (model.esPartidaPerdida()) {
            cronometroVisual.stop();
            guardarResultadoEnBaseDatos("DERROTA");
            JOptionPane.showMessageDialog(view, "Has cometido 3 errores. Fin de la partida.", "Game Over",
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
        // CORREGIDO: Eliminada la llamada redundante .getConnection() sobre el objeto
        // conn
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

    private void procesarSalida(boolean cerrarAplicacionCompleta) {
        // 1. Pausamos el cronómetro visual
        if (cronometroVisual != null) {
            cronometroVisual.stop();
        }

        // 2. Lanzamos la pregunta
        int opcion = JOptionPane.showConfirmDialog(view,
                "¿Deseas guardar tu progreso actual antes de salir?",
                "Guardar Partida",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        // 3. Evaluamos la respuesta
        if (opcion == JOptionPane.YES_OPTION) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("partida_guardada.dat"))) {
                oos.writeObject(model);
                System.out.println("Partida guardada en binario con éxito.");
            } catch (Exception ex) {
                System.err.println("Error al guardar la partida: " + ex.getMessage());
            }
            ejecutarCierre(cerrarAplicacionCompleta);

        } else if (opcion == JOptionPane.NO_OPTION) {
            ejecutarCierre(cerrarAplicacionCompleta);

        } else {
            // Si el usuario cancela, reanudamos el tiempo y sigue jugando
            if (cronometroVisual != null) {
                cronometroVisual.start();
            }
        }
    }

    // Método auxiliar para decidir el destino final
    private void ejecutarCierre(boolean cerrarAplicacionCompleta) {
        if (cerrarAplicacionCompleta) {
            System.exit(0); // Cierra todo el programa (útil para la "X")
        } else {
            regresarAlMenuInicio(); // Vuelve al menú (útil para "Abandonar Partida")
        }
    }
}