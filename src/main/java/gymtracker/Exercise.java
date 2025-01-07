package gymtracker;

import java.util.ArrayList;

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
