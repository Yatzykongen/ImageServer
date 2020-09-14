import javax.imageio.ImageIO;
import javax.jws.soap.SOAPBinding;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageServer
{
    private static final int PORT = 42069;

    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    private static ExecutorService pool = Executors.newFixedThreadPool(3);

    public static void main(String[] args)
    {
        try
        {
            ServerSocket listener = new ServerSocket(PORT);

            boolean serverOn = true;
            while (serverOn)
            {
                System.out.println("Waiting for some1 so im not lonely.....");
                Socket client = listener.accept();
                System.out.println("YAY i have a friend :D ");
                //BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                //String request = in.readLine();

                ImageHandler imageThread = new ImageHandler(client);
                imageThread.run();

                //ClientHandler clientThread = new ClientHandler(client);
                //clients.add(clientThread);

                //pool.execute(clientThread);
            }
        }
        catch (IOException e)
        {
            System.out.println("fæl");
        }
//
//
//        JFrame jFrame = new JFrame("Server");
//        jFrame.setSize(1000, 1000);
//        jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
//
//        JLabel jLabelText = new JLabel("Waiting for image from UGV...");
//
//        jFrame.add(jLabelText, BorderLayout.SOUTH);
//
//        jFrame.setVisible(true);
//
//        ServerSocket serverSocket = null;
//        try
//        {
//            serverSocket = new ServerSocket(42069);
//
//            Socket socket = serverSocket.accept();
//
//
//            InputStream inputStream = socket.getInputStream();
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
//
//            BufferedImage bufferedImage = ImageIO.read(bufferedInputStream);
//
//            bufferedInputStream.close();
//            socket.close();
//
//            JLabel jLabelPic = new JLabel(new ImageIcon(bufferedImage));
//            jLabelText.setText("Image received.");
//            jFrame.add(jLabelPic, BorderLayout.CENTER);
//        } catch (IOException e)
//        {
//            e.printStackTrace();
//        }
    }
}
