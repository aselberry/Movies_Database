import java.util.*;
import java.util.regex.*;
import java.util.Map;
import java.util.ArrayList;

// TODO: add/modify class details
public class Person {

    // TODO: make them private and add getters and setters
    String name;
    int birthYear;

    Person(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }

    @Override
    public String toString() {
        return name + ", " + birthYear;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Person other = (Person) obj;
        return birthYear == other.birthYear && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, birthYear);
    }
}