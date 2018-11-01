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
        mainFrame.setBorder(new EmptyBorder(10, 10, 10, 10));


        Color default_color = new Color(43, 109, 123);

        iniciarButton.setBackground(default_color);
        finalizarButton.setBackground(default_color);
        usuariosOnlineButton.setBackground(default_color);
        limpiarLogButton.setBackground(default_color);

        log_server.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(mainFrame);
        setTitle("myCypher_server");
        setSize(400, 500);
        setResizable(false);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("img/ico.png")));

        iniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread starter = new Thread(new ServerStart());
                starter.start();

                log_server.append("El servidor se ha inicializado. \n");
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
                log_server.append("\n Usuarios online en este momento : \n");
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

        iniciarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color default_color = new Color(56, 89, 211);
                iniciarButton.setBackground(default_color);
            }
        });
        iniciarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Color default_color = new Color(43, 109, 123);
                iniciarButton.setBackground(default_color);
            }
        });

        finalizarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color default_color = new Color(56, 89, 211);
                finalizarButton.setBackground(default_color);
            }
        });
        finalizarButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Color default_color = new Color(43, 109, 123);
                finalizarButton.setBackground(default_color);
            }
        });

        usuariosOnlineButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color default_color = new Color(56, 89, 211);
                usuariosOnlineButton.setBackground(default_color);
            }
        });
        usuariosOnlineButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Color default_color = new Color(43, 109, 123);
                usuariosOnlineButton.setBackground(default_color);
            }
        });

        limpiarLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                Color default_color = new Color(56, 89, 211);
                limpiarLogButton.setBackground(default_color);
            }
        });
        limpiarLogButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                Color default_color = new Color(43, 109, 123);
                limpiarLogButton.setBackground(default_color);
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
                log_server.append("Ha ocurrido un error inesperado.\n");
            }

        }

        @Override
        public void run() {
            String message, connect = "Conectado", disconnect = "Desconectado", chat = "Chat";
            String[] data;

            try {
                while ((message = reader.readLine()) != null) {
                    log_server.append("Recibido -> " + message + "\n");
                    data = message.split(":");

                    for (String token : data) {
                        log_server.append(token + "\n");
                    }

                    if (data[2].equals(connect)) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        userAdd(data[0]);
                    } else if (data[2].equals(disconnect)) {
                        tellEveryone((data[0] + ":se ha desconectado." + ":" + chat));
                        userRemove(data[0]);
                    } else if (data[2].equals(chat)) {
                        tellEveryone(message);
                    } else {
                        log_server.append("No se han cumplido las condiciones. \n");
                    }
                }
            } catch (Exception ex) {
                log_server.append("Se ha perdido la conexion. \n");
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
                    log_server.append("Se ha establecido la conexion. \n");
                }
            } catch (Exception ex) {
                log_server.append("Ha habido un error en la conexion. \n");
            }
        }
    }


    private void userAdd(String data) {
        String message, add = ": :Conectado", done = "Servidor: :Hecho";
        log_server.append("Antes " + data + " añadido. \n");
        users.add(data);
        log_server.append("Despues " + data + " añadido. \n");
        String[] tempList = new String[(users.size())];
        users.toArray(tempList);

        for (String token : tempList) {
            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    private void userRemove(String data) {
        String message, add = ": :Conectado", done = "Servidor: :Hecho";
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
                log_server.append("Enviando: " + message + "\n");
                writer.flush();
                log_server.setCaretPosition(log_server.getDocument().getLength());

            } catch (Exception ex) {
                log_server.append("Error al comunicarlo a los demas usuarios. \n");
            }
        }
    }


}