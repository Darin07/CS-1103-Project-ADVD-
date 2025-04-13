import java.sql.Connection;
import java.sql.Statement;
import java.util.Random;

public class InsertingWorkoutData {
    public static void main(String[] args) {
        try {
            Connection conn = WorkoutDBConnector.connect();
            Statement stmt = conn.createStatement();
            Random random = new Random();

            // Clear all data
            stmt.executeUpdate("DELETE FROM WorkoutExercise");
            stmt.executeUpdate("DELETE FROM Workouts");
            stmt.executeUpdate("DELETE FROM Exercise");
            stmt.executeUpdate("DELETE FROM MuscleGroup");
            stmt.executeUpdate("DELETE FROM Users");

            // Insert Muscle Groups
            String[] muscleGroups = {"Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio"};
            for (int i = 0; i < muscleGroups.length; i++) {
                stmt.execute(String.format("INSERT INTO MuscleGroup (MuscleGroupID, name) VALUES (%d, '%s');", i + 1, muscleGroups[i]));
            }

            // Insert Exercises
            String[][] exercises = {
                    {"Bench Press", "1"}, {"Incline Dumbbell Press", "1"}, {"Push-up", "1"},
                    {"Deadlift", "2"}, {"Bent-over Row", "2"}, {"Pull-up", "2"},
                    {"Squat", "3"}, {"Lunges", "3"}, {"Leg Press", "3"},
                    {"Shoulder Press", "4"}, {"Lateral Raise", "4"},
                    {"Bicep Curl", "5"}, {"Tricep Dip", "5"},
                    {"Plank", "6"}, {"Russian Twist", "6"},
                    {"Treadmill", "7"}, {"Cycling", "7"}, {"Jump Rope", "7"},
                    {"Mountain Climbers", "6"}, {"Burpees", "7"}
            };
            for (int i = 0; i < exercises.length; i++) {
                stmt.execute(String.format("INSERT INTO Exercise (ExerciseID, name, muscle_group_id) VALUES (%d, '%s', %s);", i + 1, exercises[i][0], exercises[i][1]));
            }

            // Insert Users
            String[] names = {"Vihaan", "John", "Alice", "Mark", "Emma", "David", "Sophia", "Daniel", "Chloe", "Ethan", "Liam", "Ava", "Noah", "Grace", "Zara"};
            for (int i = 0; i < names.length; i++) {
                String email = names[i].toLowerCase() + "@example.com";
                String phone = "555-01" + String.format("%03d", i + 1);
                String goal = switch (i % 4) {
                    case 0 -> "Muscle Building";
                    case 1 -> "Weight Loss";
                    case 2 -> "Endurance";
                    default -> "General Fitness";
                };
                String plan = (i % 2 == 0) ? "Premium" : "Basic";
                stmt.execute(String.format(
                        "INSERT INTO Users (UserID, Name, Email, PhoneNumber, workout_goal, membership_plan) " +
                                "VALUES (%d, '%s', '%s', '%s', '%s', '%s');",
                        i + 1, names[i], email, phone, goal, plan));
            }

            // Workout Day names
            String[] workoutDays = {"Push Day", "Pull Day", "Leg Day", "Cardio", "Full Body"};
            int workoutID = 1;
            int workoutExerciseID = 1;

            // Assign 5-day plans per user
            for (int userID = 1; userID <= 15; userID++) {
                for (int day = 1; day <= 5; day++) {
                    String workoutDay = workoutDays[day - 1];
                    // Choose 2–3 random exercises
                    int numberOfExercises = 2 + random.nextInt(2);
                    int[] chosenExercises = random.ints(1, 21).distinct().limit(numberOfExercises).toArray();

                    for (int exIndex : chosenExercises) {
                        // Get exercise name
                        String exNameQuery = "SELECT name FROM Exercise WHERE ExerciseID = " + exIndex;
                        var rs = stmt.executeQuery(exNameQuery);
                        String exName = rs.next() ? rs.getString("name") : "Exercise";

                        // Insert into Workouts
                        stmt.execute(String.format(
                                "INSERT INTO Workouts (WorkoutID, UserID, DayNumber, WorkoutDay, ExerciseName, Sets, Reps, Duration) " +
                                        "VALUES (%d, %d, %d, '%s', '%s', %d, %d, %d);",
                                workoutID, userID, day, workoutDay, exName,
                                3 + random.nextInt(2), 8 + random.nextInt(5), 20 + random.nextInt(41)));

                        // Insert into WorkoutExercise
                        stmt.execute(String.format(
                                "INSERT INTO WorkoutExercise (WorkoutExerciseID, WorkoutID, ExerciseID, Sets, Reps, Duration) " +
                                        "VALUES (%d, %d, %d, %d, %d, %d);",
                                workoutExerciseID++, workoutID, exIndex,
                                3 + random.nextInt(2), 8 + random.nextInt(5), 20 + random.nextInt(41)));

                        rs.close();
                        workoutID++;
                    }
                }
            }

            System.out.println("Successfully populated 15 users with randomized 5-day workouts.");
            System.out.println("Inserting into DB at: " + new java.io.File("workout_split.db").getAbsolutePath());

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
