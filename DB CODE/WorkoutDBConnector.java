import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class WorkoutDBConnector {
    private static final String URL = "jdbc:sqlite:workout_split.db";

    public static Connection connect() throws SQLException {
        ensureTablesExist(); // Make sure tables are created before connecting
        System.out.println("[connect()] DB file path: " + new File("workout_split.db").getAbsolutePath());
        return DriverManager.getConnection(URL);
    }

    public static void ensureTablesExist() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            System.out.println("[ensureTablesExist()] DB file path: " + new File("workout_split.db").getAbsolutePath());

            stmt.execute("CREATE TABLE IF NOT EXISTS Users (UserID INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT NOT NULL, Email TEXT NOT NULL, PhoneNumber TEXT NOT NULL, workout_goal TEXT, membership_plan TEXT);");
            stmt.execute("CREATE TABLE IF NOT EXISTS MuscleGroup (MuscleGroupID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL);");
            stmt.execute("CREATE TABLE IF NOT EXISTS Exercise (ExerciseID INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, muscle_group_id INTEGER, FOREIGN KEY (muscle_group_id) REFERENCES MuscleGroup(MuscleGroupID));");
            stmt.execute("CREATE TABLE IF NOT EXISTS Workouts (WorkoutID INTEGER PRIMARY KEY AUTOINCREMENT, UserID INTEGER, DayNumber INTEGER, WorkoutDay TEXT, ExerciseName TEXT, Sets INTEGER, Reps INTEGER, Duration INTEGER, FOREIGN KEY (UserID) REFERENCES Users(UserID));");
            stmt.execute("CREATE TABLE IF NOT EXISTS WorkoutExercise (WorkoutExerciseID INTEGER PRIMARY KEY AUTOINCREMENT, WorkoutID INTEGER, ExerciseID INTEGER, Sets INTEGER NOT NULL, Reps INTEGER NOT NULL, Duration INTEGER NOT NULL, FOREIGN KEY (WorkoutID) REFERENCES Workouts(WorkoutID), FOREIGN KEY (ExerciseID) REFERENCES Exercise(ExerciseID));");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            Connection conn = connect();
            System.out.println("Connection to workout_split.db successful!");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Failed to connect to DB: " + e.getMessage());
        }
    }
}
