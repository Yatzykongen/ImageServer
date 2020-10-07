import java.io.*;
import java.net.Socket;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageHandler
{
    private Socket socket;
    private InputStream inputStream;
    private ObjectInputStream objectInputStream;
    private int totalImages;

    public ImageHandler(Socket socket, ObjectInputStream objectInputStream, int totalImages) throws IOException
    {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.totalImages = totalImages;
    }

    public void run()
    {
        int imageCount = 0;
        ImageObject image = null;
        try
        {
            while (imageCount<totalImages)
            {
                image = (ImageObject) objectInputStream.readObject();
                if (!(image.equals(null)) & image.getSize()>20000)
                {
                    byteArrayToImage(image.getImageBytes(), image.getFiletype(), image.getName());

                    image = null;
                    System.out.println("Image was made!");
                    imageCount ++;
                }
            }
            socket.close();
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
        catch (ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
    }

    private static void byteArrayToImage(byte[] imageBytes, String filetype, String name) throws IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, filetype, new File("C:\\Users\\sondr\\Pictures\\Prosjekt\\"+name+"."+filetype) );
        System.out.println("image created");
    }
}