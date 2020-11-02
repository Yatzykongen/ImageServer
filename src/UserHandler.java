import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class UserHandler {
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private CommunicationServer communicationServer;
    private boolean auto = false;
    private UGVHandler UGVClient = null;
    private List<String> listUGVs = null;

    public UserHandler(Socket client, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, CommunicationServer communicationServer) {
        this.communicationServer = communicationServer;
        this.client = client;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;

    }

    public void run() {
        try {
            while (true) {
                Command command = (Command) objectInputStream.readObject();
                if(command!=null) {
                    if (command.getCommand().equalsIgnoreCase("UGVList")) {
                        listUGVs = communicationServer.getAllUGVIDs();
                        Command listCommand = new Command("ListUGV", 0, null, listUGVs);
                        objectOutputStream.writeObject(listCommand);
                        System.out.println("Sendt list to user");
                    }
                    if (command.getCommand().equalsIgnoreCase("selected UGV") && listUGVs != null && listUGVs.contains("" + command.getValue())) {
                        UGVClient = communicationServer.getClient(command.getValue()).getUGVHandler();
                        if(UGVClient != null)
                        {
                            UGVClient.setConnectionToUser(this);
                        }
                        System.out.println("Set the chosen UGV to "+command.getValue());
                    }
                    if (UGVClient != null) {
                        UGVClient.setCommand(command);
                    }
                }
                if(command.getCommand().equals("directions") && !auto) {
                    boolean[] wasd = command.getWasd();
                    System.out.println("Directions: w = " + wasd[0] + ", a = " + wasd[1] + ", s = " + wasd[2] + ", d = " + wasd[3]);
                    System.out.println("Speed: "+command.getValue());
                }
                if(command.getCommand().equals("start") && !auto)
                {
                    System.out.println("Start autonom controll");
                    auto = true;
                }
                if(command.getCommand().equals("stop") && auto)
                {
                    System.out.println("Stop autonom controll");
                    auto = false;
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());;
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());;
        }
    }
}
