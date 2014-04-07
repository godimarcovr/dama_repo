package rules;

import java.util.ArrayList;
import java.util.List;

/*
 * Mantiene lo stato della Partita interamente: le posizioni delle pedine usando Scacchiera, il turno, le pedine
 * rimaste, e gestisce la mangiata multipla
 * 
 */

public class Partita {
	private boolean turno;//true se inizia il giocatore
	private int rimasteGiocatore, rimasteCPU;
	private Scacchiera scac;
	private List<Mossa> mosse_possibili=null;
	
	public Partita(boolean turno) {
		this.turno = turno;
		this.rimasteGiocatore=12;
		this.rimasteCPU=12;
		this.scac=new Scacchiera();
	}

	//costruttore per la deep copy
	public Partita(Partita part){
		this.turno=part.getTurno();
		this.scac=new Scacchiera(part.getScac());
		this.rimasteGiocatore=part.rimasteGiocatore;
		this.rimasteCPU=part.rimasteCPU;
		if(part.mosse_possibili==null){
			this.mosse_possibili=null;
		}
		else{
			this.mosse_possibili=new ArrayList<Mossa>(part.mosse_possibili);
		}
	}
	
	//riporta la Partita allo stato di partenza
	public void reset(boolean turno){
		this.turno = turno;
		this.rimasteGiocatore=12;
		this.rimasteCPU=12;
		this.scac=new Scacchiera();
	}
	//toglie tutte le pedine dalla partita
	public void svuota(boolean turno){
		this.turno = turno;
		this.rimasteGiocatore=0;
		this.rimasteCPU=0;
		this.scac.svuota();
	}
	
	public boolean getTurno() {
		return turno;
	}
	

	public int getRimasteGiocatore() {
		return rimasteGiocatore;
	}
	
	public void addRimasteGiocatore(int mod){
		this.rimasteGiocatore+=mod;
	}

	public int getRimasteCPU() {
		return rimasteCPU;
	}
	
	public void addRimasteCPU(int mod){
		this.rimasteCPU+=mod;
	}

	public Scacchiera getScac() {
		return scac;
	}
	
	//effettua la mossa, controllando per turni e mangiate multiple e aggiornando le pedine rimaste
	public void muovi(Mossa m){
		//SE è RISPETTATO IL TURNO
		if(m!=null && this.scac.getByPosizione(m.getPartenza()) !=null
				&& this.scac.getByPosizione(m.getPartenza()).isWhite==this.getTurno()){
			this.scac.muovi(m);
			this.mosse_possibili=null;
			if(m.mangia()){
				//SE POSSO MANGIARE ANCORA
				if(this.scac.canEat(m.getArrivo())){
					//PRENDO TUTTE LE MOSSE CHE POSSO FARE
					//(RICORDO CHE SE POSSO MANGIARE MI DA SOLO QUELLE)
					List<Mossa> mosse=this.getPossibleMovesByPosizione(m.getArrivo());
					//SE NON è VUOTA CONTROLLO SE LA PRIMA è UNA MANGIATA: O SONO TUTTE MANGIATE O NON LO è NESSUNA
						this.mosse_possibili=mosse;
				}
				else{
					this.switchTurno();
				}
				if(this.scac.getByPosizione(m.getArrivo()).isWhite){
					this.rimasteCPU--;
				}
				else{
					this.rimasteGiocatore--;
				}
			}
			else{
				this.switchTurno();
			}
			
		}
	}
	
	//ritorna true se la squadra indicata ha vinto
	public boolean isVittoriaDi(boolean isWhite){
		return (isWhite?this.rimasteCPU==0:this.rimasteGiocatore==0) || this.isPlayerUnableToMove(!isWhite);
	}
	
