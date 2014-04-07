package rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * La Scacchiera contiene una matrice 8x8 di Pedina, effettivamente mantenendo in memoria la posizione
 * di ogni Pedina sul campo.
 * NB mantiene solo le info sulla posizione delle pedine! Non ha lo stato, ossia non tiene conto di chi sia il
 * turno, se sono all'interno di una mangiata doppia o no, o che la partita sia terminata o meno
 * 
 * Contiene una grande varietà di metodi
 */

public class Scacchiera {
	private Pedina[][] griglia;

	public Scacchiera() {
		this.griglia = new Pedina[8][8];
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 8; x++) {
				if (isAllowedCell(x, y)) {
					// pedine bianche nelle tre righe più in basso
					this.griglia[x][y] = new Pedina(true);
				}
			}
		}
		for (int y = 5; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				if (isAllowedCell(x, y)) {
					// pedine nere nelle tre righe più in alto
					this.griglia[x][y] = new Pedina(false);
				}
			}
		}
	}

	//costruttore per la deep copy
	public Scacchiera(Scacchiera scac) {
		this.griglia = new Pedina[8][8];
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				this.griglia[x][y] = scac.griglia[x][y] == null ? null
						: new Pedina(scac.griglia[x][y]);
			}
		}
	}
	
	//inserisce una Pedina nella Posizione indicata
	public void addPedina(Posizione p,Pedina ped){
		this.griglia[p.getX()][p.getY()]=ped;
	}

	//ritorna true se una Posizione è fuori dalla scacchiera
	private static boolean isOutside(int x, int y) {
		return x < 0 || x > 7 || y < 0 || y > 7;
	}
	private static boolean isOutside(Posizione p) {
		return isOutside(p.getX(), p.getY());
	}

	//ritorna true se è una posizione permessa
	public static boolean isAllowedCell(int x, int y) {
		// una pedina può stare in una cella se è dentro la scacchiera e su una
		// cella nera
		return (!isOutside(x, y)) && ((x + y) % 2 != 0);
	}
	private static boolean isAllowedCell(Posizione p) {
		if (p == null)
			return false;
		return isAllowedCell(p.getX(), p.getY());
	}

	//ritorna l'eventuale Pedina nella Posizione indicata
	public Pedina getByPosizione(Posizione p)
			throws ArrayIndexOutOfBoundsException {
		return this.griglia[p.getX()][p.getY()];
	}
	public Pedina getByIndici(int x, int y)
			throws ArrayIndexOutOfBoundsException {
		return this.griglia[x][y];
	}

	//controlla se una mossa può diventare una mangiata
	public boolean canEat(Mossa m) {
		// PRENDE INPUT DA isMoveAllowed, quindi usa solo partenza e arrivo
		// se una mossa finisce su una pedina già esistente, controllo che possa
		// mangiarla
		// ritorna TRUE se può mangiare la cella di arrivo, altrimenti FALSE
		Posizione dir = m.getDirezioneMossa();
		// controllo che cada su una casella permessa
		if (!isAllowedCell(m.getArrivo().somma(dir))) {
			return false;
		}
		// posso mangiare solo
		// se la posizione dove voglio andare è occupata
		// e quella dietro è libera
		// e la pedina dove voglio andare è mangiabile
		return this.getByPosizione(m.getArrivo()) != null
				&& this.getByPosizione(m.getArrivo().somma(dir)) == null
				&& this.getByPosizione(m.getPartenza()).puoMangiare(
						this.getByPosizione(m.getArrivo()));
	}
	// data una posizione con una pedina, controlla se può effettuare una mangiata
	public boolean canEat(Posizione p) {
		Pedina ped = this.getByPosizione(p);
		if(ped==null) return false;
		List<Mossa> mosse = ped.getMossePossibili(p);
		for (Mossa m : mosse) {
			// deve partire da una cella dentro che abbia una pedina, se non c'è
			// la pedina la mossa non ha senso!
			// inoltre deve finire su una cella permessa
			if (isAllowedCell(m.getPartenza())
					&& this.getByPosizione(m.getPartenza()) != null
					&& isAllowedCell(m.getArrivo())) {
				if (this.canEat(m)) {
					return true;
				}
			}
		}
		return false;

	}

	// ritorna true se il giocatore indicato ha almeno una mangiata fra le sue mosse possibili
	public boolean isMangiata(boolean isWhite) {
		Posizione[] pezzi = this.getTeamPieces(isWhite);
		for (Posizione p : pezzi) {
			if (this.canMove(p)) {
				if (this.canEat(p)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isMoveAllowed(Mossa m) {
		// prende input da getMossePossibili di Pedina

		// non controllo di quanto si sposta, ci penso a livelli superiori
		// controllo solo se parte e arriva in posizioni corrette

		// deve partire da una cella dentro che abbia una pedina, se non c'è la
		// pedina la mossa non ha senso!
		if (!isAllowedCell(m.getPartenza())
				|| this.getByPosizione(m.getPartenza()) == null) {
			return false;
		}
		// deve finire su una cella permessa
		if (!isAllowedCell(m.getArrivo())) {
			return false;
		} else {
			// e se la cella di arrivo è occupata
			if (this.getByPosizione(m.getArrivo()) != null) {
				// e quella dietro non esce dalla scacchiera
				if (isAllowedCell(m.getArrivo().somma(m.getDirezioneMossa()))) {//
					// controllo se posso mangiare
					return this.canEat(m);
				} else {
					return false;
				}
			}
		}

		return true;
	}

	//ritorna la lista di mosse permesse da quella pedina
	public List<Mossa> getAllowedMoves(List<Mossa> list) {
		// data la lista di posizioni che una pedina può raggiungere, ne ritorna
		// il sottoinsieme di quelle permesse
		// prende lista da metodo pedina
		List<Mossa> ret = new ArrayList<Mossa>();
		boolean isThereEat = false;
		for (Mossa m : list) {
			if (this.isMoveAllowed(m)) {
				if (this.canEat(m)) {
					ret.add(new Mossa(m.getPartenza(), m.getArrivo().somma(
							m.getDirezioneMossa()), m.getArrivo()));
					isThereEat = true;
				} else {
					ret.add(m);
				}
			}
		}

		// CONTROLLO MANGIATE
		// SE SO CHE CI SONO MANGIATE, FILTRO LA LISTA TENENDO SOLO LE MOSSE CHE
		// MANGIANO
		// SONO OBBLIGATO A MANGIARE

		if (isThereEat) {
			List<Mossa> newret = new ArrayList<Mossa>();
			for (Mossa m : ret) {
				if (m.mangia()) {
					newret.add(m);
				}
			}
			ret = newret;
		}
		return ret;
	}

	//ritorna true se la Pedina alla Posizione indicata può muoversi
	public boolean canMove(Posizione pos) {
		Pedina ped = this.getByPosizione(pos);
		if (ped == null)
			return false;
		List<Mossa> mosse = ped.getMossePossibili(pos);
		for (Mossa m : mosse) {
			if (this.isMoveAllowed(m)) {
				return true;
			}
		}
		return false;
	}

	//ritorna la lista di Pedina del colore indicato che possono effettuare almeno una Mossa
	public List<Posizione> whoCanMove(boolean isWhite) {
		List<Posizione> l = new ArrayList<Posizione>();
		boolean isThereEat = false;
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Pedina ped = this.getByIndici(x, y);
				if (ped != null && ped.isWhite == isWhite) {
					Posizione p = new Posizione(x, y);
					if (this.canMove(p)) {
						if (this.canEat(p)) {
							isThereEat = true;
						}
						l.add(new Posizione(x, y));
					}
				}
			}
		}

		// CONTROLLO MANGIATE
		// SE SO CHE CI SONO MANGIATE, FILTRO LA LISTA TENENDO SOLO LE PEDINE
		// CHE POSSONO MANGIARE
		// SONO OBBLIGATO A MANGIARE
		if (isThereEat) {
			List<Posizione> newret = new ArrayList<Posizione>();
			for (Posizione p : l) {
				if (this.canEat(p)) {
					newret.add(p);
				}
			}
			l = newret;
		}
		return l;
	}

	//metodo che effettua la Mossa indicata
	//sposta la pedina e si occupa di promuovere a damone in caso si verifichino le condizioni necessarie
	public void muovi(Mossa m) {
		this.griglia[m.getArrivo().getX()][m.getArrivo().getY()] = this
				.getByPosizione(m.getPartenza());
		this.griglia[m.getPartenza().getX()][m.getPartenza().getY()] = null;
		this.rimuoviPedinaByPosizione(m.getMangiata());
		// damone
		if (this.getByPosizione(m.getArrivo()).puoDamone(m.getArrivo())) {
			this.getByPosizione(m.getArrivo()).promuovi();
		}
	}

	public String toString() {
		StringBuilder ret = new StringBuilder("");
		for (int y = 7; y >= 0; y--) {
			for (int x = 0; x < 8; x++) {
				ret.append(this.griglia[x][y] == null ? "#"
						: this.griglia[x][y].toString());
			}
			ret.append("\n");
		}

		return ret.toString();
	}

	//toglie la Pedina dalla scacchiera nella Posizione indicata 
	public void rimuoviPedinaByPosizione(Posizione p) {
		if (p != null) {
			this.griglia[p.getX()][p.getY()] = null;
		}
	}

	//ritorna l'array contenente le Posizione di tutte le Pedina della squadra indicata
	public Posizione[] getTeamPieces(boolean isWhite) {
		ArrayList<Posizione> pezzi = new ArrayList<Posizione>();
		for (int y = 0; y < 8; y++) {
			for (int x = 0; x < 8; x++) {
				Pedina ped = this.getByIndici(x, y);
				if (ped != null && ped.isWhite == isWhite) {
					pezzi.add(new Posizione(x, y));
				}
			}
		}
		Posizione[] ret = new Posizione[pezzi.size()];
		return pezzi.toArray(ret);
	}

	//ritorna true se è possibile per il mangiante indicato mangiare il mangiato indicato
	public boolean canEat(Posizione mangiante, Posizione mangiato) {
		List<Mossa> mosse = this.getAllowedMoves(this.getByPosizione(mangiante)
				.getMossePossibili(mangiante));
		for (Mossa m : mosse) {
			if (mangiato.equals(m.getMangiata())) {
				return true;
			}
		}
		return false;
	}

	// data una posizione contentente una pedina, ritorna tutte le posizioni
	// contententi pedine avversarie
	// che tale pedina potrebbe mangiare in una mangiata multipla
	// uso Set per evitare le ripetizioni
	public Set<Posizione> whoCanIEatInOneTurn(Posizione start) {
		// se sono in una situazione dove non posso più mangiare mi fermo
		if (!this.canEat(start)) {
			return null;
		}
		// se invece posso, vado ad esplorare tutte le possibilità
		// siccome canEat è true, saranno solo mangiate
		List<Mossa> mosse = this.getAllowedMoves(this.getByPosizione(start)
				.getMossePossibili(start));
		Set<Posizione> mangiabili = new HashSet<Posizione>();
		// per tutte le mosse che posso fare, mi faccio dare tutte le pedine che
		// possono essere raggiunte
		// continuando a mangiare
		for (Mossa m : mosse) {
			mangiabili.add(m.getMangiata());
			Scacchiera temp = new Scacchiera(this);
			temp.muovi(m);
			Set<Posizione> recCall = temp.whoCanIEatInOneTurn(m.getArrivo());
			if (recCall != null) {
				mangiabili.addAll(recCall);
			}
		}
		return mangiabili;
	}

	// ritorna la lunghezza della più lunga mangiata multipla che la squadra indicata può fare
	public int getLongestMangiata(boolean isWhite) {
		Posizione[] pezzi = this.getTeamPieces(isWhite);
		int max = 0;
		// per ogni pezzo della squadra prendo la mangiata più lunga che può
		// fare e la confronto con il massimo attuale
		for (Posizione pezzo : pezzi) {
			max = Math.max(max, this.getLongestMangiata(pezzo));
		}
		return max;
	}

	// ritorna la lunghezza della più lunga mangiata multipla che la Pedina alla Posizione indicata può fare
	private int getLongestMangiata(Posizione pos) {
		// so già che il pezzo esiste (chiamata da getLongestMangiata(boolean)
		Pedina ped = this.getByPosizione(pos);
		if (!this.canEat(pos)) {
			return 0;
		}
		// se posso fare almeno una mangiata
		List<Mossa> mosse = this.getAllowedMoves(this.getByPosizione(pos)
				.getMossePossibili(pos));
		// so che una la faccio
		int longest = 0;
		for (Mossa m : mosse) {
			Scacchiera temp = new Scacchiera(this);
			temp.muovi(m);
			longest = Math.max(longest,
					temp.getLongestMangiata(m.getArrivo()) + 1);
		}
		return longest;
	}
	
	//ritorna vero se la pedina ha almeno due caselle diametralmente opposte vuote 
	//(ossia non è coperta da almeno un lato
	private boolean isPedinaMangiabile(Posizione pedina){
		//se sta sui bordi, non è mangiabile
		if(pedina.getX()<=0 || pedina.getX()>=7 || pedina.getY()<=0 || pedina.getY()>=7){
			return false;
		}
		//se è vuoto in basso a sinistra e in alto a destra è mangiabile
		if(this.getByIndici(pedina.getX()-1, pedina.getY()-1)==null && 
				this.getByIndici(pedina.getX()+1, pedina.getY()+1)==null){
			return true;
		}
		//se è vuoto in alto a sinistra e in basso a destra è mangiabile
		if(this.getByIndici(pedina.getX()+1, pedina.getY()-1)==null && 
				this.getByIndici(pedina.getX()-1, pedina.getY()+1)==null){
			return true;
		}
		return false;
	}
	
	//ritorna la Posizione della pedina avversaria mangiabile (isPedinaMangiabile()) più vicina a me
	public Posizione getClosestEatableEnemy(Posizione from){
		Pedina me=this.getByPosizione(from);
		if(me!=null){
			boolean enemy=!me.isWhite;
			Posizione[] nemici=this.getTeamPieces(enemy);
			int min=Integer.MAX_VALUE;
			Posizione toRet=null;
			//per tutti gli avversari
			for(Posizione nemico:nemici){
				//se ne trovo uno più vicino del più vicino finore e che sia mangiabile
				if(Posizione.getDistanzaInMosse(from, nemico)<min && this.isPedinaMangiabile(nemico)){
					//lo salvo
					min=Posizione.getDistanzaInMosse(from, nemico);
					toRet=nemico;
				}
			}
			return toRet;
		}
		return null;
	}
	
	
	//come getClosestEatableEnemy ma posso filtrare per isDamone
	public Posizione getClosestEatableDamone(Posizione from, boolean isDamone){
		Pedina me=this.getByPosizione(from);
		if(me!=null){
			boolean enemy=!me.isWhite;
			Posizione[] nemici=this.getTeamPieces(enemy);
			int min=Integer.MAX_VALUE;
			Posizione toRet=null;
			//per tutti gli avversari
			for(Posizione nemico:nemici){
				//se ne trovo uno più vicino del più vicino finore e che sia mangiabile e che sia pedina/damone
				if(Posizione.getDistanzaInMosse(from, nemico)<min 
						&& this.isPedinaMangiabile(nemico)
						&& this.getByPosizione(nemico).isDamone()==isDamone){
					//lo salvo
					min=Posizione.getDistanzaInMosse(from, nemico);
					toRet=nemico;
				}
			}
			return toRet;
		}
		return null;
	}
	
	public int getHowManyMangiate(boolean isWhite){
		List<Mossa> mosse=new ArrayList<Mossa>();
		Posizione[] pedine=this.getTeamPieces(isWhite);
		System.out.println(pedine.length);
		for(Posizione pos:pedine){
			List <Mossa> temp=this.getAllowedMoves(this.getByPosizione(pos).getMossePossibili(pos));
			for(Mossa m:temp){
				if(m.mangia()){
					mosse.add(m);
				}
			}
		}
		return mosse.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(griglia);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Scacchiera))
			return false;
		Scacchiera other = (Scacchiera) obj;
		if (!Arrays.deepEquals(griglia, other.griglia))
			return false;
		return true;
	}

	//svuota la griglia di Pedina
	public void svuota() {
		this.griglia = new Pedina[8][8];
	}

}
