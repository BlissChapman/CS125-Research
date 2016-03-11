import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

/**
 * Simple command line tools for using and testing this application.
 * This UI uses Unix-like commands to allow the addition of  several courses
 * to this application and test the functionalities we've added so far. So
 * far this is probably 30% complete.
 * 
 * TODO implement functions for all listed commands. The main things that need
 *      to be prioritized is importing files, as this will be the primary
 *      usage scenario in any real class. Displaying graph tools and creating
 *      interfaces for modifying them might take the most work out of all
 *      individual tasks.
 * 
 * @author CS125-Research
 *
 *
 * DESIGN PARADIGM:
 * 
 * -DO NOT COMMIT BROKEN CODE. Make one function at a time and test it until
 *     you are sure it works. In particular... see below.
 * -CATCH ALL EXCEPTIONS that might arise from invalid use of ProtoApp and
 *     related classes. Inform the user about an error and, if applicable, 
 *     allow her to fix it. DO NOT THROW ANY EXCEPTIONS. This application must
 *     be rigorous (ie never crash through reasonable use).
 * -Program exists in several "menu states", showing the contents of the entire
 *     application, a specific course, a specific student, a specific lecture, or
 *     a specific graph configuration. The "menu" variable is an enum dedicated 
 *     to representing this state variable.
 * -Functions should generally return a Result enum type to indicate result of
 *     running. In the end, we may modify all void functions to return Result.
 * -parseline() is the source of all input into this application. parseline()
 *     also delegates work to sub-parsers for each specific menu. Commands
 *     universal to all menus are handled in parseline() alone, whereas 
 *     specific commands are handled in specific parse functions.
 * -Create a function for every compact specific task. Better to have 50 2-line
 *     functions than 1 200-line function.
 * -Prioritize design of "import" functions that will read files from the disk 
 *     and use them to add data to the application. This is what will be used 
 *     in most real life situations.
 *  If you make a new function, tag it with "@author YOUR NAME" so that other
 *     collaborators can ask you about your design choices.
 * -Consult the author of a function if it is labeled DONE before changing it.
 */
public class SimpleUI {

	private Lecture lecPointer = null;
	private ProtoApp coursePointer = null;
	private Student studentPointer = null;
	private GraphTools grapherPointer = null;
	private Scanner stdin = new Scanner(System.in); //Reads from standard input
	private boolean savedToDisc = false; //Set true after save() is called successfully
	private boolean logCommands = true; //Log all commands entered by user
	private boolean echoCommands = true; //Echo all commands entered by the user to stdout
	
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
	
	public enum Result {CONTINUE, QUIT, FAIL, MUTATED}; //Need work
	
	private MenuType menu = MenuType.COURSES;
	
	ArrayList<String> log = new ArrayList<>();
	
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
	 * @author Navneeth Jayendran
	 * 
	 * @return The result of the function call.
	 */
	Result parseLine(){
		System.out.print(">>");
		Result r = Result.CONTINUE;
		String input = stdin.nextLine().trim();
		if (input.equals(""))
			return r;
		String[] pieces = input.split("\\$");
		String[] parsedPieces = new String[2];
		parsedPieces[0] = pieces[0].trim().toLowerCase();
		parsedPieces[1] = pieces.length > 1 ? 
				pieces[1].trim() : "";
		String cmd = parsedPieces[0];
		String arg = parsedPieces[1];
		echo(parsedPieces[0], parsedPieces[1]);
		if (cmd.equals("quit"))
			return quit();
		else if (cmd.equals("menu"))
			displayMenu();
		else if (cmd.equals("help"))
			displayIntro();
		else if (cmd.equals("ls"))
			list();
		else if (cmd.equals("save"))
			r = save();
		else if (cmd.equals("cd") && arg.isEmpty())
			closeMenu();
		else{
			switch (menu){
			case COURSES:
				r = parseCoursesLine(parsedPieces); break;
			case COURSE:
				r = parseCourseLine(parsedPieces); break;
			case LECTURE:
				r = parseLectureLine(parsedPieces); break;
			case STUDENT:
				r = parseStudentLine(parsedPieces); break;
			case GRAPH:
				r = parseGraphLine(parsedPieces); break;
			default:
				r = Result.FAIL;
			}
		}
		if (r == Result.MUTATED)
			savedToDisc = false;
		return r;
	}
	
