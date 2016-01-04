package optimization;

public class Spieler{
	private String name;
	private String pos;
	private int tp;
	private int frische;
	
	public Spieler(String name, int tp, int frische){
		this.tp = tp;
		this.frische = frische;
		this.name = name;
	}
	
	public int getTpGewinn(String einheit){
		if(einheit.contains("e1"))
			return 1;
		else if(einheit.contains("e2"))
			return 2;
		else
			return 0;
			
	}
	
	public int frischeVerlust(String einheit){
		if(einheit.contains("e1"))
			return 3;
		else if(einheit.contains("e2"))
			return 2;
		else // Erholungstraining
			return -5;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}

	public int getFrische() {
		return frische;
	}

	public void setFrische(int frische) {
		this.frische = frische;
	}
	
	
}
