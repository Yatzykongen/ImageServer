import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * @author sonrdrrrrre
 */
public class ImageHandler implements Runnable
{
    private Socket client;
    private BufferedInputStream imageStream;
    private BufferedReader in;
    private PrintWriter out;
    private InputStream inputStream;
    private FileOutputStream fileOutputStream;

    public ImageHandler(Socket imageSocket) throws IOException
    {
        this.client = imageSocket;
        imageStream = new BufferedInputStream(client.getInputStream());
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream());
        inputStream = client.getInputStream();
        //fileOutputStream = new FileOutputStream();
        System.out.println("den ble laget");
    }

    @Override
    public void run()
    {
        try
        {
            int imageCounter = 0;
            while(!client.isClosed() & imageCounter<3)
            {
                //do something
//                byte[] sizeAr = new byte[4];
//                inputStream.read(sizeAr);
//                int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();
//                System.out.println(size);
//
//                byte[] imageAr = new byte[size];
//                inputStream.read(imageAr);
//                System.out.println("fekk bilde");
//
//                BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imageAr));


//                ImageIO.write(bufferedImage, "jpg", new File("C:\\Users\\sondr\\Pictures\\Prosjekt\\image"+imageCounter+".jpg"));
//                System.out.println(imageCounter);
//                imageCounter++;
//                Thread.sleep(11000);
            System.out.println("Loading image");
            //BufferedInputStream imageStream = new BufferedInputStream(client.getInputStream());
            BufferedImage bufferedImage = ImageIO.read(imageStream);
            if(bufferedImage != (null))
            {
                System.out.println("Saving image");
                ImageIO.write(bufferedImage, "jpg", new File("C:\\Users\\sondr\\Pictures\\Prosjekt\\image" + imageCounter + ".jpg"));
                System.out.println("Image saved");
                imageCounter++;
                //imageStream.close();
            }


            }
            imageStream.close();
            client.close();
        }
        catch (IOException e)
        {
            System.err.println(e.getStackTrace());
        }
//        catch (InterruptedException e)
//        {
//
//        }

    }
}
