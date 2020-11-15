import java.io.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

/**
 * This ImageHandler class will handle the incoming images from the UGV.
 * It will also save the images locally on computer.
 * When all images is received from the UGV it calls on the server to start the bot.
 *
 * @author Sondre Nerhus
 */
public class ImageHandler implements Runnable
{
    private volatile boolean run = false;
    private final ObjectInputStream objectInputStream;
    private final AtomicInteger totalImages;
    private final AtomicReference<UserHandler> cachedUserHandler;
    private final Server server;
    private int imageCount = 0;

    /**
     * The constructor of the ImageHandler class.
     *
     * @param objectInputStream The object input stream that is used to receive objects from the UGV.
     * @param server The server that made the TCP connection.
     */
    public ImageHandler(ObjectInputStream objectInputStream, Server server)
    {
        this.objectInputStream = objectInputStream;
        this.server = server;
        totalImages = new AtomicInteger();
        cachedUserHandler = new AtomicReference<>();
    }

    /**
     * Run method for the ImageHandler class.
     * Handles incoming images from the UGV and saves them on the computer.
     * Calls on the server to start the Meshroom bot when all images are received.
     */
    @Override
    public void run()
    {
        try
        {
            while (true) {
                UserHandler userHandler = cachedUserHandler.get();
                while (imageCount < totalImages.get() && run) {
                    ImageObject image = (ImageObject) objectInputStream.readObject();
                    if (image!=null & image.getSize() > 20000) {
                        userHandler.setImage(image);
                        byteArrayToImage(image.getImageBytes(), image.getFiletype(), image.getName());
                        imageCount++;
                    }
                }
                if(imageCount != 0 && imageCount == totalImages.get())
                {
                    server.startBot(); //Starts the MeshRoom bot
                }
                imageCount = 0;
                totalImages.set(0);
                Thread.sleep(10);
            }
        }
        catch (IOException | ClassNotFoundException | InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Takes a byte array from an image and transforms it back to a image,
     * then saves it on the computer.
     *
     * @param imageBytes The bytes that will be transformed in to a image.
     * @param filetype The filetype of the image.
     * @param name The name of the image.
     * @throws IOException Throws an IOException.
     */
    private static void byteArrayToImage(byte[] imageBytes, String filetype, String name) throws IOException
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        ImageIO.write(bufferedImage, filetype, new File("C:\\Users\\sondr\\Pictures\\Prosjekt\\"+name+"."+filetype) );
    }

    /**
     * Stops the polling of images.
     */
    public void stop()
    {
        run = false;
    }

    /**
     * Starts the polling of images.
     */
    public void start()
    {
        run = true;
    }

    /**
     * Sets the number of images that the UGV will send.
     *
     * @param totalImages The number of images that the UGV will send.
     */
    public void setTotalImages(int totalImages)
    {
        this.totalImages.set(totalImages);
    }

    /**
     * Sets the user that will receive the images.
     *
     * @param userHandler The user that will receive the images.
     */
    public void setUserHandler(UserHandler userHandler)
    {
        cachedUserHandler.set(userHandler);
    }
}