package gymtracker;

public class Set {
    public String date;
    public String exercise;
    public float weight;
    public int reps; 

    @Override
    public String toString() {
        return exercise + " " + date;
    }
}
