import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

	private static final String HEADER_CONTENT_TYPE = "Content-Type";
	private static final Charset CHARSET = StandardCharsets.UTF_8;
	private static final int STATUS_OK = 200;

	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/topic", new MyHandler());
		server.setExecutor(null); // creates a default executor

		server.start();

		int port = 10000;

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Server is listening on port " + port);

			while (true) {
				Socket socket = serverSocket.accept();

				System.out.println("New client connected");
				new ServerThread(socket).start();
			}
		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			Vector<Byte> id, data = new Vector<Byte>();

			JSONObject obj = new JSONObject();

            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
			t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
			t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            
        	final Headers headers = t.getResponseHeaders();
        	
            headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
			Map<String, String> parms = Server.queryToMap(t.getRequestURI().getQuery());
			String par = parms.get("id");
			id = parseID(par);
			data = getData(id);

			obj.put(data, new Vector<Byte>());

			System.out.println(par);

			final String responseBody = obj.toString();
			final byte[] dataByte = responseBody.getBytes(CHARSET);
			t.sendResponseHeaders(STATUS_OK, dataByte.length);
			t.getResponseBody().write(dataByte);
		}

		private Vector<Byte> getData(Vector<Byte> id) {
			Vector<Byte> vect1, vect2 = new Vector<Byte>();
			vect1 = id;
			System.out.println("This the id s");


			ArrayList<Vector<Byte>> chunck = new ArrayList<Vector<Byte>>();
			chunck = ServerThread.getChunk();
			int size;
			int x[] = new int[chunck.size()];
			for (int i = 0; i < chunck.size(); i++) {
				int sum = 0;
				vect2 = chunck.get(i);
				int k = 0;
				for (int j = 0; j < 1000 && j < vect2.size(); j++) {
					sum = sum + (vect1.get(j) * vect2.get(j));
					System.out.println("ID: " + vect1.get(j) + " --- Data: " + vect2.get(j) + " --- Sum: " + sum);
				}
				x[i] = sum;
			}

			int best = 0;
			for (int i = 0; i < x.length; i++) {
				if (x[i] > x[best]) {
					best = i;
				}
			}
			for (int i = 0; i < x.length; i++) {
				System.out.println(x[i]);
			}
			System.out.println("RETURNING MESSAGE " + Integer.toString(best));
			return chunck.get(best);
		}

		private Vector<Byte> parseID(String par) {
			String str = par;
			char[] characters = str.toCharArray();
			Vector<Byte> data = new Vector<Byte>();
			int ch = 0;
			boolean negative = false;
			for (int i = 0; i < characters.length; i++) {
				if (characters[i] == '-') {
					negative = true;
					if (i == 0)
						continue;
				} else {
					if (negative == true) {
						ch = Integer.parseInt(String.valueOf(characters[i])) * -1;
						negative = false;
					} else {
						ch = Integer.parseInt(String.valueOf(characters[i]));
					}
					data.add((byte) (ch));
				}
				
			}
			return data;
		}
	}

	public static Map<String, String> queryToMap(String query) {
		Map<String, String> result = new HashMap<String, String>();
		for (String param : query.split("&")) {
			String pair[] = param.split("=");
			if (pair.length > 1) {
				result.put(pair[0], pair[1]);
			} else {
				result.put(pair[0], "");
			}
		}
		return result;
	}
}