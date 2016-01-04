import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.Scanner;

/**
 * Student class. Stores information regarding a particular Student in class, 
 * including their lecture attendance and possibly grades. Each Student
 * will contain all the proper PeerInteractions made by the student.
 *
 * @author CS125 Research
 * @todo Implement more.
 */
class Student implements Iterable<PeerInteraction>{
	public static final float WEIGHT_PROPORTIONALITY_CONSTANT = 1;
	public static final float WEIGHT_THRESHOLD = 10;
	
	/* ID of this Student. This value is immutable. */
	private final int ID;
	
	//private boolean female; //One possibility for what we could store here
	
	/* The collection of all PeerInteractions made by the Student, sorted
	 * in chronological order. */
	private ArrayList<PeerInteraction> records;

	/* Caching variables for several computationally nontrivial methods.
	 * Stores results of these method calls and is recomputed whenever
	 * the Student is mutated. */
	private float weight = Float.NaN;
	private float ratingMean = Float.NaN;
	private float ratingStdDev = Float.NaN;
	/* When the cache is to be refreshed, this boolean is set true. Any
	 * methods that use the above variables will recompute values when
	 * this boolean is set. See refreshCache()*/
	private boolean mutated = false;
	
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
		refreshCache();
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
		if (!mutated)
			return WEIGHT_PROPORTIONALITY_CONSTANT*weight;
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
	 * will sort the record by date after insertion. Throws an exception 
	 * if an entry passed in has a personID which differs from this.ID .
	 *
	 * @param entry PeerInteraction made by this Student
	 */
	public void addEntry(PeerInteraction entry){
		if (entry.getPersonID() != ID)
			throw new IllegalArgumentException(
			    String.format("Student %d given entry by %d",
				          ID, entry.getPersonID()) );
		records.add(entry);
		pushBack();
		refreshCache();
	}

	/**
	 * @return The ID code of this student.
	 */
	public int getID(){
		return ID;
	}

	/**
	 * @return Number of valid peer interaction data points given by this Student.
	 */
	public int feedbackGiven(){
		return records.size();
	}

	/**
	 * @return The average rating given by this Student for all lectures 
	 *         so far in the semester.
	 */
	public float ratingMean(){
		if (!mutated)
			return ratingMean;
		if (records.size() == 0)
			return Float.NaN;
		long sum = 0;
		for (PeerInteraction elem : records)
			sum += elem.getGrade();
		return ratingMean = ((float) sum)/records.size();
	}

	/**
	 * @return The standard deviation of all ratings given by this
	 *         Student throughout the semester
	 */
	public float ratingStdDev(){
		if (!mutated)
			return ratingStdDev;
		if (records.size() == 0)
			return Float.NaN;
		long sumSquared = 0;
		double mean = ratingMean();
		for (PeerInteraction elem : records)
			sumSquared += (elem.getGrade()*elem.getGrade());
		double meanSquared = ((float) sumSquared)/records.size();
		return ratingStdDev = (float) Math.sqrt(meanSquared-mean*mean);
	}
	
	/**
	 * Ensures that the last few entries added are from distinct lectures.
	 * If duplicates are found, all will be merged into a single entry.
	 * This is public for now because it's unclear where it should be
	 * called.
	 */
	public void mergeRecentDuplicates(){
		int last = records.size()-1;
		while (last > 0){;
			Lecture curr = Lecture.get(records.get(last));
			Lecture prev = Lecture.get(records.get(last));
			if (curr != prev)
				break;
			else
				--last;
		}
		if (last != records.size()-1){
			PeerInteraction[] repeats 
			    = new PeerInteraction[records.size()-last];
			for (int idx = 0; idx < records.size()-last; ++idx)
				repeats[idx] = records.get(idx+last);
			for (int idx = records.size()-1; idx >= last; ++idx)
				records.remove(idx); //Shame Java has no removeLast
			records.add(new PeerInteraction(repeats));
			refreshCache(); //Recomputation needed due to changes
		}
	}

