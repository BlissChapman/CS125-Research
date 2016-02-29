import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Simple command line tools for using and testing this application.
 * This UI uses Unix-like commands to allow the addition of  several courses
 * to this application and test the functionalities we've added so far. So
 * far this is probably 15% complete.
 * 
 * TODO implement functions for all listed commands
 * 
 * @author CS125-Research
 *
 */
public class SimpleUI {

	private Lecture lecPointer = null;
	private ProtoApp coursePointer = null;
	private Student studentPointer = null;
	private GraphTools grapherPointer = null;
	private Scanner stdin = new Scanner(System.in); //Reads from standard input
	
	
	/*
	 * Maps strings to ProtoApp instances. For example, if I have a course
	 * CS125, here are some common things I could do.
	 * 
	 * courseMap.put("CS125", courseMap);
	 * courseMap.containsKey("CS125") == true;
	 * coursePointer = courseMap.get("CS125"); 
	 */
	private TreeMap<String, ProtoApp> courseMap = new TreeMap<String,ProtoApp>();
	
	public enum MenuType { COURSES, COURSE, LECTURE, STUDENT, GRAPH};
	
	private MenuType menu = MenuType.COURSES;
	
	/*
	 * Association between menus and the specific command lists that
	 * correspond to each menu type.
	 */
	private static HashMap<MenuType, String> menus = new HashMap<MenuType, String>();
	static{
		//TODO alphabetize these commands and replace all tabs with spaces
		menus.put(MenuType.COURSES,
			"COURSE CREATION MENU:\n\n" +
			"mkdir $ <name>             Creates new course.\n" +
			"ls                         Display all existing courses.\n" +
			"cd $ <name>                Go to an existing course.\n");
		menus.put(MenuType.COURSE,
			"COURSE: %s\nDESCRIPTION: %s\n\n" +
			"mkdir lec $ <mm dd yyyy>   Add new lecture.\n" +
			"mkdir stud $ netID         Add new student.\n" +
			"edit title $ <title>       Change course title.\n" +
			"edit info $ <info>         Change course description.\n" +
			"ls                         Show all students and lectures.\n" +
			"ls lec                     Show all lectures.\n" +
			"ls stud                    Shows all students.\n" +
			"cd lec $ <lec#>            Select lecture.\n" +
			"cd stud $ <studID>         Select student.\n" +
			"cd                         Go back to course list.\n" +
			"import lecs $ <file>       Use file to add new lectures.\n" +
			"import entries $ <file>    Use file to add new feedback.\n" +
			"import studs $ <file>      Use file to add new students.\n" +
			"rm                         Remove this course.\n");
		menus.put(MenuType.LECTURE,
			"LECTURE %d%s\nDATE: %s\n\n" +
			"ls                         Shows all feedback for this lecture.\n" +
			"cd stud$ <studID>          Select student.\n" +
			"cd                         Go back to the course page.\n" +
			"stat                       Obtain statistical summary of lecture.\n" +
			"edit title $ <title>       Change lecture title.\n" +
			"edit info $ <info>         Change lecture description.\n" +
			"graph                      Graph lecture data.\n" +
			"graph rules                Modify weighting rules on graph.\n");
		menus.put(MenuType.STUDENT,
			"STUDENT MENU (Student #%d):\n" +
			"cd                         Go back to the course page.\n" +
			"ls                         Show all feedback given by student.\n" +
			"stat                       Obtain statistical summary of student.\n" +
			"rm                         Purge this student from the course.\n");
		menus.put(MenuType.GRAPH,
			"GRAPH RULE MENU:\n" +
			"ls						    Show all graphing constraints.\n" +
			"use <rule#>			    Switch to specific graph constraint.\n" +
			"edit <rule#>			    Change parameters of specific rule.\n" +
			"cd						    Go back to the lecture page.\n");
	};
	
