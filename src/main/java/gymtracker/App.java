package gymtracker;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * GymTracker
 */
public class App {
    private final static String[] EXERCISE_PROMPTS = {
        "name: ",
        "primary muscles: ",
        "secondary muscles: ",
        "type: "
    };
    private final static String[] SET_PROMPTS = {
        "date: ",
        "exercise: ",
        "weight: ",
        "reps: "
    };

    public static void main(String[] args) {
        Data data = new Data();

        try(Scanner inputReader = new Scanner(System.in)) {
            while (true) {
                String[] input = inputReader.nextLine().split(" ");

                if (input.length > 0 && input[0].equals("quit")) {
                    break;
                }
                else if (input.length >= 2 && input[0].equals("add")) {
                    if (input[1].equals("exercise")) {
                        String rawExercise = "";
                        if (input.length > 2) {
                            rawExercise = String.join(" ", Arrays.copyOfRange(input, 2, input.length));   
                        }
                        else {
                            rawExercise = saveInput(inputReader, EXERCISE_PROMPTS);
                        }
                        Exercise newExercise = exerciseFromInput(rawExercise);
                        if (newExercise != null) {
                            data.addExercise(exerciseFromInput(rawExercise));
                        }
                    }
                    else if (input[1].equals("set")) {
                        String rawSet = "";
                        if (input.length > 2) {
                            rawSet = String.join(" ", Arrays.copyOfRange(input, 2, input.length));
                        }
                        else {
                            rawSet = saveInput(inputReader, SET_PROMPTS);
                        }
                        Set newSet = setFromInput(rawSet);
                        if (newSet != null) {
                            data.addSet(setFromInput(rawSet));
                        }   
                    }
                }
                else if (input.length >= 2 && input[0].equals("print")) {
                    if (input.length == 2) {
                        if (input[1].equals("exercises")) {
                            data.printAllExercises();
                        }
                        else if (input[1].equals("sets")) {
                            data.printAllSets();
                        }
                    }
                    else if (input.length >= 3 && input[1].equals("exercise")) {
                        data.printExercise(String.join(" ", Arrays.copyOfRange(input, 2, input.length)));
                    }
                    else if (input.length == 4 && input[1].equals("sets") && input[2].equals("date")) {
                        data.printSetsForDate(input[3]);
                    }
                }
                else if (input.length == 2 && input[0].equals("clear")) {
                    if (input[1].equals("exercises")) {
                        data.clearExercises();
                    }
                    else if (input[1].equals("sets")) {
                        data.clearSets();
                    }
                }
                else if (input.length >= 2 && input[0].equals("delete")) {
                    if (input.length >= 3 && input[1].equals("exercise")) {
                        data.deleteExercise(String.join(" ", Arrays.copyOfRange(input, 2, input.length)));
                    }
                    else if (input.length == 2 && input[1].equals("set")) {
                        data.deleteLastSet();
                    }
                }
                else {
                    System.out.println("Incorrect command.");
                }
            }
        }
    }

    private static String saveInput(Scanner reader, String[] prompts) {
        String input = "";
        for (String prompt : prompts) {
            System.out.print(prompt);
            input += String.join(",", reader.nextLine().split(", ")) + ";";
        }
        return input;
    }

    private static Exercise exerciseFromInput(String input) {
        try {
            String[] attributes = input.split(";");
            Exercise newExercise = new Exercise();
            newExercise.name = attributes[0];
            newExercise.primaryMuscles = new ArrayList<>(Arrays.asList(attributes[1].split(",")));
            newExercise.secondaryMuscles = new ArrayList<>(Arrays.asList(attributes[2].split(",")));
            newExercise.type = attributes[3];
            return newExercise;
        }
        catch(Exception e) {
            System.out.println("Incorrect format for exercise.");
            return null;
        }
    }

    private static Set setFromInput(String input) {
        try {
            String[] attributes = input.split(";");
            Set newSet = new Set();
            newSet.date = attributes[0];
            newSet.exercise = attributes[1];
            newSet.weight = Float.parseFloat(attributes[2]);
            newSet.reps = Integer.parseInt(attributes[3]);
            return newSet;
        }
        catch(Exception e) {
            System.out.println("Incorrect format for set.");
            return null;
        }
        
    }
}
