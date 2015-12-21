import java.util.ArrayList;
import java.util.Date;

//A model for a lecture object with a unique id corresponding to the lecture number,
//all the associated feedback entries, the date, and the lecture topics.
public class Lecture {

	private static int AUTO_INCREMENT = 0;
	private int lectureNumber; //TODO: Discuss this name
	public ArrayList<FeedbackEntry> recordsByTime;
	private Date date;
	private String[] topics;// OR ArrayList<String>
	
	/**
	 * Base constructor called by all other constructors.
	 * Assigns a Date to this Lecture object.
	 * 
	 * @param d The time this lecture began.
	 */
	public Lecture(Date d){
		lectureNumber = AUTO_INCREMENT++;
		recordsByTime = new ArrayList<>();
		date = d;
	}
	
	/**
	 * Constructor with argument to assign the topics for
	 * the Lecture object.
	 * 
	 * @param d     The time this lecture began.
	 * @param topic A list of topics discussed during this lecture.
	 */
	public Lecture(Date d, String[] topics){
		this(d);
		this.topics = topics;
	}
	
	/**
	 * @return A copy of this Lecture's date.
	 */
	public Date getDate(){
		return new Date(date.getTime());
	}
	
	/**
	 * This method adds a FeedbackEntry to this Lecture. As a 
	 * precondition, entries should be added in chronological 
	 * order.
	 * 
	 * @param entry The entry to add to this Lecture. This entry should
	 *              come after the last entry added to the Lecture.
	 */
	public void add(FeedbackEntry entry){
		recordsByTime.add(entry);
	}
	
	/**
	 * Method that returns the unadjusted rating distribution of all
	 * FeedbackEntries for this Lecture. The 0th element of this
	 * array corresponds to the number of entries with ratings of
	 * 1, and the 9th to the number of entries with ratings of 10.
	 * 
	 * @return An array of ints in which arr[idx] represents the number
	 *         of FeedbackEntries with ratings of (idx+1) in this Lecture
	 */
	public int[] getRatingDistribution() {
		int[] values = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (FeedbackEntry element : recordsByTime)
			values[element.getGrade() - 1]++;
		return values;
	}
	
	/**
	 * Calculates and returns the unadjusted average rating given by
	 * all FeedbackEntries for this lecture.
	 * 
	 * @return The average rating of all FeedbackEntries in this Lecture
	 */
	public double getAverage(){
		if (recordsByTime.isEmpty())
			return -1;
		double sum = 0;
		for (FeedbackEntry element : recordsByTime)
			sum += element.getGrade();
		return sum/recordsByTime.size();
	}
	
	/**
	 * Calculates and returns the unadjusted standard deviation of ratings
	 * given by all FeedbackEntries for this lecture.
	 * 
	 * @return The rating standard deviation of all FeedbackEntries in this 
	 *         Lecture
	 */
	public double getStdDev(){
		if (recordsByTime.isEmpty())
			return -1;
		double devSq = 0;
		double mean = getAverage();
		for (FeedbackEntry element : recordsByTime)
			devSq += Math.pow(element.getGrade()-mean,2);
		return Math.sqrt(devSq/recordsByTime.size());
	}
	
	/**
	 * @return A string in the form "ID: %, Date: %, Number of Entries: %,"
	 *         "Mean: %, Standard Deviation: %"
	 */
	public String toString(){
		return String.format("ID: %d\n\tDate: ", ID) + date + String.format("\n\tNumber of Entries: %d\n\t"
				+ "Mean: %f\n\tStandard Deviation: %f", recordsByTime.size(), getAverage(), getStdDev());		
	}	
}