	/**
	 * Primary runner for this class. Reads in commands from standard input
	 * and relays them to more specific parsers for each menu (unless the
	 * commands are universal to all menus, in which case it will directly
	 * call the appropriate function).
	 */
	void parseLine(){
		System.out.print(">>");
		String input = stdin.nextLine().trim();
		if (input.equals(""))
			parseLine();
		String[] pieces = input.split("\\$");
		String[] parsedPieces = new String[2];
		parsedPieces[0] = pieces[0].trim().toLowerCase();
		parsedPieces[1] = pieces.length > 1 ? 
				pieces[1].trim() : "";
		String cmd = parsedPieces[0];
		String arg = parsedPieces[1];
		if (cmd.equals("quit"))
			return;
		if (cmd.equals("help"))
			displayMenu();
		else if (cmd.equals("ls"))
			list();
		else if (cmd.equals("cd") && arg.isEmpty())
			closeMenu();
		else{
			echo(parsedPieces[0], parsedPieces[1]);
			switch (menu){
			case COURSES:
				parseCoursesLine(parsedPieces); break;
			case COURSE:
				parseCourseLine(parsedPieces); break;
			case LECTURE:
				parseLectureLine(parsedPieces); break;
			case STUDENT:
				parseStudentLine(parsedPieces); break;
			case GRAPH:
				parseGraphLine(parsedPieces); break;
			}
		}
		parseLine();
	}
	
	/**
	 * Activated by the command "cd" with no arguments. Simply changes the menu
	 * to the one at a higher level. So in a specific course, this takes you
	 * back to the course explorer. In a specific lecture or specific student,
	 * this takes you back to the course containing that lecture or student.
	 * In the graph menu, takes you back to the lecture from which you tried
	 * to change the graph tools.
	 */
	void closeMenu(){
		switch (menu){
		case COURSE:
			menu = MenuType.COURSES; break;
		case LECTURE:
		case STUDENT:
			menu = MenuType.COURSE; break;
		case GRAPH:
			menu = MenuType.LECTURE; break;
		default:
			invalidCommand("cd");
		}
	}
	
	
	/*
	 * METHODS USED IN THE COURSES MENU. THESE ARE VERY LIMITED.
	 */
	
	
	/**
	 * Specific parser for the Courses menu. Only accepts commands that are
	 * (exclusively) listed under the course menu like cd $ arg, 
	 * mkdir lec $ arg, etc.
	 * 
	 * @param input The partially parsed {cmd, arg} inputed by the user.
	 */
	void parseCoursesLine(String[] input){
		if (input[0].equals("mkdir"))
			addCourse(input[1]);
		else if (input[0].equals("cd"))
			changeToCourse(input[1]);
		else
			invalidCommand(input[0]);
		//parseLine();
	}
	
	/**
	 * Displays all courses in this application. States "No courses." if
	 * the application has no courses.
	 */
	public void displayCourses(){
		if (courseMap.isEmpty())
			System.out.println("No courses.");
		else{
			System.out.println("List of courses:\n");
			int i = 1;
			for (Entry<String, ProtoApp> course : courseMap.entrySet())
				System.out.println(i++ + ": " + course.getKey());
		}
	}
	
	/**
	 * Selects a course in the courses menu and switches to the course menu
	 * for that course. Gives an error message if the course name passed in
	 * does not match any existing course.
	 * 
	 * @param courseName The name of the course.
	 */
	void changeToCourse(String courseName){
		if (!courseMap.containsKey(courseName))
			System.out.printf("No course named \"%s\". Enter \"ls\" to see " 
					+ "all courses.\n", courseName);
		else{
			coursePointer = courseMap.get(courseName);
			menu = MenuType.COURSE;
		}
	}
	
	/**
	 * Adds a new course of the given name. Empty strings will cause
	 * the method to print an error message. The method will not allow
	 * the user to add a course with a name matching an existing course.
	 * The method also will repeatedly prompt the user to enter a student
	 * capacity greater than or equal to 5.
	 * 
	 * @param name The name of the course to add.
	 */
	void addCourse(String name){
		if (name.length() == 0)
			System.out.println("Missing course name argument.");
		else if (courseMap.containsKey(name))
			System.out.printf("Course named \"%s\" already exists.\n", name);
		else{
			System.out.print("Enter the student capacity of this course: ");
			int cap = -1;
			while (cap < 5){
				try{
					int trialcap = stdin.nextInt();
					if (trialcap < 5){
						System.out.print("Enter a capacity greater than 4: ");
					}else{
						cap = trialcap;
						ProtoApp newone = new ProtoApp(cap);
						courseMap.put(name, newone);
						System.out.printf("Added course with capacity of " +
							"%d: \"%s\"\n", cap, name);
						stdin.nextLine();
					}
				}catch (Exception e){
					System.out.print("Invalid capacity. Try again: ");
					stdin.nextLine();
				}
			}
		}
	}
	
