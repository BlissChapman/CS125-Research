import java.util.*;

/**
 * LectureList is a specialized container for Lecture objects. It
 * @author navneeth
 *
 */
public class LectureList implements Iterable<Lecture>{
	
	ArrayList<Lecture> lecs;
	
	
	LectureList(){
		lecs = new ArrayList<>();
		Lecture sentinel = new Lecture(new Date(0));
		sentinel.setTitle("Collects garbage that comes before any other " +
				"lectures to prevent IndexOutOfBoundsException throws when " +
				"Lecture.get(Date) is called.");
		sentinel.lectureNumber = 0;
		lecs.add(sentinel);
	}
	
	LectureList(Iterable<Date> dates){
		this();
		ArrayList<Date> dateList = new ArrayList<>();
		for (Date elem : dates) 
			dateList.add(elem);
		ArrayList<Date> sortedDates = Utilities.withoutDuplicates(dateList);
		for (Date elem : sortedDates)
			lecs.add(new Lecture(elem));
	}
	
	/**
	 * @return The number of legitimate Lectures in this LectureList.
	 */
	public int size(){
		return lecs.size()-1;
	}
	
	/**
	 * @return True if no legitimate Lectures in this LectureList.
	 */
	public boolean isEmpty(){
		return size() == 0;
	}
	
	
	/**
	 * Adds a new Lecture to this course and then reassigns all existing
	 * PeerInteractions possessed by all Students in this course to their
	 * corresponding lectures under the new course schedule.
	 * 
	 * @param day The time of the new Lecture.
	 */
	public void addLecture(Date day){
		Lecture newLec = new Lecture(day);
		lecs.add(newLec);
		boolean dupe = 
				Utilities.riseSorted(lecs, lecs.size()-1, Utilities.byDate);
		if (dupe){ //Removes lecture object at previous time
			for (ListIterator<Lecture> it = lecs.listIterator(); it.hasNext(); ){
				Lecture curr = it.next();
				if (curr == newLec)
					it.remove();
			}
		}
		int i = 0;
		for (Lecture lec : lecs){
			lec.lectureNumber = i++;
		}
	}
	
	
	/**
	 * @param i Index of Lecture in list.
	 * @return The Lecture at index i in list.
	 */
	Lecture get(int i){
		return lecs.get(i);
	}
	
	/**
	 * Method that uses a right-leaning binary search of existing Lectures
	 * to associate a PeerInteraction with a Date. Runs in logarithmic time with
	 * respect to the number of Lectures. This method will return the latest
	 * Lecture preceding the entered Date.
	 *
	 * @param search The date to be associated with a Lecture.
	 *
	 * @return The Lecture corresponding to a date.
	 */
	public Lecture get(Date search){
		int lo = 0;
		int hi = lecs.size()-1;
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
	 * Slightly more expedient version of get(Date) that takes in a
	 * PeerInteraction and uses its date field as the argument for
	 * get(Date).
	 *
	 * @param key The entry to be associated with a Lecture.
	 *
	 * @return The Lecture corresponding to an entry.
	 */
	public Lecture get(PeerInteraction key){
		return get(key.getDate());
	}

	
	/**
	 * Returns a deep copy of the ArrayList containing all Lectures in this
	 * LectureList except for the sentinel Lecture.
	 * 
	 * @return An independent copy of the lecs ArrayList with the sentinel
	 *         removed.
	 */
	public ArrayList<Lecture> toList(){
		ArrayList<Lecture> out = new ArrayList<>();
		for (int i = 1; i < lecs.size(); ++i)
			out.add(new Lecture(lecs.get(i)));
		return out;
	}
	
	/**
	 * @param batch A collection of PeerInteractions that to get added to
	 *              their corresponding lectures within this LectureList.
	 */
	public void addInteractions(Iterable<PeerInteraction> batch){
		ArrayList<PeerInteraction> batchList = new ArrayList<>();
		for (PeerInteraction elem : batch){
			Lecture tgt = get(elem);
			tgt.add(elem);
		}
	}
	
	public void addInteractionsDistinct(Iterable<PeerInteraction> batch){
		ArrayList<PeerInteraction> batchList = new ArrayList<>();
		for (PeerInteraction elem : batch)
			batchList.add(elem);
		ArrayList<PeerInteraction> processedList = new ArrayList<>();
		Utilities.insertionSort(batchList, Utilities.byPersonID);
		for(int i = 0; i < batchList.size(); ++i){
			ArrayList<PeerInteraction> dupes = new ArrayList<>();
			PeerInteraction pi = batchList.get(i);
			int curr = pi.getPersonID();
			while(i < batchList.size() && 
				  (pi = batchList.get(i)).getPersonID() == curr){
				dupes.add(pi);
				i++;
			}
			processedList.add( dupes.size() > 1 ? 
					           new PeerInteraction(dupes) : pi);
		}
	}
	
	/**
	 * Removes all entries from all Lectures in this LectureList and returns
	 * them as an ArrayList.
	 * 
	 * @return All the PeerInteractions previously stored by Lectures in this
	 *         LectureList.
	 */
	public void emptyEach(){
		for (Lecture each : this)
			each.empty();
	}
	
	/**
	 * 
	 */
	public ArrayList<PeerInteraction> allInteractions(){
		ArrayList<PeerInteraction> out = new ArrayList<>();
		for (Lecture elem : this)
			for (PeerInteraction entry : elem)
				out.add(entry);
		return out;
	}
	
	/**
	 * Ensures that all PeerInteractions in this LectureList are associated
	 * with the proper Lecture based on date. This method gets 
	 */
	public void refresh(){
		ArrayList <PeerInteraction> all = allInteractions();
		emptyEach();
		addInteractions(all);
	}
	
	@Override
	public Iterator<Lecture> iterator() {
		// TODO Auto-generated method stub
		return new LectureIterator();
	}
	
	public class LectureIterator implements Iterator<Lecture>{

		int i = 1;
		
		public boolean hasNext() {
			// TODO Auto-generated method stub
			return i < lecs.size();
		}

		public Lecture next() {
			// TODO Auto-generated method stub
			return lecs.get(i++);	
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	

}
