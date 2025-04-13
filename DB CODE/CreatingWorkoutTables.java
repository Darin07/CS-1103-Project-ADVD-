import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreatingWorkoutTables {
    public static void main(String[] args) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:workout_split.db");


            Statement stmt = conn.createStatement();

            // Users Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Users (
                    UserID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Name TEXT NOT NULL,
                    Email TEXT NOT NULL,
                    PhoneNumber TEXT NOT NULL,
                    workout_goal TEXT,
                    membership_plan TEXT
                );
            """);

            // MuscleGroup Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS MuscleGroup (
                    MuscleGroupID INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                );
            """);

            // Exercise Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Exercise (
                    ExerciseID INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    muscle_group_id INTEGER,
                    FOREIGN KEY (muscle_group_id) REFERENCES MuscleGroup(MuscleGroupID)
                );
            """);

            // Workouts Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Workouts (
                    WorkoutID INTEGER PRIMARY KEY AUTOINCREMENT,
                    UserID INTEGER,
                    DayNumber INTEGER,
                    WorkoutDay TEXT,
                    ExerciseName TEXT,
                    Sets INTEGER,
                    Reps INTEGER,
                    Duration INTEGER,
                    FOREIGN KEY (UserID) REFERENCES Users(UserID)
                );
            """);

            // WorkoutExercise Table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS WorkoutExercise (
                    WorkoutExerciseID INTEGER PRIMARY KEY AUTOINCREMENT,
                    WorkoutID INTEGER,
                    ExerciseID INTEGER,
                    Sets INTEGER NOT NULL,
                    Reps INTEGER NOT NULL,
                    Duration INTEGER NOT NULL,
                    FOREIGN KEY (WorkoutID) REFERENCES Workouts(WorkoutID),
                    FOREIGN KEY (ExerciseID) REFERENCES Exercise(ExerciseID)
                );
            """);

            System.out.println("Workout tables created successfully.");
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
