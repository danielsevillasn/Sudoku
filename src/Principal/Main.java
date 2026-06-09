package Principal;

import Controlador.SudokuController;
import Modelo.SudokuModel;
import Visualización.SudokuView;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    private static final String DB_URL = "jdbc:sqlite:sudoku.db";

    public static void main(String[] args) {
        inicializarBaseDatos();

        // Estilo visual del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ejecución de la arquitectura MVC
        SwingUtilities.invokeLater(() -> {
            SudokuModel model = new SudokuModel();
            SudokuView view = new SudokuView();
            new SudokuController(model, view);
            view.setVisible(true);
        });
    }

    private static void inicializarBaseDatos() {
        File dbFile = new File("sudoku.db");
        if (!dbFile.exists()) {
            System.out.println("Base de datos no detectada. Inicializando con schema.sql...");
            try (Connection conn = DriverManager.getConnection(DB_URL);
                    Statement stmt = conn.createStatement();
                    BufferedReader br = new BufferedReader(new FileReader("schema.sql"))) {

                StringBuilder sql = new StringBuilder();
                String linea;
                while ((linea = br.readLine()) != null) {
                    sql.append(linea).append("\n");
                    if (linea.trim().endsWith(";")) {
                        stmt.execute(sql.toString());
                        sql = new StringBuilder();
                    }
                }
                System.out.println("Base de datos creada e inicializada correctamente.");
            } catch (Exception e) {
                System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            }
        }
    }
}