package view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import rules.Mossa;
import rules.Pedina;
import rules.Posizione;

/*
 * Classe che rappresenta la casella di gioco
 * Si occupa di provvedere lo sfondo del colore giusto e di visualizzare pedine e damoni
 * Inoltre gestisce tramite l'evento la chiamata all'IA e l'esecuzione della mossa
 * 
 * BIANCO E MARRONE= colore caselle normali
 * VERDE= pedine selezionabili per essere mosse
 * AZZURRINO= pedina selezionata
 * GIALLO= possibili destinazioni per la pedina selezionata
 * NERO= vittima di mangiata
 */

@SuppressWarnings("serial")
public class Casella extends JButton {

	protected DamaFrame dama;
	protected Posizione p;

	protected Casella(){
		super();
	}
	
	public Casella(int x, int y, final DamaFrame dama) {
		super();
		this.dama = dama;
		this.p = new Posizione(x, y);
		this.disegnaSfondo();
		this.disegnaPedina();

		this.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Pedina ped = dama.getPartita().getByPosizione(p);
				// SE RICLICCO SU QUELLO GIA SELEZIONATO
				if (p.equals(dama.getSelezionata())) {
					// DESELEZIONO LA PEDINA ATTUALE
					// E RESETTO LE POSSIBILITà DI MOVIMENTO
					dama.setSelezionata(null);
					dama.getPossibiliMosse().clear();
				}
				// ALTRIMENTI SE CLICCO SU QUALCOS'ALTRO
				else {
					// SE CLICCO SU UNA CASELLA VUOTA
					if (ped == null) {
						// E HO UNA PEDINA SELEZIONATA
						if (dama.getSelezionata() != null) {
							// E SCELGO UNA DESTINAZIONE VALIDA
							if (dama.isPosizioneCandidateDestination(p)) {
								// MUOVO
								Mossa move = dama.getMossaByDestination(p);
								dama.getPartita().muovi(move);
								dama.setSelezionata(null);
								dama.getPossibiliMosse().clear();

								// SE MOSSA MULTIPLA
								if (dama.getPartita().isMangiataMultipla()) {
									dama.setSelezionabileDopoMangiata(move.getArrivo());
									dama.setSelezionata(move.getArrivo());
									dama.setPossibiliMosse(dama.getPartita()
											.getPossibleMovesByPosizione(p));
								}
								// ALTRIMENTI (MOSSA CHE NON MANGIA O CHE MANGIA
								// MA SENZA POSSIBILITà DI CONTINUARE
								else {
									dama.clearSelezionabili();
								}
							}
						}
					}
					// SE CLICCO SU ALTRA PEDINA
					else {
						// SE LA PEDINA CLICCATA è FRA QUELLE SELEZIONABILI
						if (dama.getPedineSelezionabili().contains(p)) {
							// LA SELEZIONO
							dama.setSelezionata(p);
							dama.setPossibiliMosse(dama.getPartita()
									.getPossibleMovesByPosizione(p));
						}

					}
				}
				//faccio partire la procedura di disegno
				//threaddata e con invokeAndWait per problemi di sincronizzazione con l'IA
				(new Thread(){
					public void run(){
						try {
							SwingUtilities.invokeAndWait(new Thread(){
								public void run(){
									dama.riempi();
								}
							});
						} catch (InvocationTargetException
								| InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
				//Se tocca al computer, faccio partire l'ia
				//threaddata per poter vedere separatamente le mosse in caso di mossa multipla
				if(!dama.getPartita().getTurno()){
					(new Thread(){
						public void run(){
							dama.eseguiIA();
						}
					}).start();
				}
				
			}
		});

	}
	
	protected void disegnaPedina(){
		Pedina ped = this.dama.getPartita().getByPosizione(this.p);
		if (ped != null) {
			this.setIcon(ped.isWhite ? (ped.isDamone() ? this.dama.wdamone
					: this.dama.wdama) : (ped.isDamone() ? this.dama.rdamone
					: this.dama.rdama));
		}
	}
	
	protected void disegnaSfondo(){
		if (!this.p.equals(this.dama.getSelezionata())) {
			if (this.dama.isPosizioneCandidateDestination(this.p)) {
				// DESTINAZIONE
				this.setBackground(Color.yellow);
			} else {
				if (this.dama.isPosizioneCandidateEating(p)) {
					// MANGIABILE
					this.setBackground(Color.black);
				} else {
					if (this.dama.getPedineSelezionabili().contains(this.p)) {
						this.setBackground(Color.green);
					} else {
						this.setBackground(((this.p.getX() + this.p.getY()) % 2 != 0) ? new Color(136,
								62, 0) : new Color(255, 255, 255));
					}
				}
			}

		}
	}
}
