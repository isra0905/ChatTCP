import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author Israel
 */
public class FormHandlerThread extends Thread {
    private Socket user;
    private ClientUIForm frm;
    private DataInputStream input = null;
    private DataOutputStream output = null;
    private boolean running;

    public FormHandlerThread(Socket user, ClientUIForm frm) {
        this.frm = frm;
        this.user = user;
        this.running = true;
        try {
            input = new DataInputStream(this.user.getInputStream());
            output = new DataOutputStream(this.user.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//fin constructor

    @Override
    public void run() {
        try {
            output.writeUTF("connection");
            System.out.println("Connection sent");
            String action = "";
            do {
                action = input.readUTF();
                switch (action) {
                    case "list":
                        System.out.println("Case list");
                        DefaultListModel model = new DefaultListModel();
                        int j = input.readInt();
                        for (int i = 0; i < j; i++){
                            String name = input.readUTF();
                            model.addElement(name);
                            System.out.println(name);
                        }
                        System.out.println(model);
                        frm.setUserList(model);
                        break;
                    case "chat":
                        System.out.println("Case chat");
                        String content = input.readUTF();
                        System.out.println(content);
                        frm.setMessageArea(content);
                        break;
                }
            } while (running);
        } catch (Exception ex) {
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