	/*
	 * BELOW ARE THE INDIVIDUAL COURSE METHODS. THESE METHODS ARE USED TO
	 * MODIFY A SPECIFIC COURSE. THEY WILL ONLY BE CALLED WHEN THE
	 * MENU IS SET TO MENUTYPE.COURSE.
	 * 
	 * TODO Improve several methods and work on the unimplemented ones.
	 */
	
	/**
	 * This method is a specialized parse method for the Course menu. It
	 * must handle the most commands of all the menus.
	 * 
	 * @param input The parsed command-argument string array. The first
	 *              element is the command, the second the argument.
	 */
	void parseCourseLine(String[] input){
		if (input[0].equals("ls lec"))
			displayLectures();
		else if (input[0].equals("ls stud"))
			displayStudents();
		else if (input[0].equals("mkdir lec"))
			addLecture(input[1]);
		else if (input[0].equals("mkdir stud"))
			addStudent(input[1]);
		else if (input[0].equals("rm"))
			removeCourse();
		else if (input[0].equals("import lecs"))
			toDo(); //TODO make a method for this
		else if (input[0].equals("import studs"))
			toDo(); //TODO make a method for this
		else if (input[0].equals("import entries"))
			toDo(); //TODO make a method for this
		else if (input[0].equals("cd stud"))
			changeToStudent(input[1]); //DONE
		else if (input[0].equals("cd lec"))
			changeToLecture(input[1]); //DONE
		else if (input[0].equals("edit title"))
			coursePointer.courseTitle = input[1]; //DONE
		else if (input[0].equals("edit info"))
			coursePointer.courseInfo = input[1]; //DONE
		else
			invalidCommand(input[0]);
	}
	
	/**
	 * Displays all students in this application. States "No courses." if
	 * the application has no students.
	 */
	public void displayLectures(){
		if (coursePointer.lectures.isEmpty()){
			System.out.println("No lectures added.");
			return;
		}
		System.out.println("LECTURES: ");
		for (Lecture lec : coursePointer.lectures)
			System.out.printf("    %d: %s\n",
					lec.lectureNumber, lec.getDate().toString());
	}
	
	/**
	 * Adds a lecture to the current course. Uses ProtoApp.addLecture(String).
	 * If the input string cannot be parsed as a valid Java Date object,
	 * print an error message and return.
	 * 
	 * @TODO Finish implementing this. Currently it makes a lecture at the time
	 *       this function was called. It should actually parse the day
	 *       
	 * @param day A string in the format "mm dd yy" that will be used to
	 *            determine the date of the lecture. The time of day can be
	 *            set arbitrarily.
	 */
	void addLecture(String day){
		if (day.length() == 0)
			System.out.println("Missing lecture date argument.");
		//else if (courseMap.containsKey(name))
			//System.out.printf("Course named \"%s\" already exists.\n", name);
		else{
			toDo("does not actually parse date and simply creates a Lecture at " +
					"the time the command is used."); //TODO Use SimpleDateFormat
			Date newDate = new Date();
			if (coursePointer == null) //Catches a strange bug, possibly
				throw new UnsupportedOperationException("What???");
			coursePointer.addLecture(newDate);
		}
	}
	
	
	/**
	 * Displays all students in the current course. Says "No students added"
	 * if the course has no students.
	 */
	private void displayStudents(){
		if (coursePointer.students.size() == 0){
			System.out.println("No students added.");
			return;
		}
		System.out.println("STUDENTS: ");
		for (Student person : coursePointer.students)
			System.out.printf("    #%d\n", person.getID());
	}
	
	/**
	 * Adds a student to the current course using ProtoApp.addStudent().
	 * Make sure to handle exceptions thrown by that method in cases
	 * of netID duplication or full capacity.
	 * 
	 */
	void addStudent(String netID){
		try{
			coursePointer.addStudent(netID);
			System.out.printf("Successfully added \"%s\"\n", netID);
		}
		catch(IndexOutOfBoundsException e){
			System.out.printf("This course has reached its maximum capacity: "
					+ "%d\n", coursePointer.converter.capacity());
		}
		catch(IllegalArgumentException e){
			System.out.printf("NetID \"%s\" is already present in this " +
					"course.\n", netID);
		}
	}
	
