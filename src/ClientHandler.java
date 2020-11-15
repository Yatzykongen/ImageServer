import java.io.*;
import java.net.Socket;

/**
 * This ClientHandler class will store the current thread ID of the current runnable instance of the object.
 * It will also get an indication of what kind of client it is connected to so it can direct the client
 * to the correct communication protocol.
 *
 * @author Sondre Nerhus
 */

public class ClientHandler implements Runnable
{
    private final Socket client;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final Server server;
    private String instance;
    private volatile int threadID=-1;
    private UGVHandler ugvHandler;

    /**
     * The constructor of the ClientHandler class.
     *
     * @param server The server that made the TCP connection.
     * @param clientSocket The given socket by the server.
     * @throws IOException Throws IOException if connection is lost.
     */
    public ClientHandler(Server server, Socket clientSocket) throws IOException
    {
        this.server = server;
        this.client = clientSocket;
        InputStream inputStream = this.client.getInputStream();
        OutputStream outputStream = this.client.getOutputStream();
        this.objectInputStream = new ObjectInputStream(inputStream);
        this.objectOutputStream = new ObjectOutputStream(outputStream);
    }

    /**
     * Run method for the ClientHandler class.
     * Get's the current thread ID and
     * connects the newly connected client to the right communication protocol.
     */
    @Override
    public void run()
    {
        threadID = (int)Thread.currentThread().getId();
        try
        {
            Command command = (Command) objectInputStream.readObject();
            instance = command.getCommand();

            switch (instance) {
                case "UGV" -> { // If the client is a UGV it is handled with the UGV protocol.
                    System.out.println("A thread is given to the UGV with ID:"+threadID);
                    System.out.println("-----------------------------------------------");
                    ugvHandler = new UGVHandler(objectInputStream, objectOutputStream, server, threadID);
                    System.out.println("Redirects the client to the UGV protocol");
                    System.out.println("-----------------------------------------------");
                    ugvHandler.run();
                }
                case "User" -> { // If the client is a user it is handled with the user protocol.
                    System.out.println("A thread is given to the user with ID:"+threadID);
                    System.out.println("-----------------------------------------------");
                    UserHandler userHandler = new UserHandler(objectInputStream, objectOutputStream, server, threadID);
                    System.out.println("Redirects the client to the user protocol");
                    System.out.println("-----------------------------------------------");
                    userHandler.run();
                }
            }
        }
        catch (IOException | ClassNotFoundException | InterruptedException e)
        {
            e.printStackTrace();
        } finally
        {
            System.out.println(instance+" ID:"+threadID+" disconnected");
            System.out.println("-----------------------------------------------");
            try {
                client.close();
                System.out.println("Socket was closed for thread "+threadID);
                System.out.println("-----------------------------------------------");
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(threadID);
            Thread.currentThread().interrupt();
            System.out.println("Thread "+threadID+" was closed and and the client was removed from the connected clients list");
            System.out.println("-----------------------------------------------");
        }
    }

    /**
     * Method for getting the current thread ID.
     *
     * @return Returns the current thread ID
     */
    public synchronized int getThreadID()
    {
        return threadID;
    }

    /**
     * Method for getting the instance of the client connected.
     *
     * @return Returns the instance of the client connected.
     */
    public synchronized String getInstance()
    {
        return instance;
    }

    /**
     * Method for getting the UGVHandler object that was created by the CLientHandler.
     *
     * @return Returns the UGVHandler object that was created by the CLientHandler.
     */
    public synchronized UGVHandler getUGVHandler() { return ugvHandler; }
}