import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageHandler
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(42069);

        Socket socket = serverSocket.accept();
        System.out.println("Connected to client!");

        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        int imageCount = 0;
        ImageObject image = null;


        while (imageCount < 10) {
            try
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
            catch (ClassNotFoundException e)
            {
                System.out.println(e.getMessage());
            }
        }
        socket.close();
    }

    private static void byteArrayToImage(byte[] imageBytes, String filetype, String name) throws IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, filetype, new File("C:\\Users\\sondr\\Pictures\\Prosjekt\\"+name+"."+filetype) );
        System.out.println("image created");
    }
}