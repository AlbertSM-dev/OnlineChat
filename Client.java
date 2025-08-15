import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


 //Client connection thread.
public class Client implements Runnable {

    private final Socket clientSocket;
    private final Server server;
    private final String username;

    private PrintWriter out;
    private BufferedReader in;

    public Client(Socket socket, Server server, String username) {
        this.clientSocket = socket;
        this.server = server;
        this.username = username;
    }

    @Override
    public void run() {
        try {
            // Communication with the client.
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true); // 'true' for auto-flushing

            sendMessage("Welcome to the chat! You are " + username);
            server.broadcastMessage(username + " has joined the chat.", this);

            // Read messages from the client.
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                String broadcastMessage = username + ": " + clientMessage;
                server.broadcastMessage(broadcastMessage, this);
            }

        } catch (IOException e) {
            System.err.println("An error occurred with client " + username + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void cleanup() {
        server.broadcastMessage(username + " has left the chat.", this);
        
        server.removeClient(this);
        
        // Close socket.
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }
}
