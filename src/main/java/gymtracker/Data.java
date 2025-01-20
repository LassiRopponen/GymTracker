package gymtracker;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class for data storage and logic
 */
public class Data {
    private final String EXERCISE_PATH = "src/main/resources/exercises.txt";
    private final String SET_PATH = "src/main/resources/sets.txt";

    private FileHandler files;
    private ArrayList<Exercise> exerciseList;
    private ArrayList<Set> setList;

    /** 
     * Constructor that reads saved data from files
     */
    public Data() {
        this.files = new FileHandler();

        this.exerciseList = files.parseFile(EXERCISE_PATH, Exercise.class);
        this.setList = files.parseFile(SET_PATH, Set.class);
    }

    /**
     * adds a new exercise to the database and writes its information to a file
     * @param newExercise: the exercise to be added
     * @return whether the operation succeeded
     */
    public boolean addExercise(Exercise newExercise) {
        Optional<Exercise> result = exerciseList.stream()
            .filter(e -> e.name.equals(newExercise.name)).findAny();
        if (result.isPresent()) {
            System.out.println("Exercise with given name already exists.");
            return false;
        }
        exerciseList.add(newExercise);
        return files.writeToFile(EXERCISE_PATH, newExercise);
    }

    /**
     * adds a new set to the database and writes its information to a file
     * @param newSet: the set to be added
     * @return whether the operation succeeded
     */
    public boolean addSet(Set newSet) {
        Optional<Exercise> result = exerciseList.stream()
            .filter(e -> e.name.equals(newSet.exercise)).findAny();
        if (!result.isPresent()) {
            System.out.println("No exercise with given name.");
            return false;
        }
        setList.add(newSet);
        return files.writeToFile(SET_PATH, newSet);
    }

    /**
     * prints the name of every saved exercise
     */
    public void printAllExercises() {
        if (exerciseList.isEmpty()) {
            System.out.println("No exercises to print.");
            return;
        }
        for (Exercise exercise : exerciseList) {
            System.out.println(exercise);
        }
    }

    /**
     * prints the exercise and date of every set
     */
    public void printAllSets() {
        if (setList.isEmpty()) {
            System.out.println("No sets to print.");
            return;
        }
        for (Set set : setList) {
            System.out.println(set);
        }
    }

