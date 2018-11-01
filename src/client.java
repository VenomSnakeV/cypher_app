import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JTextField field_mensaje;
    private JButton desconectarseButton;
    private JButton conectarseButton;
    private JButton enviarButton;

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
        mainFrame.setBorder(new EmptyBorder(10, 10, 10, 10));

        Color default_color = new Color(43, 109, 123);

        desconectarseButton.setBackground(default_color);
        conectarseButton.setBackground(default_color);
        enviarButton.setBackground(default_color);

        field_nick.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        field_puerto.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        field_ip.setBorder(BorderFactory.createLineBorder(Color.black, 1));
        field_mensaje.setBorder(BorderFactory.createLineBorder(Color.black, 1));

        field_nick.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        field_puerto.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        field_ip.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        field_mensaje.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        log_client.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(mainFrame);
        setTitle("myCypher_client");
        setSize(700, 500);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/ico.png")));

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
                        log_client.append("El mensaje no se puede enviar. \n");
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
                        writer.println(username + ":se ha conectado.:Conectado");
                        writer.flush();
                        isConnected = true;
                    } catch (Exception ex) {
                        log_client.append("No se puede conectar con el servidor. Inténtalo otra vez. \n");
                        field_nick.setEditable(true);
                    }

                    ListenThread();

                } else if (isConnected) {
                    log_client.append("Ya estás conectado. \n");
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
        conectarseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color default_color = new Color(56, 89, 211);
                conectarseButton.setBackground(default_color);
            }
        });
        conectarseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Color default_color = new Color(43, 109, 123);
                conectarseButton.setBackground(default_color);
            }
        });
        desconectarseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color default_color = new Color(56, 89, 211);
                desconectarseButton.setBackground(default_color);
            }
        });
        desconectarseButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Color default_color = new Color(43, 109, 123);
                desconectarseButton.setBackground(default_color);
            }
        });
        enviarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color default_color = new Color(56, 89, 211);
                enviarButton.setBackground(default_color);
            }
        });
        enviarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Color default_color = new Color(43, 109, 123);
                enviarButton.setBackground(default_color);
            }
        });
        field_mensaje.addActionListener(new ActionListener() {
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
                        log_client.append("El mensaje no se puede enviar. \n");
                    }
                    field_mensaje.setText("");
                    field_mensaje.requestFocus();
                }

                field_mensaje.setText("");
                field_mensaje.requestFocus();
            }
        });
    }

    public class IncomingReader implements Runnable {
        @Override
        public void run() {
            String[] data;
            String stream, done = "Hecho", connect = "Conectado", disconnect = "Desconectado", chat = "Chat";

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
        log_client.append(data + " está desconectado.\n");
    }

    private void writeUsers() {
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);
        for (String token : tempList) {
            //users.append(token + "\n");
        }
    }

    private void sendDisconnect() {
        String bye = (username + ": :Desconectado");
        try {
            writer.println(bye);
            writer.flush();
        } catch (Exception e) {
            log_client.append("No se puede enviar el mensaje de desconexion.\n");
        }
    }

    private void Disconnect() {
        try {
            log_client.append("Desconectado.\n");
            sock.close();
        } catch (Exception ex) {
            log_client.append("Ha fallado al intentar la desconexion. \n");
        }
        isConnected = false;
        field_nick.setEditable(true);

    }

}
