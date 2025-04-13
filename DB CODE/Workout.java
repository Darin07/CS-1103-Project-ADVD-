public class Workout {
    private int dayNumber;
    private String workoutDay;
    private String exerciseName;
    private int sets;
    private int reps;
    private int duration;

    // Constructor
    public Workout(int dayNumber, String workoutDay, String exerciseName, int sets, int reps, int duration) {
        this.dayNumber = dayNumber;
        this.workoutDay = workoutDay;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
    }

    // Getters and setters (if needed)
    public int getDayNumber() {
        return dayNumber;
    }

    public String getWorkoutDay() {
        return workoutDay;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public int getDuration() {
        return duration;
    }
}
