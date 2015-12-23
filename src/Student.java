import java.util.ArrayList;

/**
 * Important: Student class. Stores information regarding a particular Student in class, 
 * including their lecture attendance and possibly grades. Each Student should probably
 * contain all the FeedbackEntries made by the student.
 *
 * @author CS125 Research
 * @todo Implement everything
 */
public class Student{
	private int ID;
	//private boolean female; //One possibility for what we could store here
	private ArrayList<FeedbackEntry> records;
	private double weight;
 

 	/**
 	 * ID Ctor. Needs work?
 	 * 
 	 * @param code The ID of the Student.
 	 */
	public Student(int code){
		ID = code;
		records = new ArrayList<FeedbackEntry>();
	}
	
	/**
	 * ID and one-entry Ctor. Is this useful?
	 * 
	 * @param code  The ID of the Student
	 * @param entry The first FeedbackEntry made by this student.
	 */
	public Student(int code, FeedbackEntry entry){
		this(code);
		records.add(entry);
	}

	/**
	 * Computes and returns the weight of this Student, used when calculating
	 * weighted grade distributions for a Lecture. 
	 * @todo Insert implementation.
	 *
	 * @return The weight given to this Student's feedback.
	 */
	public double feedbackWeight(){
		return weight; //Not complete
	}

	/**
	 * Add a FeedbackEntry to this Student's record thereof. This method 
	 * imposes a chronologial precondition.
	 * @todo Implement.
	 *
	 * @param entry FeedbackEntry made by this Student
	 */
	public void addEntry(FeedbackEntry entry){
		records.add(entry);
	}

	/**
	 * @return The ID code of this student.
	 */
	public int getID(){
		return ID;
	}

	/**
	 * Setter for ID (is this really useful?)
	 */
	public void setID(int code){
		ID = code;
	}

	/**
	 * @return Number of valid feedback data points given by this Student.
	 * @todo Implement.
	 */
	public int feedbackGiven(){
		return -1;
	}

	/**
	 * @return The average rating given by this Student for all lectures 
	 *         so far in the semester.
	 * @todo Implement if needed.
	 */
	public double ratingMean(){
		return -1;
	}

	/**
	 * @return The standard deviation of all ratings given by this
	 *         Student throughout the semester
	 * @todo Implement if needed.
	 */
	public double ratingStdDev(){
		return -1;
	}

        
}
