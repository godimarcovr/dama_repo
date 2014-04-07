package ia;

import rules.Mossa;

/*
 * Rappresenta le informazioni su una mossa
 * Usata per cercare la migliore
 */

public class DescrittoreMossa implements Comparable<DescrittoreMossa>{
	//la maggiore profondit� della mangiata avversaria dopo la mia mossa
	private int enemydepth;
	//la maggiore profondit� possibile al turno successivo (meno importante, generalmente non avviene)
	private int mydepth;
	//il numero di mangiate possibili al turno successivo
	private int howManyEats;
	//chi si muove � damone o pedina?
	private boolean isDamone;
	//mossa in questione
	private Mossa m;
	//squadra di chi si muove
	private Boolean isWhite=null;
	
	public DescrittoreMossa(int enemy,int my,int howMany, Mossa m){
		this.enemydepth=enemy;
		this.mydepth=my;
		this.howManyEats=howMany;
		this.isDamone=false;
		this.m=m;
	}
	
	public int getEnemydepth() {
		return enemydepth;
	}

	public int getMydepth() {
		return mydepth;
	}

	public void setSquadra(boolean isWhite){
		if(!this.isDamone){
			this.isWhite=isWhite;
		}
	}
	
	public boolean isWhite(){
		return this.isWhite;
	}
	
	public Mossa getM() {
		return m;
	}

	public void setM(Mossa m) {
		this.m = m;
	}

	public void setDamone(boolean isDamone) {
		this.isDamone = isDamone;
	}
	
	public boolean isDamone() {
		return isDamone;
	}

	//confronta due mosse
	@Override
	public int compareTo(DescrittoreMossa o) {
		//la mossa migliore � quella che fa mangiare meno all'avversario (TATTICA CONSERVATIVA)
		if(this.enemydepth!=o.getEnemydepth()){
			return -(this.enemydepth-o.getEnemydepth());
		}
		//a parit� di mangiata avversaria, la migliore � quella che mi d� pi� possibilit� di mangiare
		if(this.howManyEats!=o.howManyEats){
			return this.howManyEats-o.howManyEats;
		}
		//a parit� di possibilit�, scelgo quella che mi fa mangiare di pi�
		if(this.mydepth!=o.getMydepth()){
			return this.mydepth-o.getMydepth();
		}
		int my_y_dest=this.m.getArrivo().getY();
		int o_y_dest=o.m.getArrivo().getY();
		//a parit� di mossa, meglio quella che v� pi� vicina a fare damone
		if(this.isWhite!=null && o.isWhite!=null && my_y_dest!=o_y_dest){
			int myVicinanzaADamone=this.isWhite?7-my_y_dest:my_y_dest-0;
			int oVicinanzaADamone=o.isWhite?7-o_y_dest:o_y_dest-0;
			return -(myVicinanzaADamone-oVicinanzaADamone);
		}
		return 0;
	}

	@Override
	public String toString() {
		return "DescrittoreMossa [enemydepth=" + enemydepth + ", mydepth="
				+ mydepth + ", howManyEats=" + howManyEats + ", isDamone="
				+ isDamone + ", m=" + m + ", isWhite=" + isWhite + "]\n";
	}

	
	
}
