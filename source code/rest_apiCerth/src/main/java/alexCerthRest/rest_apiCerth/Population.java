
package alexCerthRest.rest_apiCerth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.util.Values;
import org.eclipse.rdf4j.model.vocabulary.OWL;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.model.vocabulary.RDFS;

import static org.eclipse.rdf4j.model.util.Values.iri;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

public class Population {

	
	
public static String sendGetRequestStr(String url)
    {
        try{
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add request header
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("api_key","8t8YKQegHHl4gMCPd5TF");

            int responseCode = con.getResponseCode();

            System.out.println("Sending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if(responseCode!=200 && responseCode!=201)
                return "Error code returned: "+responseCode;

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine + "\n");
            }
            in.close();
            return response.toString();
        }
        catch(IOException io){
            System.out.println(io.getMessage());
            return "IO Exception: " + io.getMessage();
        }
    }
	
public static String sendPostRequestRawStr(String url, String body, String format,
        String... creds)
		{
		try{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("content-type",format);
		con.setRequestProperty("api_key","8t8YKQegHHl4gMCPd5TF");
		
		// credentials
		if(creds.length == 2){
		String encoded = Base64.getEncoder()
		.encodeToString((creds[0]+":"+creds[1]).getBytes(StandardCharsets.UTF_8));  //Java 8
		con.setRequestProperty("Authorization", "Basic "+encoded);
		}
		
		// Send post request
		System.out.println("Sending 'POST' request to URL : " + url);
		con.setDoOutput(true);
		
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		bw.write(body);
		bw.flush();
		bw.close();
		
		
		int responseCode = con.getResponseCode();
		String responseText = con.getResponseMessage();
		
		
		System.out.println("Response Code : " + responseCode);
		
		//if(responseCode!=200 && responseCode!=409)
		if(responseCode!=200 && responseCode!=201 )
		return "Error code returned: "+responseCode;
		
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		return response.toString();
		}
		catch(IOException io){
		System.out.println(io.getMessage());
		return "IO Exception: " + io.getMessage();
		}
		}

public static String sendPutRequestRawStr(String url, String body, String format,
        String... creds)
		{
		try{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		
		//add request header
		con.setRequestMethod("PUT");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("content-type",format);
		con.setRequestProperty("api_key","8t8YKQegHHl4gMCPd5TF");
		
		// credentials
		if(creds.length == 2){
		String encoded = Base64.getEncoder()
		.encodeToString((creds[0]+":"+creds[1]).getBytes(StandardCharsets.UTF_8));  //Java 8
		con.setRequestProperty("Authorization", "Basic "+encoded);
		}
		
		// Send post request
		System.out.println("Sending 'PUT' request to URL : " + url);
		con.setDoOutput(true);
		
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		bw.write(body);
		bw.flush();
		bw.close();
		
		
		int responseCode = con.getResponseCode();
		String responseText = con.getResponseMessage();
		
		
		System.out.println("Response Code : " + responseCode + " " + responseText);
		
		//if(responseCode!=200 && responseCode!=409)
		if(responseCode!=200 && responseCode!=201 )
		return "Error code returned: "+responseCode;
		
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
		}
		in.close();
		System.out.println(response.toString());
		return response.toString();
		}
		catch(IOException io){
		System.out.println(io.getMessage());
		return "IO Exception: " + io.getMessage();
		}
		}

public static Model buildTest(String text) 	throws JsonSyntaxException, JsonIOException, IOException, ParseException
{
	
	System.out.println("we are in");
	
	Object obj=JSONValue.parse(text);
	JSONObject jsonObj= (JSONObject) obj;
	DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
    String Id= jsonObj.get("id").toString();
    String StringTime=jsonObj.get("time").toString();
    LocalDateTime time = LocalDateTime.parse(StringTime,DATE_TIME_FORMATTER);
        
      
        IRI ObservationInstance = Values.iri("http://www.semanticweb.org/giorgostzanetis/ontologies/2021/5/InitialxR4DRAMA", "Observation_1");
       
        ModelBuilder builder = new ModelBuilder();
        
        Model modelObservation= builder
        	  .setNamespace("xR","http://www.semanticweb.org/giorgostzanetis/ontologies/2021/5/InitialxR4DRAMA")
        	  .subject(ObservationInstance)
    		  .add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
    		  .add(RDFS.LABEL, "Observation_1")
    		  .add(RDF.TYPE,"xR:Observation")
    		  .add("ex:hasId", Id)
    		  .add("ex:hasTime", time)
    		  .build();
    		  	
	return builder.build();
}