	/**
	 * Reminds the user that she has unsaved data if she does and asks her if
	 * she wants to exit anyway, losing all unsaved changes.
	 * @author Navneeth Jayendran
	 * 
	 * @return CONTINUE if user has unsaved info and enters No, QUIT otherwise
	 * 
	 */
	Result quit(){
		if (savedToDisc)
			return Result.QUIT;
		return requestYesNo("Exit without saving? " +
				"All unsaved changes will be lost (Y/N): ")
			   ? Result.QUIT : Result.CONTINUE; //conditional expression
			
	}
	
	/**
	 * Saves the application's current state to the disc, somehow.
	 * TODO Implement everything. This will be challenging because it involves
	 * saving every class we've built so far to the disk.
	 * @author Navneeth Jayendran
	 * 
	 * @return CONTINUE if successfully saved, FAIL otherwise.
	 *
	 */
	Result save(){
		toDo("saves nothing right now, but changes application state so that "
		   + "quit will not give a warning."); //TODO Implement.
		savedToDisc = true;
		return Result.CONTINUE;
	}
	
	/**
	 * Activated by the command "cd" with no arguments. Simply changes the menu
	 * to the one at a higher level. So in a specific course, this takes you
	 * back to the course explorer. In a specific lecture or specific student,
	 * this takes you back to the course containing that lecture or student.
	 * In the graph menu, takes you back to the lecture from which you tried
	 * to change the graph tools.
	 * @author Navneeth Jayendran
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
			System.out.println("Missing course name argument.");
		}
	}
	
	
	/*
	 * METHODS USED IN THE COURSES MENU. THESE ARE SOMEWHAT LIMITED.
	 */
	
	
	/**
	 * Specific parser for the Courses menu. Only accepts commands that are
	 * (exclusively) listed under the course menu like cd $ arg, 
	 * mkdir lec $ arg, etc.
	 * @author Navneeth Jayendran
	 * 
	 * @param input The partially parsed {cmd, arg} inputed by the user.
	 */
	Result parseCoursesLine(String[] input){
		if (input[0].equals("mkdir"))
			return addCourse(input[1]);
		else if (input[0].equals("cd"))
			changeToCourse(input[1]);
		else
			return invalidCommand(input[0]);
		return Result.CONTINUE;
		//parseLine();
	}
	
