package gymtracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Locale;
import java.io.PrintWriter;

/**
 * Class for handling file operations
 */
public class FileHandler {
    /**
     * parses the saved data from a file into a list of objects
     * @param <T>: type of the list's objects
     * @param fileName: path to the file
     * @param classForParsing: the type of the lists objects
     * @return the parsed list of objects
     */
    public <T> ArrayList<T> parseFile(String fileName, Class<T> classForParsing) {
        ArrayList<T> parsedObjects = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            T parsed = classForParsing.getDeclaredConstructor().newInstance();

            String line = reader.readLine();
            while (line != null) {
                if (line.equals("end")) {
                    parsedObjects.add(parsed);
                    parsed = classForParsing.getDeclaredConstructor().newInstance();
                    line = reader.readLine();
                    continue;
                }

                String[] parts = line.split(":");
                String name = parts[0];
                String value = parts[1];
                Field attribute = classForParsing.getField(name);
                boolean isList = false;
                Class attributeType = attribute.getType();

                if (value.charAt(0) == '[' && value.charAt(value.length()-1) == ']') {
                    value = value.substring(1, value.length()-1);
                    isList = true;
                    ParameterizedType listType = (ParameterizedType) attribute.getGenericType();
                    attributeType = (Class) listType.getActualTypeArguments()[0];
                }

                if (isList) {
                    attribute.set(parsed, parseValues(value, attributeType));
                }
                else {
                    attribute.set(parsed, parseValues(value, attributeType).get(0));
                }

                line = reader.readLine();
            }
        }
        catch (FileNotFoundException e) {
            System.out.println(
                "No previous tracking data found for type " + classForParsing.getSimpleName() + ".");
        }
        catch (NumberFormatException e) {
            System.err.println(
                "Incorrect format for number in file. Unable to read tracking data.");
        }
        catch(Exception e) {
            System.err.println("Unable to read file: " + e);
        }

        return parsedObjects;
    }

    /**
     * writes an object's information to a file
     * @param <T>: the type of the object to be written
     * @param fileName: the path to the file
     * @param objectToBeWritten: the object to be written to the file
     * @return whether the operation succeeded
     */
    public <T> boolean writeToFile(String fileName, T objectToBeWritten) {
        Field[] attributes = objectToBeWritten.getClass().getFields();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            for (Field attribute : attributes) {
                String line = attribute.getName() + ":";
                if (attribute.get(objectToBeWritten) instanceof List) {
                    line += "[";
                    for (var element : ((List<Object>) attribute.get(objectToBeWritten))) {
                        line += setUpForWriting(element) + ",";
                    }
                    line = line.substring(0, line.length()-1);
                    line += "]";
                }
                else {
                    line += setUpForWriting(attribute.get(objectToBeWritten));
                }
                writer.write(line);
                writer.newLine();
            }
            writer.write("end");
            writer.newLine();
            return true;
        }
        catch(Exception e) {
            System.err.println("Unable to write to file: " + e);
            return false;
        }
    }

    /**
     * completely empties a file's contents
     * @param fileName: the path to the file
     * @return whether the operation succeeded
     */
    public boolean clearFile(String fileName) {
        try (PrintWriter clearer = new PrintWriter(fileName)) {
            return true;
        }
        catch (FileNotFoundException e) {
            System.out.println("No file found.");
            return false;
        }
        catch (Exception e) {
            System.err.println("Unable to clear file: " + e);
            return false;
        }
    }

    /**
     * parses a objects from string format
     * @param <T>: the type of the object to be returned
     * @param values: the string to be parsed
     * @param type: the type of the object to be returned
     * @return list of parsed objects
     * @throws NumberFormatException, if values for number fields cannot be parsed as numbers
     */
    private <T> ArrayList<T> parseValues(String values, Class<T> type) throws NumberFormatException {
        String[] valueArray = values.split(",");
        ArrayList<T> result = new ArrayList<>();

        for (String value : valueArray) {
            if (type == int.class) {
                result.add((T) Integer.valueOf(value));
            }
            else if (type == float.class) {
                result.add((T) Float.valueOf(value));
            }
            else if (type == String.class) {
                result.add((T) value.substring(1, value.length()-1));
            }
        }
        return result;
    }

    /**
     * returns the value as string in appripriate format for writing to file
     * @param <T>: type of the value 
     * @param value: the value to be formatted
     * @return value as string in correct format
     */
    private <T> String setUpForWriting(T value) {
        if (value.getClass() == Integer.class) {
            return String.valueOf(value);
        }
        else if (value.getClass() == Float.class) {
            return String.format(Locale.US, "%.2f", value);
        }
        else if (value.getClass() == String.class) {
            return "\"" + value + "\"";
        }
        else {
            return "";
        }
    }
}
