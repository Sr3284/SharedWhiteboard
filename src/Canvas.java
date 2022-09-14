//Implementação do Canvas

import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.image.ColorModel;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.event.MouseMotionAdapter;

import java.awt.Font;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.RenderingHints;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import java.io.IOException;
import java.rmi.RemoteException;
import java.io.ByteArrayInputStream;

public class Canvas extends JComponent {
	private String cliNome, modoAtual;
	private Color cor;
	private Point pontoAnt, pontoAtual;
	private boolean ehAdm;

	private BufferedImage imagem, ultImagem;
	private Graphics2D grap2;

	private Server server;

	public Canvas(String nome, Server server, boolean ehAdm) {
		this.cliNome = nome;
		this.ehAdm = ehAdm;
		this.server = server;
		this.cor = Color.black;
		this.modoAtual = "linha";

		setDoubleBuffered(false);

		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent mE) {

				pontoAnt = mE.getPoint();
				salvaUltimaImg();

				try {
					MensgImpl item = new MensgImpl("inicio", cliNome, modoAtual, cor, pontoAnt);
					server.enviarMsgInfo(item);
				} catch (RemoteException ex) {
					ex.printStackTrace();
				}
			}
		});

		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent mE) {
				pontoAtual = mE.getPoint();
				CriaLinha linha = new CriaLinha();

				if (grap2 != null) {
					if (modoAtual.compareTo("linha") == 0) {
						limpar();
						desenharUltimaImagem();
						linha.fazLinha(pontoAnt, pontoAtual);
					} else if (modoAtual.compareTo("borracha") == 0) {
						linha.fazLinha(pontoAnt, pontoAtual);
						pontoAnt = pontoAtual;
						grap2.setPaint(Color.white);
						grap2.setStroke(new BasicStroke(15.0f));
						try {
							MensgImpl item = new MensgImpl("desenhando", cliNome, modoAtual, Color.white, pontoAtual);
							server.enviarMsgInfo(item);
						} catch (RemoteException ex) {
							ex.printStackTrace();
						}
					}

					grap2.draw(linha.getForma());
					repaint();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent mE) {

				pontoAtual = mE.getPoint();
				CriaLinha linha = new CriaLinha();

				if (grap2 != null) {
					if (modoAtual.compareTo("linha") == 0) {
						linha.fazLinha(pontoAnt, pontoAtual);
					} else if (modoAtual.compareTo("borracha") == 0) {
						grap2.setPaint(cor);
						grap2.setStroke(new BasicStroke(1.0f));
					}

					if (modoAtual.compareTo("borracha") != 0) {
						grap2.draw(linha.getForma());
					}
					repaint();
					pontoAnt = pontoAtual;

					try {
						MensgImpl item = new MensgImpl("fim", cliNome, modoAtual, cor, pontoAtual);
						server.enviarMsgInfo(item);
					} catch (RemoteException ex) {
						ex.printStackTrace();
					}
				}
			}
		});
	}

	protected void paintComponent(Graphics g) {
		if (imagem == null) {
			if (ehAdm) {
				imagem = new BufferedImage(getSize().width, getSize().height, BufferedImage.TYPE_INT_RGB);
				grap2 = (Graphics2D)imagem.getGraphics();
				grap2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				limpar();
			} else {
				try {
					byte[] imagemBruta = server.enviarImagemAtual();
					imagem = ImageIO.read(new ByteArrayInputStream(imagemBruta));
					grap2 = (Graphics2D)imagem.getGraphics();
					grap2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				    grap2.setPaint(cor);
				} catch (IOException e) {
					System.err.println("Falha na imagem.");
				}
			}
		}
		g.drawImage(imagem, 0, 0, null);
	}

	public Color getCor() {
		return cor;
	}
	
	public String getModo() {
		return modoAtual;
	}
	
	public Graphics2D getGraphic() {
		return grap2;
	}
	
	public BufferedImage getImagem() {
		return imagem;
	}

	public void limpar() {
		grap2.setPaint(Color.white);
		grap2.fillRect(0, 0, getSize().width, getSize().height);
		grap2.setPaint(cor);
		repaint();
	}

	public void salvaUltimaImg() {
		ColorModel cm = imagem.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = imagem.copyData(null);
		ultImagem = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public void desenharUltimaImagem() {
		desenhaImagem(ultImagem);
	}

	public void desenhaImagem(BufferedImage img) {
		grap2.drawImage(img, null, 0, 0);
		repaint();
	}

	public void vermelho() {
		cor = Color.red;
		grap2.setPaint(cor);
	}
 
	public void preto() {
		cor = Color.black;
		grap2.setPaint(cor);
	}
 
	public void verde() {
		cor = Color.green;
		grap2.setPaint(cor);
	}
 
	public void azul() {
		cor = Color.blue;
		grap2.setPaint(cor);
	}
	
	public void laranja() {
		cor = Color.orange;
		grap2.setPaint(cor);
	}
	
	public void amarelo() {
		cor = Color.yellow;
		grap2.setPaint(cor);
	}
	
	public void ciano() {
		cor = Color.cyan;
		grap2.setPaint(cor);
	}

	public void linha() {
		modoAtual = "linha";
	}

	public void borracha() {
		modoAtual = "borracha";
	}

	public void notificaReset() throws RemoteException {
		server.enviarMensagem("novo");
	}
}