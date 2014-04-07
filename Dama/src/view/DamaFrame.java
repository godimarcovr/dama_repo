package view;

import ia.MeCMIA;
import ia.MossaMultipla;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import rules.Mossa;
import rules.Partita;
import rules.Posizione;

@SuppressWarnings("serial")
/*
 * Principale frame per il gioco: visualizza lo stato della partita e permette al giocatore 
 * di muovere (forza il giocatore a fare solo mosse valide usando metodi del package "rules")
 * 
 * 
 */

public class DamaFrame extends JFrame {

	private static final int WIDTH = 600, HEIGTH = 600;
	private Partita partita;
	protected ImageIcon rdama, wdama, rdamone, wdamone;
	private Posizione pedinaselezionata;
	private List<Mossa> possibili_mosse;
	private List<Posizione> pedineSelezionabili;
	public List<JButton> caselle;
	private MossaMultipla ultimaMossaIA = null;
	private Partita beforeUltimaMossaIA = null;

	public DamaFrame(Partita partita) {
		super("Dama");
		this.partita = partita;
		this.caselle = new ArrayList<JButton>();
		//Inizializzo l'IA
		MeCMIA.initialize(this.partita);
		this.possibili_mosse = new ArrayList<Mossa>();
		this.setSize(WIDTH, HEIGTH);
		this.setSelezionabiliPerNuovaMossa();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.setVisible(true);
		this.setLayout(new GridLayout(8, 8));
		this.setJMenuBar(new DamaMenuBar(this));
		//Preparo per la funzionalità di replay
		this.beforeUltimaMossaIA=new Partita(this.partita);
		this.ultimaMossaIA=new MossaMultipla(this.beforeUltimaMossaIA);
		
		//Carico le icone delle pedine
		try {
			BufferedImage img = ImageIO.read(this.getClass()
					.getResourceAsStream("/reddama.png"));
			this.rdama = new ImageIcon(img.getScaledInstance(WIDTH / 10,
					HEIGTH / 10, Image.SCALE_SMOOTH));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Immagine reddama.png non trovata!");
		}

		try {
			BufferedImage img = ImageIO.read(this.getClass()
					.getResourceAsStream("/whitedama.png"));

			this.wdama = new ImageIcon(img.getScaledInstance(WIDTH / 10,
					HEIGTH / 10, Image.SCALE_SMOOTH));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Immagine whitedama.png non trovata!");
		}

		try {
			BufferedImage img = ImageIO.read(this.getClass()
					.getResourceAsStream("/whitedamone.png"));

			this.wdamone = new ImageIcon(img.getScaledInstance(WIDTH / 10,
					HEIGTH / 10, Image.SCALE_SMOOTH));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Immagine whitedamone.png non trovata!");
		}

		try {
			BufferedImage img = ImageIO.read(this.getClass()
					.getResourceAsStream("/reddamone.png"));

			this.rdamone = new ImageIcon(img.getScaledInstance(WIDTH / 10,
					HEIGTH / 10, Image.SCALE_SMOOTH));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					"Immagine reddamone.png non trovata!");
		}

