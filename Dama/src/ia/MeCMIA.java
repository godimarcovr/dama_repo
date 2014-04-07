package ia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rules.Mossa;
import rules.Partita;
import rules.Posizione;

public class MeCMIA {
	private static Partita p;
	
	//setta la partita da considerare
	public static void initialize(Partita partita){
		p=partita;
	}
	
	//ritorna la mossa migliore secondo l'IA
	public static MossaMultipla getMosseIA(){
		return getMiglioreMossa(false);
	}
	
	private static MossaMultipla getMiglioreMossa(boolean isWhite){
		//Se è il turno dell'altro giocatore non posso muovere
		if(p.getTurno()!=isWhite){
			return null;
		}
		//Se non ci sono mosse possibili (sono bloccato) (Non dovrebbe succedere perchè la partita 
		//finirebbe prima)
		if(p.isPlayerUnableToMove(isWhite)){
			//ritorno null
			return null;
		}
		else{
			MossaMultipla ret;
			//controllo se sono in situazione di dover mangiare o meno
			System.out.println("--------------------------\nScelta mossa...");
			if(p.isMangiata()){
				System.out.println("Ricerca miglior mangiata");
				//se devo mangiare, scelgo la migliore mangiata possibile
				ret=findBestMangiata(new Partita(p),isWhite);
			}
			else{
				System.out.println("Ricerca miglior non-mangiata");
				//se non posso mangiare, cerco la mossa migliore
				ret=findBestNonMangiata(new Partita(p), isWhite);
			}
			
			System.out.println("Scelta finale: "+ret);
			return ret;
		}
	}
	
	private static MossaMultipla findBestNonMangiata(Partita partita,boolean isWhite){
		//se la mangiata avversaria più profonda è >0 cerco di minimizzarla usando una qualsiasi pedina
		//altrimenti muovo o un damone verso la pedina avversaria mangiabile più vicina
		//o una pedina senza aumentare la mangiata più profonda
		
		
		//per tutte le mosse che posso fare, mi calcolo la profondità di mangiata avversaria nel caso
		//peggiore e la mia possibile profondità di mangiata al turno successivo e quante mangiate
		//possibili al turno successivo
		boolean enemy=!isWhite;
		int deepestEat=partita.getScac().getLongestMangiata(enemy);
		System.out.println("DeepestEat: "+deepestEat);
		List<Mossa> mosse=partita.getPossibleMovesByPlayer(isWhite);
		//******************
		List<DescrittoreMossa> desc=new ArrayList<DescrittoreMossa>();
		Mossa toRet=null;
		for(Mossa m:mosse){
			Partita temp=new Partita(partita);
			temp.muovi(m);
			int maxdepth=temp.getScac().getLongestMangiata(enemy);
			int mydepth=temp.getScac().getLongestMangiata(isWhite);
			int howMany=temp.getScac().getHowManyMangiate(isWhite);
			
			DescrittoreMossa dm=new DescrittoreMossa(maxdepth,mydepth,howMany,m);
			dm.setDamone(partita.getByPosizione(m.getPartenza()).isDamone());
			dm.setSquadra(isWhite);
			desc.add(dm);
			System.out.println("Mossa: "+m);
			System.out.println("HM: "+howMany);
			System.out.println("-->enemydepth: "+maxdepth);
			System.out.println("--> mydepth: "+mydepth+"\n");
			System.out.println();
		}
		//ordino e inverto l'ordine
		//voglio la migliore per prima
		Collections.sort(desc);	
		Collections.reverse(desc);
		System.out.println(desc);
		
		//in caso muovere un damone sia la mia scelta migliore, cerco la mossa migliore fra quelle
		//prendendo la mossa fra le migliori e cercando quella che più mi avvicina alla pedina
		//più vicina fra quelle che posso mangiare
		List<DescrittoreMossa> primidamoni=new ArrayList<DescrittoreMossa>();
		boolean keepGoing=true;
		int minimumEnemyDepth=desc.get(0).getEnemydepth();
		for(int i=0;i<desc.size() && keepGoing;i++){
			if(desc.get(i).getEnemydepth()<=minimumEnemyDepth){
				if(desc.get(i).isDamone()){
					primidamoni.add(desc.get(i));
				}
			}
			else{
				keepGoing=false;
			}
		}
		for(DescrittoreMossa damoneDM:primidamoni){
			Posizione vittima=partita.getScac()
					.getClosestEatableDamone(damoneDM.getM().getPartenza(),false);
			if(vittima==null){
				vittima=partita.getScac()
						.getClosestEatableDamone(damoneDM.getM().getPartenza(),true);
			}
			if(vittima!=null){
				if(Posizione.getDistanzaInMosse(damoneDM.getM().getPartenza(),vittima)>
				Posizione.getDistanzaInMosse(damoneDM.getM().getArrivo(), vittima)){
					
					System.out.println("Scelta per inseguimento su +"+vittima+ "!! "
							+Posizione.getDistanzaInMosse(damoneDM.getM().getPartenza(),vittima)
							+" VS "+Posizione.getDistanzaInMosse(damoneDM.getM().getArrivo(), vittima));
					MossaMultipla mm=new MossaMultipla(partita);
					mm.addMossa(damoneDM.getM());
					return mm;
				}
			}
		}
		//se non trovo mosse con il damone che mi avvicinano
		//scelgo quella che avevo calcolato come la migliore
		MossaMultipla mm=new MossaMultipla(partita);
		mm.addMossa(desc.get(0).getM());
		return mm;
	}
	
