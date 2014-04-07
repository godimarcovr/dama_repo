package rules;

import java.util.Arrays;

/*
 * Un vettore di due dimensioni usato per indicare una posizione sulla scacchiera tramite le due coordinate
 * Nonostante il nome viene usato anche per altro (es. movimenti)
 * Contiene metodi vari per operazioni fra posizioni e controllo di validità
 */
public class Posizione {
	private int x,y;
	
	public Posizione(int x, int y){
		this.x=x;
		this.y=y;
	}
	
	public void incrementaXY(int dx, int dy){
		this.x+=dx;
		this.y+=dy;
	}
	
	public int getX(){
		return this.x;
	}
	
	public int getY(){
		return this.y;
	}
	
	public Posizione somma(Posizione other){
		return new Posizione(this.getX()+other.getX(),this.getY()+other.getY());
	}
	
	public void soloSegno(){
		this.x=this.x!=0?this.x/Math.abs(this.x):0;
		this.y=this.y!=0?this.y/Math.abs(this.y):0;
	}
	
	//usato per la sottrazione
	public Posizione opposto(){
		return new Posizione(-this.getX(),-this.getY());
	}
	
	public Posizione sottrazione(Posizione other){
		return this.somma(other.opposto());
	}

	@Override
	public int hashCode() {
		return this.x*10+y;
	}
	
	public boolean sameDir(Posizione other){
		return !(this.x*other.x<0 || this.y*other.y<0);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Posizione))
			return false;
		Posizione other = (Posizione) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}
	
	public boolean isAdiacenteTo(Posizione other){
		return Math.abs(this.x-other.x)==1 && Math.abs(this.y-other.y)==1;
	}
	
	public static Posizione[] getDistanti1Permessi(Posizione start){
		Posizione posizioni[]=new Posizione[4];
		int c=0;
		if(Scacchiera.isAllowedCell(start.x-1, start.y-1)){
			posizioni[c++]=new Posizione(start.x-1,start.y-1);
		}
		if(Scacchiera.isAllowedCell(start.x+1, start.y-1)){
			posizioni[c++]=new Posizione(start.x+1,start.y-1);
		}
		if(Scacchiera.isAllowedCell(start.x-1, start.y+1)){
			posizioni[c++]=new Posizione(start.x-1,start.y+1);
		}
		if(Scacchiera.isAllowedCell(start.x+1, start.y+1)){
			posizioni[c++]=new Posizione(start.x+1,start.y+1);
		}
		return Arrays.copyOfRange(posizioni, 0, c);
			
	}
	
	public static int getDistanzaInMosse(Posizione from, Posizione to){
		return Math.max(Math.abs(from.x-to.x), Math.abs(from.y-to.y));
	}

	@Override
	public String toString() {
		return "Posizione [x=" + x + ", y=" + y + "]";
	}
	
	
	
	
}
