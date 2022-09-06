
package alexCerthRest.rest_apiCerth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.json.simple.parser.ParseException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/secured")
public class JsonResource {
	
	
	@Path("/population/{type}")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createDataInJSON(@PathParam("type") String type, String data) throws JsonSyntaxException, JsonIOException, IOException, InterruptedException, ParseException
	
	{  
		Set<String> artifactoryLoggers = new HashSet<String>(Arrays.asList("org.apache.http", "groovyx.net.http", "org.eclipse.rdf4j"));
		for(String log:artifactoryLoggers) {
		    ch.qos.logback.classic.Logger artLogger = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(log);
		    artLogger.setLevel(ch.qos.logback.classic.Level.INFO);
		    artLogger.setAdditive(false);
		}
		
		Model mappingModel= null;
		String text=data;
		System.out.println("we are in");
		System.out.println(type);
		System.out.println(text);
	 if (!text.isEmpty()) {
		 
		 
        if (type.equals("{visuals}")) {
				mappingModel=Population.buildVisuals(text);
				System.out.println(mappingModel);
        }		
		else if	(type.equals("{stress_level}")) {
			
			mappingModel=Population.buildStressLvl(text);
			for (Statement st : mappingModel) {
				  System.out.println(st);
				}
			
		}
		else if	(type.equals("text")) {	
			mappingModel=Population.buildText(text);
			for (Statement st : mappingModel) {
				  System.out.println(st);
				}
		}
	 }
		
        	System.out.println("we are in model");
			
        			

    		System.out.println(mappingModel);
			GraphDB.add2(mappingModel);	//here is the issue
			//Files.deleteIfExists(Paths.get("test"+r+".ttl")); 
				return Response.status(201).entity("The data are mapped").build();
			//}
      
		//return Response.status(201).entity("The model is null!").build();	
	}
	
	
	
}
