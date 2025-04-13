import java.sql.*;

public class PrintingData {
    public static void main(String[] args) {
        try {
            Connection conn = WorkoutDBConnector.connect();
            Statement statement = conn.createStatement();

            ResultSet rs = statement.executeQuery("SELECT * FROM WorkoutExercise");
            while (rs.next()) {
                System.out.println("Workout ID: " + rs.getInt("WorkoutID") +
                        ", Exercise ID: " + rs.getInt("ExerciseID") +
                        ", Sets: " + rs.getInt("Sets") +
                        ", Reps: " + rs.getInt("Reps") +
                        ", Duration: " + rs.getInt("Duration"));
            }

            rs.close();
            statement.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
