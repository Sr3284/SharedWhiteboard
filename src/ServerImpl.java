//Implemtação do Servidor

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

import java.io.IOException;
import java.io.Serializable;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class ServerImpl extends UnicastRemoteObject 
						implements Server, Serializable {

	Set<String> removidos;
	private Gerenciador cliGerenciador;

	protected ServerImpl() throws RemoteException {
		super();
		this.cliGerenciador = new Gerenciador(this);
		removidos = new HashSet<String>();
	}

	public void registrar(Client client) throws RemoteException {
		if (this.cliGerenciador.contains(client)) {
			throw new IllegalStateException(String.format("Cliente %s já foi registrado.",
											 client.toString()));
		}

		if (this.cliGerenciador.numClients() == 0) {
			client.escolheAdm();
		}

		for (Client outroClient : this.cliGerenciador) {
			if (outroClient.getNome().compareTo(client.getNome()) == 0) {
				int min = 100;
				int max = 999;
				int random_val = (int)(Math.random() * (max - min + 1));

				String nomeNovo = client.getNome() + "_" + Integer.toString(random_val);
				client.avisaNome(nomeNovo);
			}
		}

		for (Client outroClient : this.cliGerenciador) {
			if (outroClient.ehAdm()) {
				client.addUser("*" + outroClient.getNome());
			} else {
				client.addUser(outroClient.getNome());
			}
		}

		this.cliGerenciador.addClient(client);
		client.enviarMsg("Olá " + client.getNome());
		printClients();
		atualizarListaUser("adicionar", client.getNome());
	}

	public void remover(String nome) throws IOException {
		for (Client client : this.cliGerenciador) {
			if (client.getNome().compareTo(nome) == 0) {
				this.cliGerenciador.remove(client);
				client.setRemovido();
				removidos.add(client.getNome());
			}
		}
	}

	public void removerTodos() throws IOException {
		System.out.println("Admin saiu, fechando");
		removidos.clear();

		for (Client client : this.cliGerenciador) {
			this.cliGerenciador.remove(client);
			if (client.ehAdm() == false) {
				client.fecha();
			}
		}
	}

	public void autoRemover(String nome) throws RemoteException {
		for (Client client : this.cliGerenciador) {
			if (client.getNome().compareTo(nome) == 0) {
				System.out.println(nome + " saiu");
				this.cliGerenciador.remove(client);
			}
		}

		atualizarListaUser("remover", nome);
		removidos.remove(nome);
	}

	public ArrayList<String> getListaClientes() throws RemoteException {
		ArrayList<String> cliNomes = new ArrayList<String>();

		for (Client client : this.cliGerenciador) {
			cliNomes.add(client.getNome());
		}

		return cliNomes;
	}

	public void resincronizarListaCliente() {
		for (Client client : cliGerenciador) {
			try {
				client.sincronizaListaNome();
			} catch (RemoteException err) {
				err.printStackTrace();
			}
		}
	}

	public void enviarMsgInfo(Mensg msg) throws RemoteException {
		if (removidos.contains(msg.getNome())) {
			return;
		}

		System.out.println(msg.getNome() + " " + msg.getEstado() + " " + msg.getModo());

		for (Client client : this.cliGerenciador) {
			System.out.println(client.getNome() + " recebendo...");
			client.atualizaTodos(msg);
		}
	}

	public byte[] enviarImagemAtual() throws IOException, RemoteException {
		byte[] atual = null;

		for (Client client : this.cliGerenciador) {
			if (client.ehAdm()) {
				atual = client.enviaImagem();
			}
		}
		return atual;
	}

	public void enviarImagemAberta(byte[] img) throws IOException {
		for (Client client : this.cliGerenciador) {
			if (client.ehAdm() == false) {
				client.desenhaImagemAberta(img);
			}
		}
	}

	public void enviarMensagem(String msg) throws RemoteException{
		if (msg.compareTo("novo") == 0) {
			for (Client client : this.cliGerenciador) {
				client.limpaImagem();
			}
		}
	}

	public void printClients() throws RemoteException {
		if (this.cliGerenciador.numClients() > 0) {
			System.out.println(this.cliGerenciador.numClients() + " ativos");
			for (Client client : this.cliGerenciador) {
				System.out.println(client.getNome());
			}
		} else {
			System.out.println("Nenhum cliente.");
		}
	}

	public void atualizarListaUser(String acao, String nome) throws RemoteException {
		if (this.cliGerenciador.numClients() > 0) {
			for (Client client : this.cliGerenciador) {
				if (client.getNome().compareTo(nome) != 0) {
					if (acao.compareTo("adicionar") == 0) {
						client.addUser(nome);
					} else if (acao.compareTo("remover") == 0) {
						client.removeUser(nome);
					}
				}
			}
		} else {
			System.out.println("Nenhum cliente");
		}
	}
}