import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This UserHandler class will handle the communication with the user and it will
 * access available information that the user needs from the server.
 * It will also take commands that are meant for the UGV and send it through to the UGV.
 *
 * @author Sondre Nerhus
 */
public class UserHandler {
    private final AtomicReference<ImageObject> cacheImage;
    private final ObjectInputStream objectInputStream;
    private final ObjectOutputStream objectOutputStream;
    private final Server server;
    private final int threadID;
    private UGVHandler UGVClient = null;
    private List<String> listUGVs = null;
    private ImageObject imageObject = null;
    private ObjectFile objectFile = null;

    /**
     * The constructor of the UserHandler class.
     *
     * @param objectInputStream The object input stream that is used to receive objects from the user.
     * @param objectOutputStream The object output stream that is used to send objects to the user.
     * @param server The server that made the TCP connection.
     * @param threadID The ID of the current thread.
     */
    public UserHandler(ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, Server server, int threadID) {
        this.server = server;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
        this.threadID = threadID;
        cacheImage = new AtomicReference<>();
    }

    /**
     * Run method for the UserHandler class.
     * Handles incoming commands from the user and gives the user available information from the server.
     * Lets the user connect to a chosen UGV and send command's to it.
     */
    public void run() throws IOException, ClassNotFoundException {
            while (true) {
                Command command = (Command) objectInputStream.readObject();

                if (command != null) {
                    switch (command.getCommand()) {
                        case "updateUGVList":
                            listUGVs = server.getAllUGVIDs();
                            Command listCommand = new Command("ListUGV", 0, null, listUGVs);
                            objectOutputStream.writeObject(listCommand);
                            break;

                        case "UGVSelected":
                            if (listUGVs != null && listUGVs.contains("" + command.getValue())) {
                                UGVClient = server.getClient(command.getValue()).getUGVHandler();
                                if (UGVClient != null) {
                                    UGVClient.setConnectionToUser(this);
                                }
                                printUserID();
                                System.out.println("Set the chosen UGV to " + command.getValue());
                            }
                            break;

                        case "updateUGVImage":
                            ImageObject imageObject = cacheImage.get();
                            if(imageObject!=null && imageObject!=this.imageObject)
                            {
                                this.imageObject = imageObject;
                                objectOutputStream.writeObject(imageObject);
                                printUserID();
                                System.out.println(imageObject.getName()+" was sent from server");
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
                                printUserID();
                                System.out.println("A screenshot was sent from server");
                            }
                            break;

                        case "updateObjectFile":
                            ObjectFile objectFile = server.getObjectFile();
                            if(objectFile!=null && objectFile!=this.objectFile)
                            {
                                this.objectFile = objectFile;
                                objectOutputStream.writeObject(objectFile);
                                printUserID();
                                System.out.println(objectFile.getObjFileName()+" was sent from server");
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
                            printUserID();
                            System.out.println("Gave command to UGV");
                            break;

                        default:
                            printUserID();
                            System.out.println("Wrong command was sent to server");
                            break;
                    }
                }
            }
    }

    /**
     * Sets the current image object given from the UGV so it can be sent to the user.
     *
     * @param imageObject The object that is storing the current image that was taken by the UGV
     */
    public synchronized void setImage(ImageObject imageObject) {
        cacheImage.set(imageObject);
    }

    /**
     * Gives the command from the user to the chosen UGV
     *
     * @param command The command object that was sent from the user.
     */
    public synchronized void setCommandForUGV(Command command)
    {
        if (UGVClient != null) {
            UGVClient.setCommand(command);
        }
    }

    /**
     * That's a screenshot of the main monitor from the computer running the server.
     *
     * @return Returns a buffered image of the screenshot.
     */
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

    /**
     * Crops a image to the chosen locations.
     *
     * @param bufferedImage The image that will be cropped.
     * @param startX The x coordinate of the pixel on the bottom left corner of the crop.
     * @param startY The y coordinate of the pixel on the top right corner of the crop.
     * @param endX The x coordinate of the pixel on the bottom right corner of the crop.
     * @param endY The y coordinate of the pixel on the bottom right corner of the crop.
     * @return Returns the cropped image.
     */
    private BufferedImage cropImage(BufferedImage bufferedImage, int startX, int startY, int endX, int endY)
    {
        return bufferedImage.getSubimage(startX, startY, endX-startX, endY-startY);
    }

    /**
     * Transforms a buffered image to a array of bytes.
     *
     * @param bufferedImage The buffered image you want to transform.
     * @return Returns the byte array from the buffered image.
     * @throws IOException Thrown an IOException.
     */
    private static byte[] bufferedImageToByteArray(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Prints user ID in terminal
     */
    private void printUserID()
    {
        System.out.print("User "+threadID+": ");
    }
}
