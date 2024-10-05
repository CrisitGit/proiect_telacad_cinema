package staff;

import repository.CinemaOperations;
import repository.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class StaffControl {

    private static final String USERNAME = "personalCinematograf";
    private static final String PASSWORD = "Cinem@123";
    private static final String[] FILME = {"Pulp Fiction", "Underground", "Kill Bill", "Interstellar", "The Matrix"};

    private StaffControl(){
        System.out.println("Deschidere cinematograf...");
    }

    private static StaffControl singleton;

    public static StaffControl getInstance(){
        if (singleton == null){
            singleton = new StaffControl();
        }
        return singleton;
    }

    private static void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidMovie(String film) {
        for (String validMovie : FILME) {
            if (validMovie.equalsIgnoreCase(film)) {
                return true;
            }
        }
        return false;
    }


    public static void exit(){

        initializeDatabase();

        Scanner scanner = new Scanner(System.in);
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Parola: ");
            String password = scanner.nextLine();

            if (username.equals(USERNAME) && password.equals(PASSWORD)) {
                loggedIn = true;
                System.out.println("Autentificat!");
            } else {
                System.out.println("Username sau parola gresita!");
            }
        }

        while (true) {
            System.out.print("Actiune: ");
            String command = scanner.nextLine();

            switch (command) {
                case "Rezervare":
                    System.out.print("Film (Pulp Fiction, Underground, Kill Bill, Interstellar, The Matrix): ");
                    String film = scanner.nextLine();
                    if (!isValidMovie(film)) {
                        System.out.println("Film invalid!");
                        break;
                    }
                    System.out.print("Sala: ");
                    int sala = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Nume: ");
                    String nume = scanner.nextLine();
                    System.out.print("Locuri: ");
                    int locuri = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Data (yyyy-mm-dd): ");
                    String data = scanner.nextLine();
                    CinemaOperations.addReservation(film, sala, nume, locuri, data);
                    break;
                case "Afisare rezervari":
                    System.out.print("Nume: ");
                    String numeRezervari = scanner.nextLine();
                    for (String rezervare : CinemaOperations.showReservations(numeRezervari)) {
                        System.out.println(rezervare);
                    }
                    break;
                case "Verificare capacitate":
                    System.out.print("Sala: ");
                    int capacitateSala = scanner.nextInt();
                    scanner.nextLine();
                    int locuriLibere = CinemaOperations.checkCapacity(capacitateSala);
                    System.out.println("Locuri libere: " + locuriLibere);
                    break;
                case "Sterge rezervare":
                    System.out.print("ID Rezervare: ");
                    int idRezervare = scanner.nextInt();
                    scanner.nextLine();
                    CinemaOperations.deleteReservation(idRezervare);
                    break;
                case "Salvare":
                    System.out.print("Nume fisier: ");
                    String numeFisier = scanner.nextLine();
                    CinemaOperations.saveReservationsToFile(numeFisier);
                    break;
                case "Exit":
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Comanda invalida!");
                    break;
            }

    }

    }

}
