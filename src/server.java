import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class server {
    public static void main(String[] args){
        ServerSocket server;
        Socket socket;
        DataInputStream in;
        DataOutputStream out;

        final int port = 2222;


        try {
            server = new ServerSocket(port);
            System.out.println("Servidor iniciado");

            do {
                socket = server.accept();

                System.out.println("Cliente conectado");
                in = new DataInputStream(socket.getInputStream());
                out = new DataOutputStream(socket.getOutputStream());

                String message = in.readUTF();

                System.out.println(message);

                out.writeUTF("Hola mundo desde el servidor!");

                socket.close();

                System.out.println("Cliente desconectado");
            } while (true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
