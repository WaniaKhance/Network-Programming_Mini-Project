import java.util.Random;
import java.util.Vector;

public class Publisher {
	private Vector<Byte> vector_ID = new Vector<Byte>();
	private Vector<Byte> vector_Chunks = new Vector<Byte>();
	private Vector<Byte> vector_Data = new Vector<Byte>();
	
	private Vector<Byte> vector_Data_temp = new Vector<Byte>();

	public static void main(String args[]) {
		String s = "Noah";
		Publisher pub = new Publisher();
		pub.getData(s);
		pub.generateID();
		pub.mixDataID();	
		pub.extractData();
	}

	private void getData(String data) {
		byte[] bytes = data.getBytes();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				vector_Data.add((byte) ((val & 128) == 0 ? 0 : 1));
				val <<= 1;
			}
		}
		for (int i = 0; i < vector_Data.size(); i++) {
			if (vector_Data.get(i) == 0) {
				vector_Data.set(i, (byte) -1);
			}
		}
		//return vector_Data;
	}

	private void generateID() {
		Random r = new Random();
		int max = 1;
		int min = 0;
		int randomNumber;
		for (int i = 0; i < 1000; i++) {
			randomNumber = r.nextInt((max - min) + 1) + min;
			if (randomNumber == 0)
				randomNumber = -1;

			vector_ID.add((byte) randomNumber);
		}
	}

	private void mixDataID() {
		int j = 0;
		for (int i = 0; i < vector_Data.size(); i++) {
			vector_Chunks.add((byte) (vector_ID.get(i) + ((vector_Data.get(i) * vector_ID.get(i)))));
			j++;
			if (j == 1000)
				j = 0;
		}
		System.out.println(vector_Chunks);
	}

	public Vector<Byte> getID(){
		return vector_ID;
	}
	
	public Vector<Byte> getChunk(){
		return vector_Chunks;
	}

	private void extractData() {
		int j = 0;
		for (int i = 0; i < vector_Chunks.size(); i++) {
			vector_Data_temp.add((byte) ((vector_Chunks.get(i) - vector_ID.get(i)) / vector_ID.get(i)));
			j++;
			if (j == 1000)
				j = 0;
		}
		System.out.println(vector_Data_temp);
	}
}
