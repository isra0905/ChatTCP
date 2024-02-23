import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * @author Israel
 */
public class Server {
    private static File chat = new File("./src/chat_logs.log");
    private static ArrayList<User> connectedUsers = new ArrayList<>();

    public static void main(String[] args) {

        try {
            if (!chat.exists()) chat.createNewFile();
            else {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(chat))) {
                } catch (IOException e) {
                    System.err.println("Error clearing the file: " + e.getMessage());
                }
            }
        } catch (IOException ex) {
        }

        int port = 6001;
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            while (true) {
                System.out.println("Escucho en el puerto: " + port);
                Socket cliente = server.accept();
                ObjectInputStream input = new ObjectInputStream(cliente.getInputStream());
                DataOutputStream output = new DataOutputStream(cliente.getOutputStream());
                int status;
                User u;
                try {
                    output.writeInt(status = checkUser(u = (User) (input.readObject())));
                    if (status == 0) {
                        synchronized (Thread.currentThread()) {
                            u.setConnectedSocket(cliente);
                            connectedUsers.add(u);
                        }
                        Thread hilo = new Thread(new HandlerThread(cliente));
                        hilo.start();
                    }
                } catch (Exception e) {
                    output.writeInt(3);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized static int checkUser(User newUser) {
        for (User u : connectedUsers) {
            if (u.getName().equalsIgnoreCase(newUser.getName())) {
                if (u.getId().equals(newUser.getId())) {
                    return 1;
                } else {
                    return 2;
                }
            }
        }
        return 0;
    }


    public synchronized static void removeUser(User u) {
        connectedUsers.remove(u);
    }

    public synchronized static void closeUserSocket(String id) {
        for (User u : connectedUsers) {
            if (u.getId().equals(id)) {
                try {
                    u.getConnectedSocket().close();
                } catch (Exception ex) {

                }
            }
        }
    }

    public synchronized static ArrayList<User> getConnectedUsers() {
        return connectedUsers;
    }

    public static File getChat() {
        return chat;
    }

}