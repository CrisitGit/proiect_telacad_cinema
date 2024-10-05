package repository;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class CinemaOperations {

    public static void addReservation(String film, int sala, String nume, int locuri, String data) {
        String movieQuery = "SELECT IdFilm FROM filme WHERE titlu = ?";
        String countQuery = "SELECT SUM(locuri) FROM rezervari WHERE sala = ? AND data = ?";
        String insertQuery = "INSERT INTO rezervari (film, sala, nume, locuri, data) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement movieStmt = conn.prepareStatement(movieQuery);
             PreparedStatement countStmt = conn.prepareStatement(countQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            movieStmt.setString(1, film);
            ResultSet rsMovie = movieStmt.executeQuery();
            if (rsMovie.next()) {
                int movieId = rsMovie.getInt("IdFilm");

                countStmt.setInt(1, sala);
                countStmt.setString(2, data);
                ResultSet rsCount = countStmt.executeQuery();
                if (rsCount.next()) {
                    int reservedSeats = rsCount.getInt(1);
                    if (reservedSeats + locuri > 20) {
                        System.out.println("Sala plina!");
                        return;
                    }
                }

                insertStmt.setInt(1, movieId);
                insertStmt.setInt(2, sala);
                insertStmt.setString(3, nume);
                insertStmt.setInt(4, locuri);
                insertStmt.setString(5, data);
                insertStmt.executeUpdate();
                System.out.println("Rezervare adaugata cu succes!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> showReservations(String nume) {
        List<String> reservations = new ArrayList<>();
        String query = "SELECT * FROM rezervari WHERE nume = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nume);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                reservations.add("ID: " + rs.getInt("id") + ", Film: " + rs.getString("film") + ", Sala: " + rs.getInt("sala") + ", Locuri: " + rs.getInt("locuri") + ", Data: " + rs.getString("data"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public static int checkCapacity(int sala) {
        String countQuery = "SELECT SUM(locuri) FROM rezervari WHERE sala = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement countStmt = conn.prepareStatement(countQuery)) {

            countStmt.setInt(1, sala);
            ResultSet rsCount = countStmt.executeQuery();
            if (rsCount.next()) {
                int reservedSeats = rsCount.getInt(1);
                return 20 - reservedSeats;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void deleteReservation(int reservationId) {
        String query = "DELETE FROM rezervari WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, reservationId);
            stmt.executeUpdate();
            System.out.println("Rezervare stearsa cu succes!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveReservationsToFile(String filename) {
        String query = "SELECT * FROM rezervari";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query);
             FileWriter writer = new FileWriter(filename)) {

            writer.write("Salvare: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()) + "\n");
            while (rs.next()) {
                writer.write("Nume: " + rs.getString("nume") + "\n");
                writer.write("Data rezervare: " + rs.getString("data") + "\n");
                writer.write("Film: " + rs.getString("film") + "\n\n");
            }
            System.out.println("Rezervarile au fost salvate in fisierul " + filename);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}