import java.util.*;
import java.io.*;
import java.net.*;

public class WorldMain {
	static Set<Integer> foglaltPortok = new HashSet<>();
	static Map<String, Integer> cities = new HashMap<>(); //nev port
	static Map<String, Integer> persons = new HashMap<>(); //nev port
	static int cityNumber = 0;
	static int personNumber = 0;

	public static void main( String[] args) throws Exception {
		try(ServerSocket ss = new ServerSocket(4321);
			Socket s = ss.accept();
			Scanner scIn = new Scanner(s.getInputStream());
		){
			if (args.length > 0){
				randomStart(args[0]);
			}

			String line;
			String lineTomb[];
			while (true) {
				while (scIn.hasNextLine()) {
					line = scIn.nextLine();
					lineTomb = line.split(" ");
		
					switch (lineTomb[0]) {
						case "city": {
							createCity(lineTomb[1]);
							break;
						}
						case "cityinfo": {
							cityInfo(lineTomb[1]);
							break;
						}
						case "citylist": {
							cityList();
							break;
						}
						case "person": {
							//megnezni hogy vane arg1
							createPerson(lineTomb[1]);
							break;
						}
						case "do": {
							sendDo(lineTomb[1], lineTomb[2]); // nev vagy port, sorsz
							break;
						}
						case "finished": {
							finished(lineTomb[1]);
							break;
						}
						case "exit": {
							sendExit();
							scIn.close();
							// kilepes
							break;
						}
					}
				}
			}
		}
	}

	public static void randomStart(String string) {
		Random r = new Random();
		int kilepesiIdo = Integer.parseInt(string);

	   int db = r.nextInt((5 - 3) + 1) + 3;
	   for (int i = 0; i < db; i++) {
		   // random nev a filebol
		   createCity("random");
	   }

	   db = r.nextInt((8 - 4) + 1) + 4;
	   for (int i = 0; i < db; i++) {
		   // random nev a filebol
		   createPerson("??");
	   }

	   // kilepesiIdo mulva exitet küldeni
	}

	public static Boolean foglalt(int port) {
		return foglaltPortok.contains(port);
	}

	public static void createCity( String nev) {
		if (cityNumber < 12) {
			
			int cityport;

			do {
				Random r = new Random();
				cityport = r.nextInt((35010 - 35000) + 1) + 35000;
			} while (foglalt(cityport));

			cityNumber++;
			foglaltPortok.add(cityport);
			cities.put(nev, cityport);
			final int fport = cityport;

			new Thread(() -> {
				try (ServerSocket ss = new ServerSocket(fport);
					//Socket s = ss.accept();
					//PrintWriter pw = new PrintWriter(s.getOutputStream());
					Scanner scIn = new Scanner(System.in); // nem inputstream egyenlőre
					){
						City city = new City(nev, fport);
						System.out.println(nev + " created at " + fport);
						while(true){
							while (scIn.hasNextLine()) {
								city.Action(scIn.nextLine());
							}
						}
				} catch ( Exception e) {
					e.printStackTrace();
				}
				System.out.println(nev + " stopped at " + fport);
			}).start();
		} else {
			System.out.println("failed");
		}
	}

	public static void cityInfo( String cityName){
		System.out.println(cities.get(cityName));
	}

	public static void cityList(){
		for (String name: cities.keySet()){ 
			System.out.println(name + "\n");  
		} 
	}

	public static void createPerson( String nev) {
		if (personNumber < 1002) {
			Scanner scIn = new Scanner(System.in);
			int personport;

			do {
				Random r = new Random();
				personport = r.nextInt((37000 - 36000) + 1) + 37000;
			} while (foglalt(personport));

			personNumber++;
			foglaltPortok.add(personport);
			persons.put(nev, personport);
			final int fport = personport;

			new Thread(() -> {
				try (Socket s = new Socket("localhost", fport);
					Scanner sc = new Scanner(s.getInputStream());
					PrintWriter pw = new PrintWriter(s.getOutputStream());
					){
					Person person = new Person(nev, fport);
					System.out.println(nev + " create at " + fport);
					while(true){
						while (scIn.hasNextLine()) {
							person.Action(scIn.nextLine());
						}
						//automatikus müködés?
					}
				} catch ( Exception e) {
					e.printStackTrace();
				}
			}).start();
			scIn.close();
		} else {
			System.out.println("failed");
		}
	}

	public static void sendDo(String _azon, String action) throws IOException {
		Socket socket = null;
		OutputStreamWriter osw;
		int port;
		//azont átalakítani
		if(Integer.parseInt(_azon) > 35999 && Integer.parseInt(_azon) < 37001){
			port = Integer.parseInt(_azon);
		}else if (Integer.parseInt(_azon) > 99 && Integer.parseInt(_azon) < 1){
			port = Integer.parseInt(_azon) + 35999;
		}else{
			port = persons.get(_azon);
		}

		try {
			socket = new Socket("localhost", port);
			osw = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
			osw.write(action, 0, action.length());
		} catch (IOException e) {
			System.err.print(e);
		}finally {
			socket.close();
		}
	}

	public static void finished(String nev){

	}

	public static void sendExit() throws IOException {
		for ( Integer port : foglaltPortok) {
			try (Socket s = new Socket("localhost", port);
				Scanner sc = new Scanner(s.getInputStream());
				PrintWriter pw = new PrintWriter(s.getOutputStream());
			){
				pw.println("exit");
				pw.flush();
			} catch ( UnknownHostException e) {
				System.err.print(e);
			}
		}
	}
}