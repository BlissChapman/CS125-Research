import java.util.*;


/**
 * Prototypical combined application for the research project. This object
 * contains an NRList, a Roster, and a List of Lectures for a particular
 * course. In addition, it has two strings for the course title and course
 * description.
 * 
 * @author navneeth
 *
 * TODO Find a better name, discuss any additional needed features and member
 *      variables/functions. Also discuss the merits of public modifiers.
 */
public class ProtoApp {

	public Roster students;
	public NRList converter;
	public ArrayList<Lecture> lectures;
	public GraphTools entryGrapher;
	
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
		entryGrapher = new GraphTools(students);
	}
	
	public ProtoApp(NRList studentCodes, List<Date> days){
		converter = studentCodes;
		students = new Roster(converter);
		entryGrapher = new GraphTools(students);
		List<Date> neatDays = Utilities.withoutDuplicates(days);
		lectures = new ArrayList<>();
		for (int i = 0; i < neatDays.size(); ++i){
			Date day = neatDays.get(i);
			Lecture next = new Lecture(day);
			next.lectureNumber = i+1;
			lectures.add(next);
		}
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
	 * @param  netID The netID of the student to add.
	 * @throws IllegalArgumentException If the netID is already present.
	 * @throws IndexOutOfBoundsException If the course is at fully capacity.
	 */
	public void addStudent(String netID){
		converter.addPair(netID);
		int studCode = converter.getSecret(netID);
		students.addStudent(studCode);
	}
	
	/**
	 * Removes a Student from this class. In doing this, it purges her from
	 * the Roster using Roster.remove(), from the NRList using
	 * NRList.removePair(), and removes all PeerInteractions she has submitted
	 * in all lectures. The method returns a boolean to indicate whether the
	 * Student was found and successfully removed.
	 * 
	 * @param netID The netID of the Student to be removed from this course.
	 * @return True if the Student was found and removed, false otherwise
	 */
	public boolean removeStudent(String netID){
		NetIDPair former = converter.removePair(netID); //Remove from NRList
		if (former == null)                         //But only if he exists.
			return false;
		students.removeStudent(former.getCode());   //Remove from Roster
		//Purge all their PeerInteractions from lectures.
		for (Lecture lec : lectures)
			lec.removeByStudent(former.getCode());
		return true;
	}
	
	
	/**
	 * This method takes in a list of PeerInteractions and does the following:
	 * 1) Assigns these to their respective Students.
	 * 2) Takes all PeerInteractions matched with a Student and assigns them to
	 *    their respective Lectures.
	 *
	 * @param batch An Iterable set of recent PeerInteractions to be 
	 *              distributed among students and lectures.
	 */
	public void addFeedback(Iterable<PeerInteraction> batch){
		ArrayList<PeerInteraction> toAdd = students.addInteractions(batch);
		assignEntriesToLectures(toAdd);
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
	
	/**
	 * Takes a batch of PeerInteractions and assigns them to their
	 * appropriate lectures in the course. The method then adds them
	 * to their corresponding lectures. This method does not perform any
	 * checks as to whether the PeerInteractions are valid.
	 * 
	 * @param batch A collection of PeerInteractions to be added to the course.
	 */
	private void assignEntriesToLectures(Iterable<PeerInteraction> batch){
		for (PeerInteraction elem : batch){
			Lecture tgt = Lecture.get(elem, lectures);
			tgt.add(elem);
		}
	}
	
	/**
	 * Removes all PeerInteractions from all Lectures in this course and
	 * replaces them with all PeerInteractions possessed by all Students
	 * in the Roster.
	 */
	public void reassignStudentEntriesToLectures(){
		for (Lecture lec : lectures)
			lec.refreshEntries();
		for (Student s : students)
			for (PeerInteraction elem : s){
				Lecture tgt = Lecture.get(elem, lectures);
				tgt.add(elem);
			}
	}
	
	/**
	 * Examines all the latest PeerInteractions associated with Students in the
	 * Roster, merges all those which correspond to the same lecture into a 
	 * single PeerInteraction, and, if the Lecture of said PeerInteractions
	 * matches the argument to the function, moves the merged PeerInteraction
	 * to that Lecture. The function also removes from that lecture all 
	 * PeerInteractions belonging to a Student from whom duplicate entries were
	 * found for that lecture.
	 * 
	 * @param lec The Lecture from which duplicates need to be replaced with a
	 *            merged PeerInteraction.
	 */
	public void mergeEntriesToLecture(Lecture lec){
		for (Student s : students){ //For every student
			ArrayList<PeerInteraction>  //Extract all duplicates
			  retrieved = s.mergeRecentDuplicates(lectures);
			if (retrieved.size() > 1){ //If duplicates found
				PeerInteraction merged = retrieved.get(retrieved.size()-1);
				Lecture proper = Lecture.get(merged, lectures);
				if (proper == lec){
					lec.removeByStudent(merged.getPersonID());
					lec.add(merged);
				}
			}
		}
	}
	
}
