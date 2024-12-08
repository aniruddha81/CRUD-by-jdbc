import java.sql.*;
import java.util.Scanner;

public class Main {
    private static final String URL = "jdbc:mysql://localhost:3306/mydb";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static void main(String[] args) {
        try (Scanner input = new Scanner(System.in);
             Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = con.createStatement()) {

            ResultSet rs = null;

            while (true) {
                displayMenu();
                int choice = input.nextInt();
                input.nextLine();

                switch (choice) {
                    case 1 -> rs = readData(stmt, rs);
                    case 2 -> insertData(stmt, input);
                    case 3 -> deleteData(stmt, input);
                    case 4 -> updateData(stmt, input);
                    case 5 -> {
                        System.out.println("Exiting the program...");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
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

    private static ResultSet readData(Statement stmt, ResultSet rs) {
        String query = "SELECT * FROM students";

        try {
            if (rs != null && !rs.isClosed()) rs.close(); // Close previous ResultSet
            rs = stmt.executeQuery(query);

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
        return rs;
    }

    private static void insertData(Statement stmt, Scanner input) {
        System.out.print("\nEnter student name: ");
        String name = input.nextLine();

        System.out.print("Enter student age: ");
        int age = input.nextInt();

        String query = "INSERT INTO students (name, age) VALUES ('" + name + "', " + age + ")";

        try {
            if (stmt.executeUpdate(query) > 0) {
                System.out.println("Data inserted successfully!");
                readData(stmt, null);
            } else {
                System.out.println("Failed to insert data.");
            }
        } catch (SQLException e) {
            System.err.println("Error while inserting data: " + e.getMessage());
        }
    }

    private static void deleteData(Statement stmt, Scanner input) {
        System.out.print("\nEnter the ID of the student to delete: ");
        int idToDelete = input.nextInt();

        String query = "DELETE FROM students WHERE id = " + idToDelete;

        try {
            if (stmt.executeUpdate(query) > 0) {
                System.out.println("Student with ID " + idToDelete + " deleted successfully.");
                readData(stmt, null);
            } else {
                System.out.println("No student found with ID " + idToDelete);
            }
        } catch (SQLException e) {
            System.err.println("Error while deleting data: " + e.getMessage());
        }
    }

    private static void updateData(Statement stmt, Scanner input) {
        System.out.print("\nEnter the ID of the student to update: ");
        int idToUpdate = input.nextInt();
        input.nextLine();

        if (!doesStudentExist(stmt, idToUpdate)) {
            System.out.println("No student found with ID " + idToUpdate);
            return;
        }

        System.out.print("Enter new name for student: ");
        String newName = input.nextLine();

        System.out.print("Enter new age for student: ");
        int newAge = input.nextInt();

        String query = "UPDATE students SET name = '" + newName + "', age = " + newAge + " WHERE id = " + idToUpdate;

        try {
            if (stmt.executeUpdate(query) > 0) {
                System.out.println("Student data updated successfully.");
                readData(stmt, null);
            } else {
                System.out.println("Failed to update student data.");
            }
        } catch (SQLException e) {
            System.err.println("Error while updating data: " + e.getMessage());
        }
    }

    private static boolean doesStudentExist(Statement stmt, int id) {
        String query = "SELECT 1 FROM students WHERE id = " + id;

        try (ResultSet rs = stmt.executeQuery(query)) {
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Error while checking student existence: " + e.getMessage());
        }
        return false;
    }
}
