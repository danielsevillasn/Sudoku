package Visualización;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class SudokuView extends JFrame {
    private JButton[][] celdasBotones = new JButton[9][9];
    private JButton[] botonesNumericos = new JButton[9];
    private JButton btnPista, btnNuevaPartida;
    private JComboBox<String> comboDificultad;
    
    private JLabel lblDificultad, lblErrores, lblPuntuacion, lblCronometro;
    
    private int filaSeleccionada = -1;
    private int columnaSeleccionada = -1;

    public SudokuView() {
        setTitle("Java Sudoku Classic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(750, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        inicializarComponentesSuperior();
        inicializarCuadriculaSudoku();
        inicializarPanelControl();
    }

    private void inicializarComponentesSuperior() {
        JPanel panelSuperior = new JPanel(new GridLayout(1, 4, 10, 0));
        panelSuperior.setBackground(Color.WHITE);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 15, 0, 15));

        lblDificultad = new JLabel("Dificultad: MEDIO", SwingConstants.CENTER);
        lblErrores = new JLabel("Errores: 0/3", SwingConstants.CENTER);
        lblPuntuacion = new JLabel("Puntuación: 0", SwingConstants.CENTER);
        lblCronometro = new JLabel("Tiempo: 00:00", SwingConstants.CENTER);

        Font fontStats = new Font("Arial", Font.BOLD, 14);
        lblDificultad.setFont(fontStats);
        lblErrores.setFont(fontStats);
        lblErrores.setForeground(new Color(231, 76, 60)); // Rojo visual Sudoku.com
        lblPuntuacion.setFont(fontStats);
        lblCronometro.setFont(fontStats);

        panelSuperior.add(lblDificultad);
        panelSuperior.add(lblErrores);
        panelSuperior.add(lblPuntuacion);
        panelSuperior.add(lblCronometro);

        add(panelSuperior, BorderLayout.NORTH);
    }

    private void inicializarCuadriculaSudoku() {
        JPanel panelContenedorTablero = new JPanel(new GridLayout(3, 3));
        panelContenedorTablero.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        panelContenedorTablero.setBackground(Color.WHITE);

        JPanel[][] bloques3x3 = new JPanel[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                bloques3x3[i][j] = new JPanel(new GridLayout(3, 3));
                bloques3x3[i][j].setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
                bloques3x3[i][j].setBackground(Color.WHITE);
                panelContenedorTablero.add(bloques3x3[i][j]);
            }
        }

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                celdasBotones[r][c] = new JButton("");
                celdasBotones[r][c].setFont(new Font("Arial", Font.PLAIN, 22));
                celdasBotones[r][c].setFocusPainted(false);
                celdasBotones[r][c].setBackground(Color.WHITE);
                celdasBotones[r][c].setBorder(new LineBorder(new Color(220, 220, 220), 1));
                
                final int fila = r;
                final int col = c;
                celdasBotones[r][c].addActionListener(e -> marcarCeldaSeleccionada(fila, col));

                int bloqueFila = r / 3;
                int bloqueCol = c / 3;
                bloques3x3[bloqueFila][bloqueCol].add(celdasBotones[r][c]);
            }
        }

        JPanel margenPanel = new JPanel(new BorderLayout());
        margenPanel.setBackground(Color.WHITE);
        margenPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 10));
        margenPanel.add(panelContenedorTablero, BorderLayout.CENTER);

        add(margenPanel, BorderLayout.CENTER);
    }

    private void inicializarPanelControl() {
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);

        // Selector de dificultad y nueva partida
        String[] diffs = {"Fácil", "Medio", "Difícil", "Experto"};
        comboDificultad = new JComboBox<>(diffs);
        comboDificultad.setSelectedItem("Medio");
        
        btnNuevaPartida = new JButton("Nuevo Juego");
        btnNuevaPartida.setBackground(new Color(52, 152, 219));
        btnNuevaPartida.setForeground(Color.WHITE);
        btnNuevaPartida.setFont(new Font("Arial", Font.BOLD, 13));

        gbc.gridx = 0; gbc.gridy = 0; panelDerecho.add(comboDificultad, gbc);
        gbc.gridy = 1; panelDerecho.add(btnNuevaPartida, gbc);

        // Teclado numérico interactivo 1-9
        JPanel panelTeclado = new JPanel(new GridLayout(3, 3, 5, 5));
        panelTeclado.setBackground(Color.WHITE);
        for (int i = 0; i < 9; i++) {
            botonesNumericos[i] = new JButton(String.valueOf(i + 1));
            botonesNumericos[i].setFont(new Font("Arial", Font.BOLD, 18));
            botonesNumericos[i].setBackground(new Color(245, 245, 245));
            botonesNumericos[i].setFocusPainted(false);
            panelTeclado.add(botonesNumericos[i]);
        }
        gbc.gridy = 2; panelDerecho.add(panelTeclado, gbc);

        // Botón de pistas
        btnPista = new JButton("💡 Obtener Pista");
        btnPista.setFont(new Font("Arial", Font.BOLD, 13));
        btnPista.setBackground(new Color(241, 196, 15));
        btnPista.setForeground(Color.BLACK);
        gbc.gridy = 3; panelDerecho.add(btnPista, gbc);

        add(panelDerecho, BorderLayout.EAST);
    }

    private void marcarCeldaSeleccionada(int fila, int col) {
        // Desmarcar anterior visualmente
        if (filaSeleccionada != -1 && columnaSeleccionada != -1) {
            celdasBotones[filaSeleccionada][columnaSeleccionada].setBorder(new LineBorder(new Color(220, 220, 220), 1));
        }
        
        filaSeleccionada = fila;
        columnaSeleccionada = col;
        
        // Resaltar celda activa seleccionada azul cielo
        celdasBotones[fila][col].setBorder(new LineBorder(new Color(52, 152, 219), 3));
    }

    public void actualizarTablero(int[][] matriz, boolean[][] iniciales) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                int valor = matriz[r][c];
                celdasBotones[r][c].setText(valor == 0 ? "" : String.valueOf(valor));
                
                if (iniciales[r][c]) {
                    celdasBotones[r][c].setFont(new Font("Arial", Font.BOLD, 22));
                    celdasBotones[r][c].setForeground(new Color(44, 62, 80)); // Gris oscuro/Negro
                    celdasBotones[r][c].setBackground(new Color(240, 240, 240));
                } else {
                    celdasBotones[r][c].setFont(new Font("Arial", Font.PLAIN, 22));
                    celdasBotones[r][c].setForeground(new Color(41, 128, 185)); // Azul entradas de usuario
                    celdasBotones[r][c].setBackground(Color.WHITE);
                }
            }
        }
    }

    public void actualizarEstado(String diff, int errores, int maxErrores, int puntos, int totalSegundos) {
        lblDificultad.setText("Dificultad: " + diff.toUpperCase());
        lblErrores.setText("Errores: " + errores + "/" + maxErrores);
        lblPuntuacion.setText("Puntuación: " + puntos);
        
        int mins = totalSegundos / 60;
        int secs = totalSegundos % 60;
        lblCronometro.setText(String.format("Tiempo: %02d:%02d", mins, secs));
    }

    // Getters para capturar eventos de entrada
    public int getFilaSeleccionada() { return filaSeleccionada; }
    public int getColumnaSeleccionada() { return columnaSeleccionada; }
    public String getDificultadSeleccionada() { return (String) comboDificultad.getSelectedItem(); }
    public void addNuevaPartidaListener(ActionListener al) { btnNuevaPartida.addActionListener(al); }
    public void addPistaListener(ActionListener al) { btnPista.addActionListener(al); }
    public void addTecladoNumListener(int numero, ActionListener al) { botonesNumericos[numero - 1].addActionListener(al); }
}