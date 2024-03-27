import java.util.*;
import java.util.regex.*;
import java.util.Map;
import java.util.ArrayList;

// TODO: add class details
public class Movie {

    // TODO: make them private and add getters and setters
    String title;
    Person director;
    int releaseYear;
    List<Person> actors = new ArrayList<>();
    int lengthInMinutes;

    public int getLengthInMinutes() {
        return lengthInMinutes;
    }

    Movie(String title, Person director, int releaseYear, int lengthInMinutes, List<Person> actors) {
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.lengthInMinutes = lengthInMinutes;
        this.actors = actors;
    }

    @Override
    public String toString() {
        return title + " by " + director.name + " in " + releaseYear + ", " + lengthInMinutes + " minutes";
    }

    public String detailedString() {
        StringBuilder result = new StringBuilder(toString());
        result.append("\nStarring:");
        for (Person actor : actors) {
            result.append("\n\t- ").append(actor.name).append(" at age ").append(releaseYear - actor.birthYear);
        }
        return result.toString();
    }
}