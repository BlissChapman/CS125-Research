import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import junit.framework.TestCase;

/**
* @author CS125Research
*/
public class RosterTest extends TestCase {
	
	public void tearDown() {
	}
	public void setUp() throws Exception{
		super.setUp();
	}

	/**
	 * This test creates a fake Roster object from a list of fake netIDs
	 * and a list of fake PeerInteractions. It then tests some simple
	 * things.
	 * 
	 * @throws FileNotFoundException 
	 */
	public void testRosterObject() throws FileNotFoundException {
		//STUDENTS: Number of students in class
		int STUDENTS = 100 + Utilities.gen.nextInt(700);
		//CAPACITY: Maximum size of Roster
		int CAPACITY = STUDENTS + 100 + Utilities.gen.nextInt(300);
		ArrayList<String> fakeNetIDs = Utilities.generateNetIDs(STUDENTS);
		
		/* Convert our feedback file into a PeerInteraction ArrayList
		 * and use it to generate a wordbank. This is not really 
		 * necessary for this test though.
		 */
		ArrayList<PeerInteraction> samples = new ArrayList<>();
		Scanner sc = 
		    new Scanner(new File("src/peerInteractions.fa2015.final.csv"));
		while (sc.hasNextLine()){
			try{
				samples.add(new PeerInteraction(sc.nextLine()));
			}catch (IllegalArgumentException e) {/* Do nothing */}
		}
		sc.close();
		TreeMap<String, Integer> dictMap = Utilities.dictFromInteractions(samples);
		ArrayList<String> dictionary = new ArrayList<>();;
		for (String elem : dictMap.keySet())
			dictionary.add(elem);
		//ArrayList<String> dictionary = new ArrayList<>(); //This also works
		NRList converter  = new NRList(fakeNetIDs, CAPACITY);
		/*
		 * config[0]: The probability that a student will give Feedback for a
		 *            particular lecture.
		 * config[1]: The probability that a student, given he gives Feedback,
		 *            also gives a valid partner netID.
		 * config[2/3]: The probability that a student, given he gives Feedback,
		 *              also describes his strengths/weaknesses.
		 * config[4]: p-value for geometric distribution describing the length
		 *            of the student's strength/weakness response. The
		 *            expected response length is (1/config[4]).
		 * config[5]: The probability that a student will give a duplicate
		 *            entry. This is very small in practice.
		 */
		double[] config = {0.667, 0.873, 0.70, 0.40, 0.90, 0.00};
		long now = (new Date()).getTime();     //~Date right now
		long then = now - 4l*30*24*60*60*1000; //~Four months ago
		ArrayList<String> fakeRawFeedback = 
		    Utilities.generateFeedback(50, new Date(then), new Date(now), 
		    		                  fakeNetIDs, dictionary, config);
		NRList fakeNRList = new NRList(fakeNetIDs, fakeNetIDs.size()+100);
		Roster testRoster = new Roster(fakeNRList);
		ArrayList<PeerInteraction> fakeEntries = new ArrayList<>();
		try{
			for (String entry : fakeRawFeedback){
				fakeEntries.add(new PeerInteraction(entry, fakeNRList));
			}
		}catch (Exception e){ 
			System.out.println(e.getMessage());
			assertTrue(false); //No exceptions should be thrown
		}
		//Basic sanity checks
		assertTrue(fakeNetIDs.size() == fakeNRList.size());
		assertTrue(fakeNRList.size() == testRoster.size());
		//All PeerInteractions should belong to a Student in the Roster
		for (PeerInteraction entry : fakeEntries){
			Student person = testRoster.get(entry);
			assertTrue(person != null);
			person.addEntry(entry);
		}
		//The iterators in the Student class should return PeerInteractions
		//in chronological order.
		for (Student individual : testRoster){
			Date last = new Date(0); //1970
			//individual.mergeAllDuplicates(); This does not work due to
			//dependencies on R
			for (PeerInteraction curr : individual){
				assertTrue(curr.getDate().compareTo(last) >= 0);
				last = curr.getDate();
			}
		}
	}
	

	/**
	 * TODO improve to also test 
	 *      Roster.addPeerInteractions(Iterable<PeerInteraction>)
	 */
	public void testRosterIteratorAndExceptions() {
		int trials = 101;
		while (--trials > 0){
			int CAPACITY = 1000;
			int SIZE = 200;
			int DELTA = 500;
			Roster testRoster = new Roster(CAPACITY);
			ArrayList<Integer> integers = new ArrayList<>();
			for (int i = 0; i < CAPACITY; ++i)
				integers.add(i);
			Collections.shuffle(integers);
			int count = 0;
			for (int i = 0; i < SIZE; ++i){
				int number = integers.get(i);
				testRoster.addStudent(number);
			}
			
			//Test to see that iteration works as expected
			for (Student person : testRoster)
				++count;
			assertTrue(count == testRoster.size());
			//Check that bounds exception is properly thrown
			try{
				//Randomly tries underflow or overflow by up to DELTA
				int wildcard = Math.random() > 0.50 ? CAPACITY : -DELTA;
				testRoster.addStudent(wildcard + (int)(Math.random()*DELTA));
				assertTrue(false); //Bounds check test failed.
			} catch(Exception e){
				assertTrue("Wrong exception thrown.", 
						   e instanceof IndexOutOfBoundsException);
				//System.out.println(e.getMessage());
				assertTrue("Bounds check test successful.", true);
			}
			
			//After this resize, no bounds exceptions should be thrown.
			//But the method should properly handle assignments to IDs
			//that are already taken.
			testRoster.resize(CAPACITY + DELTA);
			int unvisited = SIZE;
			for (int i = 0; i < CAPACITY + DELTA; ++i){
				try{
					testRoster.addStudent(i);
					//System.out.printf("Added Student %d\n", i);
				}catch(Exception e){
					assertTrue(e instanceof IllegalArgumentException);
					//System.out.println(e.getMessage());
					--unvisited;
				}
			}
			assertTrue(unvisited == 0); //Exception thrown for every existing Student
			assertTrue(testRoster.size() == testRoster.capacity()); //Roster full
		}
	}
}
