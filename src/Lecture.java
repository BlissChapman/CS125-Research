import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;

/**
 * A model for a lecture object with a unique id corresponding to the lecture number, all the associated peer interactions, the date, and the lecture topics.
 * This class also contains static properties and methods involved with initializing lectures and analyzing the result.
 * @author CS125Research
 */
public class Lecture implements Iterable<PeerInteraction>{

	private static int AUTO_INCREMENT = 0;
	private int lectureNumber; //TODO: Discuss this name
	public ArrayList<PeerInteraction> recordsByTime;
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
		recordsByTime = new ArrayList<PeerInteraction>();
		date = new Date(d.getTime());
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
		this.topics = topics.clone();
	}
	
	/**
	 * Constructor with argument to assign the topics for
	 * the Lecture object.
	 * 
	 * @param d     The time this lecture began.
	 * @param entry A PeerInteraction that was submitted for this Lecture
	 */
	public Lecture(Date d, PeerInteraction entry){
		this(d);
		this.add(entry);
	}
	
	/**
	 * @return the lecture number.
	 */
	public int getLectureNumber(){
		return lectureNumber;
	}
	
	/**
	 * @return A copy of this Lecture's date.
	 */
	public Date getDate(){
		return new Date(date.getTime());
	}
	
	/**
	 * This method adds a PeerInteraction to this Lecture. As a 
	 * precondition, entries should be added in chronological 
	 * order.
	 * 
	 * @param entry The entry to add to this Lecture. This entry should
	 *              come after the last entry added to the Lecture.
	 */
	public void add(PeerInteraction entry){
		recordsByTime.add(entry);
	}
	
	/**
	 * Static method that uses a right-biased binary search of existing Lectures
	 * to associate a PeerInteraction with a Lecture. Runs in logarithmic time with
	 * respect to the number of Lectures.
	 *
	 * @param key The entry to be associated with a Lecture.
	 *
	 * @return The Lecture corresponding to an entry.
	 */
	public static Lecture get(PeerInteraction key){
		ArrayList<Lecture> lecs = LectureData.lectures;
		int lo = 0;
		int hi = lecs.size()-1;
		Date search = key.getDate();
		while (hi != lo){
			int mid = (lo + hi + 1)/2;
			Date comp = lecs.get(mid).getDate();
			switch (search.compareTo(comp)){
			case -1:
				hi = mid-1; break; //Can exclude Dates that come after target
			case 1:
				lo = mid; break;  //Can't exclude Dates that come before
			default:
				return lecs.get(mid); //Unlikely case of exact match
			}
		}
		return lecs.get(lo);
	}
	
	/**
	 * Method that returns the unadjusted rating distribution of all
	 * PeerInteractions for this Lecture. The 0th element of this
	 * array corresponds to the number of entries with ratings of
	 * 1, and the 9th to the number of entries with ratings of 10.
	 * 
	 * @return An array of ints in which arr[idx] represents the number
	 *         of PeerInteractions with ratings of (idx+1) in this Lecture
	 */
	public int[] ratingDistribution() {
		int[] values = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (PeerInteraction element : recordsByTime)
			values[element.getGrade() - 1]++;
		return values;
	}
	
	public String ratingDistributionString() {
		String stringRepresentation = "";
		int[] distribution = ratingDistribution();
		for(int i = 0; i < distribution.length; i++)
			stringRepresentation += ((i + 1) + ": " + distribution[i] + ", ");
		return stringRepresentation;
	}
	
	/**
	 * Calculates and returns the unadjusted average rating given by
	 * all PeerInteractions for this lecture.
	 * 
	 * @return The average rating of all PeerInteractions in this Lecture
	 */
	public double ratingMean(){
		if (recordsByTime.isEmpty())
			return -1;
		double sum = 0;
		for (PeerInteraction element : recordsByTime)
			sum += element.getGrade();
		return sum/recordsByTime.size();
	}
	
	/**
	 * Calculates and returns the unadjusted standard deviation of ratings
	 * given by all PeerInteractions for this lecture.
	 * 
	 * @return The rating standard deviation of all PeerInteractions in this 
	 *         Lecture
	 */
	public double ratingStdDev(){
		if (recordsByTime.isEmpty())
			return -1;
		double devSq = 0;
		double mean = ratingMean();
		for (PeerInteraction element : recordsByTime)
			devSq += Math.pow(element.getGrade()-mean,2);
		return Math.sqrt(devSq/recordsByTime.size());
	}
	
	/**
	 * @return The number of PeerInteractions in this Lecture.
	 */
	public int entryCount(){
		return recordsByTime.size();
	}
	
	/**
	 * @return A string in the form "ID: %, Date: %, Number of Entries: %,"
	 *         "Mean: %, Standard Deviation: %"
	 */
	public String toString(){
		return String.format("ID: %d\n\tDate: ", lectureNumber) + date
		+ String.format("\n\tNumber of Entries: %d\n\t"
		    + "Mean: %f\n\tStandard Deviation: %f",
		    recordsByTime.size(), ratingMean(), ratingStdDev())
		+ "\n\t Rating Distribution: " + ratingDistributionString();	
	}
	
	/**
	 * @return A chronological iterator through all PeerInteractions in
	 *         this Lecture.
	 */
	public FeedbackIterator iterator() { return new FeedbackIterator(); }
	
	/**
	 * Iterator that goes through FeedbackEntries in chronological order.
	 */
	public class FeedbackIterator implements Iterator<PeerInteraction>{
		private int curr;
		
		public FeedbackIterator()    { curr = 0; }
		public PeerInteraction next()  { return recordsByTime.get(curr++); }
		public boolean hasNext()     { return curr < recordsByTime.size(); }
		public void remove()  { throw new UnsupportedOperationException(); }
																				
	}
}
