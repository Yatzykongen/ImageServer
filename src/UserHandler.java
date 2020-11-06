import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class UserHandler {
    private Socket client;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    private Server server;
    private boolean auto = false;
    private UGVHandler UGVClient = null;
    private List<String> listUGVs = null;
    private ImageObject imageObject;
    private AtomicReference<ImageObject> cacheImage = new AtomicReference<>();
    private AtomicReference<ObjectFile> cachedFile = new AtomicReference<>();
    private ObjectFile objectFile = null;

    public UserHandler(Socket client, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Server server) {
        this.server = server;
        this.client = client;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public void run() {
        try {
            while (true) {

                Command command = (Command) objectInputStream.readObject();



                if (command != null) {
                    if (command.getWasd()!=null) {
                        System.out.println("   W: "+ command.getWasd()[0]+"   A: "+ command.getWasd()[1]+"   S: "+ command.getWasd()[2]+"   D: "+ command.getWasd()[3]);
                    }

                    switch (command.getCommand()) {
                        case "updateUGVList":
                            listUGVs = server.getAllUGVIDs();
                            Command listCommand = new Command("ListUGV", 0, null, listUGVs);
                            objectOutputStream.writeObject(listCommand);
                            //System.out.println("Sent list to user");
                            break;

                        case "UGVSelected":
                            if (listUGVs != null && listUGVs.contains("" + command.getValue())) {
                                UGVClient = server.getClient(command.getValue()).getUGVHandler();
                                if (UGVClient != null) {
                                    UGVClient.setConnectionToUser(this);
                                }
                                System.out.println("Set the chosen UGV to " + command.getValue());
                            }
                            break;

                        case "updateUGVImage":
                            ImageObject imageObject = cacheImage.get();
                            if(imageObject!=null && imageObject!=this.imageObject)
                            {
                                this.imageObject = imageObject;
                                objectOutputStream.writeObject(imageObject);
                                System.out.println(imageObject.getName()+" was sent to user");
                            }else
                            {
                                objectOutputStream.writeObject(null);
                            }
                            break;

                        case "updateServerImage":
                            BufferedImage bufferedImage = takeScreenshot();
                            bufferedImage = cropImage(bufferedImage, 200, 1000,2100, 1300);
                            if (bufferedImage!=null) {
                                ImageObject imageObjectScreenshot = new ImageObject("screenshot", 0, bufferedImageToByteArray(bufferedImage), null, "png");
                                System.out.println(imageObjectScreenshot.getName());
                                objectOutputStream.writeObject(imageObjectScreenshot);
                                System.out.println("A screenshot was sent to user");
                            }
                            break;

                        case "updateObjectFile":
                            ObjectFile objectFile = server.getObjectFile();
                            if(objectFile!=null && objectFile!=this.objectFile)
                            {
                                this.objectFile = objectFile;
                                objectOutputStream.writeObject(objectFile);
                                System.out.println(objectFile.getObjFileName()+" was sent to user");
                            }else
                            {
                                objectOutputStream.writeObject(null);
                            }
                            break;

                        case "updateProgress":
                            Command progressCmd = new Command("progress", server.getProgress(), null, null);
                            objectOutputStream.writeObject(progressCmd);
                            break;

                        case "start":
                        case "stop":
                        case "manual":
                        case "manualStop":
                            setCommandForUGV(command);
                            System.out.println("Gave command to UGV handler");
                            break;

                        default:
                            System.out.println("Wrong command was sent to server");
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            ;
        } catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
            ;
        }
    }

    public synchronized void setImage(ImageObject imageObject) {
        cacheImage.set(imageObject);
    }

    public synchronized void setObjectFile(ObjectFile objectFile) {cachedFile.set(objectFile);}

    public synchronized void setCommandForUGV(Command command)
    {
        if (UGVClient != null) {
            UGVClient.setCommand(command);
        }
    }

    private BufferedImage takeScreenshot()
    {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        } catch (AWTException e) {
            System.err.println(e.getMessage());
        }
        return bufferedImage;
    }

    private BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY)
    {
        return bufferedImage.getSubimage(startX, startY, endX-startX, endY-startY);
    }

    private static final byte[] bufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
