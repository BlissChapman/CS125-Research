import java.util.ArrayList;

/**
 * Important: Student class. Stores information regarding a particular Student in class, 
 * including their lecture attendance and possibly grades. Each Student should probably
 * contain all the PeerInteractions made by the student.
 *
 * @author CS125 Research
 * @todo Implement everything
 */
public class Student{
	public static final float WEIGHT_PROPORTIONALITY_CONSTANT = 1;
	public static final float WEIGHT_THRESHOLD = 10;
	
	private int ID;
	//private boolean female; //One possibility for what we could store here
	private ArrayList<PeerInteraction> records;
	private double weight;
 

 	/**
 	 * ID Ctor. Needs work?
 	 * 
 	 * @param code The ID of the Student.
 	 */
	public Student(int code){
		ID = code;
		records = new ArrayList<PeerInteraction>();
	}
	
	/**
	 * ID and one-entry Ctor. Is this useful?
	 * 
	 * @param code  The ID of the Student
	 * @param entry The first PeerInteraction made by this student.
	 */
	public Student(int code, PeerInteraction entry){
		this(code);
		records.add(entry);
	}

	/**
	 * Computes and returns the weight of this Student, used when calculating
	 * weighted grade distributions for a Lecture. 
	 *
	 * Formula: (2 * Number of records by student) / (Number of times the record had a feedback rating of 5 OR 10)
	 * 	Weight can be a max of WEIGHT_THRESHOLD
	 *
	 * @return The weight given to this Student's feedback.
	 */
	public double feedbackWeight(){
		
		int numberOfCommonResponses = 0; // For now, if the feedback was 5 or 10, it's not important
		for(int i = 0; i < records.size(); i++){
			if(records.get(i).getGrade() == 5 || records.get(i).getGrade() == 10){
				numberOfCommonResponses++;
			}
		}
		if(numberOfCommonResponses > 0)
			weight = (2*records.size())/numberOfCommonResponses;
		else
			weight = WEIGHT_THRESHOLD;
		
		if(weight > WEIGHT_THRESHOLD)
			weight = WEIGHT_THRESHOLD;
		
		return WEIGHT_PROPORTIONALITY_CONSTANT*weight;
	}

	/**
	 * Add a PeerInteraction to this Student's record thereof. This method 
	 * imposes a chronologial precondition.
	 * @todo Implement.
	 *
	 * @param entry PeerInteraction made by this Student
	 */
	public void addEntry(PeerInteraction entry){
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
	 * @return Number of valid peer interaction data points given by this Student.
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
