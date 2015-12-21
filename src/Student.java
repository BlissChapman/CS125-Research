import Java.util.ArrayList;

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
	private boolean female; //One possibility for what we could store here
	private ArrayList<FeedbackEntry> feedbackGiven;
 
 	/**
 	 * Ctor. Needs work.
 	 */
	public Student(int code){
		ID = code;
	}
}
