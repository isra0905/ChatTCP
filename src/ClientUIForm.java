import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

/**
 * @author Israel
 */
public class ClientUIForm {
    private User user;
    private Socket socket;
    private JPanel mainPanel;
    private JButton enviarButton;
    private JTextArea typingArea;
    private JList<String> userList;
    private JTextArea messageArea;
    private JLabel userLabel;
    private JScrollPane messagePanel;
    private JSplitPane bottomSplitPane;
    private JPanel userPanel;
    private JScrollPane typingAreaScrollPanel;
    private JScrollPane userListPanel;
    private DataOutputStream output = null;
    private FormHandlerThread handler = null;

    public ClientUIForm(Socket s, User u) {
        this.user = u;
        this.socket = s;
        handler = new FormHandlerThread(this.socket, this);
        handler.start();

        try {
            output = new DataOutputStream(this.socket.getOutputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        enviarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    if(!typingArea.getText().isBlank()){
                        output.writeUTF("message");
                        output.writeUTF(" " + user.getName() + ": " + typingArea.getText() + "\r\n");
                        typingArea.setText("");
                    }
                } catch (Exception ex) {
                }
            }
        });

    }

    public synchronized void activate(String username) {
        JFrame frame = new JFrame("Chat");
        this.bottomSplitPane.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        this.userLabel.setFont(this.userLabel.getFont().deriveFont(15f));
        this.enviarButton.setFont(this.enviarButton.getFont().deriveFont(20f));
        this.messageArea.setFont(this.messageArea.getFont().deriveFont(15f));
        this.typingArea.setFont(this.typingArea.getFont().deriveFont(15f));
        this.userList.setFont(this.userList.getFont().deriveFont(15f));
        this.userLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
        this.userPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.userList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.typingArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.enviarButton.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.userListPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        this.messageArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.enviarButton.setMargin(new Insets(0, 0, 0, 0));
        this.typingAreaScrollPanel.setBorder(null);
        this.userLabel.setText(" Usuario: " + username);
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 1000);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        SwingUtilities.invokeLater(() -> { // Default action when the form is closed
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        output.writeUTF("close");
                        handler.setRunning(false);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, ex.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        super.windowClosing(e);
                    }
                }
            });
        });

        frame.setVisible(true);
    }

    public synchronized void setUserList(DefaultListModel model) {
        this.userList.setModel(model);
    }

    public synchronized void setMessageArea(String content) {
        this.messageArea.setText(content);
    }
}
