public class Main {
    public static void main(String[] args)
    {
        //Thread imageServerThread = new Thread(new ImageServer());
        Server server = new Server();
        //imageServerThread.start();
        server.run();
    }
}
