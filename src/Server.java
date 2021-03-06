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

/**
 * This Server class is responsible for making new TCP connections, handle a list of connected clients and
 * staring the Meshroom bot.
 *
 * @author Sondre Nerhus
 */

public class Server
{
    private final int PORT;
    private final ExecutorService threadPool;
    private final Map<Integer, ClientHandler> connectedClients;
    private final AtomicReference<ObjectFile> cacheObjectFile;
    private final AtomicInteger progress;
    private final MeshroomBot meshroomBot;
    private ServerSocket serverSocket;
    private volatile boolean botRunning = false;

    /**
     * The constructor of the server class.
     *
     * @param port The servers listening port.
     * @param poolSize The pool size for the connected clients.
     */
    public Server(int port, int poolSize)
    {
        this.PORT = port;
        threadPool = Executors.newFixedThreadPool(poolSize);
        connectedClients = new HashMap<>();
        cacheObjectFile = new AtomicReference<>();
        progress = new AtomicInteger();
        meshroomBot = new MeshroomBot(this);
    }

    /**
     * Run method for the server class.
     * Listens to the server socket for new connections and
     * adds the newly connected client to a HashMap with the threadID as a hashkey.
     */
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(PORT);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }

        while (true)
            try {
                System.out.println("----------------------------------------------- Waiting for connection from Client....");

                Socket client = serverSocket.accept();

                System.out.println("Client connected!");
                System.out.println("-----------------------------------------------");

                ClientHandler clientHandler = new ClientHandler(this, client);

                //Executing the run method in ClientHandler with a thread from the thread pool.
                threadPool.execute(clientHandler);

                Thread.sleep(1);

                int clientHandlerThreadID = clientHandler.getThreadID();
                if(clientHandlerThreadID!=-1) {
                    System.out.println("Client with ID: " + clientHandlerThreadID + " was added to the list connected clients!");
                    System.out.println("-----------------------------------------------");
                    connectedClients.put(clientHandlerThreadID, clientHandler);
                }
            } catch (IOException | InterruptedException e) {
                System.err.println(e.getMessage());
            }
    }

    /**
     *Method for getting a client.
     *
     * @param threadID The ID for the client that is returned.
     * @return Returns the client with the given ID.
     */
    public synchronized ClientHandler getClient(int threadID)
    {
        return connectedClients.get(threadID);
    }

    /**
     * Method for getting a list of the connected UGV's ID's.
     * Goes through the connected client and adds the UGV's ID's to a list.
     *
     * @return Returns a list of the connected UGV's ID's.
     */
    public synchronized List getAllUGVIDs()
    {
        List<String> UGVIDs= new ArrayList<>();
        for(ClientHandler client : connectedClients.values())
        {
            if(client.getInstance().equals("UGV")) UGVIDs.add(""+client.getThreadID());
        }
        return UGVIDs;
    }

    /**
     * Method for removing a client.
     *
     * @param threadID The ID for the client that is removed.
     */
    public synchronized void removeClient(int threadID)
    {
        connectedClients.remove(threadID);
    }

    /**
     * Method for starting the MeshroomBot.
     * This method will only allow one bot running at a time.
     */
    public synchronized void startBot()
    {
        if (!botRunning) {
            botRunning = true;
            threadPool.execute(meshroomBot);
        }
    }

    /**
     * Method for getting the current 3D-model generated by Meshroom.
     *
     * @return Returns the current ObjectFile that the server is holding.
     */
    public synchronized ObjectFile getObjectFile() { return cacheObjectFile.get(); }

    /**
     * Method for setting the current 3D-model generated by Meshroom.
     *
     * @param objectFile The ObjectFile that is made by the MeshroomBot.
     */
    public synchronized void setObjectFile(ObjectFile objectFile)
    {
        cacheObjectFile.set(objectFile);
    }

    /**
     * Method for setting the the botRunning flag false.
     */
    public synchronized void setBotRunningFalse() { botRunning = false; }

    /**
     * Method for setting the progress of Meshroom model generation.
     *
     * @param progress The progress with a number from 0 to 12 where 12 is a completed model.
     */
    public synchronized void setProgress(int progress)
    {
        this.progress.set(progress);
    }

    /**
     * Method for getting the progress of Meshroom model generation.
     *
     * @return Returns the progress with a number from 0 to 12 where 12 is a completed model.
     */
    public synchronized int getProgress()
    {
        return progress.get();
    }
}
