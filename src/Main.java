import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/<your_DB_name>";
    private static final String USER = "root";
    private static final String PASSWORD = "<your_DB_password>";

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in)) {
            while (true) {
                displayMenu();
                int choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1 -> readData();
                    case 2 -> insertData(input);
                    case 3 -> deleteData(input);
                    case 4 -> updateData(input);
                    case 5 -> {
                        System.out.println("Exiting the program...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\nWhat do you want?");
        System.out.println("1. Read");
        System.out.println("2. Insert");
        System.out.println("3. Delete");
        System.out.println("4. Update");
        System.out.println("5. Exit");
        System.out.print("Enter your choice: ");
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static void readData() {
        String query = "SELECT * FROM students";

        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println("\nCurrent Students in the Database:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name").replace(" ", "_");
                int age = rs.getInt("age");
                Timestamp enrollmentDate = rs.getTimestamp("enrollment_date");
                System.out.println(id + " " + name + " " + age + " " + enrollmentDate);
            }
        } catch (SQLException e) {
            System.err.println("Error while reading data: " + e.getMessage());
        }
    }

    private static void insertData(Scanner input) {
        System.out.print("\nEnter student name: ");
        String name = input.nextLine();

        System.out.print("Enter student age: ");
        int age = input.nextInt();

        String query = "INSERT INTO students (name, age) VALUES (?, ?)";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, age);

            if (pstmt.executeUpdate() > 0) {
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        System.out.println("Data inserted successfully! Generated ID: " + keys.getInt(1));
                    }
                }
                readData();
            } else {
                System.out.println("Failed to insert data.");
            }
        } catch (SQLException e) {
            System.err.println("Error while inserting data: " + e.getMessage());
        }
    }

    private static void deleteData(Scanner input) {
        System.out.print("\nEnter the ID of the student to delete: ");
        int idToDelete = input.nextInt();

        String query = "DELETE FROM students WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, idToDelete);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("Student with ID " + idToDelete + " deleted successfully.");
                readData();
            } else {
                System.out.println("No student found with ID " + idToDelete);
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting data: " + e.getMessage());
        }
    }

    private static void updateData(Scanner input) {
        System.out.print("\nEnter the ID of the student to update: ");
        int idToUpdate = input.nextInt();
        input.nextLine();

        if (!doesStudentExist(idToUpdate)) {
            System.out.println("No student found with ID " + idToUpdate);
            return;
        }

        System.out.print("Enter new name for student: ");
        String newName = input.nextLine();

        System.out.print("Enter new age for student: ");
        int newAge = input.nextInt();

        String query = "UPDATE students SET name = ?, age = ? WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setInt(3, idToUpdate);

            if (pstmt.executeUpdate() > 0) {
                System.out.println("Student data updated successfully.");
                readData();
            } else {
                System.out.println("Failed to update student data.");
            }
        } catch (SQLException e) {
            System.err.println("Error while updating data: " + e.getMessage());
        }
    }

    private static boolean doesStudentExist(int id) {
        String query = "SELECT 1 FROM students WHERE id = ?";

        try (Connection con = getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error while checking student existence: " + e.getMessage());
        }
        return false;
    }
}
