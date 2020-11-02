public class Main {
    public static void main(String[] args)
    {
        Thread imageServerThread = new Thread(new ImageServer());
        CommunicationServer communicationServer = new CommunicationServer();
        imageServerThread.start();
        communicationServer.run();
    }
}
