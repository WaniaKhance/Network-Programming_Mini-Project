import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import org.json.simple.JSONObject;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class PublisherPage {

	private static Vector<Byte> vector_ID = new Vector<Byte>();
	private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int STATUS_OK = 200;
	
    public void publishID() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/topic", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
    	Vector<Byte> id = new Vector<Byte>();
    	public MyHandler() {
    	}
        @Override
public void handle(HttpExchange t) throws IOException {
        	
        	id = vector_ID;
        	JSONObject obj = new JSONObject();
            obj.put("id", id);
            
        	final Headers headers = t.getResponseHeaders();

            headers.add("Access-Control-Allow-Origin", "*");

            headers.add("Access-Control-Allow-Methods", "GET, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type,Authorization");

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                t.sendResponseHeaders(204, -1);
                return;
            }

        	final String responseBody = obj.toString();
            headers.set(HEADER_CONTENT_TYPE, String.format("application/json; charset=%s", CHARSET));
            final byte[] rawResponseBody = responseBody.getBytes(CHARSET);
            t.sendResponseHeaders(STATUS_OK, rawResponseBody.length);
            t.getResponseBody().write(rawResponseBody);
        }
    }
    
 public void setID(Vector<Byte> id) {
        	vector_ID = id;
        }




}