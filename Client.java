import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args) throws  IOException, InterruptedException {
        try(
                final Socket server = new Socket("localhost", 4321);
                final PrintWriter out = new PrintWriter(server.getOutputStream());
        ) {
            final BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));


            Thread t1 = new Thread(() -> {
                try	{
                    String s;
                    while((s = userIn.readLine()) != null){
                        out.println(s);
                        out.flush();
                    }
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });
            t1.start();
            t1.join();
        }
    }
}
