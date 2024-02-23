import java.io.Serializable;
import java.net.Socket;

/**
 * @author Israel
 */
public class User implements Serializable {
    private String name;
    private String id;
    private Socket connectedSocket;

    public User(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Socket getConnectedSocket() {
        return connectedSocket;
    }

    public void setConnectedSocket(Socket connectedSocket) {
        this.connectedSocket = connectedSocket;
    }
}
