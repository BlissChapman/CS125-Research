import java.util.*;
import java.util.Map.Entry;

/**
 * Simple command line tools for using and testing this application.
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
	 * courseMap.put("CS125", courseMap 
	 */
	private TreeMap<String, ProtoApp> courseMap = new TreeMap<String,ProtoApp>();
	
	public enum MenuType { COURSES, COURSE, LECTURE, STUDENT, GRAPH};
	
	private MenuType menu = MenuType.COURSES;
	
	
	private static HashMap<MenuType, String> menus = new HashMap<MenuType, String>();
	static{
		menus.put(MenuType.COURSES,
			"COURSE CREATION MENU:\n\n" +
			"mkdir $ <name>         Creates new course.\n" +
			"ls                     Display all existing courses.\n" +
			"cd $ <name>            Go to an existing course.\n");
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
			"cd	                        Go back to course list.\n" +
			"import lecs $ <file>       Use file to add new lectures.\n" +
			"import entries $ <file>    Use file to add new feedback.\n" +
			"import studs $ <file>      Use file to add new students.\n" +
			"rm                         Remove this course.\n");
		menus.put(MenuType.LECTURE,
			"LECTURE %d%s\nDATE: %s\n\n" +
			"ls                     Shows all feedback for this lecture.\n" +
			"cd $ <studID>          Select student.\n" +
			"cd                     Go back to the course page.\n" +
			"stat                   Obtain statistical summary of lecture.\n" +
			"edit title $ <title>   Change lecture title.\n" +
			"edit info $ <info>     Change lecture description.\n" +
			"graph                  Graph lecture data.\n" +
			"graph rules            Modify weighting rules on graph.\n");
		menus.put(MenuType.STUDENT,
			"STUDENT MENU:\nStudent #%d\n" +
			"ls						Show all feedback given by student.\n" +
			"stat					Obtain statistical summary os student.\n" +
			"rm						Purge this student from the course.\n");
		menus.put(MenuType.GRAPH,
			"GRAPH RULE MENU:\n" +
			"ls						Show all graphing constraints.\n" +
			"use <rule#>			Switch to specific graph constraint.\n" +
			"edit <rule#>			Change parameters of specific rule.\n" +
			"cd						Go back to the lecture page.\n");
	};
	
	
	void parseLine(){
		System.out.print(">>");
		String input = stdin.nextLine().trim();
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
			switch (menu){
			case COURSES:
				parseCoursesLine(parsedPieces); break;
			case COURSE:
				parseCourseLine(parsedPieces);
				break;
			case LECTURE:
				break;
			case STUDENT:
				break;
			case GRAPH:
				break;
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
	
	void invalidCommand(String cmd){
		System.out.printf("Invalid command \"%s\".\nEnter \"help\" for a list of valid " +
				"commands.\n", cmd);
	}
	
	void parseCoursesLine(String[] input){
		System.out.printf("%s $ %s\n", input[0], input[1]);
		if (input[0].equals("mkdir"))
			addCourse(input[1]);
		else if (input[0].equals("cd"))
			changeToCourse(input[1]);
		else if (input[0].equals("mkdir lec"))
			addLecture(input[1]);
		else
			invalidCommand(input[0]);
		//parseLine();
	}
	
	void parseCourseLine(String[] input){
		System.out.printf("%s $ %s\n", input[0], input[1]);
		if (input[0].equals("mkdir lec"))
			addLecture(input[1]);
		else
			invalidCommand(input[0]);
	}
	
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
						System.out.print("Enter a capacity greater than 5: ");
					}
					else{
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

	void addLecture(String day){
		if (day.length() == 0)
			System.out.println("Missing lecture date argument.");
		//else if (courseMap.containsKey(name))
			//System.out.printf("Course named \"%s\" already exists.\n", name);
		else{
			System.out.println("Not implemented yet."); //@TODO Use SimpleDateFormat
			Date newDate = new Date();
			if (coursePointer == null)
				throw new UnsupportedOperationException("What???");
			coursePointer.addLecture(newDate);
		}
	}
	
	void removeCourse(){
		for (Map.Entry<String, ProtoApp> elem : courseMap.entrySet())
			if (elem.getValue() == coursePointer){
				courseMap.remove(elem.getKey());
				System.out.printf("Removed %s\n", elem.getKey());
			}
	}
	
	void changeToCourse(String courseName){
		if (!courseMap.containsKey(courseName))
			System.out.printf("No course named \"%s\". Enter \"ls\" to see " 
					+ "all courses.\n", courseName);
		else{
			coursePointer = courseMap.get(courseName);
			menu = MenuType.COURSE;
		}
	}
	
	
	private void displayMenu(){
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
		default:
			System.out.println(menus.get(menu)); break;
		}
	}
	
	private void list(){
		switch (menu){
		case COURSES:
			displayCourses();
			break;
		case COURSE:
			displayLectures();
			displayStudents();
			break;
		case LECTURE:
			System.out.println("Not yet implemented.");
			break;
		case STUDENT:
			System.out.println("Not yet implemented.");
			break;
		case GRAPH:
			System.out.println("Not yet implemented.");
			break;
		default:
			break;
		}
	}
	
	private void displayCourses(){
		if (courseMap.isEmpty())
			System.out.println("No courses.");
		else{
			System.out.println("List of courses:\n");
			int i = 1;
			for (Entry<String, ProtoApp> course : courseMap.entrySet())
				System.out.println(i++ + ": " + course.getKey());
		}
	}
	
	private void displayLectures(){
		if (coursePointer.lectures.isEmpty()){
			System.out.println("No lectures added.");
			return;
		}
		System.out.println("Lectures: ");
		for (Lecture lec : coursePointer.lectures)
			System.out.printf("Lecture %d: %s",
					lec.lectureNumber, lec.getDate().toString());
	}
	
	private void displayStudents(){
		if (coursePointer.students.size() == 0){
			System.out.println("No students added.");
			return;
		}
		System.out.println("Students: ");
		for (Student person : coursePointer.students)
			System.out.printf("Student %d\n", person.getID());
	}
	
	
	public static void main(String[] args){
		SimpleUI runner = new SimpleUI();
		runner.parseLine();
	}
}
