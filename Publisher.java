import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Publisher extends JFrame implements ActionListener {
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 310;
	private static final int FRAME_X_ORIGIN = 150;
	private static final int FRAME_Y_ORIGIN = 250;
	private static final String EMPTY_STRING = "";
	private static final String NEWLINE = System.getProperty("line.separator");

	private JButton publishButton;

	private JTextField inputLine;
	private JTextArea textArea;

	private  Vector<Byte> vector_ID = new Vector<Byte>();
	private Vector<Byte> vector_Chunks = new Vector<Byte>();
	private Vector<Byte> vector_Data = new Vector<Byte>();

	private Vector<Byte> vector_Data_temp = new Vector<Byte>();
	static int socketCounter;

	public static void main(String args[]) throws Exception {
		Publisher pub = new Publisher();
		pub.LABGUI();
		pub.setVisible(true);
	}

	private void getData(String data) {
		Vector<Byte> vector_temp = new Vector<Byte>();
		byte[] bytes = data.getBytes();
		for (byte b : bytes) {
			int val = b;
			for (int i = 0; i < 8; i++) {
				vector_temp.add((byte) ((val & 128) == 0 ? 0 : 1));
				val <<= 1;
			}
		}
		for (int i = 0; i < vector_temp.size(); i++) {
			if (vector_temp.get(i) == 0) {
				vector_temp.set(i, (byte) -1);
			}
		}
		vector_Data = vector_temp;
		// return vector_Data;
	}

	private void generateID() {
		Vector<Byte> vector_temp = new Vector<Byte>();
		Random r = new Random();
		int max = 1;
		int min = 0;
		int randomNumber;
		for (int i = 0; i < 1000; i++) {
			randomNumber = r.nextInt((max - min) + 1) + min;
			if (randomNumber == 0)
				randomNumber = -1;

			vector_temp.add((byte) randomNumber);
		}
		vector_ID = vector_temp;
	}

	private void mixDataID() {
		int j = 0;
		Vector<Byte> vector_temp = new Vector<Byte>();
		for (int i = 0; i < vector_Data.size(); i++) {
			vector_temp.add((byte) (vector_ID.get(j) + ((vector_Data.get(i) * vector_ID.get(j)))));
			j++;
			if (j == 1000)
				j = 0;
		}
		vector_Chunks = vector_temp;
		System.out.println(vector_Chunks);
	}

	public Vector<Byte> getID() {
		return vector_ID;
	}

	public Vector<Byte> getChunk() {
		return vector_Chunks;
	}

	private void extractData() {
		int j = 0;
		for (int i = 0; i < vector_Chunks.size(); i++) {
			vector_Data_temp.add((byte) ((vector_Chunks.get(i) - vector_ID.get(j)) / vector_ID.get(j)));
			j++;
			if (j == 1000)
				j = 0;
		}
		System.out.println(vector_Data_temp);
	}

	private void publishData() {

		try {
			// creating a socket to connect to the server
			Socket socket = new Socket("localhost", 10000);

			// get the output stream from the socket.
			OutputStream outputStream = socket.getOutputStream();
			// create an object output stream from the output stream so we can send an
			// object through it
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
			objectOutputStream.writeObject(vector_Chunks);

			socket.close();
		} catch (Exception e) {
			System.out.println("Error");
		}
	}

	public void LABGUI() {
		Container contentPane;
		// set the frame properties
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setResizable(false);
		setTitle("Net Programming LAB-2");
		setLocation(FRAME_X_ORIGIN, FRAME_Y_ORIGIN);
		contentPane = getContentPane();
		contentPane.setLayout(new FlowLayout());
		textArea = new JTextArea();
		textArea.setColumns(50);
		textArea.setRows(12);
		// textArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		textArea.setEditable(true);
		textArea.setLineWrap(true);

		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		add(scroll);

		// create and place two buttons on the frame
		publishButton = new JButton("Publish Message");
		contentPane.add(publishButton);

		// register this frame as an action listener of the two buttons

		publishButton.addActionListener(this);

		// register 'Exit upon closing' as a default close operation
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	int counter = 0;

	public void actionPerformed(ActionEvent event) {
		String s = "";
		PublisherPage publishPage = new PublisherPage();
		if (event.getSource() instanceof JButton) {
			JButton clickedButton = (JButton) event.getSource();
			if (clickedButton == publishButton) {
				s = textArea.getText().toString();
				System.out.println(s);
				this.getData(s);
				this.generateID();

				if (counter == 0) {
					try {
						publishPage.setID(vector_ID);
						publishPage.publishID();
						counter = 1;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				publishPage.setID(vector_ID);

				this.mixDataID();
				this.extractData();

				// send the data to the server
				this.publishData();

			}
		} else {
			clearText();
		}
	}

	private void addText(String newline) {
		textArea.append(newline + NEWLINE);
		inputLine.setText("");
	}

	private void clearText() {
		textArea.setText(EMPTY_STRING);
		inputLine.setText(EMPTY_STRING);
	}

}
