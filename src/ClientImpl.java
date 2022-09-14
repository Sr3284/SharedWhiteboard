//Implementação do Client

import static javax.swing.GroupLayout.Alignment.BASELINE;
import static javax.swing.GroupLayout.Alignment.CENTER;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import java.rmi.Remote;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;

public class ClientImpl extends UnicastRemoteObject 
						implements Client, Remote {

	protected ClientImpl() throws RemoteException {
		super();
		listaUsers = new DefaultListModel<>();
		ehAdm = false;
		ehRemovido = false;
	}

	static Server server;
	private boolean ehAdm,
					ehRemovido;
	private JFrame frame;
	private DefaultListModel<String> listaUsers;
	private JButton limpa, salva, abre,
					vermelho, verde, azul, preto, laranja, amarelo, ciano,
					linha, borracha;
	private Canvas clientUI;
	private Color selfColor;
	private String selfMode;
	private String clientNome;
	private Hashtable<String, Point> lstPts = new Hashtable<String, Point>();

	ActionListener actListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			LineBorder borda = new LineBorder(new Color(238,238,238), 2);
			LineBorder borderself = new LineBorder(selfColor, 2);

			if (e.getSource() == limpa) {
				clientUI.limpar();

				if (ehAdm) {
					try {
						clientUI.notificaReset();
					} catch (RemoteException ex) {
						ex.printStackTrace();
					}
				}
			} else if (e.getSource() == salva) {
				try {
					salvar();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == abre) {
				try {
					abrir();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			} else if (e.getSource() == vermelho) {
		    	clientUI.vermelho();
		    } else if (e.getSource() == verde) {
		    	clientUI.verde();
		    } else if (e.getSource() == azul) {
		    	clientUI.azul();
		    } else if (e.getSource() == preto) {
		    	clientUI.preto();
		    } else if (e.getSource() == laranja) {
				clientUI.laranja();
			} else if (e.getSource() == amarelo) {
		    	clientUI.amarelo();
		    } else if (e.getSource() == ciano) {
		    	clientUI.ciano();
		    } else if (e.getSource() == linha) {
		    	clientUI.linha();
		    	linha.setBorder(borderself);
		    	borracha.setBorder(borda);
		    }else if (e.getSource() == borracha) {
		    	clientUI.borracha();
		    	linha.setBorder(borda);
		    	borracha.setBorder(borderself);
		    }

		    selfColor = clientUI.getCor();
		    selfMode = clientUI.getModo();

		    if (e.getSource() == vermelho || e.getSource() == verde || e.getSource() == azul 
		    	|| e.getSource() == preto || e.getSource() == laranja || e.getSource() == amarelo
		    	|| e.getSource() == ciano) {

		    	LineBorder _borda = new LineBorder(selfColor, 2);
		    	switch (selfMode) {
		    		case "linha":
		    			linha.setBorder(_borda);
		    			break;
		    		case "borracha":
		    			borracha.setBorder(_borda);
		    			break;
		    		default:
		    			break; 
		    	}
		    }
		}
	};

	public static void main(String[] args) throws RemoteException {
		try {
			server = (Server)Naming.lookup("//localhost/LousaCollab");
			Client cliente = new ClientImpl();
			String nomeCliente = JOptionPane.showInputDialog("Insira o nome:");
			cliente.setNome(nomeCliente);

			try {
				server.registrar(cliente);
				System.out.println("Registrado no servidor remoto");
			} catch (RemoteException ex) {
				System.out.println("Erro ao registrar");
			}

			System.out.println(nomeCliente + " entrou");

			cliente.iniciaImagem(server);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void salvar() throws IOException {
		FileDialog salvarDialog = new FileDialog(frame, "salvar imagem", FileDialog.SAVE);
		salvarDialog.setVisible(true);

		if (salvarDialog.getFile() != null) {
			String caminhoArq = salvarDialog.getDirectory();
			String nomeArq = salvarDialog.getFile();
			ImageIO.write(clientUI.getImagem(), "png", new File(caminhoArq + nomeArq + ".png"));
		}
	}

	private void abrir() throws IOException {
		FileDialog abrirDialog = new FileDialog(frame, "abrir imagem", FileDialog.LOAD);
		abrirDialog.setVisible(true);

		if (abrirDialog.getFile() != null) {
			String caminhoArq = abrirDialog.getDirectory();
			String nomeArq =abrirDialog.getFile();
			BufferedImage imagem = ImageIO.read(new File(caminhoArq + nomeArq));
			clientUI.desenhaImagem(imagem);
			ByteArrayOutputStream imagemArr = new ByteArrayOutputStream();
			ImageIO.write(imagem, "png", imagemArr);
			server.enviarImagemAberta(imagemArr.toByteArray());
		}
	}

	public String getNome() throws RemoteException {
		return this.clientNome;
	}

	public void setNome(String nome) throws RemoteException {
		this.clientNome = nome;
	}

	public void avisaNome(String nome) throws RemoteException {
		this.clientNome = nome;
		JOptionPane.showMessageDialog(this.frame, "Nome já em uso, renomeado para: " + nome, "Aviso", JOptionPane.WARNING_MESSAGE);
	}

	public void sincronizaListaNome() throws RemoteException {
		/*for(IWhiteboardClientListener listener : this.clientListeners){
            listener.updateClientList(this.getContext().getClientNameList());
        }*/
	}

	public void enviarMsg(String msg) throws RemoteException {
		System.out.println("Mensagem do servidor: " + msg);
	}

	public double tempResposta() throws RemoteException {
		return System.currentTimeMillis();
	}

	public void addUser(String nome) throws RemoteException {
		this.listaUsers.addElement(nome);
	}

	public void removeUser(String nome) throws RemoteException {
		this.listaUsers.removeElement(nome);
	}

	public boolean ehAdm() throws RemoteException {
		return this.ehAdm;
	}

	public boolean ehRemovido() throws RemoteException {
		return this.ehRemovido;
	}

	public void escolheAdm() throws RemoteException {
		this.ehAdm = true;
	}

	public void iniciaImagem(Server server) throws RemoteException {
		frame = new JFrame("Canvas " + clientNome);
		Container conteudo = frame.getContentPane();

		clientUI = new Canvas(clientNome, server, ehAdm);

		vermelho = new JButton();
		vermelho.setBackground(Color.red);
		vermelho.setBorderPainted(false);
		vermelho.setOpaque(true);
		vermelho.setMaximumSize(new Dimension(20, 20));
	    vermelho.addActionListener(actListener);
	    verde = new JButton();
	    verde.setBackground(Color.green);
	    verde.setBorderPainted(false);
	    verde.setOpaque(true);
	    verde.setMaximumSize(new Dimension(20, 20));
	    verde.addActionListener(actListener);
	    azul = new JButton();
	    azul.setBackground(Color.blue);
	    azul.setBorderPainted(false);
	    azul.setOpaque(true);
	    azul.setMaximumSize(new Dimension(20, 20));
	    azul.addActionListener(actListener);
	    preto = new JButton();
	    preto.setBackground(Color.black);
	    preto.setBorderPainted(false);
	    preto.setOpaque(true);
	    preto.setMaximumSize(new Dimension(20, 20));
	    preto.addActionListener(actListener);
	    amarelo = new JButton();
	    amarelo.setBackground(Color.yellow);
	    amarelo.setBorderPainted(false);
	    amarelo.setOpaque(true);
	    amarelo.setMaximumSize(new Dimension(20, 20));
	    amarelo.addActionListener(actListener);
	    laranja = new JButton();
	    laranja.setBackground(Color.orange);
	    laranja.setBorderPainted(false);
	    laranja.setOpaque(true);
	    laranja.setMaximumSize(new Dimension(20, 20));
	    laranja.addActionListener(actListener);
	    ciano = new JButton();
	    ciano.setBackground(Color.cyan);
	    ciano.setBorderPainted(false);
	    ciano.setOpaque(true);
	    ciano.setMaximumSize(new Dimension(20, 20));
	    ciano.addActionListener(actListener);

	    LineBorder borda = new LineBorder(Color.black, 2);
	    linha = new JButton("Linhas");
	    linha.setToolTipText("Desenha linhas");
	    linha.setBorder(borda);
	    linha.addActionListener(actListener);
	    borda = new LineBorder(new Color(167,167,167), 2);
	    borracha = new JButton("Borracha");
	    borracha.setToolTipText("Apaga o canvas");
	    borracha.setBorder(borda);
	    borracha.addActionListener(actListener);

	    limpa = new JButton("Novo Canvas");
	    limpa.setToolTipText("Cria um canvas em branco");
	    limpa.addActionListener(actListener);
	    salva = new JButton("Salvar Canvas");
	    salva.setToolTipText("Salva o canvas como um imagem");
	    salva.addActionListener(actListener);
	    abre = new JButton("Abrir Imagem");
	    abre.setToolTipText("Abre um arquivo de imagem");
	    abre.addActionListener(actListener);

	    if (ehAdm == false) {
	    	limpa.setVisible(false);
	    	salva.setVisible(false);
	    	abre.setVisible(false);
	    }

	    JList<String> lista = new JList<>(listaUsers);
	    JScrollPane usersAtuais = new JScrollPane(lista);
	    usersAtuais.setMaximumSize(new Dimension(200, 200));

	    if (ehAdm) {
	    	lista.addMouseListener(new MouseAdapter() {
	    		public void mouseClicked(MouseEvent ev) {
	    			@SuppressWarnings("unchecked")
	    			JList<String> lista = (JList<String>)ev.getSource();

	    			if (ev.getClickCount() == 2) {
	    				int indice = lista.locationToIndex(ev.getPoint());
	    				String nomeSelecionado = lista.getModel().getElementAt(indice);
	    				int dialogResposta = JOptionPane.showConfirmDialog(frame, "Quer remover " + nomeSelecionado + "?",
	    						"Aviso", JOptionPane.YES_NO_OPTION);

	    				if (dialogResposta == JOptionPane.YES_OPTION) {
	    					try {
	    						server.remover(nomeSelecionado);
	    					} catch (IOException ex) {
	    						ex.printStackTrace();
	    					}
	    				}
	    			}
	    		}
	    	});
	    }

	    GroupLayout layout = new GroupLayout(conteudo);
	    conteudo.setLayout(layout);
	    layout.setAutoCreateGaps(true);
	    layout.setAutoCreateContainerGaps(true);

	    layout.setHorizontalGroup(layout.createSequentialGroup()
    		.addGroup(layout.createParallelGroup(CENTER)
    			.addComponent(linha)
                .addComponent(borracha)
    		)
            .addGroup(layout.createParallelGroup(CENTER)
                .addComponent(clientUI)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(vermelho)
                    .addComponent(verde)
                    .addComponent(azul)
                    .addComponent(preto)
                    .addComponent(laranja)
                    .addComponent(amarelo)
                    .addComponent(ciano)
                )
            )
            .addGroup(layout.createParallelGroup(CENTER)
            	.addComponent(limpa)
            	.addComponent(salva)
            	.addComponent(abre)
            	.addComponent(usersAtuais)
            )
        );
        
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(BASELINE)
                .addGroup(layout.createSequentialGroup()
                	.addComponent(linha)
                	.addComponent(borracha)
                ) 
                .addComponent(clientUI)
                .addGroup(layout.createSequentialGroup()
                	.addComponent(limpa)
                	.addComponent(salva)
                	.addComponent(abre)
                	.addComponent(usersAtuais)
                )
            )
            .addGroup(layout.createParallelGroup(BASELINE)
                .addComponent(vermelho)
                .addComponent(verde) 
            	.addComponent(azul)
            	.addComponent(preto)
            	.addComponent(laranja)
            	.addComponent(amarelo)
                .addComponent(ciano)
            )
        );

        layout.linkSize(SwingConstants.HORIZONTAL, limpa, salva, abre);

        if (ehAdm) {
        	frame.setMinimumSize(new Dimension(750, 600));
        } else {
        	frame.setMinimumSize(new Dimension(730, 590));
        }

        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
        	public void windowClosing(java.awt.event.WindowEvent wE) {
        		if (ehAdm) {
	        		if (JOptionPane.showConfirmDialog(frame, "Você é o administrador, quer fechar a aplicação?", 
	        			"Fechar aplicação?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
	        			try {
	        				server.removerTodos();
	        			} catch (IOException ex) {
	        				ex.printStackTrace();
	        			} finally {
	        				System.exit(0);
	        			}
	        		}
	        	} else {
	        		if (JOptionPane.showConfirmDialog(frame, "Deseja sair?", 
	        			"Fechar o Canvas?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
	        			try {
	        				server.autoRemover(clientNome);
	        			} catch (RemoteException ex) {
	        				ex.printStackTrace();
	        			} finally {
	        				System.exit(0);
	        			}
	        		}
	        	}
        	}
        });
	}

	public void limpaImagem() throws RemoteException {
		if (this.ehAdm == false) {
			this.clientUI.limpar();
		}

	}

	public boolean atualizaTodos(Mensg msg) throws RemoteException {
		if (msg.getNome().compareTo(clientNome) == 0) {
			return true;
		}

		CriaLinha linhaMsg = new CriaLinha();
		System.out.println("Desenho de: " + msg.getNome() + ", Estado: " + msg.getEstado() + ", Posição: " + msg.getPonto().x + " e " + msg.getPonto().y + ", Cor: " + msg.getCor());

		if (msg.getEstado().compareTo("inicio") == 0) {
			lstPts.put(msg.getNome(), msg.getPonto());
			return true;
		}

		selfColor = clientUI.getCor();
		Point lstPt = (Point)lstPts.get(msg.getNome());
		clientUI.getGraphic().setPaint(msg.getCor());

		if (msg.getEstado().compareTo("desenhando") == 0) {
			if (msg.getModo().compareTo("borracha") == 0) {
				clientUI.getGraphic().setStroke(new BasicStroke(15.0f));
			}

			linhaMsg.fazLinha(lstPt, msg.getPonto());
			clientUI.getGraphic().draw(linhaMsg.getForma());
			clientUI.repaint();
			lstPts.put(msg.getNome(), msg.getPonto());
			clientUI.getGraphic().setPaint(selfColor);
			return true;
		}

		if (msg.getEstado().compareTo("fim") == 0) {
			if (msg.getModo().compareTo("linha") == 0) {
				linhaMsg.fazLinha(lstPt, msg.getPonto());
			}
			clientUI.repaint();
			lstPts.remove(msg.getNome());
			clientUI.getGraphic().setPaint(selfColor);
			return true;
		}
		return false;
	}

	public byte[] enviaImagem() throws RemoteException, IOException {
		ByteArrayOutputStream imagemArr = new ByteArrayOutputStream();
		ImageIO.write(this.clientUI.getImagem(), "png", imagemArr);
		return imagemArr.toByteArray();
	}

	public void desenhaImagemAberta(byte[] img) throws IOException {
		BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(img));
		this.clientUI.desenhaImagem(imagem);
	}
	public void setRemovido() throws RemoteException {
		ehRemovido = true;
		listaUsers.removeAllElements();
		frame.setTitle("Você foi expulso");
		Thread t = new Thread(new Runnable(){
			public void run() {
				JOptionPane.showMessageDialog(frame, "Você foi expulso da sessão." + "\n" + "Seu canvas não será mais atualizado.",
					"Aviso", JOptionPane.WARNING_MESSAGE);
			}
		});
		t.start();
	}

	public void fecha() throws IOException {
		ehRemovido = true;
		frame.setTitle("Admin saiu.");
		Thread t = new Thread(new Runnable(){
	        public void run(){
	        	JOptionPane.showMessageDialog(frame, "Administrador saiu" + "\n" +
	    				"Aplicação será fechada.",
	    				"Erro", JOptionPane.ERROR_MESSAGE);
	        	System.exit(0);
	        }
	    });
		t.start();
	}
}