    /**
     * prints all the information for an exercise with given name
     * @param name: the name of the exercise to be printed
     */
    public void printExercise(String name) {
        Optional<Exercise> result = exerciseList.stream()
            .filter(e -> e.name.equals(name)).findAny();
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

    /**
     * prints all the sets for a given date
     * @param date: the date of the sets to be printed
     */
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

    /**
     * deletes an exercise with given name
     * @param name: the name of the exercise to be deleted
     * @return whether the operation succeeded
     */
    public boolean deleteExercise(String name) {
        Boolean wasDeleted = exerciseList.removeIf(e -> e.name.equals(name));
        if (!wasDeleted) {
            System.out.println("No such exercise.");
            return false;
        }
        return rewriteExercises();
    }

    /**
     * deletes the set that was last added to the database
     * @return whether the operation succeeded
     */
    public boolean deleteLastSet() {
        if (setList.isEmpty()) {
            System.out.println("No sets to delete.");
            return false;
        }
        setList.remove(setList.size()-1);
        return rewriteSets();
    }

    /**
     * clears all exercises from the database
     */
    public void clearExercises() {
        exerciseList.clear();
        if (!files.clearFile(EXERCISE_PATH)) {
            System.out.println("Unable to clear file.");
        }
    }

    /**
     * clears all sets from the database
     */
    public void clearSets() {
        setList.clear();
        if (!files.clearFile(SET_PATH)) {
            System.out.println("Unable to clear file.");
        }
    }

    /**
     * modifies the name of an exercise with given name
     * @param oldName: the old name of the exercise to be modified
     * @param newName: the new name of the exercise
     */
    public void modifyExerciseName(String oldName, String newName) {
        Optional<Exercise> result = exerciseList.stream()
            .filter(e -> e.name.equals(oldName)).findAny();
        if (!result.isPresent()) {
            System.out.println("No such exercise.");
            return;
        }
        Exercise exerciseToModify = result.get();
        exerciseToModify.name = newName;
        for (Set set : setList) {
            if (set.exercise.equals(oldName)) {
                set.exercise = newName;
            }
        }
        if (rewriteExercises()) {
            System.out.println("Modifying exercise succesful.");
        }
        else {
            System.out.println("Modifying exercises failed.");
        }
        if (rewriteSets()) {
            System.out.println("Modifying sets succesful.");
        }
        else {
            System.out.println("Modifying sets failed.");
        }
    }

    /**
     * modifies the list of primary muscles of an exercise with given name
     * @param name: the name of the exercise to be modified
     * @param newMuscles: the new list of primary muscles
     */
    public void modifyExercisePrimaryMuscles(String name, String[] newMuscles) {
        Optional<Exercise> result = exerciseList.stream()
            .filter(e -> e.name.equals(name)).findAny();
        if (!result.isPresent()) {
            System.out.println("No such exercise.");
            return;
        }
        Exercise exerciseToModify = result.get();
        exerciseToModify.primaryMuscles = new ArrayList<>(Arrays.asList(newMuscles));
        if (rewriteExercises()) {
            System.out.println("Modifying exercise succesful.");
        }
        else {
            System.out.println("Modifying exercise failed.");
        }
    }

    /**
     * modifies the list of secondary muscles of an exercise with given name
     * @param name: the name of the exercise to be modified
     * @param newMuscles: the new list of secondary muscles
     */
    public void modifyExerciseSecondaryMuscles(String name, String[] newMuscles) {
        Optional<Exercise> result = exerciseList.stream()
            .filter(e -> e.name.equals(name)).findAny();
        if (!result.isPresent()) {
            System.out.println("No such exercise.");
            return;
        }
        Exercise exerciseToModify = result.get();
        exerciseToModify.secondaryMuscles = new ArrayList<>(Arrays.asList(newMuscles));
        if (rewriteExercises()) {
            System.out.println("Modifying exercise succesful.");
        }
        else {
            System.out.println("Modifying exercise failed.");
        }
    }

    /**
     * modifies the type of an exercise with given name
     * @param name: the name of the exercise to be modified
     * @param newType: the new type of the exercise
     */
    public void modifyExerciseType(String name, String newType) {
        Optional<Exercise> result = exerciseList.stream()
            .filter(e -> e.name.equals(name)).findAny();
        if (!result.isPresent()) {
            System.out.println("No such exercise.");
            return;
        }
        Exercise exerciseToModify = result.get();
        exerciseToModify.type = newType;
        if (rewriteExercises()) {
            System.out.println("Modifying exercise succesful.");
        }
        else {
            System.out.println("Modifying exercise failed.");
        }
    }

    /**
     * rewrites the database file based on the current state of the database
     * @return whether the operation succeeded
     */
    private boolean rewriteExercises() {
        if (!files.clearFile(EXERCISE_PATH)) {
            System.out.println("Unable to clear file.");
            return false;
        }
        int failureCount = 0;
        for (Exercise exercise : exerciseList) {
            if (!files.writeToFile(EXERCISE_PATH, exercise)) {
                failureCount++;
            }
        }
        if (failureCount > 0) {
            System.out.println(failureCount + " exercises lost during operation.");
        }
        return true;
    }

    /**
     * rewrites the database file based on the current state of the database
     * @return whether the operation succeeded
     */
    private boolean rewriteSets() {
        if (!files.clearFile(SET_PATH)) {
            System.out.println("Unable to clear file.");
            return false;
        }
        int failureCount = 0;
        for (Set set : setList) {
            if (!files.writeToFile(SET_PATH, set)) {
                failureCount++;
            }
        }
        if (failureCount > 0) {
            System.out.println(failureCount + " sets lost during operation.");
        }
        return true;
    }
}
