import java.util.Arrays;

/**
 *  A simple pair POD that assigns a random number to a NetID
 *  and stores the two for use in encrypting data.
 *  @author CS125Research
 */
public class NetIDPair {
	private String netID;
	private int random;
	
	/**
	 * Struct constructor.
	 * 
	 * @param newNetid   NetID of student.
	 * @param newRandom  Random code associated with this NetID.
	 */
	public NetIDPair(String newNetID, int newRandom){
		netID = newNetID;
		random = newRandom;	
	}
	
	/**
	 * @return The code associated with this netID.
	 */	
	public int getRandom() { return random; }
	
	/**
	 * @return The string representation of this pair. Just a comma
 	 *         separated juxtaposition of netID and code.
	 */
	public String toString(){
		char[] build = new char[netID.length()];
		Arrays.fill(build, '*');
		build[0] = netID.charAt(0);
		int last = netID.length()-1;
		build[last] = netID.charAt(last);
		return (new String(build)) + ": " + random;
	}
	
	/**
	 * @return True iff searchID matches this NetIDPair's netID.
	 */
	public boolean equals(String searchID){
		return this.netID.equals(searchID);
	}
	
}