public static Model buildStressLvl (String text) 	throws JsonSyntaxException, JsonIOException, IOException, ParseException
{
	System.out.println("we are in population");
	String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	Object obj=JSONValue.parse(text);
	JSONObject jsonObj= (JSONObject) obj;

	DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"); 
    String id= jsonObj.get("User_ID").toString();
    String lvl=jsonObj.get("Stress_Level").toString();
    float lvlf=Float.parseFloat(lvl);
    String label=" ";
    if (lvlf < 30) { label = "Low"; }
    else if (lvlf>=30 && lvlf<=70) { label = "Moderated";}
    else { label = "High";}
    String StringTime=jsonObj.get("Timestamp").toString();
    String lat=jsonObj.get("Latitude").toString();
    String log=jsonObj.get("Longitude").toString();
    String ProjectId= jsonObj.get("Project_Id").toString();
    LocalDateTime time = LocalDateTime.parse(StringTime,DATE_TIME_FORMATTER);
    
    IRI ObservationInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Observation_"+uuid);
    IRI ResultInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Result_"+uuid);
    IRI FRInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "FR_"+id);
    IRI LocationInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Location_"+uuid);
    
    
    ModelBuilder builder = new ModelBuilder();
    
    builder
    	.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
    	.subject(ObservationInstance)
    	  	.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
    	  	.add(RDFS.LABEL, "Observation_"+uuid)
    	  	.add(RDF.TYPE,"xR:#Observation")
    	  	.add("xR:isConsistedIn", "xR:Project_"+ProjectId)
    	  	.add("xR:hasTime", time)
    	  	.add("xR:hasResult", ResultInstance)
		.subject(ResultInstance)
		  	.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
		  	.add(RDFS.LABEL, "Result_"+uuid)
		  	.add(RDF.TYPE,"xR:#Result")
		  	.add("xR:hasStressLevel",lvlf)
		  	.add("xR:hasResultLabel", label)
		.subject(LocationInstance)
			.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
			.add(RDFS.LABEL, "Location_"+uuid)
		  	.add(RDF.TYPE,"xR:#Location")
		  	.add("xR:hasLatitude",lat )
		  	.add("xR:hasLongitude", log)
		  	.add("xR:hasTime", time)  
		.subject(FRInstance)
			.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
			.add(RDFS.LABEL, "FR_"+id)
			.add(RDF.TYPE,"xR:#FR")
			.add("xR:included","xR:Project_"+ProjectId)
			.add("xR:related",ResultInstance) 
			.add("xR:hasFRLocation", LocationInstance)
			.build();
  
    return builder.build();	
 }

