
package alexCerthRest.rest_apiCerth;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.QueryResults;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import static org.eclipse.rdf4j.model.util.Values.iri;

public class GraphDB {
    
	public final static String serverName="xr4DramaAlex";
    public final static String serverURL="http://localhost:7200";

	
	public static void loadData(RepositoryConnection connection, String name) throws RepositoryException, IOException, RDFParseException {
	       
		connection.begin();	
		// Load files in GraphDB repository	
		connection.add(new FileInputStream(name), "urn:base", RDFFormat.TURTLE);	
		// Committing the transaction persists the data
		connection.commit();		   
	}
	
	public static void clearData(RepositoryConnection connection) {
		
		// Clear the repository 
        connection.clear();
        connection.remove((Resource) null, null, null);       
	}
	
	public static void add2 (Model m) {
		
		RepositoryManager repositoryManager = new RemoteRepositoryManager(serverURL);
		repositoryManager.init();
		
		Repository ontologyRep = new HTTPRepository(serverURL, serverName);
		ontologyRep.init();
		RepositoryConnection ontologyCon = ontologyRep.getConnection();
		ontologyCon.begin();	
		ontologyCon.add(m);	
		ontologyCon.commit();
		ontologyCon.close();
		
	}
	
	public static String getObservation ()  {
		System.out.println("we are in");
		//IRI Observation_01= iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#Observation_01");
		IRI InfOfInt=iri ("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#InformationOfInterest");
		
		RepositoryManager repositoryManager = new RemoteRepositoryManager(serverURL);
		repositoryManager.init();
		
		Repository ontologyRep = new HTTPRepository(serverURL, serverName);
		ontologyRep.init();
		RepositoryConnection ontologyCon = ontologyRep.getConnection();
		
		RepositoryResult<Statement> statements = ontologyCon.getStatements(null, null, InfOfInt);
		Model observation= QueryResults.asModel(statements);
		
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Rio.write(observation,stream, RDFFormat.JSONLD);
      	
		String finalString = new String(stream.toByteArray());
		
		ontologyCon.close();
		System.out.println(finalString);
		
		return finalString;
	}
}



