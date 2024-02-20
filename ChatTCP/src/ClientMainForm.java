import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * @author Israel
 */
public class ClientMainForm {

    private JPanel mainPanel;
    private JTextField nameField;
    private JButton buttonEnviar;
    private JLabel label;
    private JPanel buttonPanel;
    private JFrame mainFrame;

    public ClientMainForm(JFrame frame) {
        this.mainFrame = frame;
        buttonEnviar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!nameField.getText().isBlank()) {
                    super.mouseClicked(e);
                    try {
                        Socket clientSocket = new Socket("127.0.0.1", 6001);
                        User u = new User(nameField.getText(), clientSocket.getInetAddress() + ":" + clientSocket.getLocalPort());
                        new ObjectOutputStream(clientSocket.getOutputStream()).writeObject(u);
                        int serverResponse = new DataInputStream(clientSocket.getInputStream()).readInt();
                        switch (serverResponse){
                            case 0:
                                ClientUIForm c = new ClientUIForm(clientSocket);
                                c.activate(nameField.getText());
                                mainFrame.dispose();
                                break;
                            case 1:
                                JOptionPane.showMessageDialog(mainFrame, "Connection error: same port and address as another user", "Error", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case 2:
                                JOptionPane.showMessageDialog(mainFrame, "El nombre de usuario ya existe, puedes usar numeros y carácteres especiales", "Error", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            case 3:
                                JOptionPane.showMessageDialog(mainFrame, "Class not found", "Error", JOptionPane.INFORMATION_MESSAGE);
                                System.exit(3);
                                break;
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(mainFrame, "Error durante la conexión", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Necesitas escribir un nombre de usuario", "Error", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Escoge un nombre de usuario");
        ClientMainForm frm = new ClientMainForm(frame);
        frm.buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        frm.mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frm.buttonEnviar.setSize(100, 100);
        frame.setContentPane(frm.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 100);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
