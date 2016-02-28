import java.util.*;


public class ProtoApp {

	public Roster students;
	public NRList converter;
	public ArrayList<Lecture> lectures;
	public GraphTools<PeerInteraction> entryGrapher;
	
	public String courseTitle = "";
	public String courseInfo = "";
	
	
	private Comparator<Lecture> byDate = new Comparator<Lecture>(){
		public int compare(Lecture a, Lecture b){
			return a.getDate().compareTo(b.getDate());
		}
	};
	
	public ProtoApp(int cap){
		students = new Roster(cap);
		converter = new NRList(cap);
		lectures = new ArrayList<>();
		entryGrapher = new GraphTools<>(students);
	}
	
	
	public ProtoApp(NRList studentCodes, List<Date> days){
		converter = studentCodes;
		students = new Roster(converter);
		entryGrapher = new GraphTools<>(students);
		List<Date> neatDays = Utilities.withoutDuplicates(days);
		lectures = new ArrayList<>();
		for (int i = 0; i < neatDays.size(); ++i){
			Date day = neatDays.get(i);
			Lecture next = new Lecture(day);
			next.lectureNumber = i+1;
			lectures.add(next);
		}
	}
	
	public void addLecture(Date day){
		Lecture newLec = new Lecture(day);
		lectures.add(newLec);
		boolean dupe = 
				Utilities.riseSorted(lectures, lectures.size()-1, byDate);
		if (dupe){
			for (ListIterator<Lecture> it = lectures.listIterator(); it.hasNext(); ){
				Lecture curr = it.next();
				if (curr == newLec)
					it.remove();
			}
		}
		int i = 0;
		for (Lecture lec : lectures){
			lec.lectureNumber = ++i;
		}
	}
	
	/**
	 * Add a Student to this course. This simply adds a new NetIDPair
	 * to the NRList and then uses the NRList to add that Student to the
	 * Roster by code.
	 * 
	 * @param netID The netID of the student to add.
	 * @throws IllegalArgumentException If the netID is already present.
	 * @throws IndexOutOfBoundsException If the course is at fully capacity.
	 */
	public void addStudent(String netID){
		converter.addPair(netID);
		int studCode = converter.getSecret(netID);
		students.addStudent(studCode);
	}
	
	public boolean removeStudent(String netID){
		NetIDPair former = converter.removePair(netID); //Remove from NRList
		if (former == null)                         //But only if he exists.
			return false;
		students.removeStudent(former.getCode());   //Remove from Roster
		//Purge all their PeerInteractions from lectures.
		for (Lecture lec : lectures){
			ListIterator<PeerInteraction> it = lec.iterator();
			while (it.hasNext()){
				PeerInteraction check = it.next();
				if (check.getPersonID() == former.getCode())
					it.remove();
			}
		}
		return true;
	}
	
	
	/**
	 * This method takes in a list of PeerInteractions and does the following:
	 * 1) Assigns these to their respective students.
	 * 2) Goes through each student and merges all recent PeerInteractions
	 *    corresponding to the same lecture (ie so that 70 entries from the
	 *    same student on the same day will get merged into just 1).
	 * 3) Adds the merged result from each
	 *
	 * @param batch An Iterable set of recent PeerInteractions to be 
	 *              distributed among students and lectures.
	 */
	public void addFeedback(Iterable<PeerInteraction> batch){
		students.addInteractions(batch);
		/*
		for (Student s : students){ //For every student
			ArrayList<PeerInteraction>  //Extract all duplicates
			  retrieved = s.mergeRecentDuplicates(lectures);
			if (retrieved.size() > 1){ //If duplicates found
				PeerInteraction merged = retrieved.get(retrieved.size()-1);
				Lecture proper = Lecture.get(merged, lectures);
				proper.add(merged);
			}
		}
		*/
	}
	
	public void mergeRecentDuplicates(){
		for (Student s : students){ //For every student
			ArrayList<PeerInteraction>  //Extract all duplicates
			  retrieved = s.mergeRecentDuplicates(lectures);
			if (retrieved.size() > 1){ //If duplicates found
				PeerInteraction merged = retrieved.get(retrieved.size()-1);
				Lecture proper = Lecture.get(merged, lectures);
				proper.add(merged);
			}
		}
	}
	
}
