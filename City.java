import java.util.*;
import java.io.*;
import java.net.*;

public class City{
    public City(){
        String varosNev = "";
        int port = 0;

        File file = new File("city-"+varosNev + "-" + port +".txt");


        
        
        //filebairas
        FileWriter writer = new FileWriter(file);
        writer.write("Test data");
        writer.close();
    }

}