import java.io.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

public class ImageHandler implements Runnable
{
    private final ObjectInputStream objectInputStream;
    private AtomicInteger totalImages = new AtomicInteger();
    private volatile boolean run = false;
    private AtomicReference<UserHandler> cachedUserHandler = new AtomicReference<>();
    private Server server;

    public ImageHandler(ObjectInputStream objectInputStream, Server server)
    {
        this.objectInputStream = objectInputStream;
        this.server = server;
    }

    @Override
    public void run()
    {
        int imageCount = 0;
        ImageObject image;
        try
        {
            while (true) {
                UserHandler userHandler = cachedUserHandler.get();
                while (imageCount < totalImages.get() && run) {
                    image = (ImageObject) objectInputStream.readObject();
                    if (!(image.equals(null)) & image.getSize() > 20000) {
                        userHandler.setImage(image);
                        byteArrayToImage(image.getImageBytes(), image.getFiletype(), image.getName());

                        image = null;
                        imageCount++;
                    }
                }
                if(imageCount != 0 && imageCount == totalImages.get())
                {
                    server.startBot();
                }
                imageCount = 0;
                totalImages.set(0);
                Thread.sleep(10);
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
        catch (InterruptedException e)
        {
            System.err.println(e.getMessage());
        }
    }

    private static void byteArrayToImage(byte[] imageBytes, String filetype, String name) throws IOException
    {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        ImageIO.write(bufferedImage, filetype, new File("C:\\Users\\sondr\\Pictures\\Prosjekt\\"+name+"."+filetype) );
    }

    public void stop()
    {
        run = false;
    }

    public void start()
    {
        run = true;
    }

    public void setTotalImages(int totalImages)
    {
        this.totalImages.set(totalImages);
    }

    public void setUserHandler(UserHandler userHandler)
    {
        cachedUserHandler.set(userHandler);
    }
}