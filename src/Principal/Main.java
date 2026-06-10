package Principal;

import Visualización.PantallaInicio;
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

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Arranca mostrando la interfaz de bienvenida
        SwingUtilities.invokeLater(() -> {
            PantallaInicio inicio = new PantallaInicio();
            inicio.setVisible(true);
        });
    }

    private static void inicializarBaseDatos() {
        File dbFile = new File("sudoku.db");
        if (!dbFile.exists()) {
            System.out.println("Base de datos no detectada. Inicializando con schema.sql...");

            try {
                //FORZAR LA CARGA DEL DRIVER (Evita el error 'No suitable driver found')
                Class.forName("org.sqlite.JDBC");

                // Una vez cargado el driver, abrimos la conexión de forma segura
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
                }
            } catch (ClassNotFoundException e) {
                System.err
                        .println("Error crítico: No se encontró el conector SQLite en el proyecto. " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            }
        }
    }
}