	/**
	 * Deletes the current course from the courses menu and then moves back to
	 * the Courses menu. This is a fairly inelegant way of doing things.
	 */
	void removeCourse(){
		if(!requestYesNo("Are you sure you want to remove this course? Y/N: ")){
			System.out.println("Remove cancelled.");
			return;
		}
		for (Map.Entry<String, ProtoApp> elem : courseMap.entrySet())
			if (elem.getValue() == coursePointer){
				courseMap.remove(elem.getKey());
				System.out.printf("Removed %s\n", elem.getKey());
			}
		menu = MenuType.COURSES;
	}
	
	/**
	 * Changes the menu type to Student and focus on the Student whose
	 * code matches the code passed in. First parse the string as an integer
	 * and use Roster.get(int) to obtain the Student with the matching netID.
	 * 
	 * Give error messages if the string passed in cannot be parsed as an int,
	 * or if the Student is not found in the Roster.
	 * 
	 * @param code The code of the Student to switch to.
	 */
	public void changeToStudent(String code){
		try{
			int num = Integer.parseInt(code);
			studentPointer = coursePointer.students.get(num);
			if (studentPointer == null)
				System.out.printf("Student %d not found in the roster.\n",
						num);
			else
				menu = MenuType.STUDENT;
		}catch (NumberFormatException e){
			System.out.println("Please enter an integer student code.");
		}
	}
	
	
	/**
	 * Changes the menu type to Lecture and focus on the Lecture whose index in
	 * the collection of existing Lectures matches the number passed in. First
	 * parse the string as an integer and then use ProtoApp.lectures.get(int)
	 * to retrieve the right lecture.
	 * 
	 * Give error messages if the string passed in cannot be parsed as an int,
	 * or if the number is out of bounds.
	 * 
	 * @TODO Implement
	 */
	public void changeToLecture(String lecNo){
		toDo();
	}
	
	/*
	 * BELOW ARE ALL THE LECTURE METHODS. THEY ARE USED TO EXTRACT INFORMATION
	 * FROM A PARTICULAR LECTURE.
	 * 
	 * TODO Implement everything.
	 */
	
	
	/**
	 * This method is a specialized parse method for the Lecture menu. 
	 * 
	 * @param input The parsed command-argument string array. The first
	 *              element is the command, the second the argument.
	 */
	public void parseLectureLine(String[] input){
		if (input[0].equals("cd stud"))
			changeToStudent(input[1]);
		else if (input[0].equals("stat"))
			toDo(); //TODO implement
		else if (input[0].equals("edit title"))
			lecPointer.setTitle(input[1]);
		else if (input[0].equals("edit info"))
			lecPointer.setInfo(input[1]);
		else if (input[0].equals("graph"))
			toDo(); //TODO implement
		else if (input[0].equals("graph rules"))
			toDo(); //TODO implement
		else
			invalidCommand(input[0]);
	}
	
	/**
	 * Displays all the PeerInteraction objects in the particular lecture.
	 * 
	 * @TODO Decide how to implement this. Current representation is an
	 *       eyesore.
	 */
	private void displayInteractions(){
		toDo("Improve format");
		for (PeerInteraction elem : lecPointer)
			System.out.println(elem);
	}
	
	/*

	"LECTURE %d%s\nDATE: %s\n\n" +
	"ls                     Shows all feedback for this lecture.\n" +
	"cd $ <studID>          Select student.\n" +
	"cd                     Go back to the course page.\n" +
	"stat                   Obtain statistical summary of lecture.\n" +
	"edit title $ <title>   Change lecture title.\n" +
	"edit info $ <info>     Change lecture description.\n" +
	"graph                  Graph lecture data.\n" +
	"graph rules            Modify weighting rules on graph.\n");
	
	*/
	
	
	/*
	 * BELOW ARE ALL THE STUDENT METHODS. THEY ARE USED TO EXTRACT INFORMATION
	 * FROM A PARTICULAR STUDENT.
	 * 
	 * @TODO Implement everything
	 */
	
	/**
	 * This method is a specialized parse method for the Student menu. 
	 * 
	 * @param input The parsed command-argument string array. The first
	 *              element is the command, the second the argument.
	 */
	public void parseStudentLine(String[] input){
		if (input[0].equals("stat"))
			toDo(); //TODO Implement
		else if (input[0].equals("rm"))
			toDo(); //TODO Implement
		else
			invalidCommand(input[0]);
	}
	
	/*
	"STUDENT MENU (Student #%d):\n" +
	"ls						    Show all feedback given by student.\n" +
	"stat					    Obtain statistical summary of student.\n" +
	"rm						    Purge this student from the course.\n");
	*/
	
