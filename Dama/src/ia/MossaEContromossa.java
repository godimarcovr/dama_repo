package ia;

/*
 * Rappresenta una mossa multipla tenendo in considerazione la mangiata dell'aversario nel caso peggiore
 */

public class MossaEContromossa implements Comparable<MossaEContromossa>{
	MossaMultipla mm;
	int contromangiata;
	
	public MossaEContromossa(MossaMultipla mm,int contromangiata){
		this.mm=mm;
		this.contromangiata=contromangiata;
	}

	public MossaMultipla getMossaMultipla(){
		return this.mm;
	}
	
	public int getContromangiata(){
		return this.contromangiata;
	}
	
	//confronta quale tra due mosse � la migliore
	@Override
	public int compareTo(MossaEContromossa arg) {
		//chi mangia pi� pezzi e in cambio viene mangiato di meno dall'avversario � meglio
		if((this.mm.getNumeroMangiate()-this.contromangiata)!=(arg.mm.getNumeroMangiate()-arg.contromangiata)){
			return (this.mm.getNumeroMangiate()-this.contromangiata)-(arg.mm.getNumeroMangiate()-arg.contromangiata);
		}
		//in caso di pareggio, chi mangia pi� damoni � meglio
		if(this.mm.getDamoniMangiati()!=arg.mm.getDamoniMangiati()){
			return this.mm.getDamoniMangiati()-arg.mm.getDamoniMangiati();
		}
		return 0;
	}
}
