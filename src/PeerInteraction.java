import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
	
	/**
	 * This class is a simple data structure that represents an entry from the CS125 
	 * lecture feedback database. It contains a pair of encoded NetIDs, a grade, two 
	 * feedback strings, and a date string. Its primary constructor uses an `NRList`
	 * to take a CSV String and parse it as a `PeerInteraction`, with valid 
	 * PeerInteraction`s marked as such if the netIDs of the feedback passed in are
	 * distinct and both found in the `NRList` and all netID fields replaced with 
	 * integer codes.
	 * @author CS125 Research
	 */
	public class PeerInteraction {
		
		private boolean good = false;
		private int grade = -1;
		private int personID = -1;
		private int partnerID = -1;
		private String strengths;
		private String weaknesses;
		private Date date;
		
		/**
		 * Map-less String constructor. Takes an unprocessed line from a CSV
		 * and parses it as a valid PeerInteraction without using an NRList to
		 * verify NetIDs. The constructor marks the entry as bad if the
		 * NetIDs cannot be parsed as integers (It also throws an exception if
		 * the data clearly cannot represent an entry made by a student.
		 * Useful for debugging).
		 * 
		 * @param data An unprocessed line to parse. Must be formatted in the
		 *             form "netid1", "netid2", "5", "Strengths",
		 *             "Weaknesses", "Date";
		 */
		public PeerInteraction(String data){
			good = true;
			checkCorruptData(data);
			String[] separated = splitCommas(data);
			for (int i = 0; i < separated.length; ++i)
				separated[i] = process(separated[i]);
			
			//parse out personID
			personID = -1;
			try{
				personID = Integer.parseInt(separated[0]);
			}catch(Exception e) {/*Do nothing*/}
			
			//parse out the partnerID
			partnerID = -1;
			try{
				partnerID = Integer.parseInt(separated[1]);
			}catch(Exception e) {/*Do nothing*/}
			
			//parse out the grade
			grade = -1;
			try{
				grade = Integer.parseInt(separated[2]);
			}catch(Exception e){/*Do nothing*/}
			
			//check validity of parsed values that are critical
			if (personID == -1 || partnerID == -1 || grade > 10 || grade < 1 || 
			    personID == partnerID) {
				good = false;
			}
			
			//parse strengths and weakness optional text
			strengths = separated[3];
			weaknesses = separated[4];
			
			//create a java date object from the csv file's timestamp
			try{
				date = new 
				    SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(separated[5]);
			}catch(Exception e) {/*Do nothing*/}
		}
		
		/**
		 * String constructor. Takes an unprocessed line from a CSV file and
		 * parses it as a valid PeerInteraction using a passed-in NRList to
		 * verify NetIDs. The constructor marks the entry as
		 * bad if any NetIDs are missing or do not belong to any students in
		 * the NRList. [It also throws an exception if the data clearly
		 * cannot represent an entry made by a student. Useful for debugging.]
		 *
		 *  @param data  An unprocessed line to parse. Must be formatted in the
		 *               form "netid1", "netid2", "5", "Strengths", 
		 *               "Weaknesses", "Date";
		 *  @param map   A list of all netIDs of students in the class
		 *               and their corresponding codes.
		 */
		public PeerInteraction(String data, NRList map){
			good = true;
			String[] separated = checkCorruptData(data);
			for (int i = 0; i < separated.length; ++i)
				separated[i] = process(separated[i]);
			personID = map.getSecret(separated[0]);
			partnerID = map.getSecret(separated[1]);
			grade = Integer.parseInt(separated[2]);
			if (grade > 10 || grade < 1 || personID == partnerID || 
					partnerID == -1 || personID == -1)
				good = false;
			strengths = separated[3];
			weaknesses = separated[4];
			try{
				date = new 
				    SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(separated[5]);
			}catch(Exception e) {/*Do nothing*/}
		}
		
		/**
		 * This merge constructor creates a PeerInteraction whose parameters are
		 * all the same as those of the last valid element in the argument, except
		 * for the written feedback parameters. These parameters are formed
		 * from a processed concatenation of all elements in the argument.
		 * 
		 * @param duplicates An array of PeerInteractions corresponding to the
		 *                   same lecture and having the same personID.
		 */
		public PeerInteraction(List<PeerInteraction> duplicates){
			PeerInteraction ref = duplicates.get(duplicates.size()-1);
			for (int i = duplicates.size(); --i >= 0;){
				if(duplicates.get(i).valid()){
					ref =  duplicates.get(i);
					break;
				}
			}
			personID = ref.personID;
			partnerID = ref.partnerID;
			grade = ref.grade;
			good = ref.good;
			date = ref.getDate();
			if (duplicates.size() == 1){
				strengths = duplicates.get(0).strengths;
				weaknesses = duplicates.get(0).weaknesses;
				return;
			}
			StringBuilder strBuild = new StringBuilder();
			StringBuilder weakBuild = new StringBuilder();
			for (PeerInteraction elem : duplicates){
				strBuild.append('{' + elem.strengths + '}');
				weakBuild.append('{' + elem.weaknesses + '}');
			}
			strengths = strBuild.toString();
			weaknesses = weakBuild.toString();
		}
		
		/**
		 * Returns true if this entry is valid. This implies that both NetID 
		 * codes were entered and actually represent real students and that the 
		 * grade was between 1 and 10 and that the date was a valid date.
		 *
		 *  @return True for valid entries, false for invalid entries.
		 */
		public boolean valid()        {return good;}

		/**
		 *  @return How well the student rated this lecture, from 1 to 10.
		 */
		public int     getGrade()     {return grade;}

		/**
		 *  @return The int code representing the netID of the first person.
		 */
		public int     getPersonID()  {return personID;}

		/**
		 *  @return The int code representing the netID of the second person.
		 */
		public int     getPartnerID() {return partnerID;}

		/**
		 *  @return The student's report on what he/she understood from lecture.
		 */
		public String  getStrength()  { return strengths; }

		/**
		 *  @return The student's report on what he/she did not understand from 
		 *          lecture.
		 */
		public String  getWeakness()  { return weaknesses; }

		/**
		 *  @return A copy of the date of this entry.
		 */
		public Date    getDate()      { return new Date(date.getTime()); }

		/**
		 *  Method that indicates whether this entry has any written
		 *  PeerInteraction information at all.
		 *
		 *  @return True if either weaknesses or strengths is nonempty, false
		 *          otherwise.
		 */
		public boolean hasFeedback(){
			return (strengths.length() > 0 || weaknesses.length() > 0);
		}

		/**
		 *  Returns a simple but complete representation of this entry. Note
		 *  that invalid entries return "INVALID ENTRY". Not for file writing.
		 *
		 *  @return "INVALID_ENTRY" for invalid entries or a single-line 
		 * 			representation of all data members of this entry.
		 */
		public String toString(){
			String result = String.format("Student: %d Partner: %d Grade: %d "
			                  + "Good: \"%s\" Bad: \"%s\" Date: %s", 
			                     personID, partnerID, grade,
			                     strengths, weaknesses, date);
			return (!good ? "INVALID ENTRY: ":"") + result;
		}
		
		/**
		 *  Checks to see that a line passed into the constructor could possibly
		 *  represent an entry (valid or otherwise) made by a student and stored
		 *  in a CSV file. The method throws an IllegalArgumentException if the
		 *  line is invalid, namely if it doesn't have the right number of
		 *  elements or its elements are not surrounded with quotation marks.
		 *
		 *  @param line The line to be validated. 
		 */
		private String[] checkCorruptData(String line){
			SimpleDateFormat tst = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String[] separated;
			try{
				separated = splitCommas(line);
				Date tmp1 = tst.parse(separated[5]);
				int tmp2 = Integer.parseInt(separated[2]);
			}catch(IllegalArgumentException iae){
				throw new 
					IllegalArgumentException(iae.getMessage() + ": " + line);
			}
			catch(IndexOutOfBoundsException ioobe){
			    throw new IllegalArgumentException("Too many CSVs: " + line);
			}
			catch(ParseException pe){
				throw new 
				   IllegalArgumentException("Date/Rating parse error: " + line);
			}
			return separated;
		}

		/**
		 * Method that processes written feedback for better representation. 
		 * For example, should remove newlines and leading white spaces.
		 *
		 * @param An unprocessed string of written feedback.
		 * @return A processed string of written feedback.
		 */
		private String process(String unprocessed){
			if (unprocessed.length() == 0)
				return unprocessed;
			unprocessed = unprocessed.replace("\n", "\\n"); //Removes newlines
			return unprocessed.trim();     //Removes trailing/leading whitespace
		}
	

		 /**
		  * Private helper method for constructor. Splits a String into a 
		  * String[], splitting at (and getting rid of) commas ONLY IF those 
		  * commas are not enclosed by quotes. Also converts double-double 
		  * quotes into ordinary double quotes and implicitly throws an 
		  * exception if the number of partitions is not exactly 6.
		  *
		  *  @params input The string to be parsed from a CVS file.
		  *
		  *  @return A String array containing the formatted contents of input, 
		  *          split at commas iff they are enclosed by quotation marks.
		  */
		private static String[] splitCommas(String input)
		{
			String[] partitions = new String[6];
			int partition = 0;
			StringBuilder build = new StringBuilder();
			boolean evenQuotes = true;
			boolean lastQuote = false;
			int i = 0;
			char current = '\0';
			while (i < input.length()){
				current = input.charAt(i++);
				if (current != ',' || !evenQuotes){
					if (current == '\"'){
						evenQuotes = !evenQuotes;
						if (lastQuote && !evenQuotes){
							build.append(current);
							lastQuote = false;
						}else
							lastQuote = true;
					}else if (!evenQuotes){
						build.append(current);
						lastQuote = false;
					}else if (current != ' ')
						throw new 
						    IllegalArgumentException("Garbage between quotes");
				}else{
					partitions[partition++] = build.toString();
					build.setLength(0);
					lastQuote = false;
				}
			}
			if (!evenQuotes)
				throw new IllegalArgumentException("Missing closing quotes");
			else if (current == ',' || current == '\"')
				partitions[partition++] = build.toString();
			if (partition != 6)
				throw new 
				    IllegalArgumentException("Only " + partition + " CSVs");
			return partitions;
		}

	/**
	 *  A test runner for PeerInteraction that reads from a raw CSV file and 
	 *  constructs corresponding FeedbackEntry objects if possible. The 
	 *  method then prints out all corrupt lines in the file with their
	 *  line numbers, all PeerInteraction objects via PeerInteraction.toString(),
	 *  and finally the number of corrupt and clean lines in the file.
	 *  The user may opt to use an NRList in this testing. Note that valid
	 *  entries are required to have integer netID fields if no NRList is
	 *  used, otherwise the netIDs must both be found in the NRList.
	 * 
	 *  @param args Three possibilities::
	 *              1) *empty*: method prompts user to enter feedback source file 
	 *                  and (optional) NRList source file
	 *              2) {encoded feedback source file}: uses the
	 *                  FeedbackEntry(String) constructor to construct entries
	 *                  from the given source file (uses no NRList)
	 *              3) {feedback source file, roster source file}: uses the
	 *                  FeedbackEntry(String, NRList) constructor to construct
	 *                  entries from the feedback file and uses the roster
	 *                  file to create the needed NRList
	 */ 
	public static void main(String[] args){
		String feedbackSrcFile = null;
		String rosterSrcFile = null;
		boolean requestRoster = false;
		switch (args.length){
		case 2: //Set iff argc == 2
			rosterSrcFile = args[2]; 
		case 1: //Set iff argc == 1 or 2
			feedbackSrcFile = args[1];
			break;
		case 0: //Set iff argc == 0
			requestRoster = true;
			break;
		default: //Anything else is erroneous
			System.out.println("Invalid arg count.");
			return;
		}
		if (feedbackSrcFile == null){
			System.out.print("Enter feedback source file name: ");
			feedbackSrcFile = TextIO.getln();
		}
		if (requestRoster){
			System.out.print("Enter YES to use an NRList: ");
			if (TextIO.getln().toUpperCase().equals("YES")){
				System.out.print("Enter roster source file name: ");
				rosterSrcFile = TextIO.getln();
			}
		}
		NRList encryptor = null;
		if (rosterSrcFile != null){
			try{
				encryptor = new NRList(rosterSrcFile, 10000);
			}catch (Exception e){
				System.out.printf("Problem with file: %s\n", rosterSrcFile);
				return;
			}
		}
		String logFile = "";
		//Request log file from user. This functionality is disabled
		//by the multi-line comments below. Uncomment to create a log 
		//file. Useful if testing from your own machine.
		/*
		System.out.println("Enter the name of a file to log this test's "
		                 + "results, or enter nothing to skip logging.");
		logFile = TextIO.getln().trim();
		*/
		boolean log = false;
		if (logFile.length() != 0){
			TextIO.writeFile(logFile);
			log = true;
		}
		String temp = "";
		String out = "";
		if (rosterSrcFile != null)
		    temp = " using NRList from " + rosterSrcFile;
		out = String.format("\n\nVerifying integrity of %s%s on " 
		                  + new Date() + '\n', feedbackSrcFile, temp);
		System.out.println(out);
		if (log) TextIO.putln(out); //For compactness and readability
		TextIO.readFile(feedbackSrcFile);
		//results
		int invalid = 0, valid = 0;
		
		//testing
		int line = 0;
		ArrayList<String> failures = new ArrayList<String>();
		while (!TextIO.eof()) {
			++line;
			String currentLine = TextIO.getln();
			try {
				//Call appropriate constructor
				PeerInteraction test = (encryptor == null) ?
				    new PeerInteraction(currentLine) :
				    new PeerInteraction(currentLine, encryptor);
				++valid;
				out = test.toString();
				System.out.println(out);
				if (log) TextIO.putln(out);
			} catch(IllegalArgumentException e){
				++invalid;
				out = String.format("Line %d: %s\n", line, e.getMessage());
				failures.add(out);
			}
		}
		if (failures.size() != 0 && log){			
			TextIO.putf("\n\nWARNING: %d CORRUPT LINES!\n\n\n", failures.size());
		}
		for (String elem : failures){
			System.out.print(elem);
			if (log) TextIO.put(elem);
		}
		out = String.format("\n\nThere were %d clean and %d corrupt lines.\n", 
		                    valid, invalid);
		System.out.println(out);
		if (log) TextIO.putln(out);
		System.out.println("Done.");
	}
}
