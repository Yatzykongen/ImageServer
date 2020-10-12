import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class UGVHandler
{
    private Socket socket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;
    public AtomicBoolean sant;
    public UGVHandler(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) throws IOException
    {
        this.socket = socket;
        this.objectInputStream = objectInputStream;
        this.objectOutputStream = objectOutputStream;
    }

    public void run()
    {
        while (true)
        {

        }

    }
}
