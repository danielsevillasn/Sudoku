package Visualización;

import javax.swing.*;
import java.awt.*;
import Modelo.SudokuModel;
import Controlador.SudokuController;

public class PantallaInicio extends JFrame {
    private JComboBox<String> comboDificultad;
    private JButton btnJugar;
    private JButton btnSalir;

    public PantallaInicio() {
        setTitle("Java Sudoku Classic - Inicio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout(15, 15));
        getContentPane().setBackground(Color.WHITE);

        // Título principal
        JLabel lblTitulo = new JLabel("🧩 JAVA SUDOKU CLASSIC", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(44, 62, 80));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // Panel Central: Reglas Básicas
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));

        JTextArea txtReglas = new JTextArea();
        txtReglas.setEditable(false);
        txtReglas.setLineWrap(true);
        txtReglas.setWrapStyleWord(true);
        txtReglas.setFont(new Font("Arial", Font.PLAIN, 13));
        txtReglas.setMargin(new Insets(10, 10, 10, 10));
        txtReglas.setText(
            "ℹ️ SOBRE EL SUDOKU\n" +
            "El sudoku es un juego de puzle lógico interactivo excelente para la mente que no requiere realizar cálculos ni habilidades matemáticas especiales.\n\n" +
            "📜 CÓMO JUGAR (REGLAS BÁSICAS)\n" +
            "El objetivo es rellenar una cuadrícula de 9x9 con dígitos, de tal manera que cada columna, fila y sección de 3x3 contenga los dígitos del 1 al 9 sin repetirse. Un movimiento se considera incorrecto si el número ya existe en su misma fila, columna o cuadrante.\n\n" +
            "💡 CONSEJOS ÚTILES:\n" +
            "• Consejo 1: Busca filas o bloques que contengan muchos números para deducir fácilmente las posiciones en blanco restantes.\n" +
            "• Consejo 2: Usa el descarte visual por columnas y filas contiguas."
        );
        
        JScrollPane scrollReglas = new JScrollPane(txtReglas);
        scrollReglas.setBorder(BorderFactory.createTitledBorder("Reglas Básicas del Juego"));
        panelCentral.add(scrollReglas, BorderLayout.CENTER);

        // Selector de dificultad
        JPanel panelConfig = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelConfig.setBackground(Color.WHITE);
        JLabel lblSelección = new JLabel("Selecciona tu nivel:");
        lblSelección.setFont(new Font("Arial", Font.BOLD, 13));
        
        String[] diffs = {"Fácil", "Medio", "Difícil", "Experto"};
        comboDificultad = new JComboBox<>(diffs);
        comboDificultad.setSelectedItem("Medio");
        comboDificultad.setFont(new Font("Arial", Font.PLAIN, 13));
        
        panelConfig.add(lblSelección);
        panelConfig.add(comboDificultad);
        panelCentral.add(panelConfig, BorderLayout.SOUTH);
        
        add(panelCentral, BorderLayout.CENTER);

        // Panel Inferior: Botones de Acción (Jugar y Salir)
        JPanel panelBotonesAccion = new JPanel(new GridLayout(1, 2, 12, 0));
        panelBotonesAccion.setBackground(Color.WHITE);
        panelBotonesAccion.setBorder(BorderFactory.createEmptyBorder(0, 25, 25, 25));

        btnJugar = new JButton("¡Empezar a Jugar! 🚀");
        btnJugar.setFont(new Font("Arial", Font.BOLD, 14));
        btnJugar.setBackground(new Color(41, 128, 185)); // Azul rey visible
        btnJugar.setForeground(Color.WHITE);
        btnJugar.setFocusPainted(false);
        btnJugar.setOpaque(true);
        btnJugar.setBorderPainted(false);
        btnJugar.setPreferredSize(new Dimension(0, 48));

        btnSalir = new JButton("Salir del Programa ❌");
        btnSalir.setFont(new Font("Arial", Font.BOLD, 14));
        btnSalir.setBackground(new Color(192, 57, 43)); // Rojo visible
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setFocusPainted(false);
        btnSalir.setOpaque(true);
        btnSalir.setBorderPainted(false);

        btnJugar.addActionListener(e -> {
            String dificultadElegida = (String) comboDificultad.getSelectedItem();
            dispose(); // Cierra el menú inicial
            
            SwingUtilities.invokeLater(() -> {
                SudokuModel model = new SudokuModel();
                SudokuView view = new SudokuView();
                model.nuevoJuego(dificultadElegida);
                new SudokuController(model, view);
                view.setVisible(true);
            });
        });

        btnSalir.addActionListener(e -> System.exit(0));

        panelBotonesAccion.add(btnJugar);
        panelBotonesAccion.add(btnSalir);
        add(panelBotonesAccion, BorderLayout.SOUTH);
    }
}