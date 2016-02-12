import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A utilities class containing various static methods that may be of use
 * in other classes, particularly in making large test cases.
 * 
 * @author CS125-Research
 *
 */
public class Utilities {
	
	public static Random gen = new Random();
	
	@SuppressWarnings("rawtypes")
	public static final Comparator<Comparable> naturalOrder = 
	    new Comparator<Comparable>()
	{
		@SuppressWarnings("unchecked")
		public int compare(Comparable first, Comparable second){
			return first.compareTo(second);
		}
	};
	
	/**
	 * Takes an ArrayList of Strings, sorts it in alphabetical order, and
	 * returns an ArrayList with the same elements as the argument but
	 * without any duplicate elements.
	 * 
	 * @param words The words
	 * @return
	 */
	private static ArrayList<String> filterDuplicates(ArrayList<String> words){
		ArrayList<String> results = new ArrayList<>();
		String last = "";
		Collections.sort(words);
		for (String elem : words){
			if (!elem.equals(last))
				results.add(elem);
			last = elem;
		}
		return results;
	}
	
	/**
	 * Generates a random integer from a geometric distribution with success
	 * probability p.
	 * 
	 * @param p Probability of success at a given trial, in the interval [0,1].
	 * @return The number of trials needed for a success.
	 */
	private static int randomGeometric(double p){
		int out = 1;
		while (Math.random() > p)
			++out;
		return out;
	}
	
	/**
	 * Creates a random "sentence" of specified length from a set of
	 * "words" in a dictionary.
	 * 
	 * @param dict   The set of all words that can be used in the sentence.
	 * @param length The length of the target sentence.
	 * @return A random sentence whose words are all found in dictionary.
	 */
	private static String randomSentence(ArrayList<String> dict, int length){
		StringBuilder b = new StringBuilder();
		int size = dict.size();
		if (size == 0)
			return "";
		while (length-- > 0){
			int randIdx = gen.nextInt(size);
			b.append(dict.get(randIdx));
			if (length != 0)
				b.append(' ');
		}
		return b.toString();
	}
	
	/**
	 * Creates fake roster information (all "netID"s are strings of six
	 * lowercase alphabets followed by a digit).
	 * 
	 * @param number The number of students in this fake roster.
	 * @return An ArrayList filled with random netIDs.
	 */
	public static ArrayList<String> generateRoster(int number){
		ArrayList<String> netIDs = new ArrayList<>();
		while(netIDs.size() < number){
			while(netIDs.size() < number){
				StringBuilder curr = new StringBuilder();
				for (int i = 0; i < 6; ++i){
					char letter = (char) ('a' + gen.nextInt(25));
					curr.append(letter);
				}
				char digit = (char) ('0' + gen.nextInt(9));
				curr.append(digit);
				netIDs.add(curr.toString());
			}
			netIDs = filterDuplicates(netIDs);
		}
		return netIDs;
	}
	
	/**
	 * This method generates an ArrayList of Strings based off of various input
	 * parameters. These Strings are meant to simulate real feedback
	 * using random generators. The method is designed to somewhat
	 * simulate real data by partitioning the data into several time intervals,
	 * each corresponding to a particular lecture (though these time intervals
	 * are all the same size). During these each interval, few students will
	 * give feedback more than once. However, not all students will leave
	 * feedback. A double array will be passed in to specify the desired
	 * probabilities of various events. A String array is used to generate
	 * random sentences for the PeerInteraction strengths and weaknesses
	 * parameters.
	 *  
	 * @param lectures The number of "lectures" from the start to end date
	 * @param start    The date of the first lecture.
	 * @param stop     A date after the last lecture.
	 * @param netIDs   A list of all netIDs in the roster.
	 * @param dict     A list of words to be used in generating feedback.
	 * @param config   A double array of size 6 whose elements are all in the
	 *                 range [0,1] that specifies the probabilities of various
	 *                 events.
	 * @return
	 */
	public static ArrayList<String> 
	    generateFeedback(int lectures, Date start, Date stop, 
			             ArrayList<String> netIDs, ArrayList<String> dict,
			             double[] config)
	{
		SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long range = stop.getTime() - start.getTime();
		long interval = range/lectures;
		//config = {ATTENDANCE, VALID_PARTNER, HAS_FEEDBACK, 
		//          STRENGTH_P_VAL, WEAKNESS_P_VAL, DUPLICATE};
		long currentTime = start.getTime();
		int numStudents = netIDs.size();
		ArrayList<String> products = new ArrayList<>();
		while (lectures-- > 0){
			//Forgive this monstrosity. Excessive indentation makes me cry.
			for (String person : netIDs) if (Math.random() < config[0]) do{
				long time = currentTime + (long) (Math.random()*interval);
				String date = form.format(new Date(time));
				String partner = Math.random() < config[1] ?
						netIDs.get(gen.nextInt(numStudents)) :
						"null";
				int rating = 1 + gen.nextInt(10);
				boolean writeResponse = Math.random() < config[2];
				String strength = writeResponse ?
					randomSentence(dict, randomGeometric(config[3])) :
				    "";
				String weakness = writeResponse ?
					randomSentence(dict, randomGeometric(config[4])) :
					"";
				String product = String.format(
				    "\"%s\", \"%s\", \"%d\", \"%s\", \"%s\", \"%s\"",
				    person, partner, rating, strength, weakness, date);
				products.add(product);
			}while(Math.random() < config[5]);
			currentTime += interval;
		}
		return products;
	}
	
	
	/**
	 * Takes in a collection of PeerInteraction objects and generates an
	 * ArrayList containing all the words used in strength and weakness fields
	 * of all elements of the collection. This is simply used to later generate
	 * fake PeerInteraction objects.
	 * 
	 * @param samples A collection of PeerInteractions 
	 * @return An ArrayList containing all distinct elements from the argument.
	 */
	public static ArrayList<String> 
	    dictFromInteractions(Iterable<PeerInteraction> samples)
	{
		ArrayList<String> output = new ArrayList<>();
		for (PeerInteraction entry : samples)
			if (entry.hasFeedback()){
				String cat = entry.getStrength() + ' ' + entry.getWeakness();
				Scanner wordExtractor = new Scanner(cat);
				while (wordExtractor.hasNext())
					output.add(wordExtractor.next().replace("\"", "\"\""));
				wordExtractor.close();
			}
		Collections.sort(output);
		return output;
	}
	
