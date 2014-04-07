package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/*
 * Classe che rappresenta la menubar del damaframe
 * 
 */

@SuppressWarnings("serial")
public class DamaMenuBar extends JMenuBar {
	
	private DamaFrame frame;
	
	public DamaMenuBar(DamaFrame dama){
		super();
		this.frame=dama;
		JMenu menuPartita=new JMenu("Partita");
		JMenu menuModifica=new JMenu("Modifica");
		this.add(menuPartita);
		this.add(menuModifica);
		
		//Menu partita
		
		//tasto per ricominciare la partita
		JMenuItem miNuova=new JMenuItem("Ricomincia partita");
		miNuova.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.ricomincia();
			}
			
		});
		
		//tasto per visualizzare il replay
		JMenuItem miReplay=new JMenuItem("Replay last move");
		miReplay.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.replay();
			}
			
		});
		
		//tasto per uscire dal gioco
		JMenuItem miEsci=new JMenuItem("Esci");
		miEsci.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
			
		});
		menuPartita.add(miNuova);
		menuPartita.add(miReplay);
		menuPartita.add(miEsci);
		
		
		//Menu modifica
		
		//tasto per svuotare la damiera ed entrare in modalità modifica
		JMenuItem miModificaNew=new JMenuItem("Svuota e Modifica");
		miModificaNew.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.resetForEdit();
			}
			
		});
		
		//tasto per modificare la situazione corrente
		JMenuItem miModifica=new JMenuItem("Modifica Corrente");
		miModifica.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.riempiConEdit();
			}
			
		});
		
		
		//tasto per uscire dalla modalità Modifica e continuare la partita
		JMenuItem miContinua=new JMenuItem("Continua Partita");
		miContinua.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.continueGame();
			}
			
		});
		
		menuModifica.add(miModificaNew);
		menuModifica.add(miModifica);
		menuModifica.add(miContinua);
	}
}
