import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.io.*;
import java.net.*;

public class WorldMain {
	static Set<Integer> foglaltPortok = new HashSet<>();
	static Map<String, Integer> cities = new HashMap<>();
	static Map<String, Integer> persons = new HashMap<>();
	static int cityNumber = 0;
	static int personNumber = 0;

	public static void main(String[] args) throws Exception {
		try (ServerSocket ss = new ServerSocket(4321);
				Socket s = ss.accept();
				Scanner scIn = new Scanner(s.getInputStream());) {
			if (args.length > 0) {
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
						if (lineTomb.length < 2)
							createCity("");
						else
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
						if (lineTomb.length < 2)
							createPerson("");
						else
							createPerson(lineTomb[1]);
						break;
					}
					case "do": {
						sendDo(lineTomb[1], lineTomb[2]);
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

	public static void randomStart(String string) throws FileNotFoundException {
		Random r = new Random();
		int kilepesiIdo = Integer.parseInt(string);

		int db = r.nextInt((5 - 3) + 1) + 3;
		for (int i = 0; i < db; i++)
			createCity("");

		db = r.nextInt((8 - 4) + 1) + 4;
		for (int i = 0; i < db; i++)
			createPerson("");

		new java.util.Timer().schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				try {
					sendExit();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, kilepesiIdo * 1000);
	}

	public static Boolean foglalt(int port) {
		return foglaltPortok.contains(port);
	}

	public static void createCity(String nev) throws FileNotFoundException {
		if (cityNumber < 11) {

			int cityport;
			if (nev == "")
				nev = randomCityName();

			do {
				Random r = new Random();
				cityport = r.nextInt((35010 - 35000) + 1) + 35000;
			} while (foglalt(cityport));

			cityNumber++;
			foglaltPortok.add(cityport);
			cities.put(nev, cityport);
			final int fport = cityport;
			final String vNev = nev;

			new Thread(() -> {
				try (ServerSocket ss = new ServerSocket(fport);) {
					City city = new City(vNev, fport);
					Set<ClientData> otherClients = new HashSet<>();
					System.out.println(vNev + " created at " + fport);
					while (true) {

						// multijoin
						ClientData client = new ClientData(ss);
						synchronized (otherClients) {
							otherClients.add(client);
						}

						new Thread(() -> {
							while (client.sc.hasNextLine()) {
								String line = client.sc.nextLine();

								synchronized (otherClients) {
									for (ClientData other : otherClients) {
										try {
											if(line == "exit") return;
											city.Action(line);
											System.out.println(vNev + " megkapta az utasitast");
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								}
							}

							synchronized (otherClients) {
								otherClients.remove(client);
								try {
									client.close();
								} catch (Exception e) {
								}
							}
						}).start();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println(vNev + " stopped at " + fport);
			}).start();
		} else {
			System.out.println("failed");
		}
	}

	private static String randomCityName() throws FileNotFoundException {
		String token1 = "";
		Scanner inFile1 = new Scanner(new File("cities.txt"));
		List<String> temps = new ArrayList<String>();
		while (inFile1.hasNextLine()) {
			token1 = inFile1.nextLine();
			temps.add(token1);
		}
		inFile1.close();

		Random rand = new Random();
		return temps.get(rand.nextInt(temps.size()));
	}

	private static String randomPersonName() throws FileNotFoundException {

		String token1 = "";
		Scanner inFile1 = new Scanner(new File("persons.txt"));
		List<String> temps = new ArrayList<String>();
		while (inFile1.hasNextLine()) {
			token1 = inFile1.nextLine();
			temps.add(token1);
		}
		inFile1.close();

		Random rand = new Random();
		return temps.get(rand.nextInt(temps.size()));
	}

	public static void cityInfo(String cityName) {
		if (cities.containsKey(cityName)) {
			System.out.println(cities.get(cityName));
		} else {
			System.out.println("none");
		}
	}

	public static void cityList() {
		for (String name : cities.keySet()) {
			System.out.println(name);
			System.out.println("");
		}
	}

	public static void createPerson(String nev) throws FileNotFoundException {
		if (cityNumber == 0) {
			System.out.println("failed");
			return;
		}
		if (personNumber < 1002) {
			int personport;
			if (nev == "")
				nev = randomPersonName();

			personport = 36000;
			while (foglalt(personport)) {
				personport++;
			}

			personNumber++;
			foglaltPortok.add(personport);
			persons.put(nev, personport);
			final int fport = personport;
			final String vNev = nev;

			new Thread(() -> {
				try (ServerSocket ss = new ServerSocket(fport);
					Scanner scIn = new Scanner(System.in);) {
					Random generator = new Random();
					String order;
					Object[] values = cities.values().toArray();
					int randomValue = (int) values[generator.nextInt(values.length)];
					Person person = new Person(vNev, fport, randomValue); // cityport kell
					System.out.println(vNev + " " + (fport - 36000) + " (city: " + randomValue + ")");

					// automatikus müködés? random szám és váltakozva?
					Runnable helloRunnable = new Runnable() {
						public void run() {
							try {
								String ord = randomOrder();
								person.Action(ord, cities);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					};

					ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
					executor.scheduleAtFixedRate(helloRunnable, 0, 3, TimeUnit.SECONDS);

					while (true) {
						while (scIn.hasNextLine()) {
							order = scIn.nextLine();
							person.Action(order, cities);
							System.out.println(person + " got the order " + order);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}).start();
		} else {
			System.out.println("failed");
		}
	}

	private static String randomOrder() {
		Random r = new Random();
		int rand = r.nextInt(3);
		switch (rand){
			case 0:
				System.out.println("random go");
				return "go";
			case 1:
				System.out.println("random spend");
				return "spend";
			case 2:
				System.out.println("random selfie");
				return "selfie";
		}
		System.out.println("ide nem kéne eljutni, ja vagyis ERROR");
		return "";
	}

	public static void sendDo(String _azon, String action) throws IOException {
		Socket socket = null;
		OutputStreamWriter osw;
		int port;
		
		if(Integer.parseInt(_azon) > 35999 && Integer.parseInt(_azon) < 37001){
			port = Integer.parseInt(_azon);
		}else if (Integer.parseInt(_azon) < 1002 && Integer.parseInt(_azon) >= 0){
			port = Integer.parseInt(_azon) + 36000;
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
				if(port < 35011){ // city
					pw.println("exit");
					pw.flush();
				}
				else{ // person
					pw.println("info");
					pw.println("exit");
					pw.flush();
				}
			} catch ( UnknownHostException e) {
				System.err.print(e);
			}
		}

		System.exit(0);
	}
}

class ClientData implements AutoCloseable {
	Socket s;
	Scanner sc;
	PrintWriter pw;

	ClientData(ServerSocket ss) throws Exception {
		s = ss.accept();
		sc = new Scanner(s.getInputStream());
		pw = new PrintWriter(s.getOutputStream());
	}

	public void close() throws Exception {
		if (s == null) return;
		s.close();
	}
}