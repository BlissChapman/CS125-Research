public class NetIDPair {
	private String netid;
	private int random;
	
	public NetIDPair(String newNetid, int newRandom)
	{
		netid = newNetid;
		random = newRandom;	
	}
	
	public String getNetID() { return netid; }
	public int getRandom() { return random; }
	public String toString() { return netid + "," + random; }
	public boolean equals(String searchID)
	{
//		System.out.println("Equals was called.");
		return this.netid.equals(searchID);
	}
	
}
