package ia;

import java.util.ArrayList;
import java.util.List;

import rules.Mossa;
import rules.Partita;

/*
 * Rappresenta una sequenza di mosse (usata per rappresentare una mangiata multipla)
 */


public class MossaMultipla {
	private List<Mossa> mosse;
	private Partita original;
	private boolean isMangiata;
	
	public MossaMultipla(Partita partita){
		this.original=new Partita(partita);
		this.mosse=new ArrayList<Mossa>();
	}
	
	//aggiunge una mossa alla lista di mosse
	//viene aggiunta solo se una continuazione valida della catena (arrivo penultima=partenza ultima)
	public void addMossa(Mossa m){
		if(this.mosse.isEmpty()){
			this.mosse.add(m);
			this.isMangiata=m.mangia();
		}
		else{
			if(this.isMangiata && m.mangia() && this.mosse.get(this.mosse.size()-1).getArrivo().equals(m.getPartenza())){
				this.mosse.add(m);
			}
		}
	}
	
	public void addMosse(List<Mossa> mosse){
		for(Mossa m:mosse){
			this.addMossa(m);
		}
	}
	
	public int getNumeroMangiate(){
		return this.isMangiata?this.mosse.size():0;
	}
	
	//ritorna il numero di damoni che sono stati mangiati durante la mossa
	public int getDamoniMangiati(){
		if(!this.isMangiata){
			return 0;
		}
		else{
			int c=0;
			for(Mossa m:this.mosse){
				if(this.original.getByPosizione(m.getMangiata()).isDamone()){
					c++;
				}
			}
			return c;
		}
	}
	
	
	
	public List<Mossa> getMosse() {
		return mosse;
	}

	public Partita getOriginal() {
		return original;
	}

	public boolean isMangiata() {
		return isMangiata;
	}

	@Override
	public String toString() {
		String ret="";
		for (Mossa m:this.mosse) {
			ret+=m.toString()+"\n";
		}
		return ret;
	}
}