public static Model buildText (String text) 	throws JsonSyntaxException, JsonIOException, IOException, ParseException
{


	ModelBuilder builder = new ModelBuilder();
	
	Instant instant = Instant.now();
	DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").withZone( ZoneId.systemDefault() );
	String formater_output = DATE_TIME_FORMATTER.format( instant );
												
	
	Object obj=JSONValue.parse(text);
	JSONObject jsonObj= (JSONObject) obj;
	JSONObject json_meta= (JSONObject) jsonObj.get("meta");
	JSONObject json_data= (JSONObject) jsonObj.get("data");
	LocalDateTime current_time=  LocalDateTime.parse(formater_output,DATE_TIME_FORMATTER);
	String id=json_meta.get("id").toString().replaceAll("-", "");
	String projectId= json_meta.get("project_id").toString();

	
	String type= json_data.get("type").toString();
	String uuid = UUID.randomUUID().toString().replaceAll("-", "");

	IRI ObservationInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Observation_"+uuid);
	IRI LocationInstance = null;
	IRI InfoOfIntInstance = null;
	
	if(type.equals("incident")) {
	JSONArray objJASON_Array=(JSONArray) json_data.get("situations");
	
	builder
 	.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
 		.subject(ObservationInstance)
	 	.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
	  	.add(RDFS.LABEL, "Observation_"+uuid)
	  	.add("xR:isRelatedWithSimmo", json_meta.get("id").toString())
	  	.add(RDF.TYPE,"xR:#Observation")
	  	.add("xR:hasObservationType", type)
	  	.add("xR:hasCurrentTime", current_time)
	  	.add("xR:isConsistedIn", "xR:Project_"+projectId);
	
	
	if(json_meta.containsKey("entity")) {
		String entity= json_meta.get("entity").toString();
		builder
	 	.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
	 		.subject(ObservationInstance)
		  	.add("xR:comesFromEntity", entity);
	}
	 uuid = UUID.randomUUID().toString().replaceAll("-", "");
	 
	 
	for (int i = 0; i < objJASON_Array.size(); i++) {
		
		JSONObject json_sit=(JSONObject)objJASON_Array.get(i);
		
		if (json_sit.containsKey("location") ) { 
			String locationURI=json_sit.get("location").toString();
			uuid = UUID.randomUUID().toString().replaceAll("-", "");

			if (locationURI !=null ) {  	
			
			 LocationInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Location_"+uuid); 
			
			builder
			.subject(LocationInstance)
				.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
				.add(RDFS.LABEL, "Location_"+uuid)
			  	.add(RDF.TYPE,"xR:#Location")
			  	.add("xR:hasURI", locationURI);
			
			
			
			if(json_sit.containsKey("location") && json_sit.containsKey("coordinates") ) {
				
				
				String coordinates=json_sit.get("coordinates").toString();
				String parts[] = coordinates.split("(?<= N)");
				String latitude= parts[0];
				String longtitude= parts[1];
				System.out.println(latitude);
				System.out.println(longtitude);
				if(latitude !=null && longtitude!= null) {
					builder
					.subject(LocationInstance)
					  	.add("xR:hasLatitude",latitude )
					  	.add("xR:hasLongitude", longtitude);
				}
			}
			}
		}
		
		System.out.println(json_sit);
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		IRI ResultInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Result_"+uuid);
		builder
		
		.subject(ResultInstance)
		  	.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
		  	.add(RDFS.LABEL, "TextualMetadata_"+uuid)
		  	.add(RDF.TYPE,"xR:#TextualMetadata")
		  	.add("xR:hasLabel", json_sit.get("label").toString());
		
		System.out.println("Safe spot alex");
		
		if (json_sit.containsKey("risk_level")) {
			String risk_lvl=json_sit.get("risk_level").toString();
		
			builder
			.subject(ResultInstance)	
			  	.add("xR:hasTextualRiskLabel", risk_lvl);
		}
		
		System.out.println("Safe spot alex 1");
		
		if ((json_sit.get("affected_objects"))!=null) { 	
			
		JSONArray objJASON_Array_2=(JSONArray) json_sit.get("affected_objects");
		for (int j = 0; j < objJASON_Array_2.size(); j++) {
			
			 
			InfoOfIntInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "InformationOfInterest"+ uuid +"_"+ j); 
			

			String objects= objJASON_Array_2.get(j).toString();
			
			
			builder
			.subject(InfoOfIntInstance)
				.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
		  		
				.add(RDFS.LABEL, "InformationOfInterest"+ uuid +"_"+ j)
		  		.add(RDF.TYPE,"xR:#InformationOfInterest")
				.add("xR:hasType", objects)
				.add("xR:featureOf",ObservationInstance)
				.add("xR:hasTextualMetadata", ResultInstance);
			
			if (LocationInstance != null) { 
			
			builder
			.subject(InfoOfIntInstance)
				.add("xR:hasLocation",LocationInstance);
			}
			
		}
		
		if(objJASON_Array_2.size()==0) {
			uuid = UUID.randomUUID().toString().replaceAll("-", "");
			 InfoOfIntInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "InformationOfInterest"+ uuid);
				
				
				
				builder
				.subject(InfoOfIntInstance)
					.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
			  		.add(RDFS.LABEL, "InformationOfInterest"+ uuid)
			  		.add(RDF.TYPE,"xR:#InformationOfInterest")
					.add("xR:featureOf",ObservationInstance)
					.add("xR:hasTextualMetadata", ResultInstance);
				
				if (LocationInstance != null) { 
				
				builder
				.subject(InfoOfIntInstance)
					.add("xR:hasLocation",LocationInstance);
				}	
		}
		}
		
		
		
	}
	}
	else if	(type.equals("logistics")){
		System.out.print("Alex");
		JSONArray objJASON_Array=(JSONArray) json_data.get("facilities");
		
		builder
	 	.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
	 		.subject(ObservationInstance)
		 	.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
		  	.add(RDFS.LABEL, "Observation_"+uuid)
		  	.add("xR:isRelatedWithSimmo", json_meta.get("id").toString())
		  	.add(RDF.TYPE,"xR:#Observation")
		  	.add("xR:hasObservationType", type)
		  	.add("xR:hasCurrentTime", current_time)
		  	.add("xR:isConsistedIn", "xR:Project_"+projectId);
		
		
		if(json_meta.containsKey("entity")) {
			String entity= json_meta.get("entity").toString();
			builder
		 	.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
		 		.subject(ObservationInstance)
			  	.add("xR:comesFromEntity", entity);
		}
		 
			for (int i = 0; i < objJASON_Array.size(); i++) {
				uuid = UUID.randomUUID().toString().replaceAll("-", "");
				JSONObject json_sit=(JSONObject)objJASON_Array.get(i);
				
				if (json_sit.containsKey("location") ) { 
					String locationURI=json_sit.get("location").toString();
					uuid = UUID.randomUUID().toString().replaceAll("-", "");

					if (locationURI !=null ) {  	
					
					 LocationInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Location_"+uuid); 
					
					builder
					.subject(LocationInstance)
						.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
						.add(RDFS.LABEL, "Location_"+uuid)
					  	.add(RDF.TYPE,"xR:#Location")
					  	.add("xR:hasURI", locationURI);
					
					
					
					if(json_sit.containsKey("location") && json_sit.containsKey("coordinates") ) {
						
						
						String coordinates=json_sit.get("coordinates").toString();
						String parts[] = coordinates.split("(?<= N)");
						String latitude= parts[0];
						String longtitude= parts[1];
						System.out.println(latitude);
						System.out.println(longtitude);
						if(latitude !=null && longtitude!= null) {
							builder
							.subject(LocationInstance)
							  	.add("xR:hasLatitude",latitude )
							  	.add("xR:hasLongitude", longtitude);
						}
					}
					
					if (json_sit.containsKey("cost")) {
						String cost = json_sit.get("cost").toString();
						builder
						.subject(LocationInstance)
						.add("xR:hasCost", cost);
					}
					
					}
				}
				
				
				System.out.println(json_sit);
				IRI ResultInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Result_"+uuid);
				builder
				.subject(ResultInstance)
				  	.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
				  	.add(RDFS.LABEL, "TextualMetadata_"+uuid)
				  	.add(RDF.TYPE,"xR:#TextualMetadata")
				  	.add("xR:hasLabel", json_sit.get("type").toString());
				
				System.out.println("Safe spot alex");
				
				
				
				
				
			}//closes the big for loop
		
	}//closes the else condition

	 return builder.build();	
   
	
}

