//Interface da mensagem de comunicação

import java.awt.Color;
import java.awt.Point;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Mensg extends Remote {
	public String getEstado() throws RemoteException;
	public String getNome() throws RemoteException;
	public String getModo() throws RemoteException;
	public Color getCor() throws RemoteException;
	public Point getPonto() throws RemoteException;
}