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
	
	//confronta quale tra due mosse è la migliore
	@Override
	public int compareTo(MossaEContromossa arg) {
		//chi mangia più pezzi e in cambio viene mangiato di meno dall'avversario è meglio
		if((this.mm.getNumeroMangiate()-this.contromangiata)!=(arg.mm.getNumeroMangiate()-arg.contromangiata)){
			return (this.mm.getNumeroMangiate()-this.contromangiata)-(arg.mm.getNumeroMangiate()-arg.contromangiata);
		}
		//in caso di pareggio, chi mangia più damoni è meglio
		if(this.mm.getDamoniMangiati()!=arg.mm.getDamoniMangiati()){
			return this.mm.getDamoniMangiati()-arg.mm.getDamoniMangiati();
		}
		return 0;
	}
}
