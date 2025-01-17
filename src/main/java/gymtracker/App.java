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
        "primary muscles (muscle 1, muscle 2, ...): ",
        "secondary muscles (muscle 1, muscle 2, ...): ",
        "type: "
    };
    private final static String[] SET_PROMPTS = {
        "date: ",
        "exercise: ",
        "weight: ",
        "reps: "
    };
    private final static String[] COMMANDS = {
        "add exercise: add exercise with prompts",
        "add exercise <name>;<primary muscles>;<secondary muscles>;<type>: add exercise without prompts",
        "add set: add set with prompts",
        "add set: <date>;<exercise>;<weight>;<reps>: add sets without prompts",
        "print exercises: print all exercises",
        "print exercises <name>: print one exercise",
        "print sets: print all sets",
        "print sets date <date>: print sets for date",
        "clear exercises: clear all exercises",
        "clear sets: clear all sets",
        "delete exercise <name>: delete exercise with given name",
        "delete set: delete set that was added last",
        "modify exercise name: modify exercise name with prompts",
        "modify exercise primary: modify exercise primary muscles with prompts",
        "modify exercise secondary: modify exercise secondary muscles with prompts",
        "modify exercise type: modify exercise type with prompts",
        "quit: exit the program",
        "help: see these instructions"
    };

    public static void main(String[] args) {
        Data data = new Data();

        try(Scanner inputReader = new Scanner(System.in)) {
            System.out.println("Welcome to GymTracker. Type \"help\" to see commands.");

            boolean appIsOn = true;
            while (appIsOn) {
                String[] input = inputReader.nextLine().split(" ");

                if (input.length == 0) {
                    continue;
                }

                String operation = input[0];
                String[] tailOfInput = Arrays.copyOfRange(input, 1, input.length);

                switch(operation) {
                    case("quit"):
                        appIsOn = false;
                        break;
                    case("add"):
                        addFromInput(tailOfInput, inputReader, data);
                        break;
                    case("print"):
                        printFromInput(tailOfInput, data);
                        break;
                    case("clear"):
                        clearFromInput(tailOfInput, data);
                        break;
                    case("delete"):
                        deleteFromInput(tailOfInput, data);
                        break;
                    case("modify"):
                        modifyFromInput(tailOfInput, inputReader, data);
                        break;
                    case("help"):
                        for (String command : COMMANDS) {
                            System.out.println(command);
                        }
                        break;
                    default:
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

    private static void addFromInput(String[] input, Scanner inputReader, Data data) {
        if (input.length == 0) {
            System.out.println("Too few arguments for add operation.");
            return;
        }
        if (!input[0].equals("exercise") && !input[0].equals("set")) {
            System.out.println("Add is only possible for exercise or set.");
            return;
        }
        String rawInput = "";
        if (input.length > 1) {
            rawInput = String.join(" ", Arrays.copyOfRange(input, 1, input.length));   
        }
        if (input[0].equals("exercise")) {
            if (rawInput.isEmpty()) {
                rawInput = saveInput(inputReader, EXERCISE_PROMPTS);
            }
            Exercise newExercise = exerciseFromInput(rawInput);
            if (newExercise != null) {
                if (data.addExercise(newExercise)) {
                    System.out.println("Exercise added succefully");
                }
                else {
                    System.out.println("Failed to add exercise.");
                }
            }
        }
        else if (input[0].equals("set")) {
            if (rawInput.isEmpty()) {
                rawInput = saveInput(inputReader, SET_PROMPTS);
            }
            Set newSet = setFromInput(rawInput);
            if (newSet != null) {
                if (data.addSet(newSet)) {
                    System.out.println("Set added succesfully.");
                }
                else {
                    System.out.println("failed to add set.");
                }
            }   
        }
    }

    private static void printFromInput(String[] input, Data data) {
        if (input.length == 0) {
            System.out.println("Too few arguments for print operation.");
        }
        else if (input[0].equals("exercises")) {
            if (input.length == 1) {
                data.printAllExercises();
            }
            else if (input.length >= 2) {
                data.printExercise(String.join(" ", Arrays.copyOfRange(input, 1, input.length)));
            }
        }
        else if (input[0].equals("sets")) {
            if (input.length == 1) {
                data.printAllSets();
            }
            else if (input[1].equals("date")) {
                if (input.length == 3) {
                    data.printSetsForDate(input[2]);
                }
                else if (input.length == 2) {
                    System.out.println("Please specify a date.");
                }
                else {
                    System.out.println("Date should be a single argument.");
                }
            }
            else {
                System.out.println("Incorrect format for print sets operation.");
            }
        }
        else {
            System.out.println("Print is only possible for exercises or sets.");
        }
    }

    private static void clearFromInput(String[] input, Data data) {
        if (input.length == 0) {
            System.out.println("Too few arguments for clear operation.");
        }
        else if (input.length > 1) {
            System.out.println("Too many arguments for clear operation.");
        }
        else if (input[0].equals("exercises")) {
            data.clearExercises();
        }
        else if (input[0].equals("sets")) {
            data.clearSets();
        }
        else {
            System.out.println("Clear is only possible for exercises or sets.");
        }
    }

    private static void deleteFromInput(String[] input, Data data) {
        if (input.length == 0) {
            System.out.println("Too few arguments for delete operation.");
        }
        else if (input[0].equals("exercise")) {
            if (input.length >= 2) {
                if (data.deleteExercise(
                    String.join(" ", Arrays.copyOfRange(input, 2, input.length)))
                ) {
                    System.out.println("Exercise deleted succesfully.");
                }
                else {
                    System.out.println("Failed to delete exercise.");
                }
            }
            else {
                System.out.println("Too few arguments for delete exercise operation.");
            }
        }
        else if (input[0].equals("set")) {
            if (input.length == 1) {
                if (data.deleteLastSet()) {
                    System.out.println("Set deleted succesfully.");
                }
                else {
                    System.out.println("Failed to delete set.");
                }
            }
            else {
                System.out.println("Too many arguments for delete set operation.");
            }
        }
        else {
            System.out.println("Delete is only possible for exercise or set");
        }
    }

    private static void modifyFromInput(String[] input, Scanner inputReader, Data data) {
        if (input.length < 2) {
            System.out.println("Too few arguments for modify operation.");
        }
        else if (input.length > 2) {
            System.out.println("Too many arguments for modify operation.");
        }
        else if (input[0].equals("exercise")) {
            if (input[1].equals("name")) {
                System.out.print("Exercise to modify: ");
                String oldExercise = inputReader.nextLine();
                System.out.print("New name: ");
                String newName = inputReader.nextLine();
                data.modifyExerciseName(oldExercise, newName);
            }
            else if (input[1].equals("primary")) {
                System.out.print("Exercise to modify: ");
                String oldExercise = inputReader.nextLine();
                System.out.print("New muscles: ");
                String[] newMuscles = inputReader.nextLine().split(", ");
                data.modifyExercisePrimaryMuscles(oldExercise, newMuscles);
            }
            else if (input[1].equals("secondary")) {
                System.out.print("Exercise to modify: ");
                String oldExercise = inputReader.nextLine();
                System.out.print("New muscles: ");
                String[] newMuscles = inputReader.nextLine().split(", ");
                data.modifyExerciseSecondaryMuscles(oldExercise, newMuscles);
            }
            else if (input[1].equals("type")) {
                System.out.print("Exercise to modify: ");
                String oldExercise = inputReader.nextLine();
                System.out.print("New type: ");
                String newType = inputReader.nextLine();
                data.modifyExerciseType(oldExercise, newType);
            }
            else {
                System.out.println(
                    "possible arguments for modify exercise are name, primary, secondary and type."
                );
            }
        }
        else {
            System.out.println("Modify is only possible for exercise.");
        }
    }
}
