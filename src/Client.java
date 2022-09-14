//Interface Cliente

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;

public interface Client extends Remote {
	public String getNome() throws RemoteException;
	public void setNome(String nome) throws RemoteException;
	public void avisaNome(String nome) throws RemoteException;
	public void sincronizaListaNome() throws RemoteException;
	public void enviarMsg(String msg) throws RemoteException;
	public double tempResposta() throws RemoteException;
	public void addUser(String nome) throws RemoteException;
	public void removeUser(String nome) throws RemoteException;
	public boolean ehAdm() throws RemoteException;
	public boolean ehRemovido() throws RemoteException;
	public void escolheAdm() throws RemoteException;
	public void iniciaImagem(Server server) throws RemoteException;
	public void limpaImagem() throws RemoteException;
	public boolean atualizaTodos(Mensg msg) throws RemoteException;
	public byte[] enviaImagem() throws RemoteException, IOException;
	public void desenhaImagemAberta(byte[] img) throws IOException;
	public void setRemovido() throws RemoteException;
	public void fecha() throws IOException;
}