# 🏋️ Workout Split Manager

A Java-based desktop application that generates and manages personalized 5-day workout routines for multiple users using an embedded SQLite database. Developed as part of the CS 1103 coursework.


## 🚀 Features

- 📋 GUI built with JavaFX
- 🧑 Personalized user profiles (Name, Goal, Membership, etc.)
- 🗓️ Dynamic 5-day workout splits: Push, Pull, Legs, Cardio, Full Body
- 🔁 Randomized exercise generation per user
- 🧠 Normalized SQLite database schema
- 📂 Full CRUD capability using JDBC
- 📊 GUI-based data visualization for Users, Exercises, and Workouts


## 🧱 Tech Stack

- Java 17+
- JavaFX
- SQLite (via `sqlite-jdbc.jar`)
- SLF4J (logging with `slf4j-api.jar` and `slf4j-simple.jar`)
- IntelliJ IDEA (Recommended IDE)


## 🧩 ER Diagram Overview

- `Users`: Stores user profile data
- `Workouts`: 5-day plans per user
- `Exercise`: All available exercises
- `MuscleGroup`: Categories like Chest, Legs, etc.
- `WorkoutExercise`: Many-to-many link between workouts and exercises


## 📦 How to Run

1. Clone or download the project folder
2. Ensure the following JARs are in the root folder:
   - `sqlite-jdbc.jar`
   - `slf4j-api.jar`
   - `slf4j-simple.jar`
3. In IntelliJ:
   - Add all JARs to **File > Project Structure > Modules > Dependencies**
   - Set the working directory to the project folder (`$PROJECT_DIR$`)
4. Run `CreatingWorkoutTables.java` to generate tables
5. Run `InsertingWorkoutData.java` to populate randomized data
6. Run `Menu.java` to launch the GUI
   

## 🙌 Author

**Vihaan Dumont, Darin Thomson, Abdulrahman Abdelsadek, Dwayne Luy**  
Bachelor of Computer Science  
University of New Brunswick, Saint John  
