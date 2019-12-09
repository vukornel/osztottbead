import java.util.*;
import java.io.*;
import java.net.*;

public class WorldMain{
	public static void main(String[] args) throws Exception {
        Set<ClientData> otherClients = new HashSet<>();
        
        if (args.length > 0){
			Random r = new Random();
			int kilepesiIdo = args[0];

			//city
			int cityNum = r.nextInt((5 - 3) + 1) + 3;
			for(int i = 0; i < cityNum; i++){
				createCity();
			}

			//person
			int personNum = r.nextInt((8 - 4) + 1) + 4;
			for(int i = 0; i < personNum; i++){
				createPerson();
			}
        }

		try (
			ServerSocket ss = new ServerSocket(12345);
		) {
			while (true) {
				//megkéne hívni
				createCity();
				//valamikor
				createPerson();
			}
		}
	}

	public static Boolean foglalt() {
		// foglalt-e a port (city)
		return false;
	}

	public static void createCity() {
		if (!foglaltemindencity) {
			do {
				Random r = new Random();
				int cityport = r.nextInt((35010 - 35000) + 1) + 35000;
			} while (foglalt());

			ClientData client = new ClientData(new ServerSocket(cityport));
			synchronized (otherClients) {
				otherClients.add(client);
			}

			new Thread(() -> {
				while (client.sc.hasNextLine()) {
					String line = client.sc.nextLine();
					// portok?
					// new city? person?

					synchronized (otherClients) {
						for (ClientData other : otherClients) {
							other.pw.println(line);
							other.pw.flush();

							// ide jönnek a cmdk
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
		} else {
			// valaszolni h failed
		}
	}

	public static void createPerson() {
		if (!foglaltemindenperson) {
			do {
				Random r = new Random();
				int personport = r.nextInt((37000 - 36000) + 1) + 37000;
			} while (foglalt());

			ClientData client = new ClientData(new ServerSocket(personport));
			synchronized (otherClients) {
				otherClients.add(client);
			}

			new Thread(() -> {
				while (client.sc.hasNextLine()) {
					String line = client.sc.nextLine();
					//portok?
					//new city? person?

					synchronized (otherClients) {
						for (ClientData other : otherClients) {
							other.pw.println(line);
							other.pw.flush();
							
							//ide jönnek a cmdk
						}
					}
				}

				synchronized (otherClients) {
					otherClients.remove(client);
					try {
						client.close();
					} catch (Exception e) {}
				}
			}).start();
		}
		else{
			//valaszolni h failed
		}
	}
}