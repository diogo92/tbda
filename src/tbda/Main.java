package tbda;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;

import tbda.model.Agenda;
import tbda.model.Consulta;
import tbda.model.Doente;
import tbda.model.Medico;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;

public class Main {

	private MongoClient client;
	private String user;
	private String database;
	private String password;
	private MongoClientURI uri;
	static MyMap agendaMedico;
	static boolean[] reportAnginaEnfarte;

	public Main() {

		agendaMedico = new MyMap();
		reportAnginaEnfarte = new boolean[10];

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
				doente.insertOne(new Document("codd", doenteValue.getCodd())
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
				
				BasicDBObject query = new BasicDBObject("codm", agendaValue.getCodm());
				
				BasicDBObject codm = null;
				
				for (Document doc : medico.find(query)) {
					if((int)doc.get("codm") == agendaValue.getCodm()){
						codm = new BasicDBObject("$ref","medico").append("$id", doc.get("_id")).append("$db", "clinica");
						break;
					}
				}
				
				agenda.insertOne(new Document("nagenda", agendaValue
						.getNagenda()).append("dia", agendaValue.getDia())
						.append("codm",codm)
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
				Boolean found = false;
				BasicDBObject query = new BasicDBObject("codm", consultaValue.getCodd());
				BasicDBObject query2 = new BasicDBObject("nagenda",consultaValue.getNagenda());
				BasicDBObject codd = null;
				BasicDBObject nagenda = null;
				
				for (Document doc : medico.find(query)) {
					if((int)doc.get("codm") == consultaValue.getCodd()){
						found = true;
						codd = new BasicDBObject("$ref","medico").append("$id", doc.get("_id")).append("$db", "clinica");
						break;
					}
				}
				
				if(!found){
					query = new BasicDBObject("codd",consultaValue.getCodd());
					for (Document doc: doente.find(query)){
						if((int)doc.get("codd") == consultaValue.getCodd()){
							found = true;
							codd = new BasicDBObject("$ref","doente").append("$id", doc.get("_id")).append("$db", "clinica");
							break;
						}
					}
				}
				
				for (Document doc : agenda.find(query2)) {
					if((int)doc.get("nagenda") == consultaValue.getNagenda()){
						nagenda = new BasicDBObject("$ref","agenda").append("$id", doc.get("_id")).append("$db", "clinica");
						break;
					}
				}
				
				consulta.insertOne(new Document("nagenda", nagenda)
						.append("hora", consultaValue.getHora())
						.append("preço", consultaValue.getPreço())
						.append("situação", consultaValue.getSituação())
						.append("relatório", consultaValue.getRelatório())
						.append("codd", codd));
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
		
		//Mostrar o nome e data de nascimento dos oftalmologistas
		BasicDBObject queryOftalmologia = new BasicDBObject("especialidade", "Oftalmologia");
		FindIterable<Document> iterableMedico = clinica.getCollection("medico").find(queryOftalmologia);
		
		System.out.println("OFTALMOLOGISTAS:");
		for (Document document : iterableMedico) {
			System.out.print(document.get("nome"));
	        System.out.print(" ");
	        System.out.println(document.get("data_nasce"));
		}
		
		System.out.println();

		//Relatório da atividade clínica
		FindIterable<Document> iterableAgenda = clinica.getCollection("agenda").find();
		System.out.println("RELATORIO DE ATIVIDADE MEDICA:");
		for (Document document : clinica.getCollection("medico").find()) {
			System.out.println(document.get("codm") + " " + document.get("nome"));
			BasicDBObject ref = new BasicDBObject("$ref","medico").append("$id", document.get("_id")).append("$db", "clinica");
			BasicDBObject query = new BasicDBObject("codm",ref);
			for (Document agendaEntry : clinica.getCollection("agenda").find(query)) {
				System.out.println("\t"+ "Dia: " + agendaEntry.get("dia"));
				BasicDBObject ref2 =  new BasicDBObject("$ref","agenda").append("$id", agendaEntry.get("_id")).append("$db", "clinica");
				BasicDBObject query2 = new BasicDBObject("nagenda",ref2);
				for(Document consultaEntry : clinica.getCollection("consulta").find(query2)){
					DBRef ref3 = (DBRef) consultaEntry.get("codd");
					BasicDBObject ref4 = new BasicDBObject("_id", ref3.getId());
					for (Document doenteEntry : clinica.getCollection(ref3.getCollectionName()).find(ref4)) {
						System.out.println("\t\t" + "Horas: " + consultaEntry.get("hora") + " ; Doente: " + doenteEntry.get("nome") + " ; Relatório: " + consultaEntry.get("relatório"));
					}
				}
			}
		}
		
		//Médicos que tenham relatório como Enfarte ou Angina
		System.out.println("MÉDICOS COM PELO MENOS UM RELATÓRIO DE ANGINA OU ENFARTE");
		for(int i = 0; i < reportAnginaEnfarte.length; i++){
			if(!reportAnginaEnfarte[i])
				continue;
			BasicDBObject queryFinal = new BasicDBObject("codm", i);
			FindIterable<Document> iterableMedico2 = clinica.getCollection("medico").find(queryFinal);
			iterableMedico2.forEach(new Block<Document>() {
			    @Override
			    public void apply(final Document document) {
			        System.out.print(document.get("nome"));
			        System.out.print(" ");
			        System.out.println(document.get("especialidade"));
			    }
			});
		}
		
		main.client.close();
	}
}
