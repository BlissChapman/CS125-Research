import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;

/**
 * A Lecture object class primarily associated with the time (a Java 
 * Date object) of a lecture. The Lecture object contains all PeerInteractions
 * associated with the particular lecture and an optional title, description,
 * and number. Lecture provides a static binary search method that can
 * associate a PeerInteraction with the right Lecture given a List of
 * Lectures. It also computes several statistical variables such as rating
 * mean and rating standard deviation. Lastly, it provides a distribution of
 * all ratings in the Lecture subject to externally defined filters and
 * weights (see Weighter.java and GraphTools.java).
 * 
 * @author CS125Research
 * 
 * TODO Discuss the design of this class and its member variables. Decide if
 *      it needs more functionalities.
 */
public class Lecture implements Iterable<PeerInteraction>{

	public int lectureNumber; //TODO: Discuss this name
	public ArrayList<PeerInteraction> recordsByTime;
	private Date date;
	private String title = "";
	private String description = "";
	
	/**
	 * Base constructor called by all other constructors.
	 * Assigns a Date to this Lecture object.
	 * 
	 * @param d The time this lecture began.
	 */
	public Lecture(Date d){
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
		title = topics[0];
		description = topics[1];
	}
	
	/**
	 * Copy constructor.
	 * @param other A Lecture to be copied.
	 */
	public Lecture(Lecture other){
		title = other.title;
		description = other.description;
		lectureNumber = other.lectureNumber;
		recordsByTime = new ArrayList<PeerInteraction>();
		for (PeerInteraction elem : other.recordsByTime)
			recordsByTime.add(elem);
	}
	
	/**
	 * Constructor with argument to assign the topics for
	 * the Lecture object. @TODO discuss merits of this constructor
	 * 
	 * @param d     The time this lecture began.
	 * @param entry A PeerInteraction that was submitted for this Lecture
	 */
	public Lecture(Date d, PeerInteraction entry){
		this(d);
		this.add(entry);
	}
	
	/**
	 * @param title The desired title of the lecture. Null arguments result in
	 *              exceptions thrown.
	 */
	public void setTitle(String title){
		if (title == null)
			throw new NullPointerException();
		this.title = title;
	}
	
	/**
	 * @return The title of this lecture.
	 */
	public String getTitle(){
		return title;
	}
	
	
	/**
	 * @param description The desired description for this lecture. Null
	 *                    arguments result in exceptions thrown.
	 */
	public void setInfo(String description){
		if (description == null)
			throw new NullPointerException();
		this.description = description;
	}
	
	/**
	 * @return The description for this course.
	 */
	public String getInfo(){
		return description;
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
	 * @return A copy of the ArrayList contained within this Lecture.
	 */
	public ArrayList<PeerInteraction> toList(){
		ArrayList<PeerInteraction> out = new ArrayList<>();
		for (PeerInteraction elem : recordsByTime)
			out.add(elem);
		return out;
	}
	
	/**
	 * Sets the PeerInteraction collection of this Lecture to a new empty
	 * collection.
	 *
	 */
	public void empty(){
		recordsByTime = new ArrayList<>();
	}
	
	/**
	 * Purges the Lecture of all PeerInteractions submitted by a Student with
	 * the matching integer code passed into the function and returns all
	 * matching PeerInteractions as an ArrayList.
	 * 
	 * @param code The integer code of the Student whose entries must be purged.
	 * @return The ArrayList of all PeerInteractions belonging to said Student.
	 */
	public ArrayList<PeerInteraction> removeByStudent(int code){
		ArrayList<PeerInteraction> out = new ArrayList<>();
		for (ListIterator<PeerInteraction> it = recordsByTime.listIterator();
			 it.hasNext();){
			PeerInteraction curr = it.next();
			if (curr.getPersonID() == code){
				out.add(curr);
				it.remove();
			}
		}
		return out;
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
	public static Lecture get(PeerInteraction key, List<Lecture> lecs){
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
	public int[] ratingDistributionInt() {
		int[] values = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (PeerInteraction element : recordsByTime)
			values[element.getGrade() - 1]++;
		return values;
	}
	
	public double[] ratingDistributionDouble(Weighter<PeerInteraction> scale) {
		double[] values = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		for (PeerInteraction element : recordsByTime)
			values[element.getGrade() - 1] += scale.weight(element);
		return values;
	}
	
	public String ratingDistributionString() {
		String stringRepresentation = "";
		int[] distribution = ratingDistributionInt();
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
	public ListIterator<PeerInteraction> iterator(){ 
		return recordsByTime.listIterator(); 
	}
																
}
