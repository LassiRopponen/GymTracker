package gymtracker;

import java.util.ArrayList;

/**
 * Class for storing exercise information as an object
 */
public class Exercise {
    public String name;
    public ArrayList<String> primaryMuscles;
    public ArrayList<String> secondaryMuscles;
    public String type;

    @Override
    public String toString() {
        return name;
    }
}
