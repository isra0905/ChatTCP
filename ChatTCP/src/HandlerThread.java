import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Israel
 */
public class HandlerThread implements Runnable {
    private Socket client;
    private DataInputStream input = null;
    private DataOutputStream output = null;

    public HandlerThread(Socket client) {
        this.client = client;
        try {
            input = new DataInputStream(client.getInputStream());
            output = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//fin constructor

    @Override
    public void run() {
        System.out.println("Atiendo petición cliente --> " +
                client.getInetAddress() + ":" + client.getPort());
        String action = "";
        do{
            try {
                action = input.readUTF();
                switch (action) {
                    case "close":
                        String id = client.getInetAddress() + ":" + client.getPort();
                        System.out.println("case close: "+id);
                        Server.removeUser(id);
                        Thread.currentThread().interrupt();
                        break;
                    default:
                        String id2 = client.getInetAddress() + ":" + client.getPort();
                        System.out.println(2);
                        Server.removeUser(id2);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }while (!action.equals("close"));
    }
}
