import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    // port on which the server will listen.
    private static final int PORT = 8080;

    // A thread-safe list to hold all connections.
    private final List<Client> clients = new CopyOnWriteArrayList<>();

    // A thread-safe counter to generate IDs.
    private final AtomicInteger userIdCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        new Server().startServer();
    }

    public void startServer() {
        System.out.println("Chat Server is starting on port: " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started successfully. Waiting for clients...");

            while (true) {
                // The accept() method blocks until clients connects.
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());

                String username = "User" + userIdCounter.incrementAndGet();

                Client clientHandler = new Client(clientSocket, this, username);

                clients.add(clientHandler);

                // Start a new thread for the client.
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error in the server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message, Client sender) {
        System.out.println("Broadcasting message: " + message);
        for (Client client : clients) {
            client.sendMessage(message);
        }
    }

    public void removeClient(Client clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client " + clientHandler.getUsername() + " has disconnected.");
    }
}