	/**
	 * Displays all courses in this application. States "No courses." if
	 * the application has no courses.
	 * @author Navneeth Jayendran
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
	 * @author Navneeth Jayendran
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
	 * @author Navneeth Jayendran
	 * 
	 * @param name The name of the course to add.
	 */
	Result addCourse(String name){
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
						newone.courseTitle = name;
						courseMap.put(name, newone);
						System.out.printf("Added course with capacity of " +
							"%d: \"%s\"\n", cap, name);
						stdin.nextLine(); //Flush buffer
						return Result.MUTATED;
					}
				}catch (Exception e){
					System.out.print("Invalid capacity. Try again: ");
					stdin.nextLine(); //Flush buffer
				}
			}
		}
		return Result.FAIL;
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
	 * @author Navneeth Jayendran
	 * 
	 * @param input The parsed command-argument string array. The first
	 *              element is the command, the second the argument.
	 * @return 
	 */
	Result parseCourseLine(String[] input){
		if (input[0].equals("ls lec"))
			displayLectures();
		else if (input[0].equals("ls stud"))
			displayStudents();
		else if (input[0].equals("mkdir lec"))
			return addLecture(input[1]);
		else if (input[0].equals("mkdir stud"))
			return addStudent(input[1]);
		else if (input[0].equals("rm"))
			return removeCourse();
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
		else if (input[0].equals("edit title")){
			coursePointer.courseTitle = input[1]; //DONE
			return Result.MUTATED;
		}
		else if (input[0].equals("edit info")){
			coursePointer.courseInfo = input[1]; //DONE
			return Result.MUTATED;
		}
		else
			return invalidCommand(input[0]);
		return Result.CONTINUE;
	}
	
	/**
	 * Displays all students in this application. States "No courses." if
	 * the application has no students.
	 * @author Navneeth Jayendran
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
	 * @author Navneeth Jayendran
	 * 
	 * @TODO Finish implementing this. Currently it makes a lecture at the time
	 *       this function was called. It should actually parse the day
	 *       
	 * @param day A string in the format "mm dd yy" that will be used to
	 *            determine the date of the lecture. The time of day can be
	 *            set arbitrarily.
	 */
	Result addLecture(String day){
		if (day.length() == 0){
			System.out.println("Missing lecture date argument.");
			return Result.FAIL;
		}
		//else if (courseMap.containsKey(name))
			//System.out.printf("Course named \"%s\" already exists.\n", name);
		else{
			toDo("does not actually parse date and simply creates a Lecture at " +
					"the time the command is used."); //TODO Use SimpleDateFormat
			Date newDate = new Date();
			if (coursePointer == null) //Catches a strange bug, possibly
				throw new UnsupportedOperationException("What???");
			coursePointer.addLecture(newDate);
			return Result.MUTATED;
		}
	}
	
	
	/**
	 * Displays all students in the current course. Says "No students added"
	 * if the course has no students.
	 * @author Navneeth Jayendran
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
	 * @author Navneeth Jayendran
	 */
	Result addStudent(String netID){
		try{
			coursePointer.addStudent(netID);
			System.out.printf("Successfully added \"%s\"\n", netID);
			return Result.MUTATED;
		}
		catch(IndexOutOfBoundsException e){
			System.out.printf("This course has reached its maximum capacity: "
					+ "%d\n", coursePointer.converter.capacity());
		}
		catch(IllegalArgumentException e){
			System.out.printf("NetID \"%s\" is already present in this " +
					"course.\n", netID);
		}
		return Result.FAIL;
	}
	
	/**
	 * Deletes the current course from the courses menu and then moves back to
	 * the Courses menu. This is a fairly inelegant way of doing things.
	 * @author Navneeth Jayendran
	 */
	Result removeCourse(){
		if(!requestYesNo("Are you sure you want to remove this course? Y/N: ")){
			System.out.println("Remove cancelled.");
			return Result.FAIL;
		}
		for (Map.Entry<String, ProtoApp> elem : courseMap.entrySet())
			if (elem.getValue() == coursePointer){
				courseMap.remove(elem.getKey());
				System.out.printf("Removed %s\n", elem.getKey());
			}
		menu = MenuType.COURSES;
		return Result.MUTATED;
	}
	
	/**
	 * Changes the menu type to Student and focus on the Student whose
	 * code matches the code passed in. First parse the string as an integer
	 * and use Roster.get(int) to obtain the Student with the matching netID.
	 * 
	 * Give error messages if the string passed in cannot be parsed as an int,
	 * or if the Student is not found in the Roster.
	 * @author Navneeth Jayendran
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
	 * @author Navneeth Jayendran
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
	 * TODO Implement stuff.
	 */
	
	
	/**
	 * This method is a specialized parse method for the Lecture menu.
	 * @author Navneeth Jayendran
	 * 
	 * @param input The parsed command-argument string array. The first
	 *              element is the command, the second the argument.
	 * @return 
	 */
	public Result parseLectureLine(String[] input){
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
		else{
			return invalidCommand(input[0]);
		}
		return Result.CONTINUE;
	}
	
	/**
	 * Displays all the PeerInteraction objects in the particular lecture.
	 * @author Navneeth Jayendran
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
	 * @author Navneeth Jayendran
	 * 
	 * @param input The parsed command-argument string array. The first
	 *              element is the command, the second the argument.
	 * @return Result of execution.
	 */
	public Result parseStudentLine(String[] input){
		if (input[0].equals("stat"))
			toDo(); //TODO Implement
		else if (input[0].equals("rm"))
			toDo(); //TODO Implement
		else
			return invalidCommand(input[0]);
		return Result.CONTINUE;
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
	
	/**
	 * Specialized parser for the Graph menu. This does very little and
	 * will probably stay that way.
	 * @author Navneeth Jayendran
	 * 
	 * @return Result of execution.
	 */
	public Result parseGraphLine(String[] input){
		if (input[0] == "use")
			toDo(); //TODO implement
		else if (input[0] == "edit")
			toDo(); //TODO Implement
		else
			return invalidCommand(input[0]);
		return Result.CONTINUE;
	}
	
	/**
	 * Displays the different weighters available for making graphs
	 * alongside numbers.
	 * @author Navneeth Jayendran
	 */
	private void displayWeighters(){
		System.out.print("1. Default.\n" + 
	                     "2. Filter by student rating standard deviation\n" +
				         "3. Filter by student rating mean\n" +
	                     "4. Prioritize students without many 5s and 10s\n" +
				         "5. Filter by date range\n");
	}
	
	/*
	 * BELOW ARE ALL THE UNIVERSAL DISPLAY METHODS. THEY DO NOT PROMPT ANYTHING 
	 * FROM STANDARD INPUT AND MERELY PRINT THINGS OUT TO STANDARD OUTPUT. MOST
	 * DO NOT ACCEPT ANY ARGUMENTS.
	 * 
	 */
	
	/**
	 * Polymorphic method that is called in response to the command "menu". 
	 * This method is exclusively called by the parseLine() function. The 
	 * method simply prints out the appropriate menu string associated with the
	 * current menu type.
	 * @author Navneeth Jayendran
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
	 * Polymorphic method that is called in response to the command "ls". This
	 * method is called in the parseLine() method exclusively. What this
	 * actually displays is dependent upon the menu type.
	 * @author Navneeth Jayendran
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
	
	/**
	 * Displays the introductory information for SimpleUI, including four
	 * specialized universal commands. This is called in response to the
	 * command "help".
	 * @author Navneeth Jayendran 
	 */
	public void displayIntro(){
		System.out.print(
			 "Welcome to this prototypical user interface for the CS125 Lecture Feedback Analysis Application!\n\n"
			+"This UI takes in Unix-like commands from the user, given in the form \"cmd $ args\" or just\n"
			+"\"cmd\", after displaying the prompt >>. Many commands are not implemented yet.\n\n"
	
			+"Special commands:\n" 
		    +"\"menu\" display all commands specific to a menu\n"
			+"\"quit\" stop the application\n"
			+"\"help\" display this introduction again\n"
			+"\"save\" save application instance to disc (not yet implemented)\n\n"
			);
	}
	
	/*
	 * HELPER FUNCTIONS, MOSTLY USED TO REDUCE REDUNDANT PRINT STATEMENTS.
	 * 
	 */
	
	/**
	 * States that the command passed in does not exist.
	 * @author Navneeth Jayendran
	 * 
	 * @param cmd The invalid command that does not exist.
	 * @return FAIL, always.
	 */
	
	private Result invalidCommand(String cmd){
		System.out.printf("Invalid command \"%s\".\nEnter \"menu\" for a list of valid " +
				"commands for this menu, or \"help\" for more general commands.\n", cmd);
		return Result.FAIL;
	}
	
	/**
	 * Helper method that prompts a user to enter Y/N and converts the 
	 * response to a boolean. The method will do this until a valid
	 * input is received.
	 * @author Navneeth Jayendran
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
	
	/**
	 * Straightforward. Put this wherever you have commands that have not
	 * been implemented at all to inform the user.
	 * @author Navneeth Jayendran //DONE
	 */
	private void toDo(){
		System.out.println("Not yet implemented.");
	}
	
	/**
	 * Modified version of toDo() that allows you to specify more details
	 * as to how something isn't fully implemented yet. This is for
	 * commands that do SOMETHING, but not necessarily to completion.
	 * @author Navneeth Jayendran //DONE
	 * 
	 * @param details Specific ways in which functionality has not been fully
	 *                implemented.
	 */
	private void toDo(String details){
		System.out.printf("Not fully implemented: %s\n", details);
	}
	
	/**
	 * Prints out a command and argument separated by a $ character. This may
	 * only be useful in debugging. It also logs commands if
	 * 
	 * @param cmd The command.
	 * @param arg The argument.
	 */
	private void echo(String cmd, String arg){
		String sEcho = String.format("ECHO: %s $ %s", cmd, arg);
		String sLog = String.format("%s $ %s", cmd, arg);
		if (echoCommands)
			System.out.println(sEcho);
		if (logCommands)
			log.add(sLog);
	}
	
	/**
	 * Very simple main method.
	 */
	public static void main(String[] args){		
		SimpleUI runner = new SimpleUI();
		runner.displayIntro();
		while(runner.parseLine() != Result.QUIT);
		System.out.println("Goodbye!");
	}
}
