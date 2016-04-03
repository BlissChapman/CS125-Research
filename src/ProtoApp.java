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
	public LectureList lectures;
	public GraphTools entryGrapher;
	public Weighter<PeerInteraction> currentWeighter;
	
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
		lectures = new LectureList();
		entryGrapher = new GraphTools(students);
		currentWeighter = entryGrapher.ALL_ENTRIES;
	}
	
	public ProtoApp(NRList studentCodes, List<Date> days){
		converter = studentCodes;
		students = new Roster(converter);
		entryGrapher = new GraphTools(students);
		lectures = new LectureList(days);
	}
	
	/**
	 * Uses the existing Roster and course information to simply parse
	 * a PeerInteration 
	 * @param csv
	 * @return
	 */
	public PeerInteraction parseInteraction(String csv){
		try{
			PeerInteraction out = new PeerInteraction(csv, converter);
			return out;
		}catch(IllegalArgumentException e){
			return null;
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
		lectures.addLecture(day);
		lectures.refresh();
	}
	
	/**
	 * Adds a 
	 * @param days
	 */
	public void addLectures(Iterable<Date> days){
		for (Date day : days)
			lectures.addLecture(day);
		lectures.refresh();
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
		int query = converter.getSecret(netID);
		if (query >= 0)
			return removeStudent(query);
		else
			return false;
			
	}
	
	public boolean removeStudent(int code){
		NetIDPair former = converter.removePair(code); //Remove from NRList
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
		assignEntriesToStudents(batch);
		assignEntriesToLectures(batch);
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
	 * to their corresponding students. This method does not perform any
	 * checks as to whether the PeerInteractions are valid.
	 * 
	 * @param batch A collection of PeerInteractions to be added to the course.
	 */
	public void assignEntriesToLectures(Iterable<PeerInteraction> batch){
		lectures.addInteractions(batch);
	}
	
	public void assignEntriesToStudents(Iterable<PeerInteraction> batch){
		students.addInteractions(batch);
	}
	
	/**
	 * Removes all PeerInteractions from all Lectures in this course and
	 * replaces them with all PeerInteractions possessed by all Students
	 * in the Roster.
	 * 
	 * @param mergeDupes Whether or not to merge duplicate entries into one.
	 */
	public void reassignStudentEntriesToLectures(boolean mergeDupes){
		lectures.emptyEach();
		ArrayList<PeerInteraction> all = new ArrayList<>();
		for (Student s : students) for (PeerInteraction elem : s)
				all.add(elem);
		if(mergeDupes)   lectures.addInteractionsDistinct(all);
		else             lectures.addInteractions(all);
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
			  retrieved = s.mergeRecentDuplicates(lectures.toList());
			if (retrieved.size() > 1){ //If duplicates found
				PeerInteraction merged = retrieved.get(retrieved.size()-1);
				Lecture proper = Lecture.get(merged, lectures.toList());
				if (proper == lec){
					lec.removeByStudent(merged.getPersonID());
					lec.add(merged);
				}
			}
		}
	}
	
	public double[] weightedDistribution(Lecture lec, 
			Weighter<PeerInteraction> rule){
		return lec.ratingDistributionDouble(rule);
	}
	
	public double[] weightedDistribution(Lecture lec){
		return lec.ratingDistributionDouble(currentWeighter);
	}
}
