import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Server
{
    private static final int PORT = 42069;
    private static final int POOL_SIZE = 6;

    private ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);

    private ServerSocket serverSocket;

    private ClientHandler clientHandler;

    private int clientHandlerThreadID;

    private final Map<Integer, ClientHandler> connectedClients;

    private AtomicReference<ObjectFile> cacheObjectFile = new AtomicReference<>();

    private AtomicInteger progress = new AtomicInteger();

    private volatile boolean botRunning = false;

    private MeshroomBot meshroomBot = new MeshroomBot(this);

    public Server()
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

    public synchronized void startBot()
    {
        if (!botRunning) {
            botRunning = true;
            threadPool.execute(meshroomBot);
        }
    }

    public synchronized ObjectFile getObjectFile() { return cacheObjectFile.get(); }

    public synchronized void setObjectFile(ObjectFile objectFile)
    {
        cacheObjectFile.set(objectFile);
    }

    public synchronized void setBotRunningFalse() { botRunning = false; }

    public synchronized void setProgress(int progress)
    {
        this.progress.set(progress);
    }

    public synchronized int getProgress()
    {
        return progress.get();
    }
}
