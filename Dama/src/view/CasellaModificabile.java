package view;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import rules.Pedina;
import rules.Posizione;
import rules.Scacchiera;

/*
 * Rappresenta la casella modificabile, ossia permette di mettere e togliere pedine
 * 
 */


@SuppressWarnings("serial")
public class CasellaModificabile extends Casella {

	public CasellaModificabile(int x, int y, final DamaFrame dama) {
		super();
		this.dama = dama;
		this.p = new Posizione(x, y);
		this.disegnaSfondo();
		this.disegnaPedina();
		this.addMouseListener(new MouseListener(){

			@Override
			public void mousePressed(MouseEvent e) {
				Pedina ped=dama.getPartita().getByPosizione(p);
				//se non c'è pedina, ne metto una bianca col click sinistro e una nera col destro
				if(ped==null){
					if(Scacchiera.isAllowedCell(p.getX(), p.getY())){
						dama.getPartita().getScac()
						.addPedina(p, SwingUtilities.isLeftMouseButton(e)?new Pedina(true):new Pedina(false));
						if(SwingUtilities.isLeftMouseButton(e)){
							dama.getPartita().addRimasteGiocatore(1);
						}
						else{
							dama.getPartita().addRimasteCPU(1);
						}
							
					}
					
				}
				else{
					//se c'è una pedina ed è bianca
					if(ped.isWhite){
						//se ho fatto click sinistro
						if(SwingUtilities.isLeftMouseButton(e)){
							//diventa damone
							Pedina toGive=new Pedina(true);
							toGive.promuovi();
							dama.getPartita().getScac().addPedina(p, toGive);
						}
						if(SwingUtilities.isRightMouseButton(e)){
							//altrimenti la degrado (damone->pedina o pedina-->null)
							if(ped.isDamone()){
								dama.getPartita().getScac().addPedina(p, new Pedina(true));
							}
							else{
								dama.getPartita().getScac().addPedina(p, null);
								dama.getPartita().addRimasteGiocatore(-1);
							}
							
						}
					}
					else{
						//viceversa per le pedine nere
						if(SwingUtilities.isRightMouseButton(e)){
							Pedina toGive=new Pedina(false);
							toGive.promuovi();
							dama.getPartita().getScac().addPedina(p, toGive);
						}
						if(SwingUtilities.isLeftMouseButton(e)){
							if(ped.isDamone()){
								dama.getPartita().getScac().addPedina(p, new Pedina(false));
							}
							else{
								dama.getPartita().getScac().addPedina(p, null);
								dama.getPartita().addRimasteCPU(-1);
							}
						}
					}
				}
				//disegno
				(new Thread(){
					public void run(){
						try {
							SwingUtilities.invokeAndWait(new Thread(){
								public void run(){
									dama.riempiConEdit();
								}
							});
						} catch (InvocationTargetException | InterruptedException e) {
							e.printStackTrace();
						}
					}
				}).start();
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {}

			@Override
			public void mouseExited(MouseEvent arg0) {	}

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
	}
	
	@Override
	protected void disegnaSfondo(){
		this.setBackground(((this.p.getX() + this.p.getY()) % 2 != 0) ? new Color(136, 62, 0)
		: new Color(255, 255, 255));
	}
}
