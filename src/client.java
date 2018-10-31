import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class client extends JFrame {
    private JPanel mainFrame;
    private JTextArea log_client;
    private JTextField field_ip;
    private JTextField field_puerto;
    private JTextField field_nick;
    private JButton desconectarseButton;
    private JButton conectarseButton;
    private JButton enviarButton;
    private JTextField field_mensaje;

    private String username, address = "localhost";
    private ArrayList users = new ArrayList();
    private int port = 2222;
    private Boolean isConnected = false;

    private Socket sock;
    private BufferedReader reader;
    private PrintWriter writer;


    public static void main(String[] args){
        client client_gui_form = new client();
        client_gui_form.setVisible(true);

    }

    private client() {

        add(mainFrame);
        setTitle("myCypher_client");
        setSize(700, 500);


        enviarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String nothing = "";
                if ((field_mensaje.getText()).equals(nothing)) {
                    field_mensaje.setText("");
                    field_mensaje.requestFocus();
                } else {
                    try {
                        writer.println(username + ":" + field_mensaje.getText() + ":" + "Chat");
                        writer.flush(); // flushes the buffer
                    } catch (Exception ex) {
                        log_client.append("Message was not sent. \n");
                    }
                    field_mensaje.setText("");
                    field_mensaje.requestFocus();
                }

                field_mensaje.setText("");
                field_mensaje.requestFocus();
            }
        });
        conectarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isConnected) {
                    username = field_nick.getText();
                    field_nick.setEditable(false);

                    try {
                        sock = new Socket(address, port);
                        InputStreamReader streamreader = new InputStreamReader(sock.getInputStream());
                        reader = new BufferedReader(streamreader);
                        writer = new PrintWriter(sock.getOutputStream());
                        writer.println(username + ":has connected.:Connect");
                        writer.flush();
                        isConnected = true;
                    } catch (Exception ex) {
                        log_client.append("Cannot Connect! Try Again. \n");
                        field_nick.setEditable(true);
                    }

                    ListenThread();

                } else if (isConnected) {
                    log_client.append("You are already connected. \n");
                }
            }
        });
        desconectarseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendDisconnect();
                Disconnect();
            }
        });
    }

    public class IncomingReader implements Runnable {
        @Override
        public void run() {
            String[] data;
            String stream, done = "Done", connect = "Connect", disconnect = "Disconnect", chat = "Chat";

            try {
                while ((stream = reader.readLine()) != null) {
                    data = stream.split(":");

                    if (data[2].equals(chat)) {
                        log_client.append(data[0] + ": " + data[1] + "\n");
                        log_client.setCaretPosition(log_client.getDocument().getLength());
                    } else if (data[2].equals(connect)) {
                        log_client.removeAll();
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        userRemove(data[0]);
                    } else if (data[2].equals(done)) {
                        //users.setText("");
                        writeUsers();
                        users.clear();
                    }
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void ListenThread() {
        Thread IncomingReader = new Thread(new IncomingReader());
        IncomingReader.start();
    }

    //--------------------------//

    private void userAdd(String data) {
        users.add(data);
    }

    private void userRemove(String data) {
        log_client.append(data + " is now offline.\n");
    }

    private void writeUsers() {
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
        for (String token : tempList) {
            //users.append(token + "\n");
        }
    }

    private void sendDisconnect() {
        String bye = (username + ": :Disconnect");
        try {
            writer.println(bye);
            writer.flush();
        } catch (Exception e) {
            log_client.append("Could not send Disconnect message.\n");
        }
    }

    private void Disconnect() {
        try {
            log_client.append("Disconnected.\n");
            sock.close();
        } catch (Exception ex) {
            log_client.append("Failed to disconnect. \n");
        }
        isConnected = false;
        field_nick.setEditable(true);

    }

}
