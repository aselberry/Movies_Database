import java.util.*;
import java.util.regex.*;
import java.io.*;
import java.nio.file.*;


public class Handler {
    public static final String EXIT = "exit";
    Scanner scanner = new Scanner(System.in);

    public List<Movie> movies;
    public Map<String, Person> people;
    public List<Person> listNewPeople;
    Handler() {
        this.movies = new ArrayList<>();
        this.listNewPeople = new ArrayList<>();
    }


    public List<Movie> getMovies(){
        return this.movies;
    }


    public List<Movie> ReadFileAndParseToObjects(Path filePath){
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");
                String[] directorData = columns[1].split("-");
                Person director = new Person(directorData[0].trim(), Integer.parseInt(directorData[1].trim()));
                String[] tuple = (columns[4].split(","));
                List<Person> actors = new ArrayList<>();
                for(int i=0; i< tuple.length; i++){
                    String[] actorsData = tuple[i].split("-");
                    Person actor = new Person(actorsData[0].trim(), Integer.parseInt(actorsData[1].trim()));
                    actors.add(actor);

                }
                Movie movie = new Movie(columns[0], director,Integer.parseInt(columns[2]), Integer.parseInt(columns[3]), actors);
                movies.add(movie);
            }

            return movies;

        } catch (FileNotFoundException e) {
            System.err.println("File cannot be opened! ");
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            System.err.println("Something is wrong! " + e.getMessage());
        }
        return null;
    }

    public static void writeMoviesToFile(List<Movie> movies, Path directoryPath) {
        try {
            Files.createDirectories(directoryPath);

            for (Movie movie : movies) {
                String fileName = movie.title.replaceAll("\\s+", "_") + ".txt";
                Path filePath = directoryPath.resolve(fileName);

                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write(movie.title + ";" +
                            formatPerson(movie.director) + ";" +
                            movie.releaseYear + ";" +
                            convertToTimeFormat(movie.lengthInMinutes) + ";");

                    List<Person> actors = movie.actors;
                    for (Person actor : actors) {
                        writer.write(formatPerson(actor));
                        if (actors.indexOf(actor) < actors.size() - 1) {
                            writer.write(",");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatPerson(Person person) {
        return person.name + "-" + person.birthYear;
    }

    public void listMoviesBasedOnFlags(String[] tokens, List<String> flags) {


            boolean verbose = flags.contains("-v");
            boolean titleFilter = flags.contains("-t");
            boolean directorFilter = flags.contains("-d");
            boolean actorFilter = flags.contains("-a");
            boolean lengthAscending = flags.contains("-la");
            boolean lengthDescending = flags.contains("-ld");

            List<Movie> filteredMovies = new ArrayList<>(movies);

            if(verbose && titleFilter || verbose && actorFilter || verbose && directorFilter){
                String regex = tokens[Arrays.asList(tokens).indexOf("-t") + 1].replace("\"", "");
                if(isValidRegex(regex)){
                    listMoviesWithStarring(movies);
                }

            }else if(verbose && (!titleFilter) && (!actorFilter) && (!directorFilter)){
                listMoviesWithStarring(movies);
            }

            if (titleFilter) {
                String titleKeyword = tokens[Arrays.asList(tokens).indexOf("-t")+1].replace("\"", "").trim();
                System.out.println(titleKeyword);
                if(isValidRegex(titleKeyword)){
                    Pattern pattern = Pattern.compile(".*" + titleKeyword + ".*", Pattern.CASE_INSENSITIVE);
                    Iterator<Movie> iterator = filteredMovies.iterator();
                    while (iterator.hasNext()) {
                        Movie movie = iterator.next();
                        Matcher matcher = pattern.matcher(movie.title);
                        if (!matcher.find()) {
                            iterator.remove();
                        }
                    }

                    if(filteredMovies.isEmpty()){
                        System.out.println("No Movie with this title!");
                    }else{
                        printMovies(filteredMovies);
                        System.out.println("Filtering by title has been done succesfully!");
                    }
                }else{
                    System.err.print("Invalid Regex!");
                    return;
                }


            }

            if (directorFilter) {
                String directorRegex = tokens[Arrays.asList(tokens).indexOf("-d") + 1].replace("\"", "");
                if(isValidRegex(directorRegex)){
                    Pattern pattern = Pattern.compile(".*" + directorRegex + ".*", Pattern.CASE_INSENSITIVE);
                    Iterator<Movie> iterator = filteredMovies.iterator();
                    while (iterator.hasNext()) {
                        Movie movie = iterator.next();
                        Matcher matcher = pattern.matcher(movie.director.name);
                        if (!matcher.find()) {
                            iterator.remove();

                        }
                    }

                    if(filteredMovies.isEmpty()){
                        System.out.println("No diretor with this name!");
                    }else{
                        printMovies(filteredMovies);
                        System.out.println("Filtering by directors has been done succesfully!");
                    }
                }else{
                    System.err.print("Invalid regex! It should start with quotation marks!");
                    return;
                }

            }
            if (actorFilter) {
                String actorsRegex = tokens[Arrays.asList(tokens).indexOf("-d") + 1].replace("\"", "").trim();
                if(isValidRegex(actorsRegex)){
                    Pattern pattern = Pattern.compile(".*" + actorsRegex + ".*", Pattern.CASE_INSENSITIVE);
                    Iterator<Movie> iterator = movies.iterator();
                    boolean matchFound = false;
                    List<Movie> actorBasedFilteredMovies = new ArrayList<>();
                    while (iterator.hasNext()) {
                        Movie movie = iterator.next();
                        for (Person actor : movie.actors) {
                            Matcher matcher = pattern.matcher(actor.name);
                            if (matcher.find()) {
                                matchFound = true;
                                actorBasedFilteredMovies.add(movie);
                            }
                        }

                    }

                    if(!matchFound){
                        System.out.println("No diretor with this name!");
                    }else{
                        printMovies(actorBasedFilteredMovies);
                        System.out.println("Filtering by directors has been done succesfully!");
                    }
                }else{
                    System.err.print("Invalid Regex!");
                    return;
                }

            }

            if (lengthAscending || lengthDescending) {
                if (lengthDescending) {
                    filteredMovies.sort(Comparator.comparingInt(Movie::getLengthInMinutes).reversed());
                } else {
                    filteredMovies.sort(Comparator.comparingInt(Movie::getLengthInMinutes));
                }
                printMovies(filteredMovies);

            }


    }

    public boolean isValidRegex(String regex){
        if(regex.startsWith("\"") && regex.endsWith("\"")){
            System.out.println(regex);
            return true;
        }
        return false;
    }

    public void listMovies(List<Movie> movies){
        for(Movie movie : movies){
            System.out.println(movie.title + " by " + movie.director.name + " in " + movie.releaseYear + ", " + convertToTimeFormat(movie.lengthInMinutes));
        }
    }

    public void listMoviesWithStarring(List<Movie> movies){
        for(Movie movie : movies){
            System.out.println(movie.title + " by " + movie.director.name + " in " + movie.releaseYear + " , " + movie.lengthInMinutes);
            System.out.println("Starring: ");
            for (Person actor : movie.actors) {
                System.out.println(actor.name + " at age " + (movie.releaseYear - actor.birthYear));
            }
            System.out.print("\n");
        }

    }

    public void addEntry(String[] tokens, List<String> flags, List<Movie> movies) {

        boolean isAddPerson = flags.contains("-p");
        boolean isAddMovie = flags.contains("-m");

        if (isAddPerson) {
            System.out.print("Name: ");
            String name = scanner.nextLine();
            System.out.print("Year of birth: ");
            String yearOfBirth = scanner.next();
            listNewPeople.add(new Person(name, Integer.valueOf(yearOfBirth)));
            System.out.println("Person has been added to Database successfully!");
        }

        if (isAddMovie) {
            System.out.print("Title: ");
            String title = scanner.next();
            int length = getAndValidateLength();
            Person director = getAndValidateDirector(movies);
            System.out.print("Year: ");
            int yearOfRelease = scanner.nextInt();
            System.out.println("Starring (give the actors data in the following format - ActorName LastName-BirthYear. eg: Asel Temiralieva-2004): ");
            List<Person> actors = getListOfActorsFromUser(movies);
            movies.add(new Movie(title, director, yearOfRelease, length, actors));
            System.out.println("Movie Has been added to Database successfully!");

        }
    }

    int getAndValidateLength() {
        final Pattern pattern = Pattern.compile("\\d\\d:\\d\\d", Pattern.CASE_INSENSITIVE);
        String length = "";
        while (true) {
            System.out.print("Length: ");
            length = scanner.next();
            if (pattern.matcher(length).matches()) {
                break;
            } else {
                System.out.println("- Bad input format (hh:mm), try again!");
            }
        }

        return convertToMinutes(length);
    }

    Person getAndValidateDirector(List<Movie> movies) {
        String director = "";
        Person newDirector;
        while (true) {
            System.out.print("Director: ");
            boolean flag = false;
            //scanner.nextLine();
            director = scanner.nextLine();
            for(Movie movie : movies){
                for(Person person : listNewPeople){
                    if(movie.director.name.equalsIgnoreCase(director) || person.name.equalsIgnoreCase(director)){
                        flag = true;
                        break;
                    }
                }

            }
            if (flag) {
                newDirector = new Person(director, 0);
                break;
            }else {
                System.out.println("- We could not find " + director + ", try again!");
            }
        }
        return newDirector;
    }

    List<Person> getListOfActorsFromUser(List<Movie> movies) {
        List<Person> actors = new ArrayList<>();
        while (true) {
            boolean isFound=false;
            scanner.nextLine();
            String input = scanner.nextLine();
            if(input.equalsIgnoreCase(EXIT)){
                break;
            }else{
                String[] parsedInput = input.split("-");
                if(parsedInput.length != 2){
                    System.out.println("Invalid Input! Expected: FirstName LastName-BirthYear");
                    continue;
                }
                String actorName = parsedInput[0].trim();
                int actorBirthYear = Integer.parseInt(parsedInput[1].trim());
                    for(Movie movie : movies){
                        for(Person actor : movie.actors){
                            for(Person person : listNewPeople){
                                if(actor.name.equals(actorName) || person.name.equals(actorName)){
                                    actors.add(new Person(actorName, actorBirthYear));
                                    isFound = true;
                                    break;
                                }else{
                                    System.out.println("We could not fin," + actorName + " please try again!");
                                }
                            }


                        }
                    }

            }

        }
        return actors;
    }

    int convertToMinutes(String timeString) {
        // Split the time string into hours and minutes
        String[] parts = timeString.split(":");

        // Parse hours and minutes
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);

        // Calculate the total minutes
        return hours * 60 + minutes;

    }

    public static String convertToTimeFormat(int totalMinutes) {
        if (totalMinutes < 0) {
            throw new IllegalArgumentException("Total minutes must be non-negative");
        }

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        return String.format("%02d:%02d", hours, minutes);
    }

    public void deletePerson(String[] token, List<Movie> movies) {
        if(token.length <= 1){
            System.err.println("We need parameter");
        }else{
            String personName = token[2] + " " + token[3];
            Person personToDelete = new Person("", 0);
            Boolean isFound=false;
            System.out.println(personName);

            for(Movie movie: movies){
                for(Person actor : movie.actors){
                    if(actor.name.equals(personName) || isAmongPeople(personName, listNewPeople)){
                        if((movie.director.name.equals(personName))){
                            System.out.println("Cannot delete a person who is a director of a movie.");
                            return;
                        }else{
                            personToDelete = actor;
                            isFound=true;
                        }
                    }else{
                        System.out.println("Cannot find the person in the database");
                        return;
                    }
                }
            }

            for(Movie movie : movies){
                if(isFound){
                    movie.actors.remove(personToDelete);
                }
            }

            if(listNewPeople.contains(personToDelete)){
                listNewPeople.remove(personToDelete);
            }
            System.out.println("Person deleted successfully.");
        }


    }

    boolean isAmongPeople(String actorName, List<Person> people){

        for(Person actor : people){
            if(actor.name.equals(actorName)){
                return true;
            }
        }

        return false;
    }

    public void printMovies(List<Movie> movies) {
            for (Movie movie : movies) {
                System.out.println("Title: " + movie.title);
                System.out.println("Director: " + movie.director);
                System.out.println("Year of Release: " + movie.director);
                System.out.println("Length: " + convertToTimeFormat(movie.lengthInMinutes));

                System.out.print("Starring: ");
                for (Person actor : movie.actors) {
                    System.out.println(actor.name + "; ");
                }
                System.out.print("\n");
            }
    }
}