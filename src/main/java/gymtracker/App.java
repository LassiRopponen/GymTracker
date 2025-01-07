package gymtracker;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * GymTracker
 */
public class App {
    final static String EXERCISE_PATH = "src/main/resources/exercises.txt";
    final static String SET_PATH = "src/main/resources/sets.txt";

    public static void main(String[] args) {
        FileHandler files = new FileHandler();

        ArrayList<Exercise> exercises = files.parseFile(EXERCISE_PATH, Exercise.class);
        ArrayList<Set> sets = files.parseFile(SET_PATH, Set.class);

        try(Scanner inputReader = new Scanner(System.in)) {
            while (true) {
                String input = inputReader.nextLine();

                if (input.equals("quit")) {
                    break;
                }
                if (input.equals("add exercise")) {
                    Exercise newExercise = new Exercise();

                    System.out.print("name: ");
                    newExercise.name = inputReader.nextLine();

                    System.out.print("primary muscles: ");
                    newExercise.primaryMuscles = new ArrayList<>(
                        Arrays.asList(inputReader.nextLine().split(", ")));

                    System.out.print("secondary muscles: ");
                    newExercise.secondaryMuscles = new ArrayList<>(
                        Arrays.asList(inputReader.nextLine().split(", ")));

                    System.out.print("type: ");
                    newExercise.type = inputReader.nextLine();

                    exercises.add(newExercise);
                    files.writeToFile(EXERCISE_PATH, newExercise);
                }
                if (input.equals("add set")) {
                    Set newSet = new Set();

                    System.out.print("date: ");
                    newSet.date = inputReader.nextLine();

                    System.out.print("exercise: ");
                    newSet.exercise = inputReader.nextLine();

                    System.out.print("weight: ");
                    newSet.weight = Float.parseFloat(inputReader.nextLine());

                    System.out.print("reps: ");
                    newSet.reps = Integer.parseInt(inputReader.nextLine());

                    sets.add(newSet);
                    files.writeToFile(SET_PATH, newSet);
                }
            }
        }
    }
}
