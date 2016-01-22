/**
 * Abstract weighter class used to assign a weight to a given object
 * (conventionally between 0 and 1) for use in graphing weighted or
 * filtered data.
 */
 
public interface Weighter <T> {
  
    /**
     * Key method for the Weighter class. Assigns a weight to an
     * element passed in, usually based on some information found* in the Roster.
     *
     * @param element An object to be assigned a weight.
     * @return A weight, conventionally between 0 and 1.
     */
	public double weight(T element);
  
}