	/*
	 * BELOW ARE ALL THE GRAPH METHODS. THEY ARE USED TO CHANGE THE PARAMETERS
	 * OF INDIVIDUAL GRAPH CONSTRAINTS AND TO CHANGE THE TYPE OF CONSTRAINT
	 * USED. NOTE THAT GRAPHERS ARE GLOBAL TO EACH COURSE INSTANCE.
	 * 
	 * TODO Implement everything
	 */
	
	public void parseGraphLine(String[] input){
		if (input[0] == "use")
			toDo(); //TODO implement
		else if (input[0] == "edit")
			toDo(); //TODO Implement
		else
			invalidCommand(input[0]);
	}
	
	/**
	 * Displays the different weighters available for making graphs
	 * alongside numbers.
	 */
	private void displayWeighters(){
		System.out.print("1. Default.\n" + 
	                     "2. Filter by student rating standard deviation\n" +
				         "3. Filter by student rating mean\n" +
	                     "4. Prioritize students without many 5s and 10s\n" +
				         "5. Filter by date range\n");
	}
	
	/*
	 * BELOW ARE ALL THE DISPLAY METHODS. THEY DO NOT PROMPT ANYTHING FROM
	 * STANDARD INPUT AND MERELY PRINT THINGS OUT TO STANDARD OUTPUT. MOST
	 * DO NOT ACCEPT ANY ARGUMENTS.
	 * 
	 */
	
	/**
	 * Variadic method that is called in response to the command "help". This
	 * method is exclusively called by the parseLine() function. The method
	 * simply prints out the appropriate menu string associated with the
	 * current menu type.
	 */
	public void displayMenu(){
		switch(menu){
		case COURSE:
			System.out.printf(menus.get(menu), coursePointer.courseTitle, 
					coursePointer.courseInfo);
			break;
		case LECTURE:
			System.out.printf(menus.get(menu), lecPointer.lectureNumber, 
					lecPointer.getDate().toString());
			break;
		case STUDENT:
			System.out.printf(menus.get(menu), studentPointer.getID());
			break;
		case GRAPH:
			System.out.println(menus.get(menu));
			break;
		default:
			System.out.println(menus.get(menu)); break;
		}
	}
	
	
	/**
	 * Variadic method that is called in response to the command "ls". This
	 * method is called in the parseLine() method exclusively. What this
	 * actually displays is dependent upon the menu type.
	 */
	public void list(){
		switch (menu){
		case COURSES:
			displayCourses();
			break;
		case COURSE:
			displayLectures();
			displayStudents();
			break;
		case LECTURE:
			displayInteractions();
			break;
		case STUDENT:
			toDo();
			break;
		case GRAPH:
			displayWeighters();
			break;
		default:
			break;
		}
	}
	
	
	/*
	 * HELPER FUNCTIONS, MOSTLY USED TO REDUCE REDUNDANT PRINT STATEMENTS.
	 * 
	 */
	
	/**
	 * States that the command passed in does not exist.
	 * @param cmd The invalid command that does not exist.
	 */
	
	private void invalidCommand(String cmd){
		System.out.printf("Invalid command \"%s\".\nEnter \"help\" for a list of valid " +
				"commands.\n", cmd);
	}
	
	/**
	 * Helper method that prompts a user to enter Y/N and converts the 
	 * response to a boolean. The method will do this until a valid
	 * input is received.
	 * 
	 * @param message The initial prompt message given to the user.
	 * @return True if the user entered Y, false if he entered N
	 */
	private boolean requestYesNo(String message){
		String input = "";
		System.out.print(message);
		while(true){
			input = stdin.nextLine().toLowerCase();
			if (!(input.equals("y") || input.equals("n"))){
				System.out.print("Invalid response. Enter Y/N: ");
			}
			else
				break;
		}
		return input.equals("y");
	}
	
	private void toDo(){
		System.out.println("Not yet implemented.");
	}
	
	private void toDo(String details){
		System.out.printf("Not fully implemented: %s\n", details);
	}
	
	/**
	 * Prints out a command and argument separated by a $ character. This may
	 * only be used in debugging.
	 * 
	 * @param cmd The command.
	 * @param arg The argument.
	 */
	private void echo(String cmd, String arg){
		System.out.printf("ECHO: %s $ %s\n", cmd, arg);
	}
	
	public static void main(String[] args){
		SimpleUI runner = new SimpleUI();
		runner.parseLine();
	}
}
