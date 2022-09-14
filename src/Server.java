//Inteface do Servidor

import java.rmi.Remote;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.io.IOException;

public interface Server extends Remote {
	public void registrar(Client client) throws RemoteException;
	public void remover(String nome) throws IOException;
	public void removerTodos() throws IOException;
	public void autoRemover(String nome) throws RemoteException;
	public ArrayList<String> getListaClientes() throws RemoteException;
	public void resincronizarListaCliente() throws RemoteException;
	public void enviarMsgInfo(Mensg msg) throws RemoteException;
	public byte[] enviarImagemAtual() throws IOException, RemoteException;
	public void enviarImagemAberta(byte[] img) throws IOException;
	public void enviarMensagem(String msg) throws RemoteException;
	public void printClients() throws RemoteException;
}