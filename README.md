**About**

A Java console application developed to list and alter a database of movies, directors and actors. It uses a file handling to retrieve and modify data. 

**Tools and Technology Stack**
- Programming Language: Java
- Topics: OOP, Regular Expressions, Hashmaps
- Data Storage: File (to be improved and switched to databases)

**Detailed description of the database:**
We know the following facts about a movie:
	- Its title
	- Its director
	- Its release year
	- Some of its actors
	- Its length (given in minutes)
It is possible to have different movies with the same title, but movies with the same title and same director should be treated as the same.
An actor can be a director and a director can be an actor. We know the birth year of each actor and director. We can indentify these people with their name.

On startup it tries to connect to the database and informs the user about its success. In case of failure, terminates the program.
After successful connection, the program should support the commands provided on standard input.

**Guidlines to use an app:**
- If the user enters "l" it lists the movies (each on a separate line), following this format:
 <title> by in , Starring: - at age - at age - If the user adds the "-t" switch, after that between quotes a regex can be given to match the title with. 

Example: l -t "Die .*" -v - 
That will print Die Hard 1. and Die Hard 2. with its starring.

- The user can also add a -d switch. After that a regex should be given to filter the results by the movie's director.

- We can add an -a switch as well. After that we can give a regex. It should list all of the movies that have at least one actor whose name matches with the regex -la switch lists the movies with ascending order by their length -ld switch lists the movies with descending order by their length.

By default movies are listed alphabetically by their title. If one of the ordering switches present, the same length movies should be ordered alphabetically by their titles.

Switches can be in any order. In case of parametrized switches, the parameter should be right after the switch. Check and handle any incorrect query format (example: both -la and -ld are present, no parameter after -d, and regexes are not quoted or corrupted). Inform user about the bad input format.

- If the user enters "a" then they can add new entries to the database
	- "a -p" allows to add new people. After entering this the user will be prompted to give a name and the year of birth for the person.
	- "a -m" allows to add new movie. After entering this the user will be prompted to give a title, then give the length in hh:mm format, then name the director, then give the year of release, finally name the actors. Actors are given line by line. When an actor (or the director) is not available in the database, print an error message. Do this until a correct name is entered. In case of actors "exit" is a special value, this will finalize the record and add it to the database.
	- Output should be something like this:
		> a -m
		> Title: Star Wars 1.
		> Length: 13
		> - Bad input format (hh:mm), try again!
		> Length: 02:16
		> Director: George Luca
		> - We could not find "George Luca", try again!
		> George Lucas
		> Released in: 1999
		> Starring: Liam Neeson
		> Ewan McGregor
		> Natalie Portman
		> exit

- With "d -p" users can delete people from the database. After -p switch, a string should be added. If it is the exact name of a person in the database, deletes them, and also deletes them from every movie they starred in. If they are a director in a movie, then we cannot delete them and the program should notify the user about this. The program should also notify the user if the person cannot be found in the database.
