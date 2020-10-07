import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Future;

public class ClientHandler implements Runnable
{
    private Socket client;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;

    public ClientHandler(Socket clientSocket) throws IOException
    {
        this.client = clientSocket;
        this.inputStream = this.client.getInputStream();
        this.objectInputStream = new ObjectInputStream(inputStream);
    }

    @Override
    public void run()
    {
        try
        {
            Command command = (Command) objectInputStream.readObject();

            switch (command.getCommand())
            {
                case "UGV":
                    UGVHandler ugvHandler = new UGVHandler();
                    break;

                case "Image":
                    System.out.println("A image thread is connected");
                    ImageHandler imageHandler = new ImageHandler(client, objectInputStream, command.getValue());
                    System.out.println("Run ImageHandler");
                    imageHandler.run();
                    System.out.println("ImageHandler done");
                    break;

                case "User":
                    UserHandler userHandler = new UserHandler();
                    break;
            }

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            Thread.currentThread().interrupt();
            System.out.println("Thread was closed");
        }
    }
}
