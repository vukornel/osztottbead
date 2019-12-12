import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Person {
    private String nev;
    private int port;
    private int cityPort;

    public Person(String _nev, int _port, int _cityPort) throws IOException {
        nev = _nev;
        port = _port;
        cityPort = _cityPort;
        try (Socket s = new Socket("localhost", _cityPort);
        Scanner sc = new Scanner(s.getInputStream());
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        ){
            pw.println("arrive " + nev);
            pw.flush();
        } catch (UnknownHostException e) {
            System.err.print(e);
        }
    }

    public void Action(String _action, Map<String, Integer> nextCityPort) throws IOException {
        switch(_action){
            case "go":
                try (Socket s = new Socket("localhost", cityPort);
                    Scanner sc = new Scanner(s.getInputStream());
                    PrintWriter pw = new PrintWriter(s.getOutputStream());
                ){
                    pw.println("leave " + nev);
                    pw.flush();
                } catch (UnknownHostException e) {
                    System.err.print(e);
                }
                Random generator = new Random();
                Object[] values = nextCityPort.values().toArray();
                int randomValue = (int)values[generator.nextInt(values.length)];

                cityPort = randomValue;
                try (Socket s = new Socket("localhost", cityPort);
                Scanner sc = new Scanner(s.getInputStream());
                PrintWriter pw = new PrintWriter(s.getOutputStream());
                ){
                    pw.println("arrive " + nev);
                    pw.flush();
                } catch (UnknownHostException e) {
                    System.err.print(e);
                }
                break;
            case "spend":
                int amount = ((int) Math.random() * 991) + 10;
                try (Socket s = new Socket("localhost", cityPort);
                    Scanner sc = new Scanner(s.getInputStream());
                    PrintWriter pw = new PrintWriter(s.getOutputStream());
                ){
                    pw.println("spend " + nev + " " + amount);
                    pw.flush();
                } catch (UnknownHostException e) {
                    System.err.print(e);
                }
                break;
            case "selfie":
                try (Socket s = new Socket("localhost", cityPort);
                    Scanner sc = new Scanner(s.getInputStream());
                    PrintWriter pw = new PrintWriter(s.getOutputStream());
                ){
                    pw.println("selfie " + nev);
                    pw.flush();
                } catch (UnknownHostException e) {
                    System.err.print(e);
                }
                break;
            case "exit":
                try (Socket s = new Socket("localhost", 4321);
                    PrintWriter pw = new PrintWriter(s.getOutputStream());
                ){
                    pw.println("finished " + nev);
                    pw.flush();
                } catch (UnknownHostException e) {
                    System.err.print(e);
                }
                break;
        }
    }
}