import java.io.*;

public class City {
    private String varosNev;
    private int port;

    public City(String _nev, int _port) {
        varosNev = _nev;
        port = _port;
    }

    public void Action(String _action){

    }

    public void fajlba(String input){
        File file = new File("city-" + varosNev + "-" + port + ".txt");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(input);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}