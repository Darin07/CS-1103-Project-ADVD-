import java.sql.Connection;
import java.sql.Statement;

public class ClearWorkoutTables {
    public static void main(String[] args) {
        try (Connection conn = WorkoutDBConnector.connect();
             Statement stmt = conn.createStatement()) {

            // Temporarily disable foreign key checks
            stmt.execute("PRAGMA foreign_keys = OFF");

            // Delete all data from workout-related tables
            stmt.executeUpdate("DELETE FROM WorkoutExercise");
            stmt.executeUpdate("DELETE FROM Workouts");
            stmt.executeUpdate("DELETE FROM Exercise");
            stmt.executeUpdate("DELETE FROM MuscleGroup");
            stmt.executeUpdate("DELETE FROM Users");

            // Re-enable foreign key checks
            stmt.execute("PRAGMA foreign_keys = ON");

            System.out.println("All workout data cleared.");
        } catch (Exception e) {
            System.err.println("Error clearing tables: " + e.getMessage());
        }
    }
}
