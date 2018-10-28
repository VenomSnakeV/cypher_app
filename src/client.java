import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.io.IOException;

public class client {
    public static void main(String[] args){

        final String host = "127.0.0.1";
        final int port = 2222;

        DataInputStream in;
        DataOutputStream out;

        try {
            Socket socket = new Socket(host,port);

            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("Hola mundo desde el cliente!");

            String message = in.readUTF();

            System.out.println(message);

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