public static Model buildVisuals (String text) 	throws JsonSyntaxException, JsonIOException, IOException, ParseException
{
	System.out.println("we are in population");
	String uuid = UUID.randomUUID().toString().replaceAll("-", "");
	
	Instant instant = Instant.now();
	DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS").withZone( ZoneId.systemDefault() );
	String formater_output = DATE_TIME_FORMATTER.format( instant );
	LocalDateTime current_time=  LocalDateTime.parse(formater_output,DATE_TIME_FORMATTER);
	
	Object obj=JSONValue.parse(text);
	JSONObject jsonObj= (JSONObject) obj;
	JSONObject json_header= (JSONObject) jsonObj.get("header");
	
	String StringTime=json_header.get("timestamp").toString();
	LocalDateTime time = LocalDateTime.parse(StringTime,DATE_TIME_FORMATTER);
	String simmoid= json_header.get("simmoid").toString();
	String projectId= json_header.get("project_id").toString();
	
	ModelBuilder builder = new ModelBuilder();
	IRI ObservationInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Observation_"+uuid);
	
	
	builder
	.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
	.subject(ObservationInstance)
  		.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
  		.add(RDFS.LABEL, "Observation_"+uuid)
  		.add(RDF.TYPE,"xR:#Observation")
  		.add("xR:isConsistedIn", "xR:Project_"+projectId)
  		.add("xR:hasTime", time)
  		.add("xR:hasCurrentTime", current_time);
  		
	
	if(json_header.get("entity").equals("video")) { 
		IRI VideoInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Video_"+uuid);
		builder
		.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
		.subject(ObservationInstance)
			.add("xR:hasMultimedia", VideoInstance)
			.subject(VideoInstance)
	  			.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
	  			.add(RDFS.LABEL, "Video_"+uuid)
	  			.add(RDF.TYPE,"xR:#Video")
	  			.add("xR:hasSIMMORef", simmoid);
	}
	else if(json_header.get("entity").equals("image")) { 
		IRI ImageInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "Image_"+uuid);
		builder
		.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
		.subject(ObservationInstance)
			.add("xR:hasMultimedia", ImageInstance)
			.subject(ImageInstance)
	  			.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
	  			.add(RDFS.LABEL, "Image_"+uuid)
	  			.add(RDF.TYPE,"xR:#Image")
	  			.add("xR:hasSIMMORef", simmoid);
	}
	else if(json_header.get("entity").equals("twitter_post")) { 
		IRI TwitterPostInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "TwitterPost_"+uuid);
		builder
		.setNamespace("xR","http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA")
		.subject(ObservationInstance)
			.add("xR:hasMultimedia", TwitterPostInstance)
			.subject(TwitterPostInstance)
	  			.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
	  			.add(RDFS.LABEL, "TwitterPost_"+uuid)
	  			.add(RDF.TYPE,"xR:#TwitterPost")
	  			.add("xR:hasSIMMORef", simmoid);
	}
	
	JSONArray objJASON_Array=(JSONArray) jsonObj.get("shotInfo");
	for (int i = 0; i < objJASON_Array.size(); i++) {
		
		IRI VisualMetaInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "VisualMetadata_"+ uuid +"_"+ i);
		
		JSONObject json_shot=(JSONObject)objJASON_Array.get(i);
		
		String fShotId= json_shot.get("shotIdx").toString();
		int shotId= Integer.parseInt(fShotId);
		
		String sStartFrame= json_shot.get("startFrame").toString();
		int startFrame= Integer.parseInt(sStartFrame);
		String sEndFrame= json_shot.get("endFrame").toString();
		int endFrame= Integer.parseInt(sEndFrame);
		
		String fPeople= json_shot.get("peopleInDanger").toString();
		int people= Integer.parseInt(fPeople);
		String fanimal = json_shot.get("animalsInDanger").toString();//new code
		int animal = Integer.parseInt(fanimal);//new code
		
		String fVehicles= json_shot.get("vehiclesInDanger").toString();
		int vehicles= Integer.parseInt(fVehicles);
		String fRiver= json_shot.get("riverOvertop").toString();
		boolean river=Boolean.parseBoolean(fRiver);
		String area= json_shot.get("area").toString();
		String fAreaProb= json_shot.get("areaProb").toString();
		float areaProb = 0; if ( fAreaProb == "none") { areaProb = 0;}  else {  areaProb= Float.parseFloat(fAreaProb);}
		String fOutdoor= json_shot.get("outdoor").toString();
		boolean outdoor=Boolean.parseBoolean(fOutdoor);
		String emergency= json_shot.get("emergencyType").toString();
		String fEmergProb= json_shot.get("emergencyProb").toString();
		float emergencyProb= Float.parseFloat(fEmergProb);
		uuid = UUID.randomUUID().toString().replaceAll("-", "");
		builder
		.subject(VisualMetaInstance)
			.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
	  		.add(RDFS.LABEL, "VisualMetadata_"+ uuid +"_"+ i) 
			
	  		.add(RDF.TYPE,"xR:#VisualMetadata")
	  		.add("xR:hasShotId",shotId)
	  		.add("xR:hasStartFrame",startFrame)
	  		.add("xR:hasEndFrame",endFrame)
	  		.add("xR:peopleInDanger", people)
	  		.add("xR:animalInDanger", animal)// new code
	  		.add("xR:vehicleInDanger", vehicles)
	  		.add("xR:hasRiverOvertop", river)
	  		.add("xR:hasArea", area)
	  		.add("xR:hasAreaProb", areaProb)
	  		.add("xR:isOutdoor", outdoor)
	  		.add("xR:hasEmergencyProb", emergencyProb)
	  		.add("xR:hasEmergency",emergency)
	  	.subject(ObservationInstance)
	  		.add("xR:hasMetadata", "VisualMetadata_"+ uuid +"_"+ i);
		
		
		//some new code for visuals//
		if ((json_shot.get("infraInDanger"))!=null) {
			JSONArray jsonInfra =(JSONArray) json_shot.get("infraInDanger");
			
			for (int j = 0; j < jsonInfra.size(); j++) {
				
				String infra = jsonInfra.get(j).toString();
				builder
				.subject(VisualMetaInstance)
				.add("xR:infraInDanger", infra);
			}
		}
		
		// the new code ends here //
		
		//some new code for visuals//
		if ((json_shot.get("objectsInDanger"))!=null) {
			JSONArray jsonObject =(JSONArray) json_shot.get("objectsInDanger");
			
			for (int j = 0; j < jsonObject.size(); j++) {
				
				String object = jsonObject.get(j).toString();
				builder
				.subject(VisualMetaInstance)
				.add("xR:objectInDanger", object);
			}
		}
		
		// the new code ends here //
		
		if ((json_shot.get("objectsFound"))!=null) {
			
			
		JSONArray objJASON_Array_2=(JSONArray) json_shot.get("objectsFound");
		
		
		for (int j = 0; j < objJASON_Array_2.size(); j++) {
			
			IRI InfoOfIntInstance = Values.iri("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA", "InformationOfInterest"+ uuid +"_"+ i+"_"+j); 
			
			JSONObject json_objects=(JSONObject)objJASON_Array_2.get(j);
			
			String type= json_objects.get("type").toString();
			String fObjectProb= json_objects.get("probability").toString();
			float ObjectProb= Float.parseFloat(fObjectProb);
			
			builder
			.subject(InfoOfIntInstance)
			.add(RDF.TYPE, OWL.NAMEDINDIVIDUAL)
	  		.add(RDFS.LABEL, "InformationOfInterest"+ uuid +"_"+ i+"_"+j) 
			
	  		.add(RDF.TYPE,"xR:#InformationOfInterest")
			.add("xR:hasType", type)
			.add("xR:hasProbability", ObjectProb)
			.add("xR:featureOfMetaInstance",VisualMetaInstance); 
		}
		}
	}
	
	return builder.build();
	
}		




}