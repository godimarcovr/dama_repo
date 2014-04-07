package ui;

import java.util.Scanner;
import rules.*;

/*
 * main usato per far partire dama in modalità testuale
 * Usata inizialmente per il testing
 * 
 */

public class TextUI {

	public static Scanner s=new Scanner(System.in);
	
	public static void main(String[] args) {
		Partita p=new Partita(true);
		while(true){
			System.out.println(p.getScac().toString());
			p.getScac().muovi(getMossa());
		}
		
	}
	
	private static Mossa getMossa(){
		
		int a,b;
		System.out.println("Partenza");
		do{
			a=s.nextInt();
		} while (a<0 || a>7);
		do{
			b=s.nextInt();
		} while (b<0 || b>7);
		Posizione start=new Posizione(a,b);
		
		System.out.println("Arrivo");
		do{
			a=s.nextInt();
		} while (a<0 || a>7);
		do{
			b=s.nextInt();
		} while (b<0 || b>7);
		Posizione arrivo=new Posizione(a,b);
		
		System.out.println("Mangiata?");
		Posizione gnam;
		a=s.nextInt();
		if(a!=-1){
			while (a<0 || a>7){
				a=s.nextInt();
			}
			do{
				b=s.nextInt();
			} while (b<0 || b>7);
			gnam=new Posizione(a,b);
		}
		else{
			gnam=null;
		}
		
		return new Mossa(start, arrivo, gnam);
	}

}
