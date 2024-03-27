import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.*;
import java.nio.file.Paths;
import java.nio.file.Path;


class Main {

    public static void main(String[] args) {


        Path filePath = Paths.get("C:\\Users\\User\\Desktop\\Movies_Database\\Movies_Database\\src\\file.txt");
        if (Files.exists(filePath)) {
            System.out.println("Connected to Database successfully!");
            Handler handler = new Handler();
            List<Movie> movies = handler.ReadFileAndParseToObjects(filePath);

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter a new command (or type 'exit' to quit): ");
                String userInput = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    System.out.println("Exiting the program.");
                    System.out.println("Do you want to save changes? Press Y/N");
                    String isSave = scanner.nextLine();
                    if(isSave.equalsIgnoreCase("Y")){
                        handler.writeMoviesToFile(movies, Path.of("file.txt"));
                        System.out.println("Changes have been saved successfully! ");
                        break;
                    }else{
                        System.out.println("Alright Good Bye!");
                        break;
                    }
                }

                String[] newArgs = userInput.split("\\s+");
                if((newArgs.length != 0) && (newArgs[0].equals("l") || newArgs[0].equals("a") || newArgs[0].equals("d"))){
                    processInput(newArgs, handler, movies);
                }else{
                    System.out.println("Please enter the valid command that starts from l, a, or d!");
                    continue;
                }
            }
            scanner.close();
        } else {
            System.err.println("File does not exist!");
        }

    }

    private static void processInput(String[] input, Handler handler, List<Movie> movies){
        String[] tokens = input.clone();
        ArrayList<String> flags = new ArrayList<>();
        String command = tokens[0];
        ArrayList<String> parameters = new ArrayList<>();

        for (int i = 1; i < tokens.length; i++) {
            if (tokens[i].startsWith("-")) {
                if (validateFlagFormat(command, tokens[i])) {
                    flags.add(tokens[i]);
                    if (flagRequiresParameter(command, tokens[i])) {
                        i++;
                        if (i < tokens.length) {
                            parameters.add(tokens[i]);
                        } else {
                            System.err.print("Error: Flag '" + tokens[i - 1] + "' requires a parameter. Try again!");
                            break;
                        }
                    }
                } else {
                    System.err.print("Error: Invalid flag format - " + tokens[i]);
                }
            } else {
                parameters.add(tokens[i]);
                break;
            }

        }

        if(flags.contains("-la") && flags.contains("-ld")){
            System.err.print("You cannot filter in ascending and descending order simulteneously! Try again!");
            return;
        }

        switch (command) {
            case "l":
                if(tokens.length == 1){
                    handler.listMovies(movies);
                }
                handler.listMoviesBasedOnFlags(Arrays.copyOfRange(tokens, flags.size() + 1, tokens.length), flags);
                break;
            case "a":
                handler.addEntry(Arrays.copyOfRange(tokens, flags.size() + 1, tokens.length), flags, movies);
                break;
            case "d":
                handler.deletePerson(tokens, movies);
                break;
            default:
                System.out.println("Invalid command");
        }
    }

    public static boolean validateFlagFormat(String command, String flag) {
        Scanner scanner = new Scanner(System.in);
        if(command.equals("l")){
            String[] lflags = {"-v", "-t", "-d", "-a", "-la", "-ld"};
            Arrays.sort(lflags);
            int index = Arrays.binarySearch(lflags, flag);
            if(index >= 0){
                return true;
            }
        }
        if(command.equals("a")){
            String[] lflags = {"-p", "-m"};
            Arrays.sort(lflags);
            int index = Arrays.binarySearch(lflags, flag);
            if(index >= 0){
                return true;
            }
        }

        if(command.equals("d")){
            String[] lflags = {"-p"};
            Arrays.sort(lflags);
            int index = Arrays.binarySearch(lflags, flag);
            if(index >= 0){
                return true;
            }
        }
        return false;
    }

    // Function to check if a flag requires a parameter
    public static boolean flagRequiresParameter(String command, String flag) {
        String[] possibleParamList = {"-t", "-d", "-a"};
        Arrays.sort(possibleParamList);
        int index = Arrays.binarySearch(possibleParamList, flag);
        if(index >= 0){
            return true;
        }
        return false;
    }
}

