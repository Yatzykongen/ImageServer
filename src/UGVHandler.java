import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class UGVHandler
{
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Command command;
    private AtomicReference<Command> cacheCommand = new AtomicReference<>();
    private UserHandler userHandler = null;
    private ImageHandler imageHandler;
    private int numberOfImages = -1;
    private Thread imageHandlerThread;
    private Server server;

    public UGVHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Server server) throws IOException
    {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.server = server;
        imageHandler = new ImageHandler(socket, objectInputStream, server);
        imageHandlerThread = new Thread(imageHandler);
        imageHandlerThread.start();
    }

    public void run()
    {
        try {
            int timesWithoutResponse = 0;
            while (true) {
                Command command = cacheCommand.get();
                if(command!=null && command!=this.command)
                {
                    this.command = command;
                    objectOutputStream.writeObject(command);
                    System.out.println("A command was sent to the UGV");
                    if(command.getCommand().equalsIgnoreCase("start"))
                    {
                        imageHandler.setTotalImages(command.getValue());
                        imageHandler.setUserHandler(userHandler);
                        imageHandler.start();
                        System.out.println("A image handler was started");
                    }
                    if(command.getCommand().equalsIgnoreCase("stop"))
                    {
                        imageHandler.stop();
                        System.out.println("Image handler was stopped by the User ");
                    }
                    timesWithoutResponse = 0;
                }
                if (timesWithoutResponse>10)
                {
                    objectOutputStream.writeObject(new Command("ping",0, null, null));
                    timesWithoutResponse = 0;
                }
                Thread.sleep(100);
                timesWithoutResponse++;
            }
        }catch (IOException e)
        {
            System.err.println(e.getMessage());
        }catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public synchronized void setCommand(Command command)
    {
        cacheCommand.set(command);
    }

    public synchronized void setConnectionToUser(UserHandler userHandler)
    {
        this.userHandler = userHandler;
    }
}
