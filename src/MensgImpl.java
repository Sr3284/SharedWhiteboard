//Implementação de Mensg

import java.awt.Color;
import java.awt.Point;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MensgImpl extends UnicastRemoteObject implements Mensg {
	private String estado, nome, modo;
	private Color cor;
	private Point ponto;

	public MensgImpl(String estado, String nome, String modo, Color cor, Point ponto) throws RemoteException{
		try {	
			this.estado = estado;
			this.nome = nome;
			this.modo = modo;
			this.cor = cor;
			this.ponto = ponto;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public String getEstado(){
		return this.estado;
	}

	public String getNome() {
		return this.nome;
	}

	public String getModo() {
		return this.modo;
	}

	public Color getCor() {
		return this.cor;
	}

	public Point getPonto() {
		return this.ponto;
	}
}