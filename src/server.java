import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class server extends JFrame {
    private JTextArea log_server;
    private JPanel mainFrame;
    private JButton iniciarButton;
    private JButton usuariosOnlineButton;
    private JButton limpiarLogButton;
    private JButton finalizarButton;


    private ArrayList clientOutputStreams;
    private ArrayList<String> users;

    public static void main(String[] args){
        server server_gui_form = new server();
        server_gui_form.setVisible(true);
    }

    private server() {

        add(mainFrame);
        setTitle("myCypher_server");
        setSize(400, 500);


        iniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread starter = new Thread(new ServerStart());
                starter.start();

                log_server.append("Server started...\n");
            }
        });
        finalizarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        usuariosOnlineButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log_server.append("\n Online users : \n");
                for (String current_user : users) {
                    log_server.append(current_user);
                    log_server.append("\n");
                }
            }
        });
        limpiarLogButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log_server.setText("");
            }
        });
    }

    public class ClientHandler implements Runnable {
        BufferedReader reader;
        Socket sock;
        PrintWriter client;

        private ClientHandler(Socket clientSocket, PrintWriter user) {
            client = user;
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } catch (Exception ex) {
                log_server.append("Unexpected error... \n");
            }

        }

        @Override
        public void run() {
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat";
            String[] data;

            try {
                while ((message = reader.readLine()) != null) {
                    log_server.append("Received: " + message + "\n");
                    data = message.split(":");

                    for (String token : data) {
                        log_server.append(token + "\n");
                    }

                    if (data[2].equals(connect)) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone((data[0] + ":has disconnected." + ":" + chat));
                        userRemove(data[0]);
                    } else if (data[2].equals(chat)) {
                        tellEveryone(message);
                    } else {
                        log_server.append("No Conditions were met. \n");
                    }
                }
            } catch (Exception ex) {
                log_server.append("Lost a connection. \n");
                ex.printStackTrace();
                clientOutputStreams.remove(client);
            }
        }
    }

    public class ServerStart implements Runnable {
        @Override
        public void run() {
            clientOutputStreams = new ArrayList();
            users = new ArrayList();

            try {
                ServerSocket serverSock = new ServerSocket(2222);

                while (true) {
                    Socket clientSock = serverSock.accept();
                    PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                    clientOutputStreams.add(writer);

                    Thread listener = new Thread(new ClientHandler(clientSock, writer));
                    listener.start();
                    log_server.append("Got a connection. \n");
                }
            } catch (Exception ex) {
                log_server.append("Error making a connection. \n");
            }
        }
    }


    private void userAdd(String data) {
        String message, add = ": :Connect", done = "Server: :Done";
        log_server.append("Before " + data + " added. \n");
        users.add(data);
        log_server.append("After " + data + " added. \n");
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    private void userRemove(String data) {
        String message, add = ": :Connect", done = "Server: :Done";
        users.remove(data);
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    private void tellEveryone(String message) {

        for (Object clientOutputStream : clientOutputStreams) {
            try {
                PrintWriter writer = (PrintWriter) clientOutputStream;
                writer.println(message);
                log_server.append("Sending: " + message + "\n");
                writer.flush();
                log_server.setCaretPosition(log_server.getDocument().getLength());

            } catch (Exception ex) {
                log_server.append("Error telling everyone. \n");
            }
        }
    }


}