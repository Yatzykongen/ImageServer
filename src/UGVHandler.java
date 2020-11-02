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

    public UGVHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException
    {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public void run()
    {
        try {
            int timesWithoutRespons = 0;
            while (true) {
                Command command = cacheCommand.get();
                if(command!=null && command!=this.command)
                {
                    this.command = command;
                    objectOutputStream.writeObject(command);
                    System.out.println("command was sendt to ugv");
                    timesWithoutRespons = 0;
                }
                if (timesWithoutRespons>100)
                {
                    objectOutputStream.writeObject(new Command("ping",0, null, null));
                    timesWithoutRespons = 0;
                }
                Thread.sleep(100);
                timesWithoutRespons++;
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
