/**
 * This project is the main project in the course Real-Time Programming at NTNU in Ålesund.
 *
 * This is the main class for the server. It starts the server by creating a new instance of the server class.
 * If the server would need more than one listening port it would be possible to make more than one instance
 * of the server class with different ports.
 *
 * @author Sondre Nerhus
 * @version 0.1
 */
public class Main {
    public static void main(String[] args)
    {
        Server server = new Server(42069, 6);
        server.run();
    }
}
