import javafx.application.Application;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;



import java.sql.*;

public class Menu extends Application {
    private Connection conn;
    private Scene mainMenuScene, userViewScene, exerciseScene, allWorkoutScene;
    private TableView<User> userTable = new TableView<>();
    private TableView<Workout> workoutTable = new TableView<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            conn = WorkoutDBConnector.connect();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        mainMenuScene = buildMainMenuScene(stage);
        userViewScene = buildUserViewScene(stage);
        exerciseScene = buildExerciseScene(stage);
        allWorkoutScene = buildAllWorkoutScene(stage);

        stage.setTitle("Workout_split DB");
        stage.setScene(mainMenuScene);
        stage.show();
    }

    private Scene buildMainMenuScene(Stage stage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f5f5f5;");

        layout.getChildren().addAll(
                createNavButton("User", "#4285F4", () -> stage.setScene(userViewScene)),
                createNavButton("Exercises", "#34A853", () -> stage.setScene(exerciseScene)),
                createNavButton("All Workouts", "#FABB05", () -> stage.setScene(allWorkoutScene)),
                createNavButton("Exit", "#EA4335", stage::close)
        );

        return new Scene(layout, 350, 400);
    }

    private Button createNavButton(String label, String color, Runnable action) {
        Button btn = new Button(label);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        btn.setMinWidth(180);
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Scene buildUserViewScene(Stage stage) {
        userTable.getColumns().setAll(
                createCol("ID", u -> u.idProperty().asString()),
                createCol("Name", User::nameProperty),
                createCol("Goal", User::goalProperty),
                createCol("Email", User::emailProperty),
                createCol("Phone", User::phoneProperty),
                createCol("Membership", User::membershipProperty)
        );
        userTable.setItems(loadUsers());

        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (!row.isEmpty()) {
                    workoutTable.setItems(loadWorkoutsForUser(row.getItem().getId()));
                }
            });
            return row;
        });

        workoutTable.getColumns().setAll(
                createCol("Day", Workout::dayNumberProperty),
                createCol("Workout Day", Workout::workoutDayProperty),
                createCol("Exercise", Workout::exerciseNameProperty),
                createCol("Sets", w -> w.setsProperty().asString()),
                createCol("Reps", w -> w.repsProperty().asString()),
                createCol("Duration", w -> w.durationProperty().asString())
        );

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(mainMenuScene));

        VBox layout = new VBox(10, backBtn, new Label("Users"), userTable, new Label("Workout Plan"), workoutTable);
        layout.setPadding(new Insets(20));
        return new Scene(layout, 950, 600);
    }

    private Scene buildExerciseScene(Stage stage) {
        TextArea output = new TextArea();
        output.setEditable(false);
        StringBuilder sb = new StringBuilder();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT e.ExerciseID, e.name, m.name AS muscle_group " +
                             "FROM Exercise e JOIN MuscleGroup m ON e.muscle_group_id = m.MuscleGroupID"
             )) {
            while (rs.next()) {
                sb.append("ID: ").append(rs.getInt("ExerciseID"))
                        .append(" | Name: ").append(rs.getString("name"))
                        .append(" | Muscle Group: ").append(rs.getString("muscle_group")).append("\n");
            }
        } catch (SQLException e) {
            sb.append("Error loading exercises.");
        }

        output.setText(sb.toString());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(mainMenuScene));

        VBox layout = new VBox(10, backBtn, new Label("All Exercises"), output);
        layout.setPadding(new Insets(20));
        return new Scene(layout, 600, 500);
    }

    private Scene buildAllWorkoutScene(Stage stage) {
        TextArea output = new TextArea();
        output.setEditable(false);
        StringBuilder sb = new StringBuilder();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT w.UserID, u.Name, w.DayNumber, w.WorkoutDay, w.ExerciseName, w.Sets, w.Reps, w.Duration " +
                             "FROM Workouts w JOIN Users u ON w.UserID = u.UserID ORDER BY w.UserID, w.DayNumber")) {
            while (rs.next()) {
                sb.append("User: ").append(rs.getString("Name"))
                        .append(" | Day ").append(rs.getInt("DayNumber"))
                        .append(" | ").append(rs.getString("WorkoutDay"))
                        .append(" | ").append(rs.getString("ExerciseName"))
                        .append(" | Sets: ").append(rs.getInt("Sets"))
                        .append(" | Reps: ").append(rs.getInt("Reps"))
                        .append(" | Duration: ").append(rs.getInt("Duration")).append(" mins\n");
            }
        } catch (SQLException e) {
            sb.append("Error loading workouts.");
        }

        output.setText(sb.toString());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> stage.setScene(mainMenuScene));

        VBox layout = new VBox(10, backBtn, new Label("All Workouts"), output);
        layout.setPadding(new Insets(20));
        return new Scene(layout, 800, 600);
    }

    private <T> TableColumn<T, String> createCol(String title, javafx.util.Callback<T, ObservableValue<String>> mapper) {
        TableColumn<T, String> col = new TableColumn<>(title);
        col.setCellValueFactory(cellData -> mapper.call(cellData.getValue()));
        return col;
    }

    private ObservableList<User> loadUsers() {
        ObservableList<User> users = FXCollections.observableArrayList();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Users")) {
            while (rs.next()) {
                System.out.println("Found user: " + rs.getString("Name"));

                users.add(new User(
                        rs.getInt("UserID"),
                        rs.getString("Name"),
                        rs.getString("workout_goal"),
                        rs.getString("Email"),
                        rs.getString("PhoneNumber"),
                        rs.getString("membership_plan")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private ObservableList<Workout> loadWorkoutsForUser(int userId) {
        ObservableList<Workout> list = FXCollections.observableArrayList();
        try {
            String sql = "SELECT w.DayNumber, e.Name AS ExerciseName, we.Sets, we.Reps, we.Duration " +
                    "FROM Workouts w JOIN WorkoutExercise we ON w.WorkoutID = we.WorkoutID " +
                    "JOIN Exercise e ON we.ExerciseID = e.ExerciseID WHERE w.UserID = ? ORDER BY w.DayNumber";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            String[] types = {"Push Day", "Pull Day", "Leg Day", "Cardio", "Full Body"};
            int lastDay = -1;

            while (rs.next()) {
                int day = rs.getInt("DayNumber");
                String type = types[(day - 1) % types.length];
                String dayStr = (day == lastDay) ? "" : String.valueOf(day);
                String typeStr = (day == lastDay) ? "" : type;

                list.add(new Workout(dayStr, typeStr, rs.getString("ExerciseName"),
                        rs.getInt("Sets"), rs.getInt("Reps"), rs.getInt("Duration")));
                lastDay = day;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static class User {
        private final int id;
        private final SimpleIntegerProperty idProp;
        private final SimpleStringProperty name, goal, email, phone, membership;

        public User(int id, String name, String goal, String email, String phone, String membership) {
            this.id = id;
            this.idProp = new SimpleIntegerProperty(id);
            this.name = new SimpleStringProperty(name);
            this.goal = new SimpleStringProperty(goal);
            this.email = new SimpleStringProperty(email);
            this.phone = new SimpleStringProperty(phone);
            this.membership = new SimpleStringProperty(membership);
        }

        public int getId() { return id; }
        public SimpleIntegerProperty idProperty() { return idProp; }
        public SimpleStringProperty nameProperty() { return name; }
        public SimpleStringProperty goalProperty() { return goal; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty phoneProperty() { return phone; }
        public SimpleStringProperty membershipProperty() { return membership; }
    }

    public static class Workout {
        private final SimpleStringProperty dayNumber;
        private final SimpleStringProperty workoutDay;
        private final SimpleStringProperty exerciseName;
        private final SimpleIntegerProperty sets, reps, duration;

        public Workout(String day, String type, String ex, int s, int r, int d) {
            dayNumber = new SimpleStringProperty(day);
            workoutDay = new SimpleStringProperty(type);
            exerciseName = new SimpleStringProperty(ex);
            sets = new SimpleIntegerProperty(s);
            reps = new SimpleIntegerProperty(r);
            duration = new SimpleIntegerProperty(d);
        }

        public SimpleStringProperty dayNumberProperty() { return dayNumber; }
        public SimpleStringProperty workoutDayProperty() { return workoutDay; }
        public SimpleStringProperty exerciseNameProperty() { return exerciseName; }
        public SimpleIntegerProperty setsProperty() { return sets; }
        public SimpleIntegerProperty repsProperty() { return reps; }
        public SimpleIntegerProperty durationProperty() { return duration; }
    }

    @Override
    public void stop() throws Exception {
        if (conn != null) conn.close();
    }
}
