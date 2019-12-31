import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class ChatClient extends JFrame implements Runnable {

    Socket socket;
    JTextArea ta;
    JButton send, logout;
    JTextField tf;

    Thread thread;

    DataOutputStream dout;
    DataInputStream din;

    String LoginName;

    ChatClient(String login) throws IOException {
        super(login);
        LoginName = login;


        ta = new JTextArea(18, 50);
        ta.setEditable(false);
        tf = new JTextField(50);

        send = new JButton("Send");
        logout = new JButton("Logout");

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!tf.getText().isEmpty())
                    try {
                        dout.writeUTF(LoginName + " " + "DATA " + tf.getText());
                        tf.setText("");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
            }
        });

        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (!tf.getText().isEmpty())
                        try {
                            dout.writeUTF(LoginName + " " + "DATA " + tf.getText());
                            tf.setText("");
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dout.writeUTF(LoginName + " " + "LOGOUT ");
                    System.exit(1);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        socket = new Socket("localhost", 5217);

        din = new DataInputStream(socket.getInputStream());
        dout = new DataOutputStream(socket.getOutputStream());

        dout.writeUTF(LoginName);
        dout.writeUTF(LoginName + " " + "LOGIN");

        thread = new Thread(this);
        thread.start();
        setup();

    }

    private void setup() {
        setSize(600, 400);

        JPanel panel = new JPanel();

        panel.add(new JScrollPane(ta));
        panel.add(tf);
        panel.add(send);
        panel.add(logout);

        add(panel);

        setVisible(true);
    }

    @Override
    public void run() {
        while (true) {
            try {
                ta.append("\n " + din.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
