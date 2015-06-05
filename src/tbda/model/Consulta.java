package tbda.model;

public class Consulta {
	
	int nagenda;
	int hora;
	int preço;
	int situação;
	String relatório;
	int codd;
	
	public int getNagenda() {
		return nagenda;
	}
	public void setNagenda(int nagenda) {
		this.nagenda = nagenda;
	}
	public int getHora() {
		return hora;
	}
	public void setHora(int hora) {
		this.hora = hora;
	}
	public int getPreço() {
		return preço;
	}
	public void setPreço(int preço) {
		this.preço = preço;
	}
	public int getSituação() {
		return situação;
	}
	public void setSituação(int situação) {
		this.situação = situação;
	}
	public String getRelatório() {
		return relatório;
	}
	public void setRelatório(String relatório) {
		this.relatório = relatório;
	}
	public int getCodd() {
		return codd;
	}
	public void setCodd(int codd) {
		this.codd = codd;
	}
	
}
