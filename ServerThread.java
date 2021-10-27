import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread {
	private Socket socket;
	private String resultString;
	private final static Logger logr = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private static ArrayList<Vector<Byte>> chunck = new ArrayList<Vector<Byte>>();

	public ServerThread(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			// get the input stream from the connected socket
	        InputStream inputStream = socket.getInputStream();
	        // create a DataInputStream so we can read data from it.
	        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

	        // read the list of messages from the socket
	        Vector<Byte> incomingData = (Vector<Byte>) objectInputStream.readObject();
	        
	        //add the data here
	        if(chunck.size() >0) {
	        	System.out.println("Reached Here");
	        	compareChunks(incomingData);
	        }
	        chunck.add(incomingData);
	        
	        System.out.println("Received [" + incomingData.size() + "] messages from: " + socket);
	        // print out the text of every message
	        System.out.println("All messages:");
	        chunck.forEach((msg)-> System.out.println(msg.toString()));
	        System.out.println("Closing sockets.");
	        socket.close();
	        
		} catch (IOException | ClassNotFoundException ex) {
			logr.log(Level.SEVERE, "Log from the server thread");
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	private void compareChunks(Vector<Byte> incomingMessage) {
		Vector<Byte> vect1, vect2 = new Vector<Byte>();
		vect1 = incomingMessage;
		int size;
		int x[] = new int[chunck.size()];
		for(int i=0; i<chunck.size(); i++) {
			int sum = 0;
			vect2 = chunck.get(i);
			
			if(vect1.size()>vect2.size()) {
				size = vect2.size();
			}else {
				size = vect1.size();
			}
			
			for(int j=0; j<size; j++) {
				sum = sum + vect1.get(j) * vect2.get(j);
			}
			x[i] = sum;
		}
	}

	public static ArrayList<Vector<Byte>> getChunk() {
		return chunck;
	}
}