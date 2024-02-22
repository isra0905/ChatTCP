import java.io.*;
import java.net.Socket;

/**
 * @author Israel
 */
public class HandlerThread implements Runnable {
    private Socket client;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private BufferedWriter writer = null;

    public HandlerThread(Socket client) {
        this.client = client;
        try {
            this.writer = new BufferedWriter(new FileWriter(Server.getChat(), true));
            this.input = new DataInputStream(client.getInputStream());
            this.output = new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//fin constructor

    @Override
    public void run() {
        System.out.println("Atiendo petición cliente --> " +
                client.getInetAddress() + ":" + client.getPort());
        String action = "";
        do {
            try {
                action = input.readUTF();
                switch (action) {
                    case "close":
                        System.out.println("Case close");
                        String id = client.getInetAddress() + ":" + client.getPort();
                        User userToRemove = null;
                        for (User u : Server.getConnectedUsers()) {
                            if (u.getId().equals(id)) {
                                userToRemove = u;
                            }
                        }
                        Server.closeUserSocket(id);
                        Server.removeUser(userToRemove);
                        System.out.println("Closed socket --> " + id);
                        checkConnectedUsers();
                        break;
                    case "connection":
                        System.out.println("Case connection");
                        writer.flush();
                        checkConnectedUsers();
                        output.writeUTF("chat");
                        output.writeUTF(getFileContent(Server.getChat()));
                        break;
                    case "message":
                        writer.write(input.readUTF());
                        writer.flush();
                        sendMessage();
                        System.out.println("Case message");
                        break;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } while (!action.equals("close"));
    }

    public synchronized void checkConnectedUsers() {
        try {
            for (User u : Server.getConnectedUsers()) {
                new DataOutputStream(u.getConnectedSocket().getOutputStream()).writeUTF("list");
                new DataOutputStream(u.getConnectedSocket().getOutputStream()).writeInt(Server.getConnectedUsers().size());
                for (User user : Server.getConnectedUsers()) {
                    new DataOutputStream(u.getConnectedSocket().getOutputStream()).writeUTF(" " + user.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized void sendMessage() {
        try {
            for (User u : Server.getConnectedUsers()) {
                new DataOutputStream(u.getConnectedSocket().getOutputStream()).writeUTF("chat");
                new DataOutputStream(u.getConnectedSocket().getOutputStream()).writeUTF(getFileContent(Server.getChat()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public synchronized String getFileContent(File f){
        String content = "", line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(f));
            while ((line = reader.readLine()) != null) {
                content += line + "\r\n";
                System.out.println(line);
            }
        }catch (Exception ex){
        }
        return content;
    }
}
