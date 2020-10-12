import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable
{
    private Socket client;
    private InputStream inputStream;
    private OutputStream outputStream;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public ClientHandler(Socket clientSocket) throws IOException
    {
        this.client = clientSocket;
        this.inputStream = this.client.getInputStream();
        this.outputStream = this.client.getOutputStream();
        this.objectInputStream = new ObjectInputStream(inputStream);
        this.objectOutputStream = new ObjectOutputStream(outputStream);
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
                    System.out.println("A UGV thread is connected");
                    UGVHandler ugvHandler = new UGVHandler(client, objectInputStream, objectOutputStream);
                    System.out.println("Run UGV");
                    ugvHandler.run();
                    System.out.println("UGV done");
                    break;

                case "Image":
                    System.out.println("A image thread is connected");
                    ImageHandler imageHandler = new ImageHandler(client, objectInputStream, command.getValue());
                    System.out.println("Run ImageHandler");
                    imageHandler.run();
                    System.out.println("ImageHandler done");
                    break;

                case "User":
                    System.out.println("A User thread is connected");
                    UserHandler userHandler = new UserHandler();
                    System.out.println("Run User");
                    userHandler.run();
                    System.out.println("User done");
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
