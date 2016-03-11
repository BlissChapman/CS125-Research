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
	
	/**
	 * 
	 * @author Navneeth Jayendran
	 *
	 * How to read this verbose tripe:
	 * 1) NaturalOrder is the class name.
	 * 2) NaturalComparator is parameterized to type T. So you may instantiate
	 *    it and use it like:
	 *      NaturalOrder<Integer> intcomp = new NaturalOrder<>();
	 *      print intcomp(new Integer(10), new Integer(15));  //prints -1
	 *      print intcomp(20, 20) //prints 0... I think. Assuming autoboxing.
	 * 3) T extends Comparable<T>, which means that the class T must implement
	 *    the method T.compareTo(T other).
	 * 4) Plug it into any function that requires a Comparator to sort data and
	 *    whatnot.
	 */
	public class NaturalOrder<T extends Comparable<T>>
	  implements Comparator<T>
	{
		/** 
		 * Because "first" is of type T, which implements Comparable<T>,
		 * we know that we are allowed to call first.compareTo(second).
		 * 
		 * @param first  Thing to compare.
		 * @param second Thing to be compared.
		 * @return 1 if first > second, -1 if first < second, 0 otherwise
		 */
		public int compare(T first, T second){
			return first.compareTo(second);
		}
	};
	
	/**
	 * Takes an ArrayList of generic type, sorts it in alphabetical order, and
	 * returns a sorted ArrayList with the same elements as the argument but
	 * without any duplicate elements.
	 * 
	 * @param items  A list of unsorted items that may possess duplicates.
	 * @return An ArrayList containing no two copies of the same element, such
	 *         that each element therein was also found in the input.
	 */
	public static <T extends Comparable<T>> ArrayList<T> 
	  withoutDuplicates(List<T> items)
	{
		ArrayList<T> results = new ArrayList<T>();
		if (items.size() == 0) 
			return results;
		Collections.sort(items);
		T last = items.get(0);
		results.add(last);
		for (T elem : items){
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
	public static int randomGeometric(double p){
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
	public static ArrayList<String> generateNetIDs(int number){
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
			netIDs = withoutDuplicates(netIDs);
		}
		return netIDs;
	}
	
	/**
	 * This method returns an ArrayList of evenly spaced dates meant to
	 * imitate lecture dates.
	 * 
	 * @param days  The number of days.
	 * @param start The first date in the range.
	 * @param stop  The upper bound on the last date.
	 * @return The 
	 */
	public static ArrayList<Date> generateDateRange(int days, Date start, Date stop){
		long interval = (stop.getTime() - start.getTime())/days;
		ArrayList<Date> span = new ArrayList<>();
		span.add(start);
		long currTime = start.getTime();
		while (days-- > 0){
			currTime += interval;
			span.add(new Date(currTime));
		}
		return span;
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
	public static TreeMap<String, Integer> 
	    dictFromInteractions(Iterable<PeerInteraction> samples)
	{
		TreeMap<String, Integer> output = new TreeMap();
		for (PeerInteraction entry : samples)
			if (entry.hasFeedback()){
				String cat = entry.getStrength() + ' ' + entry.getWeakness();
				Scanner wordExtractor = new Scanner(cat);
				while (wordExtractor.hasNext()){
					String curr = wordExtractor.next().replace("\"", "\"\"");
					if (output.containsKey(curr)){
						//Good thing Java 7 has no "replaceValue()" method \s
						int inc = output.get(curr) + 1;
						output.remove(curr);
						output.put(curr, inc); //At least there's autoboxing
					}
				}
				wordExtractor.close();
			}
		//Collections.sort(output); //TreeMap is already sorted :)
		return output;
	}
	
	/**
	 * Helper function that pushes an element in an almost-sorted list
	 * forward until it is in its right position. Used as a helper for
	 * insertion-sort, among other things.
	 * 
	 * @param coll     The collection to be sorted.
	 * @param idx      The index
	 * @param ordering A comparator dictating how the list must be sorted.
	 * @return true if a duplicate was found.
	 */
	public static <T> boolean riseSorted(List<T> coll, int idx,
			                            Comparator<T> ordering){
		T tmp = null;
		while (idx > 0 &&
			   ordering.compare(coll.get(idx-1), coll.get(idx)) > 0){
			tmp = coll.get(idx);
			coll.set(idx, coll.get(idx-1));
			coll.set(idx-1, tmp);
			--idx;
		}
		if (idx > 0 && coll.get(idx).equals(coll.get(idx-1)))
			return true;
		return false;
	}
	
	/**
	 * Insertion sort is a simple sorting algorithm that essentially sorts
	 * a list of size N by (recursively)
	 * 1) Sorting the first k elements.
	 * 2) Pushing the (k+1)th element forward until the first (k+1) elements
	 *    in the list are sorted.
	 * Insertion sort is ideal for lists that are almost sorted already.
	 * In such cases, the runtime is linear (whereas mergesort and
	 * quicksort will be linearithmic at best).
	 * 
	 * @param coll     A list of elements to be sorted.
	 * @param ordering The
	 */
	public static <T> void insertionSort(List<T> coll, Comparator<T> ordering){
		for (int k = 1; k < coll.size(); ++k)
			riseSorted(coll, k, ordering);
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
		final int LECTURES = 25;
		final int STUDENTS = 100;
		final int CAPACITY = 2*STUDENTS; //Must be greater than STUDENTS
		
		/* Creates a list of randomly generated netIDs.*/
		ArrayList<String> fakeNetIDs = Utilities.generateNetIDs(STUDENTS);
		
		/* Creates a list of evenly spaced dates starting at "then" and ending
		 * before "now". The number of dates creates is given by LECTURES.
		 */
		Date now = new Date();
		Date then = new Date(now.getTime() - 4l*30l*24l*3600000l); //~4 months ago
		ArrayList<Date> dateRange = 
			Utilities.generateDateRange(LECTURES, now, then);
		ArrayList<PeerInteraction> samples = new ArrayList<>();
		Scanner sc = 
		    new Scanner(new File("src/peerInteractions.fa2015.final.csv"));
		while (sc.hasNextLine())
			samples.add(new PeerInteraction(sc.nextLine()));
		
		/* Extracts words from a real csv of PeerInteractions and puts all
		 * those words in a dictionary.*/
		TreeMap<String, Integer> mapDict = Utilities.dictFromInteractions(samples);
		ArrayList<String> dictionary = new ArrayList<>();
		for (String elem : mapDict.keySet())
			dictionary.add(elem);
		NRList converter  = new NRList(fakeNetIDs, CAPACITY);
		ProtoApp trialrun = new ProtoApp(converter, dateRange);
		System.out.println();
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
		ArrayList<String> fakeRawFeedback = 
		    Utilities.generateFeedback(LECTURES, then, now, 
		    		                  fakeNetIDs, dictionary, config);
		ArrayList<Integer> allCodes = new ArrayList<>();
		for (NetIDPair elem : converter)
				allCodes.add(elem.getCode());
		ArrayList<PeerInteraction> fakeEntries = new ArrayList<>();
		String last = "";
		System.out.println("Dictionary: \n\n");
		for (String elem : dictionary) if (!elem.equals(last)){
			System.out.println(elem);
			last = elem;
		}
		System.out.println("\n\nFake Roster: ");
		for (String elem : fakeNetIDs)
			System.out.println(elem);
		System.out.println("\n\nFake Feedback: ");
		int count = 0;
		int validCount = 0;
		int feedbackCount = 0;
		for (String elem : fakeRawFeedback){
			PeerInteraction curr = new PeerInteraction(elem, converter);
			++count;
			if (curr.valid())
				++validCount;
			if (curr.hasFeedback())
				++feedbackCount;
			if (trialrun.students.get(curr.getPersonID()) == null){
				System.out.println("Did not find " + curr.getPersonID());
				TextIO.getln();
			}
			System.out.println(elem);
		}
		System.out.printf("\n\"Attendance\": %.2f%% (Doesn't take into "
		    + "account duplicates, so expect this to be higher than the "
		    + "config parameter)\n", (100.0*count)/(LECTURES*STUDENTS));
		System.out.printf("Validity: %.2f%%\n", (100.0*validCount)/count);
		System.out.printf("Responsiveness: %.2f%%\n", 
		                  (100.0*feedbackCount)/count);
		
		trialrun.addFeedback(samples);
		Lecture test = trialrun.lectures.get(5);
		trialrun.entryGrapher.RATING_STD_DEV.minDev = 0;
		trialrun.entryGrapher.RATING_STD_DEV.maxDev = 3;
		double[] rawdata = test.ratingDistributionDouble(trialrun.entryGrapher.ALL_ENTRIES);
		double[] data = test.ratingDistributionDouble(trialrun.entryGrapher.RATING_STD_DEV);
		System.out.println(Arrays.toString(rawdata));
		System.out.println(Arrays.toString(data));
	}
	
}