		this.riempi();
	}

	// Usato in riempi() per poter usare showMessageDialog
	private JFrame getThis() {
		return this;
	}

	//Distrugge e ricostruisce tutte le caselle per disegnarle
	public void riempi() {
		this.getContentPane().removeAll();
		this.caselle.clear();
		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				Casella c = new Casella(x, y, this);
				this.add(c);
				this.caselle.add(c);
			}
		}
		this.revalidate();

		//se la partita è finita, visualizzo la schermata finale
		if (this.partita.isFinished()) {
			SwingUtilities.invokeLater(new Thread() {
				public void run() {
					if (partita.isVittoriaDi(true)) {
						JOptionPane.showMessageDialog(getThis(),
								"Il giocatore vince la partita!!");
					}
					if (partita.isVittoriaDi(false)) {
						JOptionPane.showMessageDialog(getThis(),
								"Il computer vince la partita!!");
					}
					
					setTitle("Partita Terminata");
					if(JOptionPane.showOptionDialog(getThis(), "Scegli cosa fare", "PARTITA TERMINATA"
							, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
							, null, new String[]{"Ricomincia","Chiudi"}, null)==0){
						ricomincia();
					}
					else{
						System.exit(0);
					}
				}
			});

		}
	}

	//Resetta la partita come se fosse appena iniziata
	public void ricomincia(){
		this.setTitle("Dama");
		this.getContentPane().setLayout(new GridLayout(8,8));
		this.clearSelezionabili();
		this.setSelezionata(null);
		this.getPossibiliMosse().clear();
		this.getPartita().reset(true);
		this.setSelezionabiliPerNuovaMossa();
		this.riempi();
	}
	
	//Distrugge le caselle e le ricostruisce in modalità Modifica
	public void riempiConEdit() {
		this.setSelezionata(null);
		this.getPossibiliMosse().clear();
		this.setTitle("-Modalità Modifica (Modifica>Continua Partita per riprendere)-");
		this.getContentPane().removeAll();
		this.caselle.clear();
		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				CasellaModificabile c = new CasellaModificabile(x, y, this);
				this.add(c);
				this.caselle.add(c);
			}
		}
		this.revalidate();
	}

	//Setta le variabili necessarie al replay
	public void setForReplay(Partita part, MossaMultipla mm) {
		this.ultimaMossaIA = mm;
		this.beforeUltimaMossaIA = part;
	}

	public MossaMultipla getUltimaMossaIA() {
		return ultimaMossaIA;
	}

	public Partita getBeforeUltimaMossaIA() {
		return beforeUltimaMossaIA;
	}

	//esegue il replay
	public void replay() {
		this.partita = new Partita(this.getBeforeUltimaMossaIA());
		MeCMIA.initialize(this.partita);
		
		(new Thread() {
			public void run() {
				getPedineSelezionabili().clear();
				//per ogni mossa dell'ultimo turno dell'IA
				for (Mossa m : ultimaMossaIA.getMosse()) {
					//disegno la scacchiera
					try {
						SwingUtilities.invokeAndWait(new Thread() {
							public void run() {
								riempi();
							}
						});
					} catch (InvocationTargetException | InterruptedException e) {	}
					//attendo
					try {
						Thread.sleep(450);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//eseguo la mossa
					getPartita().muovi(m);
				}
				//disegno la scacchiera
				try {
					SwingUtilities.invokeAndWait(new Thread() {
						public void run() {
							setSelezionabiliPerNuovaMossa();
							riempi();
						}
					});
				} catch (InvocationTargetException | InterruptedException e) {	}
				setSelezionabiliPerNuovaMossa();
			}
		}).start();
	}

	public void setSelezionata(Posizione p) {
		this.pedinaselezionata = p;
	}

	public Posizione getSelezionata() {
		return this.pedinaselezionata;
	}

	public Partita getPartita() {
		return this.partita;
	}

	public List<Mossa> getPossibiliMosse() {
		return this.possibili_mosse;
	}

	public void setPossibiliMosse(List<Mossa> l) {
		this.possibili_mosse = l;
	}

	//ritorna true se la posizione indicata è una possibile destinazione per una mossa del giocatore
	public boolean isPosizioneCandidateDestination(Posizione p) {
		if(this.possibili_mosse==null) return false;
		for (Mossa m : this.possibili_mosse) {
			if (m.getArrivo().equals(p)) {
				//se trovo una mossa fra quelle legali che ha come destinazione quella richiesta
				return true;
			}
		}

		return false;
	}

	//ritorna true se la posizione indicata è una possibile candidata per essere mangiata da una mossa
	public boolean isPosizioneCandidateEating(Posizione p) {
		if (this.possibili_mosse == null) {
			return false;
		}
		for (Mossa m : this.possibili_mosse) {
			//se fra tutte le mosse legali c'è nè una che mangia la pedina indicata
			if (m.getMangiata() != null && m.getMangiata().equals(p)) {
				return true;
			}
		}

		return false;
	}

	//ritorna la mossa fra quelle possibili che finisce sulla destinazione indicata
	public Mossa getMossaByDestination(Posizione p) {
		for (Mossa m : this.possibili_mosse) {
			if (m.getArrivo() != null && m.getArrivo().equals(p)) {
				return m;
			}
		}
		return null;
	}

	public List<Posizione> getPedineSelezionabili() {
		return pedineSelezionabili;
	}
	
	//setta le pedine che possono essere selezionate per il movimento
	public void setSelezionabiliPerNuovaMossa() {
		this.pedineSelezionabili = this.partita.getScac().whoCanMove(true);
	}

	//setta la pedina che può essere selezionata per il movimento (mangiata multipla)
	public void setSelezionabileDopoMangiata(Posizione p) {
		this.pedineSelezionabili.clear();
		this.pedineSelezionabili.add(p);
	}

	public void clearSelezionabili() {
		this.pedineSelezionabili.clear();
	}
	
	//richiama l'Intelligenza Artificiale per fare la mossa
	public void eseguiIA() {

		if (!this.getPartita().isFinished()) {
			this.setForReplay(new Partita(this.partita), MeCMIA.getMosseIA());
			// dentro un thread per uscire dall'event dispatch thread e fare
			// aggiornamenti in modo indipendente
			// non ho particolari problemi di sincronizzazione perché la gui è
			// bloccata finchè non vengono eseguite tutte le mosse
			(new Thread() {
				public void run() {
					//per ciascuna mossa che l'IA compie
					for (Mossa m : ultimaMossaIA.getMosse()) {
						//disegno
						try {
							SwingUtilities.invokeAndWait(new Thread() {
								public void run() {
									riempi();
								}
							});
						} catch (InvocationTargetException
								| InterruptedException e) {
						}
						//attendo
						try {
							Thread.sleep(450);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						//muovo
						getPartita().muovi(m);
					}
					//disegno
					try {
						SwingUtilities.invokeAndWait(new Thread() {
							public void run() {
								setSelezionabiliPerNuovaMossa();
								riempi();
							}
						});
					} catch (InvocationTargetException | InterruptedException e) {
					}
				}
			}).start();

		}

	}
	//svuoto la scacchiera e mi metto in modalità Modifica
	public void resetForEdit() {
		this.partita.svuota(true);
		this.riempiConEdit();
	}
	
	//riprende il gioco dopo la modifica partendo dal turno del giocatore
	public void continueGame() {
		this.setTitle("Dama");
		this.setSelezionabiliPerNuovaMossa();
		this.riempi();

	}
}
