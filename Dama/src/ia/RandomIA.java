package ia;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rules.Mossa;
import rules.Partita;
import rules.Pedina;
import rules.Posizione;

/*
 * Classe per l'IA usata durante lo sviluppo
 * Produce una mossa legale a random
 */

public class RandomIA{

	private static Partita p;
	
	public static void initialize(Partita partita){
		p=partita;
	}
	
	public static Mossa getMossaIA() {
		List<Posizione> pedine=new ArrayList<Posizione>();
		pedine.addAll(p.getScac().whoCanMove(false));
		Random ran=new Random();
		if(!pedine.isEmpty()){
			Posizione pos=pedine.get(ran.nextInt(pedine.size()));
			Pedina ped=p.getScac().getByPosizione(pos);
			List<Mossa> mosse=p.getPossibleMovesByPosizione(pos);
			//mosse.addAll(p.getScac().getAllowedMoves(ped.getMossePossibili(pos)));
			//mosse.addAll(p.getPossibleMovesByPosizione(pos));
			return mosse.get(ran.nextInt(mosse.size()));
		}
		else{
			return null;
		}
		
	}
	
}
