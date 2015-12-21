/**
 * A container which stores all Students in the class for processing. Implements
 * a constant-time getter method which maps student ID codes to their
 * corresponding Student objects. 
 *
 * @author CS125 Research
 * @todo Add useful features. Also implement Student class and add needed
 *       functionality to NRList class.
 */
public class Roster{

    private Student[] map;
    private int numStudents;
	
    /**
     * Constructor for the Roster class. Creates an array of specified capacity
     * and fills it up with new Student objects. The Students are initialized
     * based off the ID codes in the NRList argument.
     */
    public Roster(NRList codes, int capacity){
        map = new Student[capacity];
	    /* Goes through all the ID codes in the NRList argument and creates a
	     * Student object for each one. The Students created are stored in an
	     * array at the index corresponding to their ID codes. So map[154] would
	     * correspond to the Student whose ID code is 154. That being said, we
	     * should ensure that the range of Student ID codes is relatively small
	     * or else the array will waste space.
	     */
        for (NetIDPair elem : codes){ //NRList needs a functionality like this
            map[elem.getID()] = new Student(elem.getID());
            numStudents = codes.size(); //NRList needs a functionality like this
        }
    }

	/**
	 * Given an ID, returns the Student with the matching ID in the Roster. 
	 * Returns null if the ID is not found. This method is guarranteed to
	 * run in constant time.
	 *
	 * @param ID The code associated with a particular student.
	 *
	 * @return The student associated with the given code.
	 */
	public Student get(int ID){
		if (ID >= map.size() || ID < 0)
			return null;
		else
			return map[ID];
	}
	
	/**
	 * @return The number of students in the Roster.
	 */
	public int size() { return numStudents; }
	
	/**
	 * In case new Students join the class, allows addition of new Student to
	 * Roster. Ensure extra space when constructing Roster or else this will
	 * not be possible.
	 */
    public void addStudent(int ID){
        if (ID < 0 || ID >= map.size())
            throw new OutOfBoundsException(ID + " is out of Roster Range.");
        if (map[ID] != null)
            throw new IllegalArgumentException(ID + " is already taken.");
        map[ID] = new Student(ID);
        ++numStudents;
	}
	 
    /**
	 * Resizes this Roster to allow addition of more Students. If shrinking
	 * the Roster would cut off existing students, throws an exception.
	 *
	 * @param newSize New size of Roster.
	 */
	public void resize(int newSize){
	/*
	 * @todo Implement
	 */
    }
}
