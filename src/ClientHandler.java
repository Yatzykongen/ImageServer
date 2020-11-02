import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private Socket client;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private CommunicationServer communicationServer;
    private String instance;
    private int threadID=-1;
    private UGVHandler ugvHandler;
    private ImageReceiver imageReceiver;
    private UserHandler userHandler;

    public ClientHandler(CommunicationServer communicationServer, ImageServer imageServer, Socket clientSocket) throws IOException
    {
        this.communicationServer = communicationServer;
        this.client = clientSocket;
        this.inputStream = this.client.getInputStream();
        this.outputStream = this.client.getOutputStream();
        this.objectInputStream = new ObjectInputStream(inputStream);
        this.objectOutputStream = new ObjectOutputStream(outputStream);
    }

    @Override
    public void run()
    {
        threadID = (int)Thread.currentThread().getId();
        try
        {
            Command command = (Command) objectInputStream.readObject();
            instance = command.getCommand();
            System.out.println(instance);

            switch (instance)
            {
                case "UGV":
                    System.out.println("A UGV thread is connected");
                    ugvHandler = new UGVHandler(client, objectInputStream, objectOutputStream);
                    System.out.println("Run UGV");
                    ugvHandler.run();
                    System.out.println("UGV done");
                    break;

                case "Image":
                    System.out.println("A image thread is connected");
                    imageReceiver = new ImageReceiver(client, objectInputStream, command.getValue());
                    System.out.println("Run ImageHandler");
                    imageReceiver.run();
                    System.out.println("ImageHandler done");
                    break;

                case "User":
                    System.out.println("A User thread is connected");
                    userHandler = new UserHandler(client, objectInputStream, objectOutputStream, communicationServer);
                    System.out.println("Run User");
                    userHandler.run();
                    System.out.println("User done");
                    break;
            }
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            try {
                client.close();
                System.out.println("Socket was closed for thread "+threadID);
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
            communicationServer.removeClient(threadID);
            Thread.currentThread().interrupt();
            System.out.println("Thread "+threadID+" was closed");
            return;
        }
    }

    public synchronized int getThreadID()
    {
        return threadID;
    }

    public synchronized String getInstance()
    {
        return instance;
    }

    public synchronized UGVHandler getUGVHandler() { return ugvHandler; }
}