	/**
	 * Simple main method that demonstrates the behavior of these test
	 * methods. It reads from the final CSV file of all PeerInteractions
	 * and constructs fake feedback using a randomly generated roster of
	 * students and randomly generates written feedback using words from
	 * the real FA2015 lecture feedback.
	 * 
	 * @param args Nothing
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException{
		final int LECTURES = 10;
		final int STUDENTS = 100;
		final int CAPACITY = 2*STUDENTS;
		ArrayList<String> fakeRoster = Utilities.generateRoster(STUDENTS);		
		
		ArrayList<PeerInteraction> samples = new ArrayList<>();
		Scanner sc = 
		    new Scanner(new File("src/peerInteractions.fa2015.final.csv"));
		while (sc.hasNextLine()){
			samples.add(new PeerInteraction(sc.nextLine()));
		}
		ArrayList<String> dictionary = Utilities.dictFromInteractions(samples);
		NRList converter  = new NRList(fakeRoster, CAPACITY);
		Roster fakeClass = new Roster(converter, CAPACITY);
		/*
		 * config[0]: The probability that a student will give feedback for a
		 *            particular lecture.
		 * config[1]: The probability that a student, given he gives feedback,
		 *            also gives a valid partner netID.
		 * config[2]: The probability that a student, given he gives
		 *            feedback, also gives strengths or weaknesses.
		 * config[3/4]: p-value for geometric distribution describing the length
		 *            of the student's strength/weakness response. The
		 *            expected response length is (1/config[4]).
		 * config[5]: The probability that a student will give a duplicate
		 *            entry. This is very small in practice.
		 */
		double[] config = {0.667, 0.873, 0.15, 0.70, 0.40, 0.005};
		Date now = new Date();
		Date then = new Date(now.getTime() - 4*30*24*3600000l); //~4 months ago
		ArrayList<String> fakeRawFeedback = 
		    Utilities.generateFeedback(LECTURES, then, now, 
		    		                  fakeRoster, dictionary, config);
		NRList fakeNRList = new NRList(fakeRoster, fakeRoster.size()+300);
		ArrayList<PeerInteraction> fakeEntries = new ArrayList<>();
		String last = "";
		System.out.println("Dictionary: \n\n");
		for (String elem : dictionary) if (!elem.equals(last)){
			System.out.println(elem);
			last = elem;
		}
		System.out.println("\n\nFake Roster: ");
		for (String elem : fakeRoster)
			System.out.println(elem);
		System.out.println("\n\nFake Feedback: ");
		int count = 0;
		int validCount = 0;
		int feedbackCount = 0;
		for (String elem : fakeRawFeedback){
			PeerInteraction curr = new PeerInteraction(elem, fakeNRList);
			++count;
			if (curr.valid())
				++validCount;
			if (curr.hasFeedback())
				++feedbackCount;
			System.out.println(elem);
		}
		System.out.printf("\n\"Attendance\": %.2f%% (Doesn't take into "
		    + "account duplicates, so expect this to be higher than the "
		    + "config parameter)\n", (100.0*count)/(LECTURES*STUDENTS));
		System.out.printf("Validity: %.2f%%\n", (100.0*validCount)/count);
		System.out.printf("Responsiveness: %.2f%%\n", 
		                  (100.0*feedbackCount)/count);
		
		fakeClass.addInteractions(samples);
		GraphTools grapher = new GraphTools(fakeClass);
		
		Main.main(new String[0]);
		Lecture test = LectureData.lectures.get(5);
		grapher.RATING_STD_DEV
		double[] data = test.ratingDistributionDouble(grapher.RATING_STD_DEV);
		
		System.out.println("HERE");
		for (int i = 0; i < data.length; i++) {
			System.out.println(data[i]);
		}
	}
	
}
