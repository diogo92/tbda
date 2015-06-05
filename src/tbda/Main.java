package tbda;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.bson.Document;

import tbda.model.Agenda;
import tbda.model.Consulta;
import tbda.model.Doente;
import tbda.model.Medico;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

public class Main {

	private MongoClient client;
	private String user;
	private String database;
	private String password;
	private MongoClientURI uri;

	public Main() {

		user = "tbdE";
		database = "dbE";
		password = "voumudarapassword";
		String uriString = "mongodb://tbdE:grupoE@maltese.fe.up.pt:27017/dbE";

	}

	private void connect() {
		// uri = new MongoClientURI(uriString);
		uri = new MongoClientURI("mongodb://localhost:27017");
		client = new MongoClient(uri);
	}

	public static void main(String[] args) {

		Main main = new Main();
		main.connect();

		MongoDatabase clinica = main.client.getDatabase("clinica");
		clinica.drop();

		MongoCollection<Document> medico = clinica.getCollection("medico");
		MongoCollection<Document> doente = clinica.getCollection("doente");
		MongoCollection<Document> agenda = clinica.getCollection("agenda");
		MongoCollection<Document> consulta = clinica.getCollection("consulta");

		Gson gson = new Gson();
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"data/medico.json"));
			Medico[] medicoData = gson.fromJson(br, Medico[].class);
			for (Medico medicoValue : medicoData) {
				medico.insertOne(new Document("codm", medicoValue.getCodm())
						.append("nome", medicoValue.getNome())
						.append("NIF", medicoValue.getNIF())
						.append("morada", medicoValue.getMorada())
						.append("cod_postal", medicoValue.getCod_postal())
						.append("telefone", medicoValue.getTelefone())
						.append("data_nasce", medicoValue.getData_nasce())
						.append("especialidade", medicoValue.getEspecialidade()));
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"data/doente.json"));
			Doente[] doenteData = gson.fromJson(br, Doente[].class);
			for (Doente doenteValue : doenteData) {
				doente.insertOne(new Document("codm", doenteValue.getCodd())
						.append("nome", doenteValue.getNome())
						.append("NIF", doenteValue.getNIF())
						.append("morada", doenteValue.getMorada())
						.append("cod_postal", doenteValue.getCod_postal())
						.append("telefone", doenteValue.getTelefone())
						.append("data_nasce", doenteValue.getData_nasce())
						.append("profissão", doenteValue.getProfissão()));
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"data/agenda.json"));
			Agenda[] agendaData = gson.fromJson(br, Agenda[].class);
			for (Agenda agendaValue : agendaData) {
				agenda.insertOne(new Document("nagenda", agendaValue
						.getNagenda()).append("dia", agendaValue.getDia())
						.append("codm", agendaValue.getCodm())
						.append("hora_inicio", agendaValue.getHora_inicio())
						.append("no_doentes", agendaValue.getNo_doentes()));
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					"data/consulta.json"));
			Consulta[] consultaData = gson.fromJson(br, Consulta[].class);
			for (Consulta consultaValue : consultaData) {
				consulta.insertOne(new Document("nagenda", consultaValue.getNagenda())
						.append("hora", consultaValue.getHora())
						.append("preço", consultaValue.getPreço())
						.append("situação", consultaValue.getSituação())
						.append("relatório", consultaValue.getRelatório())
						.append("codd", consultaValue.getCodd()));
				Document modifier = new Document("no_doentes",1);
				Document increment = new Document("$inc",modifier);
				Document search = new Document("nagenda",consultaValue.getNagenda());
				UpdateResult update = agenda.updateOne(search,increment);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		main.client.close();
	}

}
