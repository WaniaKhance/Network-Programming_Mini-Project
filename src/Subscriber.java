import java.util.Vector;

public class Subscriber {
	
	private static Vector<Byte> vector_Data = new Vector<Byte>();
	private static Vector<Byte> vector_ID = new Vector<Byte>();
	private static Vector<Byte> vector_Chunks = new Vector<Byte>();
	
	public static void main(String args) {
		Publisher sub = new Publisher();
		vector_ID = sub.getID();
		vector_Chunks = sub.getChunk();
		
		extractData();
		
	}
	
	private static void extractData() {
		int j = 0;
		for (int i = 0; i < vector_Chunks.size(); i++) {
			vector_Data.add((byte) ((vector_Chunks.get(i) - vector_ID.get(i)) / vector_ID.get(i)));
			j++;
			if (j == 1000)
				j = 0;
		}
		System.out.println(vector_Data);
	}
}
