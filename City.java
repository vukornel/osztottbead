import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class City {
    private String varosNev;
    private int port;
    private int spentMoney;
    private int selfieNum;

    public City(String _nev, int _port) {
        varosNev = _nev;
        port = _port;
        spentMoney = 0;
        selfieNum = 0;
    }

    public void Action(String _action) throws UnknownHostException, IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date();
        String[] acTomb = _action.split(" ");
        final Socket server = new Socket("localhost", 4321);

        switch (acTomb[0]){
            case "arrive":
                fajlba(formatter.format(date) + " " + acTomb[1] + " arrived");
                break;
            case "leave":
                fajlba(formatter.format(date) + " " + acTomb[1] + " left");
                break;
            case "spend":
                fajlba(formatter.format(date) + " " + acTomb[1] + " spent" ); //osszeg
                spentMoney += Integer.parseInt(acTomb[2]); //nem
                break;
            case "selfie":
                fajlba(formatter.format(date) + " " + acTomb[1] + " took a selfie" );
                selfieNum++;
                break;
            case "info":
                final PrintWriter out = new PrintWriter(server.getOutputStream());
                out.println(spentMoney);
                out.println(selfieNum);
                out.flush();
                break;
            case "exit":
                break;
        }

        server.close();
    }

    public void fajlba(String input){
        File file = new File("city-" + varosNev + "-" + port + ".txt");
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));
            writer.write(input + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}