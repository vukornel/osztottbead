import java.util.*;
import java.io.*;
import java.net.*;

public class WorldMain{
	public static void main(String[] args) throws Exception {
        Set<ClientData> otherClients = new HashSet<>();
        
        if (args.length > 0){
            //automaziált blabla
        }

		try (
			ServerSocket ss = new ServerSocket(12345);
		) {
			while (true) {
				ClientData client = new ClientData(ss);
				synchronized (otherClients) {
					otherClients.add(client);
				}

				new Thread(() -> {
					while (client.sc.hasNextLine()) {
						String line = client.sc.nextLine();

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
		}
	}
}