import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommunicationServer
{
    private static final int PORT = 42069;
    private static final int POOL_SIZE = 5;

    private ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);

    private ServerSocket serverSocket;

    private ClientHandler clientHandler;

    private int clientHandlerThreadID;

    private final Map<Integer, ClientHandler> connectedClients;

    public CommunicationServer()
    {
        connectedClients = new HashMap<>();
    }

    public void run()
    {
        boolean serverOn = true;

        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }

        while (serverOn)
        {
            try
            {
                System.out.println("Waiting for connection from Client....");

                Socket client = serverSocket.accept();

                System.out.println("Client connected!");

                clientHandler = new ClientHandler(this,null, client);

                threadPool.execute(clientHandler);

                Thread.sleep(1);

                clientHandlerThreadID = clientHandler.getThreadID();
                System.out.println("New connection accepted, client ID = " + clientHandlerThreadID);
                connectedClients.put(clientHandlerThreadID, clientHandler);
            } catch (IOException e)
            {
                System.err.println(e.getMessage());
            }catch (InterruptedException e)
            {
                System.err.println(e.getMessage());
            }
        }
    }

    public synchronized ClientHandler getClient(int threadID)
    {
        return connectedClients.get(threadID);
    }

    public synchronized List getAllUGVIDs()
    {
        List<String> UGVIDs= new ArrayList<>();
        for(ClientHandler client : connectedClients.values())
        {
            if(client.getInstance().equals("UGV")) UGVIDs.add(""+client.getThreadID());
        }
        return UGVIDs;
    }

    public void removeClient(int threadID)
    {
        connectedClients.remove(threadID);
    }
}
