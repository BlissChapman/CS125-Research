/**
 * Abstract weighter class used to assign a weight to a given object
 * (conventionally between 0 and 1) for use in graphing weighted or
 * filtered data.
 */
 
abstract public class Weighter <T> {

    /* (Almost) All Weighters rely on student information. */
    protected final Roster students;
  
    /**
     * Baseline constructor. Sets the value of this Weighter's
     * Roster member variable to the argument passed in.
     *
     * @param students The Roster containing all students, which will
     *                 generally be used for computing weights.
     */
    public Weighter(Roster students){
        this.students = students;
    }
  
    /**
     * Key method for the Weighter class. Assigns a weight to an
     * element passed in, usually based on some information found* in the Roster.
     *
     * @param element An object to be assigned a weight.
     * @return A weight, conventionally between 0 and 1.
     */
    abstract public double weight(T element);
  
}
