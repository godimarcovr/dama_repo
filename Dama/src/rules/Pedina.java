package rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Oggetto che rappresenta una Pedina sulla scacchiera. Contiene informazioni quale il colore e se è damone o no.
 * Inoltre si occupa di fornire i movimenti possibili della pedina.
 * La Pedina non ha alcuna conoscenza della posizione in cui si trova o dello stato della scacchiera.
 */
public class Pedina {
	public final boolean isWhite;
	private boolean isDamone;
	//indico quali variazioni di coordinata una pedina deve avere se compie una mossa
	private int[] movimentiXpermessi, movimentiYpermessi;
	
	private static int[] movAll={-1,1}
						,movYPedW={1}
						,movYPedB={-1};
	
	public Pedina(boolean white){
		this.isWhite=white;
		this.isDamone=false;
		this.movimentiXpermessi=movAll;
		if(white){
			this.movimentiYpermessi=movYPedW;
		}
		else{
			this.movimentiYpermessi=movYPedB;
		}
	}
	
	public Pedina(Pedina ped){
		this(ped.isWhite);
		if(ped.isDamone){
			this.promuovi();
		}
	}
	
	public void promuovi(){
		this.isDamone=true;
		this.movimentiYpermessi= movAll;
	}
	
	private static boolean contiene(int[] a, int x){
		for(int i:a){
			if(i==x){
				return true;
			}
		}
		return false;
	}
	
	public boolean canDoThisMove(Mossa m){
		//calcolo la variazione sulla x e sulla y
		Posizione delta=m.getArrivo().sottrazione(m.getPartenza());
		//se la variazione di x è fra quelle permesse e lo stesso per la variazione di y
		if (contiene(this.movimentiXpermessi,delta.getX()) && contiene(this.movimentiYpermessi,delta.getY())){
			return true;
		}
		return false;
		
	}
	
	//ritorna tutti i possibili movimenti che la pedina è in grado di compiere (ignora le condizioni della scacchiera)
	private List<Posizione> getMovimentiPossibili(){
		List<Posizione> ret=new ArrayList<Posizione>();
		for (int x : this.movimentiXpermessi) {
			for(int y : this.movimentiYpermessi){
				ret.add(new Posizione(x,y));
			}
		}
		return ret;
	}
	
	//data la posizione di partenza, ritorna tutte le possibili mosse eseguibili (ignora la scacchiera)
	public List<Mossa> getMossePossibili(Posizione start){
		List<Posizione> moves=this.getMovimentiPossibili();
		ArrayList<Mossa> ret=new ArrayList<Mossa>();
		for (int i = 0; i < moves.size(); i++) {
			ret.add(new Mossa(start,start.somma(moves.get(i))));
		}
		return ret;
	}
	
	public boolean puoMangiare(Pedina other){
		return (this.isWhite!=other.isWhite) && (this.isDamone || !other.isDamone);
	}

	public boolean puoDamone(Posizione p){
		if(this.isDamone)
			return false;
		return (this.isWhite && p.getY()==7) || (!this.isWhite && p.getY()==0);
	}
	
	public boolean isDamone(){
		return this.isDamone;
	}
	
	public String toString(){
		if(this.isWhite){
			if(this.isDamone){
				return "☺";
			}
			else{
				return "o";
			}
		}
		else{
			if(this.isDamone){
				return "☻";
			}
			else{
				return "*";
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isDamone ? 1231 : 1237);
		result = prime * result + (isWhite ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(movimentiXpermessi);
		result = prime * result + Arrays.hashCode(movimentiYpermessi);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Pedina))
			return false;
		Pedina other = (Pedina) obj;
		if (isDamone != other.isDamone)
			return false;
		if (isWhite != other.isWhite)
			return false;
		if (!Arrays.equals(movimentiXpermessi, other.movimentiXpermessi))
			return false;
		if (!Arrays.equals(movimentiYpermessi, other.movimentiYpermessi))
			return false;
		return true;
	}
	
	
}