	private static MossaMultipla findBestMangiata(Partita partita,boolean isWhite){
		List<Posizione> squadra=partita.getScac().whoCanMove(isWhite);
		System.out.println("Scelta fra:"+squadra);
		List<MossaMultipla> possibiliMosse=new ArrayList<MossaMultipla>();
		//per ogni pezzo che posso muovere (e far mangiare)
		for(Posizione pezzo:squadra){
			//mi segno tutte le possibili mangiate che può fare
			possibiliMosse.addAll(visit(new Partita(partita),pezzo));
		}
		//per ogni mossa multipla che posso fare, mi segno la più grande contromossa avversaria
		List<MossaEContromossa> mosseConContromosse=new ArrayList<MossaEContromossa>();
		for(MossaMultipla mm:possibiliMosse){
			mosseConContromosse.add(calcolaContromossaPeggiore(new Partita(partita), mm, isWhite));
		}
		return Collections.max(mosseConContromosse).getMossaMultipla();
	}


	private static List<MossaMultipla> visit(Partita partita, Posizione pezzo) {
		List<MossaMultipla> ret=new ArrayList<MossaMultipla>();
		//se posso mangiare
		if(partita.getScac().canEat(pezzo)){
			//esploro tutte le possibilità
			List<Mossa> mosse=partita.getPossibleMovesByPosizione(pezzo);
			for(Mossa m:mosse){
				Partita temp=new Partita(partita);
				temp.muovi(m);
				for(MossaMultipla mm:visit(temp,m.getArrivo())){
					//lo creo con partita, perchè ci devo aggiungere la mossa m
					MossaMultipla toAdd=new MossaMultipla(partita);
					toAdd.addMossa(m);
					toAdd.addMosse(mm.getMosse());
								ret.add(toAdd);
				}
}
		}
		//altrimenti ritorno una mossa vuota
		else{
			ret.add(new MossaMultipla(partita));
		}
		return ret;
	}
	
	private static MossaEContromossa calcolaContromossaPeggiore(Partita partita,MossaMultipla mm, boolean isWhite){
		//faccio tutta la mossa
		for(Mossa m:mm.getMosse()){
			partita.muovi(m);
		}
		return new MossaEContromossa(mm, partita.getScac().getLongestMangiata(!isWhite));
	}
}
