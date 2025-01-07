package gymtracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Locale;

public class FileHandler {
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
        catch(Exception e) {
            System.out.println("Unable to read file: " + e);
        }

        return parsedObjects;
    }

    public <T> void writeToFile(String fileName, T objectToBeWritten) {
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
        }
        catch(Exception e) {
            System.out.println("Unable to write to file: " + e);
        }
    }

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
