//Classe responsável pelo desenho

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Line2D;

public class CriaLinha {
	private Shape forma;

	public CriaLinha() {
		super();
	}

	public Shape getForma() {
		return forma;
	}

	public void fazLinha(Point inicio, Point fim) {
		forma = new Line2D.Double(inicio.x, inicio.y, fim.x, fim.y);
	}
}