import java.util.Arrays;
import java.io.Serializable;

/**
 * A simple pair POD that couples an integer ID code with 
 * a NetID and stores the two for use in encrypting data.
 * @author CS125Research
 */
public class NetIDPair implements Serializable{	
	/**
	 * 
	 */
	private static final long serialVersionUID = -873475199284705672L;
	
	private String netID;
	private int code;
	
	/**
	 * Struct constructor.
	 * 
	 * @param newNetid  NetID of student.
	 * @param newCode   Code associated with this NetID.
	 */
	public NetIDPair(String newNetID, int newCode){
		netID = newNetID;
		code = newCode;	
	}
	
	/**
	 * @return The code associated with this netID.
	 */	
	public int getCode() { return code; }
	
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
		return (new String(build)) + ": " + code;
	}
	
	/**
	 * @return True iff searchID matches this NetIDPair's netID.
	 */
	public boolean equals(String searchID){
		return this.netID.equals(searchID);
	}
	
	/**
	 * @return True iff other.netID matches this NetIDPair's netID.
	 */
	public boolean equals(NetIDPair other){
		return this.netID.equals(other.netID);
	}
	
}
