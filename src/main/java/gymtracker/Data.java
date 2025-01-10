package gymtracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

public class Data {
    private final String EXERCISE_PATH = "src/main/resources/exercises.txt";
    private final String SET_PATH = "src/main/resources/sets.txt";

    private FileHandler files;
    private ArrayList<Exercise> exerciseList;
    private ArrayList<Set> setList;

    public Data() {
        this.files = new FileHandler();

        this.exerciseList = files.parseFile(EXERCISE_PATH, Exercise.class);
        this.setList = files.parseFile(SET_PATH, Set.class);
    }

    public void addExercise(Exercise newExercise) {
        exerciseList.add(newExercise);
        files.writeToFile(EXERCISE_PATH, newExercise);
    }

    public void addSet(Set newSet) {
        setList.add(newSet);
        files.writeToFile(SET_PATH, newSet);
    }

    public void printAllExercises() {
        for (Exercise exercise : exerciseList) {
            System.out.println(exercise);
        }
    }

    public void printAllSets() {
        for (Set set : setList) {
            System.out.println(set);
        }
    }

    public void printExercise(String name) {
        Optional<Exercise> result = exerciseList.stream().filter(e -> e.name.equals(name)).findAny();
        if (!result.isPresent()) {
            System.out.println("No such exercise.");
            return;
        }
        Exercise exerciseToPrint = result.get();
        System.out.println("name: " + exerciseToPrint.name);
        System.out.print("primary muscles:");
        exerciseToPrint.primaryMuscles.forEach(m -> System.out.print(" " + m));
        System.out.println("");
        System.out.print("secondary muscles:");
        exerciseToPrint.secondaryMuscles.forEach(m -> System.out.print(" " + m));
        System.out.println("");
        System.out.println("type: " + exerciseToPrint.type);
    }

    public void printSetsForDate(String date) {
        ArrayList<Set> setsToPrint = new ArrayList<>(
            setList.stream().filter(s -> s.date.equals(date)).collect(Collectors.toList()));
        if (setsToPrint.isEmpty()) {
            System.out.println("No exercises for given date.");
            return;
        }
        DecimalFormat format = new DecimalFormat("0.##");
        for (Set set : setsToPrint) {
            System.out.println(
                set.exercise + ", " +
                format.format(set.weight) + " kg, " +
                set.reps + " reps"
            );
        }
    }

    public void deleteExercise(String name) {
        Boolean wasDeleted = exerciseList.removeIf(e -> e.name.equals(name));
        if (!wasDeleted) {
            System.out.println("No such exercise.");
            return;
        }
        files.clearFile(EXERCISE_PATH);
        for (Exercise exercise : exerciseList) {
            files.writeToFile(EXERCISE_PATH, exercise);
        }
    }

    public void deleteLastSet() {
        if (setList.isEmpty()) {
            System.out.println("No sets to delete.");
            return;
        }
        setList.remove(setList.size()-1);
        files.clearFile(SET_PATH);
        for (Set set : setList) {
            files.writeToFile(SET_PATH, set);
        }
    }

    public void clearExercises() {
        exerciseList.clear();
        files.clearFile(EXERCISE_PATH);
    }

    public void clearSets() {
        setList.clear();
        files.clearFile(SET_PATH);
    }
}
