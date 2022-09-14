//Classe que gerencia os clientes

import java.util.Set;
import java.util.Iterator;
import java.util.Collections;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

public class Gerenciador implements Iterable<Client> {
	private Set<Client> clients;
	private Server server;

	public Gerenciador(Server server) {
		this.clients = Collections.newSetFromMap(new ConcurrentHashMap<Client, Boolean>());
		this.server = server;
		new Thread(new ClientPing(this)).start();
	}

	public void addClient(Client client) {
		try {
			this.clients.add(client);
			this.server.resincronizarListaCliente();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}

	public boolean contains(Client client) {
		return this.clients.contains(client);
	}

	public int numClients() {
		return this.clients.size();
	}

	public void remove(Client client) {
		try {
			this.clients.remove(client);
			this.server.resincronizarListaCliente();
		} catch (RemoteException ex) {
			ex.printStackTrace();
		}
	}

	public Iterator<Client> iterator() {
		return clients.iterator();
	}

	class ClientPing implements Runnable {
		Gerenciador admin;

		public ClientPing(Gerenciador admin) {
			this.admin = admin;
		}

		public void run() {
			while(true) {
				if (admin.numClients() > 0) {
					for (Iterator<Client> iterator = this.admin.iterator(); iterator.hasNext();) {
						Client client = iterator.next();
						try {
							client.tempResposta();
						} catch (RemoteException ex) {
							System.err.println("Conexão perdida...");
							admin.remove(client);
						}
					}
				} else {
					System.out.println("Nenhum cliente");
				}

				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException ex) {
					continue;
				}
			}
		}
	}
}