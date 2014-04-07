package ui;

import rules.Partita;
import view.DamaFrame;
/*
 * Classe usata per avviare la dama in modalità GUI
 * 
 */


public class SwingUI {
	
	public static void main(String[] args) {
		Partita p=new Partita(true);
		DamaFrame df=new DamaFrame(p);
		
	}
}