	//ritorna tutte le possibili mosse per la Pedina alla Posizione indicata
	//tiene conto di turni e mangiate multiple
	public List<Mossa> getPossibleMovesByPosizione(Posizione p){
		if(this.scac.getByPosizione(p)!=null && this.scac.getByPosizione(p).isWhite!=this.turno){
			return new ArrayList<Mossa>();
		}
		if(this.mosse_possibili!=null && !this.mosse_possibili.isEmpty()){
			return this.mosse_possibili;
		}
		List<Mossa> ret=null;
		if(this.scac.getByPosizione(p)==null){
			return new ArrayList<Mossa>();
		}
		else{
			return this.scac.getAllowedMoves(this.scac.getByPosizione(p).getMossePossibili(p));
		}
	}
	
	//ritorna la lista di tutte le mosse possibili per il giocatore indicato
	public List<Mossa> getPossibleMovesByPlayer(boolean isWhite){
		List<Mossa> ret=new ArrayList<Mossa>();
		Posizione[] pedine=this.scac.getTeamPieces(isWhite);
		for(Posizione pos:pedine){
			ret.addAll(this.getPossibleMovesByPosizione(pos));
		}
		return ret;
	}
	
	//ritorna true se il giocatore non ha mosse disponibili
	public boolean isPlayerUnableToMove(boolean isWhite){
		return this.scac.whoCanMove(isWhite).isEmpty();
	}
	
	//ritorna vero se il giocatore di cui è il turno dovrà mangiare o meno
	public boolean isMangiata(){	
		return this.scac.isMangiata(this.getTurno());
	}
	
	//ritorna true se sono all'interno di una mangiata multipla
	public boolean isMangiataMultipla(){
		//mosse_possibili è !=null SOLO SE SONO IN CASO DI MANGIATA MULTIPLA
		return this.mosse_possibili!=null;
	}
	
	//cambia il turno
	public void switchTurno(){
		this.turno=!this.turno;
	}
	//ritorna la Pedina alla Posizione indicata
	public Pedina getByPosizione(Posizione p){
		//WRAPPER PER FUNZIONE DI SCACCHIERA
		return this.scac.getByPosizione(p);
	}
	
	//ritorna true se la partita è finita
	public boolean isFinished(){
		return this.getTurno()?(this.rimasteGiocatore==0 || this.isPlayerUnableToMove(true))
				:(this.rimasteCPU==0 ||this.isPlayerUnableToMove(false));
	}
	
	//ritorna il numero di pedine che possono mangiarmi
	public int getHowManyCanEatMe(Posizione p){
		Pedina ped=this.getByPosizione(p);
		int ret=0;
		if(ped==null)
			return 0;
		boolean otherCol=!ped.isWhite;
		//se nessuno dell'altra squadra può mangiarmi
		if(!this.getScac().isMangiata(otherCol)){
			//non verrò mangiato
			return 0;
		}
		else{
			Posizione[] avversari=this.scac.getTeamPieces(otherCol);
			//per ogni avversario
			for(Posizione pos_avv:avversari){
				//se fra le pedine che può raggiungere in una mangiata multipla c'è quella richiesta
				if(this.scac.whoCanIEatInOneTurn(pos_avv).contains(p)){
					//me lo segno
					ret++;
				}
			}
		}
		return ret;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mosse_possibili == null) ? 0 : mosse_possibili.hashCode());
		result = prime * result + rimasteCPU;
		result = prime * result + rimasteGiocatore;
		result = prime * result + ((scac == null) ? 0 : scac.hashCode());
		result = prime * result + (turno ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Partita))
			return false;
		Partita other = (Partita) obj;
		if (mosse_possibili == null) {
			if (other.mosse_possibili != null)
				return false;
		} else if (!mosse_possibili.equals(other.mosse_possibili))
			return false;
		if (rimasteCPU != other.rimasteCPU)
			return false;
		if (rimasteGiocatore != other.rimasteGiocatore)
			return false;
		if (scac == null) {
			if (other.scac != null)
				return false;
		} else if (!scac.equals(other.scac))
			return false;
		if (turno != other.turno)
			return false;
		return true;
	}	
	
}
