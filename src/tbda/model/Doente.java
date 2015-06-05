package tbda.model;

public class Doente {
	
	int codd;
	String nome;
	String NIF;
	String morada;
	String cod_postal;
	String telefone;
	String data_nasce;
	String profissão;
	
	public int getCodd() {
		return codd;
	}
	public void setCodd(int codd) {
		this.codd = codd;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNIF() {
		return NIF;
	}
	public void setNIF(String nIF) {
		NIF = nIF;
	}
	public String getMorada() {
		return morada;
	}
	public void setMorada(String morada) {
		this.morada = morada;
	}
	public String getCod_postal() {
		return cod_postal;
	}
	public void setCod_postal(String cod_postal) {
		this.cod_postal = cod_postal;
	}
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	public String getData_nasce() {
		return data_nasce;
	}
	public void setData_nasce(String data_nasce) {
		this.data_nasce = data_nasce;
	}
	public String getProfissão() {
		return profissão;
	}
	public void setProfissão(String profissão) {
		this.profissão = profissão;
	}

}
