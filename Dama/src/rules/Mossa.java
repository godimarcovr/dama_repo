package rules;

/*
 * Un oggetto contenente 3 Posizione, rappresenta una mossa di una pedina sulla scacchiera.
 * La mangiata è opzionale.
 * Non viene fatto alcun controllo sulla validità della mossa. (Esistenza pedina, movimento corretto ecc) e non
 * supporta le mangiate multiple.
 */

public class Mossa {
	private Posizione partenza, arrivo;
	private Posizione mangiata;

	public Mossa(int xStart, int yStart, int xFine, int yFine) {
		this.partenza=new Posizione(xStart, yStart);
		this.arrivo=new Posizione(xFine, yFine);
		this.mangiata=null;
	}

	public Mossa(Posizione partenza, Posizione arrivo) {
		this(partenza.getX(),partenza.getY(),arrivo.getX(),arrivo.getY());
	}

	public Mossa(Posizione partenza, Posizione arrivo, Posizione mangiata) {
		this(partenza,arrivo);
		this.mangiata = mangiata;
	}

	public Posizione getPartenza() {
		return partenza;
	}

	public Posizione getArrivo() {
		return arrivo;
	}

	public Posizione getMangiata() {
		return mangiata;
	}
	
	public boolean mangia(){
		return this.mangiata!=null;
	}

	public void setMangiata(Posizione mangiata) {
		this.mangiata = mangiata;
	}
	
	public Posizione getDirezioneMossa(){
		return this.arrivo.sottrazione(this.partenza);
	}
	
	@Override
	public String toString() {
		return "Mossa [partenza=" + partenza + ", arrivo=" + arrivo
				+ ", mangiata=" + mangiata + "]";
	}
	
	
	
	
	
	
}
