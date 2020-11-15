import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This UGVHandler class will handle the communication with the UGV.
 * It will also start ImageHandler in a new thread.
 *
 * @author Sondre Nerhus
 */
public class UGVHandler
{
    private final ObjectOutputStream objectOutputStream;
    private final ImageHandler imageHandler;
    private final AtomicReference<Command> cacheCommand;
    private final int threadID;
    private Command command;
    private UserHandler userHandler = null;

    /**
     * The constructor of the UserHandler class.
     *
     * @param objectInputStream The object input stream that is used to receive objects from the UGV.
     * @param objectOutputStream The object output stream that is used to send objects to the UGV.
     * @param server The server that made the TCP connection.
     * @param threadID The ID of the current thread.
     * @throws IOException Throws IOException
     */
    public UGVHandler(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Server server, int threadID) throws IOException
    {
        this.objectOutputStream = objectOutputStream;
        this.threadID = threadID;
        cacheCommand = new AtomicReference<>();
        imageHandler = new ImageHandler(objectInputStream, server);
        Thread imageHandlerThread = new Thread(imageHandler);
        imageHandlerThread.start();
    }

    /**
     * Run method for the UGVHandler class.
     * Handles out going commands to the UGV.
     * Starts the image polling from the UGV when the start command is sent from user.
     * Pings the UGV if no other command is sent to the UGV to see if the connection is still open.
     */
    public void run() throws IOException, InterruptedException
    {
            int timesWithoutResponse = 0;
            while (true) {
                Command command = cacheCommand.get();
                if(command!=null && command!=this.command && command.getCommand()!=null)
                {
                    this.command = command;
                    objectOutputStream.writeObject(command);
                    switch (command.getCommand()) {
                        case "start" -> {
                            printUGVID();
                            System.out.println("A image handler was started by the user");
                            imageHandler.setTotalImages(command.getValue());
                            imageHandler.setUserHandler(userHandler);
                            imageHandler.start();
                        }
                        case "stop" -> {
                            printUGVID();
                            System.out.println("Image handler was stopped by the User");
                            imageHandler.stop();
                        }
                        case "manual" -> {
                            printUGVID();
                            System.out.println("A manual command was sent from user");
                        }
                        case "manualStop" -> {
                            printUGVID();
                            System.out.println("A manual mode was stopped by the user");
                        }
                    }
                    timesWithoutResponse = 0;
                }
                if (timesWithoutResponse>100)
                {
                    objectOutputStream.writeObject(new Command("ping",0, null, null));
                    timesWithoutResponse = 0;
                }
                Thread.sleep(10);
                timesWithoutResponse++;
            }
    }

    /**
     * Sets the current command object given from the user so it can be sent to the UGV.
     *
     * @param command The command that the user want to send to the UGV.
     */
    public synchronized void setCommand(Command command)
    {
        cacheCommand.set(command);
    }

    /**
     * Sets up the direct connection to the user that want's to have control over the UGV.
     *
     * @param userHandler The user that want's to control the UGV.
     */
    public synchronized void setConnectionToUser(UserHandler userHandler)
    {
        this.userHandler = userHandler;
    }

    /**
     * Prints UGV ID in terminal
     */
    private void printUGVID()
    {
        System.out.print("UGV "+threadID+": ");
    }
}