	/**
	 * Performs the same function as mergeRecentDuplicates(), but does
	 * so for all entries added. This might be slower but also might
	 * preferable due to lack of preconditions.
	 */
	public void mergeAllDuplicates(){
		ArrayList<PeerInteraction> newRecords = new ArrayList<>();
		ArrayList<PeerInteraction> bucket = new ArrayList<>();
		Lecture last = null;
		PeerInteraction[] merged = new PeerInteraction[1];
		for (PeerInteraction entry : records){
			Lecture curr = Lecture.get(entry);
			if (curr != last && bucket.size() > 0){
				merged = new PeerInteraction[bucket.size()];
				bucket.toArray(merged);
				newRecords.add(new PeerInteraction(merged));
				bucket = new ArrayList<>();
			}
			bucket.add(entry);
			last = curr;
		}
		if (bucket.size() > 0){
			merged = new PeerInteraction[bucket.size()];
			bucket.toArray(merged);
			newRecords.add(new PeerInteraction(merged));
		}
		records = newRecords;
	}
	
	/**
	 * Private function that refreshes all cached variables. Setting
	 * mutated to true causes all functions to recompute instead
	 * of returning their cached values.
	 */
	private void refreshCache(){
		mutated = true;
		ratingMean();
		ratingStdDev();
		feedbackWeight();
		mutated = false;
	}
	
	/**
	 * Private helper function that pushes back an unsorted entry to
	 * its proper spot in Student.records. Called whenever an entry is
	 * added.
	 */
	private void pushBack(){
		int last = records.size()-1;
		while (last > 0 
		    && records.get(last-1).getDate().compareTo(
		           records.get(last).getDate()) > 0){
		//^ Java verbosity in a nutshell.
		//  Translation: records[last-1].getDate() > records[last].getDate();
			Collections.swap(records, last-1, last);
			--last;
		}
	}


	/**
	 * Chronological iterator that points to the first element of
	 * records.
	 */
	public InteractionIterator iterator() { return new InteractionIterator(); }

	public class InteractionIterator implements Iterator<PeerInteraction>{
		private int curr;
		public InteractionIterator()     { curr = 0; }
		public boolean hasNext()         { return curr < records.size();}
		public PeerInteraction next()    { return records.get(curr++); }
		public void remove() { throw new UnsupportedOperationException(); } 
	}

	
	/**
	 * For now, this test method only works for feedback files without
	 * NRLists (so all netIDs must be integers). It also writes nothing
	 * to any files. It mainly tests to see if mergeDuplicates works.
	 * The parameters DUPLICATE and INVALID_ID are ratios that indicate
	 * the probability of this test inserting duplicate entries and
	 * trying to add entries with a mismatched ID (for testing).
	 * 
	 * @args The source file for PeerInteractions or nothing
	 */
	public static void main(String[] args){
		LectureData.initialize();
		String fileName = args.length == 1 ? args[0] : null;
		double DUPLICATE = 0.00;
		double INVALID_ID = 0.15;
		Scanner safeScan = new Scanner(System.in);
		if (fileName == null){
			System.out.print("Enter the name of a feedback source file: ");
			fileName = safeScan.nextLine();
			TextIO.readFile(fileName);
		}
		ArrayList<PeerInteraction> results = new ArrayList<PeerInteraction>();
		while (!TextIO.eof()){
			try{
				PeerInteraction temp = new PeerInteraction(TextIO.getln());
				results.add(temp);
			}catch(Exception e) {/* Do nothing */}
		}
		PeerInteraction rand;
		do{
			rand = results.get((int) (results.size()*Math.random()));
		}while(!rand.valid());
		Student trial = new Student(rand); //This will lead to duplication
		Collections.shuffle(results);     //Tests functionality of pushBack()
		for (PeerInteraction elem : results){
			if (elem.getPersonID() != trial.getID() &&
			    Math.random() >= INVALID_ID)
				continue;
			try{
				trial.addEntry(elem);
				while (Math.random() < DUPLICATE)
					trial.addEntry(elem);
			}catch(IllegalArgumentException e){
				System.out.printf("Caught bad add: %s\n", e.getMessage());
			}
		}
		System.out.println("Data with possible duplicates:");
		for(PeerInteraction elem : trial)
			System.out.println(elem);
		trial.mergeAllDuplicates();
		System.out.println("\n\nData with duplicates merged:");
		for (PeerInteraction elem : trial)
			System.out.println(elem);
	}
}
