package tbda;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.bson.Document;
import org.bson.types.ObjectId;

import tbda.model.Agenda;
import tbda.model.Consulta;
import tbda.model.Doente;
import tbda.model.Medico;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCursor;
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
				Object id = null;
				
				for (Document doc : medico.find(query)) {
					if((int)doc.get("codm") == agendaValue.getCodm()){
						id = doc.get("_id");
						break;
					}
				}
				agenda.insertOne(new Document("nagenda", agendaValue
						.getNagenda()).append("dia", agendaValue.getDia())
						.append("codm", id)
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
				Object id = null ;
				BasicDBObject query = new BasicDBObject("codm", consultaValue.getCodd());
				
				for (Document doc : medico.find(query)) {
					if((int)doc.get("codm") == consultaValue.getCodd()){
						found = true;
						id = doc.get("_id");
						break;
					}
				}
				
				if(!found){
					query = new BasicDBObject("codd",consultaValue.getCodd());
					for (Document doc: doente.find(query)){
						if((int)doc.get("codd") == consultaValue.getCodd()){
							found = true;
							id = doc.get("_id");
							break;
						}
					}
				}
				
				consulta.insertOne(new Document("nagenda", consultaValue.getNagenda())
						.append("hora", consultaValue.getHora())
						.append("preço", consultaValue.getPreço())
						.append("situação", consultaValue.getSituação())
						.append("relatório", consultaValue.getRelatório())
						.append("codd", id));
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
		iterableMedico.forEach(new Block<Document>() {
		    @Override
		    public void apply(final Document document) {
		        System.out.print(document.get("nome"));
		        System.out.print(" ");
		        System.out.println(document.get("data_nasce"));
		    }
		});
		
		//Relatório da atividade clínica
		FindIterable<Document> iterableAgenda = clinica.getCollection("agenda").find();
		System.out.println("RELATORIO DE ATIVIDADE MEDICA:");
		
		iterableAgenda.forEach(new Block<Document>() {
			ArrayList<Document> agendas = new ArrayList<Document>();
		    @Override
		    public void apply(Document document) {
		    	agendas.add(document);
		    	agendaMedico.put((int) document.get("codm"), document);
		    }
		});
		
		for(int i = 1; i <= clinica.getCollection("medico").count(); i++){
			
			BasicDBObject queryCodm = new BasicDBObject("codm", i);
			FindIterable<Document> iterableMedico2 = clinica.getCollection("medico").find(queryCodm);
			final MongoDatabase clinicaCopy = clinica;
			
			iterableMedico2.forEach(new Block<Document>() {
			    @Override
			    public void apply(Document document) {
			    	System.out.print(document.get("codm"));
			        System.out.print(" ");
			        System.out.println(document.get("nome"));
			        
			        if(agendaMedico.get((Integer) document.get("codm")) == (null))
			        	System.out.println("O medico nao tem consultas agendadas!");
			        else {
			        	for (int i = 0; i < agendaMedico.get(document.get("codm")).size(); i++){
			        		System.out.println(agendaMedico.get(document.get("codm")).get(i).get("dia"));
			        		BasicDBObject queryNagenda = new BasicDBObject("nagenda", agendaMedico.get(document.get("codm")).get(i).get("nagenda"));
			        		final int codm = (int) document.get("codm");
			    			FindIterable<Document> iterableConsulta = clinicaCopy.getCollection("consulta").find(queryNagenda);
			    			iterableConsulta.forEach(new Block<Document>() {
			    			    @Override
			    			    public void apply(Document document) {
			    			    	System.out.print(document.get("hora") + " ");
			    			    	BasicDBObject queryCodd = new BasicDBObject("codd", document.get("codd"));
			    			    	FindIterable<Document> iterableDoente = clinicaCopy.getCollection("doente").find(queryCodd);
			    			    	iterableDoente.forEach(new Block<Document>() {
			    			    		@Override
			    			    		public void apply(Document document){
			    			    			System.out.print(document.get("nome") + " ");
			    			    		}
			    			    	});
			    			    	System.out.println(document.get("relatório"));
			    			    	
			    			    	//Ver quais os médicos que têm enfarte e angina para a terceira pergunta
			    			    	if(document.get("relatório").equals("Enfarte") || document.get("relatório").equals("Angina"))
			    			    		reportAnginaEnfarte[codm] = true;
			    			    }
			    			});
			        	}
			        }
			    }
			});
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
