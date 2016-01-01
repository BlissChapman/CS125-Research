import java.util.ArrayList;

/**
 * Important: Student class. Stores information regarding a particular Student in class, 
 * including their lecture attendance and possibly grades. Each Student should probably
 * contain all the PeerInteractions made by the student.
 *
 * @author CS125 Research
 * @todo Implement everything
 */
class Student{
	public static final float WEIGHT_PROPORTIONALITY_CONSTANT = 1;
	public static final float WEIGHT_THRESHOLD = 10;
	
	/**
	 * ID of this Student. This value is immutable.
	 */
	private final int ID;
	//private boolean female; //One possibility for what we could store here
	
	/**
	 * The collection of all PeerInteractions made by the Student, sorted
	 * in chronological order.
	 */
	private ArrayList<PeerInteraction> records;
	
	private double weight = Double.NaN;
	
	
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
	 * @param entry The first PeerInteraction made by this student.
	 */
	public Student(PeerInteraction entry){
		this(entry.getPersonID());
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
		if (records.size() == 0)
			return Double.NaN;
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
	 * imposes a chronologial precondition. Throws an exception if an
	 * entry passed in has a personID which differs from this.ID .
	 *
	 * @param entry PeerInteraction made by this Student
	 */
	public void addEntry(PeerInteraction entry){
		if (entry.getPersonID() != ID)
			throw new IllegalArgumentException(
				String.format("Student %d given entry by %d",
				              ID, entry.getPersonID()));
		records.add(entry);
	}

	/**
	 * @return The ID code of this student.
	 */
	public int getID(){
		return ID;
	}

	/**
	 * @return Number of valid peer interaction data points given by this Student.
	 * @todo Implement.
	 */
	public int feedbackGiven(){
		return records.size();
	}

	/**
	 * @return The average rating given by this Student for all lectures 
	 *         so far in the semester.
	 */
	public double ratingMean(){
		if (records.size() == 0)
			return Double.NaN;
		long sum = 0;
		for (PeerInteraction elem : records)
			sum += elem.getGrade();
		return ((double) sum)/records.size();
	}

	/**
	 * @return The standard deviation of all ratings given by this
	 *         Student throughout the semester
	 */
	public double ratingStdDev(){
		if (records.size() == 0)
			return Double.NaN;
		long sumSquared = 0;
		double ratingMean = ratingMean();
		for (PeerInteraction elem : records)
			sumSquared += (elem.getGrade()*elem.getGrade());
		double meanSquared = ((double) sumSquared)/records.size();
		return Math.sqrt(meanSquared-ratingMean*ratingMean);
	}
}
