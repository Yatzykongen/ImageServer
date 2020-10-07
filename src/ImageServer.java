import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageServer
{
    private static final int PORT = 42069;
    private static final int POOL_SIZE = 5;

    private static ArrayList<ClientHandler> clients = new ArrayList<>();

    private static ExecutorService threadPool = Executors.newFixedThreadPool(POOL_SIZE);


    public static void main(String[] args)
    {
        try
        {
            ServerSocket listener = new ServerSocket(PORT);

            boolean serverOn = true;
            while (serverOn)
            {
                System.out.println("Waiting for connection from UGV....");

                Socket client = listener.accept();

                System.out.println("UGV connected!");

                threadPool.execute(new ClientHandler(client));

                //ClientHandler clientHandler = new ClientHandler(client);
                //clients.add(clientHandler);

                //ImageHandler imageThread = new ImageHandler(client, 10);
                //imageThread.run();

                //ClientHandler clientThread = new ClientHandler(client);
                //clients.add(clientThread);

                //pool.execute(clientThread);
            }
        }
        catch (IOException e)
        {
            System.out.println("Connection to UGV failed!!");
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
