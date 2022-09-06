
package alexCerth.retrievalCerth;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;  
import java.time.Instant; 
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.*;
import java.util.Arrays;
import java.util.List;
import java.util.Base64;
import java.util.UUID;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.mongodb.*;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import java.net.URLEncoder;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.LoggerFactory;
import org.json.JSONException;
import org.json.JSONString;




public class Retrieve {

    public static String queriesExecution(String input) {
        Gson gson = new Gson();

        JsonElement json = gson.fromJson(input, JsonElement.class);
        JsonObject jobject = json.getAsJsonObject();

        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(Server.SERVER);
        repositoryManager.initialize();

        Repository testRepository = repositoryManager.getRepository(Server.REPOSITORY);
        JSONObject response = new JSONObject();


        String query = "PREFIX xR: <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA>\r\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n" +

            "select distinct ?multimedia ?vtime ?type ?prob ?observation ?people ?vehicle ?area ?ar_prob ?outdoor ?em_type ?em_prob ?s_id ?stime ?stress_result ?stress_level ?stress_lbl ?lbl ?uri ?t_label ?sit_label ?object ?ttime ?o_type ?ioit where { " +
            "	?observation a <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#Observation> . \r\n" +
            "    ?observation xR:isConsistedIn ?project_id.\r\n" +
            "    OPTIONAL { #visuals \r\n" +
            "       ?observation xR:hasCurrentTime ?vtime.\r\n" +
            "       ?ioi ?p <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#InformationOfInterest> .\r\n" +
            "    	?ioi xR:hasType ?type.\r\n" +
            "    	?ioi xR:hasProbability ?prob.\r\n" +

            "	    ?ioi xR:featureOfMetaInstance ?metadata." +
            "    	?observation xR:hasMultimedia ?multimedia.\r\n" +
            "		?metadata xR:peopleInDanger ?people.\r\n" +
            "       ?metadata xR:vehicleInDanger ?vehicle.\r\n" +
            "		?metadata xR:hasEmergency ?em_type.\r\n" +
            "		?metadata xR:hasEmergencyProb ?em_prob.\r\n" +
            "		?metadata xR:hasArea ?area.\r\n" +
            "		?metadata xR:hasAreaProb ?ar_prob.\r\n" +
            "		?metadata xR:isOutdoor ?outdoor.\r\n" +
            " ?metadata xR:hasShotId ?s_id." +
            "       FILTER (?vtime>\"" + jobject.get("first_timestamp").getAsString() + "\"^^xsd:dateTime && ?vtime<\"" + jobject.get("last_timestamp").getAsString() + "\"^^xsd:dateTime)\r\n" +
            "    }\r\n" +
            "    OPTIONAL {   # sensors\r\n" +
            "       ?observation xR:hasTime ?stime.\r\n" +
            "    	?observation xR:hasResult ?stress_result.\r\n" +
            "    	?stress_result xR:hasStressLevel ?stress_level.\r\n" +
            "  ?stress_result xR:hasResultLabel ?stress_lbl." +
            "		?user a <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#FR> .\r\n" +
            "    	?user xR:included ?project_id.\r\n" +
            " ?user xR:related ?stress_result. " +
            "       ?user rdfs:label ?lbl." +
            "    	" +
            "FILTER (?stime>\"" + jobject.get("first_timestamp").getAsString() + "\"^^xsd:dateTime && ?stime<\"" + jobject.get("last_timestamp").getAsString() + "\"^^xsd:dateTime)\r\n" +
            "    }\r\n" +

            " OPTIONAL { #text\r\n" +
            "        ?observation xR:hasCurrentTime ?ttime.\r\n" +
            "        ?ioit xR:featureOf ?observation.\r\n" +
            "        ?ioit xR:hasTextualMetadata ?textual_meta.\r\n" +
            "      \r\n" +
            "		 ?observation xR:hasObservationType ?o_type.\r\n" +
            "		 ?textual_meta xR:hasLabel ?sit_label.\r\n" +
            "        OPTIONAL {\r\n" +
            "            ?ioit xR:hasLocation ?location.\r\n" +
            "        	?location xR:hasURI ?uri.\r\n" +
            "        }\r\n" +
            "        OPTIONAL {\r\n" +
            "         \r\n" +
            "        	?textual_meta xR:hasTextualRiskLabel ?t_label.\r\n" +
            "        }\r\n" +
            "        optional {\r\n" +
            "			?ioit xR:hasType ?object.\r\n" +
            "        }" +
            "    	FILTER (?ttime>\"" + jobject.get("first_timestamp").getAsString() + "\"^^xsd:dateTime && ?ttime<\"" + jobject.get("last_timestamp").getAsString() + "\"^^xsd:dateTime)\r\n" +
            "    }\r\n" +
            "    FILTER (?project_id=xR:Project_" + jobject.get("project_id").getAsString() + ")\r\n" +

            "} ";
        System.out.println(query);
        // Open a connection to the database
        RepositoryConnection testRepoConnection = testRepository.getConnection();

        TupleQuery tupleQuery = testRepoConnection.prepareTupleQuery(query);

        TupleQueryResult qresult = tupleQuery.evaluate();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        JSONObject header = new JSONObject();
        header.put("id", uuid);
        header.put("first_timestamp", jobject.get("first_timestamp").getAsString());
        header.put("last_timestamp", jobject.get("last_timestamp").getAsString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));

        header.put("created_at", formatter.format(date).toString());

        JSONArray projects = new JSONArray();
        JSONArray visuals = new JSONArray();
        JSONArray stress_lvls = new JSONArray();
        JSONArray texts = new JSONArray();

        JSONObject visual = new JSONObject();
        JSONObject text = new JSONObject();
        JSONObject stress_lvl = new JSONObject();

        JSONObject situation = new JSONObject();

        JSONArray objects = new JSONArray();
        JSONObject object = new JSONObject();
        ArrayList < String > observations = new ArrayList < String > ();
        String last_observation = "null";
        String last_sit_label = "";
        while (qresult.hasNext()) {
            BindingSet st = qresult.next();

            // add visuals
            if (st.hasBinding("vtime") && st.hasBinding("type") && st.hasBinding("prob") && st.hasBinding("observation") && st.hasBinding("people") && st.hasBinding("vehicle") && st.hasBinding("s_id") &&
                st.hasBinding("em_type") && st.hasBinding("em_prob")) {

                if (last_observation.equals("text")) {

                    situation.put("objects", objects);
                    text.put("situation", situation);
                    texts.put(text);
                    objects = new JSONArray();
                }

                if (!observations.contains(st.getValue("observation").stringValue() + st.getValue("s_id"))) {
                    if (last_observation.equals("visual")) {
                        visual.put("objects", objects);
                        visuals.put(visual);
                        objects = new JSONArray();
                    }
                    observations.add(st.getValue("observation").stringValue() + st.getValue("s_id"));
                    visual = new JSONObject();
                    objects = new JSONArray();
                    visual.put("shotId", st.getValue("s_id").stringValue());
                    visual.put("timestamp", st.getValue("vtime").stringValue());
                    visual.put("peopleInDanger", st.getValue("people").stringValue());
                    visual.put("vehiclesInDanger", st.getValue("vehicle").stringValue());
                    visual.put("emergencyType", st.getValue("em_type").stringValue());
                    visual.put("emergencyProbability", st.getValue("em_prob").stringValue());
                    visual.put("area", st.getValue("area").stringValue());
                    visual.put("areaProbability", st.getValue("ar_prob").stringValue());
                    visual.put("outdoor", st.getValue("outdoor").stringValue());
                }

                object = new JSONObject();
                object.put("type", st.getValue("type").stringValue());
                object.put("prob", Double.parseDouble(st.getValue("prob").stringValue()));
                objects.put(object);
                last_observation = "visual";
            }
            if (st.hasBinding("stime") && st.hasBinding("stress_level") && st.hasBinding("stress_lbl") && st.hasBinding("lbl")) {
                if (last_observation.equals("visual")) {
                    visual.put("objects", objects);
                    visuals.put(visual);
                    objects = new JSONArray();
                } else if (last_observation.equals("text")) {

                    situation.put("objects", objects);
                    text.put("situation", situation);
                    texts.put(text);
                    objects = new JSONArray();
                }
                stress_lvl = new JSONObject();
                stress_lvl.put("timestamp", st.getValue("stime").stringValue());
                stress_lvl.put("stress_level", st.getValue("stress_level").stringValue());
                stress_lvl.put("stress_label", st.getValue("stress_lbl").stringValue());
                stress_lvl.put("user_id", st.getValue("lbl").stringValue().replace("FR_", ""));
                stress_lvls.put(stress_lvl);

                last_observation = "stress_lvl";
            }
            if (st.hasBinding("ttime") && st.hasBinding("observation") && st.hasBinding("sit_label") && st.hasBinding("o_type")) {

                if (last_observation.equals("visual")) {
                    visual.put("objects", objects);
                    visuals.put(visual);
                    objects = new JSONArray();
                }

                if (!observations.contains(st.getValue("ioit").stringValue())) {
                    if (last_observation.equals("text")) {
                        if (!last_sit_label.equals(st.getValue("sit_label").stringValue())) {
                            situation.put("objects", objects);
                            text.put("situation", situation);
                            texts.put(text);
                            text = new JSONObject();
                            situation = new JSONObject();
                            objects = new JSONArray();
                        }
                    }
                    
                    observations.add(st.getValue("ioit").stringValue());
                    text.put("timestamp", st.getValue("ttime").stringValue());
                    text.put("type", st.getValue("o_type").stringValue());

                    if (st.hasBinding("uri")) {
                        text.put("location", st.getValue("uri").stringValue());
                    }
                    if (st.hasBinding("t_label")) {
                        text.put("risk_level", st.getValue("t_label").stringValue());
                    }
                }

                object = new JSONObject();
                if (!st.hasBinding("object")) {

                    situation.put("label", st.getValue("sit_label").stringValue());
                    situation.put("objects", objects);
                    last_sit_label = st.getValue("sit_label").stringValue();

                } else {

                    if (!hasValue(objects, "type", st.getValue("object").stringValue())) {

                        situation.put("label", st.getValue("sit_label").stringValue());
                        object.put("type", st.getValue("object").stringValue());
                        objects.put(object);
                        last_sit_label = st.getValue("sit_label").stringValue();
                    }
                }
                last_observation = "text";
            }
        }

        if (last_observation.equals("visual")) {
            visual.put("objects", objects);
            visuals.put(visual);
            objects = new JSONArray();
        } else if (last_observation.equals("text")) {
            situation.put("objects", objects);

            text.put("situation", situation);
            texts.put(text);
            objects = new JSONArray();

        }

        JSONObject project = new JSONObject();
        project.put("project_id", jobject.get("project_id").getAsString());
        project.put("visuals", visuals);
        project.put("stress_lvl", stress_lvls);
        project.put("text", texts);

        projects.put(project);
        header.put("project", projects);

        response.put("header", header);
        testRepoConnection.close();
        return response.toString();
    }

    public static String queriesExecutionForBackend(String input) {
        Gson gson = new Gson();

        JsonElement json = gson.fromJson(input, JsonElement.class);
        JsonObject jobject = json.getAsJsonObject();

        RemoteRepositoryManager repositoryManager = new RemoteRepositoryManager(Server.SERVER);
        repositoryManager.initialize();

        Repository testRepository = repositoryManager.getRepository(Server.REPOSITORY);
        JSONObject response = new JSONObject();


        String query = "PREFIX xR: <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA>\r\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\r\n" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\r\n" + 
            "select distinct ?v_simmo ?v_type ?multimedia ?vtime ?type ?prob ?observation ?people ?vehicle ?area ?ar_prob ?outdoor ?em_type ?em_prob ?s_id ?stime ?stress_result ?stress_level ?stress_lbl ?lbl ?uri ?t_label ?sit_label ?object ?ttime ?o_type ?ioit ?s_long ?s_lat ?text_simmo ?o_entity where { " +
            "	?observation a <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#Observation> . \r\n" +
            "    ?observation xR:isConsistedIn ?project_id.\r\n" +
            "    OPTIONAL { #visuals \r\n" +
            "       ?observation xR:hasCurrentTime ?vtime.\r\n" +
            "       ?ioi ?p <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#InformationOfInterest> .\r\n" +
            "    	?ioi xR:hasType ?type.\r\n" +
            "    	?ioi xR:hasProbability ?prob.\r\n" +
            "	    ?ioi xR:featureOfMetaInstance ?metadata." +
            "    	?observation xR:hasMultimedia ?multimedia.\r\n" +
            "        ?multimedia xR:hasSIMMORef ?v_simmo.\r\n" + 
            "		?multimedia rdf:type ?v_type.\r\n"+
            "		?metadata xR:peopleInDanger ?people.\r\n" +
            "       ?metadata xR:vehicleInDanger ?vehicle.\r\n" +
            "		?metadata xR:hasEmergency ?em_type.\r\n" +
            "		?metadata xR:hasEmergencyProb ?em_prob.\r\n" +
            "		?metadata xR:hasArea ?area.\r\n" +
            "		?metadata xR:hasAreaProb ?ar_prob.\r\n" +
            "		?metadata xR:isOutdoor ?outdoor.\r\n" +
            "       ?metadata xR:hasShotId ?s_id." +
            "       FILTER (?v_type!=<http://www.w3.org/2002/07/owl#NamedIndividual>)\r\n" + 
            ""+
            "       FILTER (?vtime>\"" + jobject.get("first_timestamp").getAsString() + "\"^^xsd:dateTime && ?vtime<\"" + jobject.get("last_timestamp").getAsString() + "\"^^xsd:dateTime)\r\n" +
            "    }\r\n" +
            "    OPTIONAL {   # sensors\r\n" +
            "       ?observation xR:hasTime ?stime.\r\n" +
            "    	?observation xR:hasResult ?stress_result.\r\n" +
            "    	?stress_result xR:hasStressLevel ?stress_level.\r\n" +
            "  ?stress_result xR:hasResultLabel ?stress_lbl." +
            "		?user a <http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#FR> .\r\n" +
            "    	?user xR:included ?project_id.\r\n" +
            " ?user xR:related ?stress_result. " +
            "       ?user rdfs:label ?lbl." +
            "    	?user xR:hasFRLocation ?loc. \r\n" + 
            "    ?loc xR:hasLongitude ?s_long.\r\n" + 
            "    ?loc xR:hasLatitude ?s_lat.\r\n" +
            " ?loc xR:hasTime ?stime.\r\n"+
            "FILTER (?stime>\"" + jobject.get("first_timestamp").getAsString() + "\"^^xsd:dateTime && ?stime<\"" + jobject.get("last_timestamp").getAsString() + "\"^^xsd:dateTime)\r\n" +
            "    }\r\n" +

            " OPTIONAL { #text\r\n" +
            "        ?observation xR:hasCurrentTime ?ttime.\r\n" +
            "		?observation xR:isRelatedWithSimmo ?text_simmo."+
            "        ?ioit xR:featureOf ?observation.\r\n" +
            "        ?ioit xR:hasTextualMetadata ?textual_meta.\r\n" +
            "      \r\n" +
            "		 ?observation xR:hasObservationType ?o_type.\r\n" +
            "		OPTIONAL{ ?observation xR:comesFromEntity ?o_entity. }\r\n" +
            "		 ?textual_meta xR:hasLabel ?sit_label.\r\n" +
            "        OPTIONAL {\r\n" +
            "            ?ioit xR:hasLocation ?location.\r\n" +
            "        	?location xR:hasURI ?uri.\r\n" +
            "        }\r\n" +
            "        OPTIONAL {\r\n" +
            "         \r\n" +
            "        	?textual_meta xR:hasTextualRiskLabel ?t_label.\r\n" +
            "        }\r\n" +
            "        optional {\r\n" +
            "			?ioit xR:hasType ?object.\r\n" +
            "        }" +
            "    	FILTER (?ttime>\"" + jobject.get("first_timestamp").getAsString() + "\"^^xsd:dateTime && ?ttime<\"" + jobject.get("last_timestamp").getAsString() + "\"^^xsd:dateTime)\r\n" +
            "    }\r\n" +
            "    FILTER (?project_id=xR:Project_" + jobject.get("project_id").getAsString() + ")\r\n" +
            "} ";
        System.out.println(query);
        // Open a connection to the database
        RepositoryConnection testRepoConnection = testRepository.getConnection();

        TupleQuery tupleQuery = testRepoConnection.prepareTupleQuery(query);

        TupleQueryResult qresult = tupleQuery.evaluate();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        JSONObject header = new JSONObject();
        header.put("id", uuid);
        header.put("first_timestamp", jobject.get("first_timestamp").getAsString());
        header.put("last_timestamp", jobject.get("last_timestamp").getAsString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));

        header.put("created_at", formatter.format(date).toString());

        JSONArray projects = new JSONArray();
        JSONArray visuals = new JSONArray();
        JSONArray stress_lvls = new JSONArray();
        JSONArray texts = new JSONArray();

        JSONObject visual = new JSONObject();
        JSONObject text = new JSONObject();
        JSONObject stress_lvl = new JSONObject();

        JSONObject situation = new JSONObject();

        JSONArray objects = new JSONArray();
        JSONObject object = new JSONObject();
        ArrayList < String > observations = new ArrayList < String > ();
        String last_observation = "null";
        String last_sit_label = "";
        while (qresult.hasNext()) {
            BindingSet st = qresult.next();

            // add visuals
            if (st.hasBinding("vtime") && st.hasBinding("type") && st.hasBinding("prob") && st.hasBinding("observation") && st.hasBinding("people") && st.hasBinding("vehicle") && st.hasBinding("s_id") &&
                st.hasBinding("em_type") && st.hasBinding("em_prob") && st.hasBinding("v_simmo") && st.hasBinding("v_type")) {

                if (last_observation.equals("text")) {

                    situation.put("objects", objects);

                    text.put("situation", situation);
                    texts.put(text);
                    objects = new JSONArray();

                }

                if (!observations.contains(st.getValue("observation").stringValue() + st.getValue("s_id"))) {
                    if (last_observation.equals("visual")) {
                        visual.put("objects", objects);
                        visuals.put(visual);
                        objects = new JSONArray();

                    }
                    observations.add(st.getValue("observation").stringValue() + st.getValue("s_id"));
                    visual = new JSONObject();
                    objects = new JSONArray();
                    visual.put("shotId", st.getValue("s_id").stringValue());
                    visual.put("timestamp", st.getValue("vtime").stringValue());
                    visual.put("peopleInDanger", st.getValue("people").stringValue());
                    visual.put("vehiclesInDanger", st.getValue("vehicle").stringValue());
                    visual.put("emergencyType", st.getValue("em_type").stringValue());
                    visual.put("emergencyProbability", st.getValue("em_prob").stringValue());
                    visual.put("area", st.getValue("area").stringValue());
                    visual.put("areaProbability", st.getValue("ar_prob").stringValue());
                    visual.put("outdoor", st.getValue("outdoor").stringValue());
                    visual.put("simmo", st.getValue("v_simmo").stringValue());
                    visual.put("entity", st.getValue("v_type").stringValue().replaceAll("http://www.semanticweb.org/ontologies/2021/5/InitialxR4DRAMA#", ""));


                }

                object = new JSONObject();
                object.put("type", st.getValue("type").stringValue());
                object.put("prob", Double.parseDouble(st.getValue("prob").stringValue()));
                objects.put(object);
                last_observation = "visual";
            }
            if (st.hasBinding("stime") && st.hasBinding("stress_level") && st.hasBinding("stress_lbl")&& st.hasBinding("s_long") && st.hasBinding("s_lat") && st.hasBinding("lbl")) {
                if (last_observation.equals("visual")) {
                    visual.put("objects", objects);
                    visuals.put(visual);
                    objects = new JSONArray();

                } else if (last_observation.equals("text")) {

                    situation.put("objects", objects);
                    text.put("situation", situation);
                    texts.put(text);
                    objects = new JSONArray();

                }
                stress_lvl = new JSONObject();
                stress_lvl.put("timestamp", st.getValue("stime").stringValue());
                stress_lvl.put("stress_level", st.getValue("stress_level").stringValue());
                stress_lvl.put("stress_label", st.getValue("stress_lbl").stringValue());
                stress_lvl.put("longitude", st.getValue("s_long").stringValue());
                stress_lvl.put("latitude", st.getValue("s_lat").stringValue());

                stress_lvl.put("user_id", st.getValue("lbl").stringValue().replace("FR_", "")); 
                stress_lvls.put(stress_lvl);

                last_observation = "stress_lvl";
            }
            if (st.hasBinding("ttime") && st.hasBinding("observation") && st.hasBinding("sit_label") && st.hasBinding("o_type")) {

                if (last_observation.equals("visual")) {
                    visual.put("objects", objects);
                    visuals.put(visual);
                    objects = new JSONArray();

                }

                if (!observations.contains(st.getValue("ioit").stringValue())) {
                    if (last_observation.equals("text")) {
                        if (!last_sit_label.equals(st.getValue("sit_label").stringValue())) {
                            situation.put("objects", objects);

                            text.put("situation", situation);
                            texts.put(text);

                            text = new JSONObject();
                            situation = new JSONObject();
                            objects = new JSONArray();
                        }
                    }
                    observations.add(st.getValue("ioit").stringValue());

                    text.put("timestamp", st.getValue("ttime").stringValue());

                    text.put("type", st.getValue("o_type").stringValue());
                    
                  
                    if (st.hasBinding("o_entity")) {
                        text.put("entity", st.getValue("o_entity").stringValue());
                    }
                    else {
                    	text.put("entity", "");
                    }
                             
                    if (st.hasBinding("text_simmo")) {
                    	text.put("simmo", st.getValue("text_simmo").stringValue());
                    }
                    
                    if (st.hasBinding("uri")) {
                        text.put("location", st.getValue("uri").stringValue());
                    }
                    else {
                    	text.put("location", "");
                    }
                    if (st.hasBinding("t_label")) {
                        text.put("risk_level", st.getValue("t_label").stringValue());
                    }
                    else {
                    	text.put("risk_level", "");
                    }
                }

                object = new JSONObject();
                if (!st.hasBinding("object")) {

                    situation.put("label", st.getValue("sit_label").stringValue());
                    situation.put("objects", objects);
                    last_sit_label = st.getValue("sit_label").stringValue();

                } else {

                    if (!hasValue(objects, "type", st.getValue("object").stringValue())) {

                        situation.put("label", st.getValue("sit_label").stringValue());

                        object.put("type", st.getValue("object").stringValue());
                        objects.put(object);
                        last_sit_label = st.getValue("sit_label").stringValue();
                    }
                }
                last_observation = "text";
            }
        }

        if (last_observation.equals("visual")) {
            visual.put("objects", objects);
            visuals.put(visual);
            objects = new JSONArray();

        } else if (last_observation.equals("text")) {
            situation.put("objects", objects);
            text.put("situation", situation);
            texts.put(text);
            objects = new JSONArray();


        }

        JSONObject project = new JSONObject();
        project.put("project_id", jobject.get("project_id").getAsString());
        project.put("visuals", visuals);
        project.put("stress_lvl", stress_lvls);
        project.put("text", texts);

        projects.put(project);
        header.put("project", projects);

        response.put("header", header);
        testRepoConnection.close();
        return response.toString();
    }

    public static String queriesPOItext(String input) {
        Gson gson = new Gson();

        JsonElement json = gson.fromJson(input, JsonElement.class);
        JsonObject jobject = json.getAsJsonObject();


        JsonObject json_meta = new JsonObject();
        JsonObject  json_data = new JsonObject();
    	json_meta = jobject.getAsJsonObject("meta").getAsJsonObject("location");
    	
    	
    	 int flag = 0;
    	if (!json_meta.equals(new JsonObject()) ) {
        json_data = jobject.getAsJsonObject("data");
       
        for (int i = 0; i < json_data.getAsJsonArray("situations").size(); i++) {
        	
    	   String sourceText = "";
    	   sourceText = jobject.getAsJsonObject("meta").get("sourceText").toString();
    	   String user = "";
    	   user = jobject.getAsJsonObject("meta").get("entity").toString();
    	   
    	   String response = "";
    	   response = sendGetRequestStr("https://geoservice.xr4drama.up2metric.com:8001/projects/");
    	   System.out.println(response);
    	   
    	   String[] parts = response.split("\"id\":");
    	   
    	   String[] latitudeList = response.split("\"latitude\":");
    	   String[] longitudeList = response.split("\"longitude\":");
    	   
    	   List<String> ids = new ArrayList<String>();
    	   for(int j = 1; j< parts.length; j++){
    		   ids.add(parts[j].split(",\"status\"")[0].toString());
    		   }
           
    	   List<String> latitudeListFinal = new ArrayList<String>();
    	   for(int j = 1; j < latitudeList.length; j++){
    		   latitudeListFinal.add(latitudeList[j].split(",\"longitude\":")[0].toString());
    		   }
    	   
    	   List<String> longitudeListFinal = new ArrayList<String>();
    	   for(int j = 1; j < longitudeList.length; j++){
    		   longitudeListFinal.add(longitudeList[j].split("},\"")[0].toString());
    		   }
    	   
    	   
    	      	   
    	   //create json for new pois
    	   double longitude = 0.0;
           double latitude = 0.0;
           latitude = json_meta.get("coordinates").getAsJsonArray().get(0).getAsDouble();
           longitude = json_meta.get("coordinates").getAsJsonArray().get(1).getAsDouble();
           String categoryConst = "Disaster Management";
           String subcategoryConst = "Civil Protection";
           //categoryConst = jobject.getAsJsonObject("data").get("category").toString().replace("\"", "");
           //subcategoryConst  = jobject.getAsJsonObject("data").get("subcategory").toString().replace("\"", "");
           
           System.out.println(categoryConst);
           System.out.println(subcategoryConst);
           //end here code for new pois json
           
           //procedure to find project ids
    	   List<Double> longitudeSE = new ArrayList<Double>();
    	   for(int j = 1; j < longitudeListFinal.size();  j = j + 2){
    		   double helper = 0;
    		   helper = Float.valueOf(longitudeListFinal.get(j));
    		   longitudeSE.add(helper);
    		   }
    	   
    	   List<Double> longitudeNW = new ArrayList<Double>();
    	   for(int j = 0; j < longitudeListFinal.size();  j = j + 2){
    		   double helper = 0;
    		   helper = Float.valueOf(longitudeListFinal.get(j));
    		   longitudeNW.add(helper);
    		   }
    	   
    	   List<Double> latitudeSE = new ArrayList<Double>();
    	   for(int j = 1; j < latitudeListFinal.size();  j = j + 2){
    		   double helper = 0;
    		   helper = Float.valueOf(latitudeListFinal.get(j));
    		   latitudeSE.add(helper);
    		   }
    	   
    	   List<Double> latitudeNW = new ArrayList<Double>();
    	   for(int j = 0; j < latitudeListFinal.size();  j = j + 2){
    		   double helper = 0;
    		   helper = Float.valueOf(latitudeListFinal.get(j));
    		   latitudeNW.add(helper);
    		   } 
    	  
           
           List<Integer> clusterIDs = new ArrayList<Integer>();
    	   for(int j = 0; j < longitudeSE.size(); j++) {
    		   double nwLat = 0;
    		   double seLat = 0;
    		   double nwLon = 0;
    		   double seLon = 0;
    		   nwLat = latitudeNW.get(j);
    		   seLat = latitudeSE.get(j);
    		   nwLon = longitudeNW.get(j);
    		   seLon = longitudeSE.get(j);
    		   System.out.println(seLat);
    		   System.out.println(latitude);
    		   System.out.println(nwLat);
    		   System.out.println("//////////////////////");
    		   System.out.println(nwLon);
    		   System.out.println(longitude);
    		   System.out.println(seLon);
    		   System.out.println("/////////NEW//////////");
    		   if( seLat <= latitude && latitude <= nwLat && nwLon <= longitude && longitude <= seLon ) {
    			   clusterIDs.add(Integer.valueOf(ids.get(j)));
    			}
    	   }//for
    	   //end finding project ids
    	   
    	   //find pois based on project ids
    	   List<String> idsPOIS = new ArrayList<String>();
    	   List<String> categoryPOIS = new ArrayList<String>();
    	   List<String> subcategoryPOIS = new ArrayList<String>();
    	   List<String> latitudePOIS = new ArrayList<String>();
    	   List<String> longitudePOIS = new ArrayList<String>();
    	   System.out.println(clusterIDs.size());
    	   
    	   for (int t = 0; t < clusterIDs.size(); t++) {
    		 String poisResponse = "";
		     int indexMin = 0;
		     indexMin = clusterIDs.get(t);
		     poisResponse = sendGetRequestStr("https://geoservice.xr4drama.up2metric.com:8001/projects/" + indexMin + "/points-of-interest?current_user=%22" + user.replace("\"", "") + "%22&order=ASC&as_point=true");
		     
		     try{
		     String[] poisIDs = poisResponse.split("\"id\":");
		     
		     for(int j = 1; j< poisIDs.length; j++){
		    	 idsPOIS.add(indexMin + "-" + poisIDs[j].split(",\"category\"")[0].toString());
    		   }
		     
		     String[] poisCategory = poisResponse.split("\"category\":");
		     for(int j = 1; j< poisCategory.length; j++){
		    	 categoryPOIS.add(poisCategory[j].split(",\"subcategory\"")[0].toString());
    		   }

		     String[] poisSubcategory = poisResponse.split("\"subcategory\":");
		     for(int j = 1; j< poisSubcategory.length; j++){
		    	 subcategoryPOIS.add(poisSubcategory[j].split(",\"username\"")[0].toString());
    		   }
		     
		     String[] lonPOIS = poisResponse.split("\"coordinates\"");
		     for(int j = 1; j< lonPOIS.length; j++){
		    	 longitudePOIS.add(lonPOIS[j].split(",")[0].toString().replace(":[", ""));
    		   }
		     
		     String[] latPOIS = poisResponse.split("\"coordinates\"");
		     for(int j = 1; j< latPOIS.length; j++){
		    	 latitudePOIS.add(latPOIS[j].split(",")[1].toString().replace(":[", "").replace("]}}", "").replace("]", ""));
    		   }
		     }
		     catch(Exception e){
	               System.err.println(e.getCause());
	          }
		     }

    	   
    	   //Find the relative pois
           List<String> relativePOI = new ArrayList<String>();
    	   for(int j = 0; j < idsPOIS.size(); j++) {
    		   double lat = 0;
    		   double lon = 0;
    		   String category = "";
    		   String subcategory = "";
    		   lat = Double.parseDouble(latitudePOIS.get(j).replace("}", ""));
    		   lon = Double.parseDouble(longitudePOIS.get(j).replace("}", ""));
    		   category = categoryPOIS.get(j).replace("\"", "");
    		   subcategory = subcategoryPOIS.get(j).replace("\"", "");
    		   if (Math.abs(latitude - Math.abs(lat)) < 0.05) {
    			   if (Math.abs(longitude - Math.abs(lon)) < 0.05) {
    				   if (category.equals(categoryConst)) {
    					   if (subcategory.split(",name")[0].equals(subcategoryConst)) {
    						   relativePOI.add(idsPOIS.get(j));
    					   }
    				   }
    			   }
    		   }
    	   }
    	   
    	   System.out.println(relativePOI.size());
    	   
    	   //some code for the creation of DSS information
    	   String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
           JSONObject jsonDSS = new JSONObject();
           jsonDSS.put("type", "Textual");
           jsonDSS.put("id", jobject.getAsJsonObject("meta").get("id").toString().replace("\"", ""));
           jsonDSS.put("Timestamp", timeStamp);
           jsonDSS.put("category", categoryConst);
           jsonDSS.put("subcategory", subcategoryConst);
           jsonDSS.put("location", json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("label").toString().replace("\"", ""));
           JSONArray coordinatesArrDSS = new JSONArray();
           coordinatesArrDSS.put(longitude);
           coordinatesArrDSS.put(latitude);
           jsonDSS.put("geometry",coordinatesArrDSS);
           JSONArray allAffected = new JSONArray();
           
           ArrayList<String> listsvd_ids = new ArrayList<String>();  
           for (int k=0;k < json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("affected_objects").getAsJsonArray().size();k++){ 
            listsvd_ids.add(json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("affected_objects").getAsJsonArray().get(k).toString());
           } 
           
           ArrayList<String> newList = new ArrayList<String>();
           for (int j = 0; j < json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("affected_objects").getAsJsonArray().size(); j++) {
               if (!newList.contains(json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("affected_objects").getAsJsonArray().get(j).toString())) {
                   newList.add(json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("affected_objects").getAsJsonArray().get(j).toString());
               }
           }
           
    	   for(int j = 0; j < newList.size(); j++) {
	           JSONObject affected = new JSONObject();
	           int occurrences = Collections.frequency(listsvd_ids, newList.get(j).toString());
	           
	           affected.put("Class", newList.get(j).toString().replace("\"", ""));
	           affected.put("No", occurrences);
	           allAffected.put(affected);
    	   }
    	   jsonDSS.put("affected_objects", allAffected);
    	   System.out.println(jsonDSS);
    	   
           
    	   try{
    		   
    		   ConnectionString connectionString = new ConnectionString("mongodb://xr4d_dss_user:x38vC&26@160.40.53.24:27017/?authSource=XR4D_DSS");
        	   MongoClientSettings settings = MongoClientSettings.builder()
        	           .applyConnectionString(connectionString)
        	           .build();
        	   MongoClient mongoClient = MongoClients.create(settings);
        	   MongoDatabase database = mongoClient.getDatabase("XR4D_DSS");
        	   MongoCollection<Document> toys = database.getCollection("test");
        	   Document doc = Document.parse(jsonDSS.toString());
        	   toys.insertOne(doc);
      
        	   
        	   LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        	   Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
        	   rootLogger.setLevel(Level.OFF);
        	   mongoClient.close();
        	 
       
           } catch(Exception e){
               System.err.println(e.getCause());
           }
           
    	   //ends find relative pois
    	   //relativePOI.clear();
    	   if (relativePOI.size() > 0) {
    		   //update pois

          for (int j = 0; j < relativePOI.size(); j++) {//change this later to relativePOI list
        	  	/*
    	           JSONObject commentInstance = new JSONObject();
    	           commentInstance.put("content", sourceText.toString().replace("\"", ""));
    			   String idPOI = "";
    			   System.out.println(commentInstance);
    			   idPOI = "https://geoservice.xr4drama.up2metric.com:8001/projects/" + relativePOI.get(j).split("-")[0].toString() + "/points-of-interest/" + relativePOI.get(j).split("-")[1].toString() + "/comments?current_user=" + user.replace("\"", "");
    			   System.out.println(idPOI);
    			   sendPostRequestRawStr(idPOI, commentInstance.toString(), "application/json");
    			 */
	           JSONObject attributeInstance = new JSONObject();
	           JSONObject attribute = new JSONObject();
	           attribute.put("label", json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("label").toString().replace("\"", ""));
	           attribute.put("affected_objects", json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("affected_objects").toString().replace("\"", ""));
	           attribute.put("description", sourceText.toString().replace("\"", ""));
	           
	           attributeInstance.put("attributes", attribute);
			   String idPOI = "";
			   System.out.println(attributeInstance);
			   idPOI = "https://geoservice.xr4drama.up2metric.com:8001/projects/" + relativePOI.get(j).split("-")[0].toString() + "/points-of-interest/" + relativePOI.get(j).split("-")[1].toString() + "?current_user=" + user.replace("\"", "");
			   System.out.println(idPOI);
			   sendPutRequestRawStr(idPOI, attributeInstance.toString(), "application/json");
        	  
        	  
    		   }
    	   }
    	   else {//create new pois
    		   //code for updating a poi
    		   
	           JSONObject jsonObject = new JSONObject();
	           JSONObject attributeObject = new JSONObject();
	           jsonObject.put("category",categoryConst);
	           jsonObject.put("subcategory", subcategoryConst);
	           
	           attributeObject.put("label", json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("label").toString().replace("\"", ""));
	           attributeObject.put("affected_objects", json_data.getAsJsonArray("situations").get(i).getAsJsonObject().get("affected_objects").toString().replace("\"", ""));
	           attributeObject.put("description", sourceText.toString().replace("\"", ""));
	           
	           jsonObject.put("current_user", user.replace("\"", ""));
	           JSONObject geometryObject = new JSONObject();
	           geometryObject.put("type","Point");
	           JSONArray coordinatesArr = new JSONArray();
	           coordinatesArr.put(longitude);//the above are the theoretically correct commands
	           coordinatesArr.put(latitude);
           
	           geometryObject.put("coordinates",coordinatesArr);
	           jsonObject.put("geometry",geometryObject);
	           
	           jsonObject.put("attributes",attributeObject);
	           
	           String jsonBody = jsonObject.toString();
	           System.out.println(jsonBody);
	           for (int t = 0; t < clusterIDs.size(); t++) {
	        	   String id = "";
	           	   int indexMinNew = 0;
	           	   indexMinNew = clusterIDs.get(t);
	           	   id = "https://geoservice.xr4drama.up2metric.com:8001/projects/" + indexMinNew + "/points-of-interest/";
	           	   sendPostRequestRawStr(id, jsonBody, "application/json");
	           	   }
    	   }
		   
           flag = 1;
           
       }}
      else {return "The instance did not contain location information";}
    	
    if (flag == 1) {return "The data was send";}
    else {return "The data was not send";}
    
    }
    
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
            
//            if(responseCode!=200 && responseCode!=409)
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
        
    public static boolean hasValue(JSONArray json, String key, String value) {
        for (int i = 0; i < json.length(); i++) { // iterate through the JsonArray
            // first I get the 'i' JsonElement as a JsonObject, then I get the key as a string and I compare it with the value
            if (json.getJSONObject(i).get(key).toString().equals(value)) return true;
        }
        return false;
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
  
    public static String queriesPOIvisual(String input) {
        Gson gson = new Gson();

        //code to create and send the json to the DSS
        JsonElement json = gson.fromJson(input, JsonElement.class);
        JsonObject jobject = json.getAsJsonObject();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
        JSONObject jsonDSS = new JSONObject();
        jsonDSS.put("type", "Visual");
        jsonDSS.put("Timestamp", timeStamp);
        jsonDSS.put("id", jobject.getAsJsonObject("header").get("project_id").toString().replace("\"", ""));
        
        JSONArray shotInfo = new JSONArray();
        
        String allImage = "";
        for (int i =0; i < jobject.getAsJsonArray("shotInfo").size(); i++) {
        	JSONObject shot = new JSONObject();
        	int shotID = Integer.parseInt(jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().get("shotIdx").toString());
        	double people = jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().get("peopleInDanger").getAsDouble();
        	double vehicle = jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().get("vehiclesInDanger").getAsDouble();
        	float probArea = Float.parseFloat(jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().get("areaProb").toString());
        	shot.put("shotIdx", shotID);
        	shot.put("peopleInDanger", people);
        	shot.put("vehiclesInDanger", vehicle);
        	shot.put("area", jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().get("area").toString().replace("\"", ""));
        	shot.put("areaProb", probArea);
        	JSONArray objectsAll = new JSONArray();
        	for (int j=0; j < jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().getAsJsonArray("objectsFound").size(); j++) {
        		JSONObject objectSolo = new JSONObject();
        		float probObject = Float.parseFloat(jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().getAsJsonArray("objectsFound").get(j).getAsJsonObject().get("probability").toString());
        		objectSolo.put("type", jobject.getAsJsonArray("shotInfo").get(i).getAsJsonObject().getAsJsonArray("objectsFound").get(j).getAsJsonObject().get("type").toString().replace("\"", ""));
        		objectSolo.put("probability", probObject);
        		objectsAll.put(objectSolo);
        	}
        	shot.put("objectsFound", objectsAll);
        	shotInfo.put(shot);
        	
        }
        jsonDSS.put("shotInfo", shotInfo);
               
 	   try{

		   ConnectionString connectionString = new ConnectionString("mongodb://xr4d_dss_user:x38vC&26@160.40.53.24:27017/?authSource=XR4D_DSS");
    	   MongoClientSettings settings = MongoClientSettings.builder()
    	           .applyConnectionString(connectionString)
    	           .build();
    	   MongoClient mongoClient = MongoClients.create(settings);
    	   MongoDatabase database = mongoClient.getDatabase("XR4D_DSS");
    	   MongoCollection<Document> toys = database.getCollection("test");
    	   Document doc = Document.parse(jsonDSS.toString());
    	   toys.insertOne(doc);
  
    	   
    	   LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    	   Logger rootLogger = loggerContext.getLogger("org.mongodb.driver");
    	   rootLogger.setLevel(Level.OFF);
    	   mongoClient.close();
    	 
   
       } catch(Exception e){
           System.err.println(e.getCause());
       }
 	   
 	  
 	   //code for poi creation or update
 	  
 	  int flag = 0;
   	  String user = "";
   	  user = jobject.getAsJsonObject("header").get("project_id").toString().replace("\"", "");
 	  for (int i =0; i < jobject.getAsJsonArray("shotInfo").size(); i++) {

   	   
   	   String response = "";
   	   response = sendGetRequestStr("https://geoservice.xr4drama.up2metric.com:8001/projects/");
   	   
   	   
   	   String[] parts = response.split("\"id\":");
   	   String[] latitudeList = response.split("\"latitude\":");
   	   String[] longitudeList = response.split("\"longitude\":");
   	   
   	   List<String> ids = new ArrayList<String>();
   	   for(int j = 1; j< parts.length; j++){
   		   ids.add(parts[j].split(",\"status\"")[0].toString());
   		   }
          
   	   List<String> latitudeListFinal = new ArrayList<String>();
   	   for(int j = 1; j < latitudeList.length; j++){
   		   latitudeListFinal.add(latitudeList[j].split(",\"longitude\":")[0].toString());
   		   }
   	   
   	   List<String> longitudeListFinal = new ArrayList<String>();
   	   for(int j = 1; j < longitudeList.length; j++){
   		   longitudeListFinal.add(longitudeList[j].split("},\"")[0].toString());
   		   }
   	   
   	   
   	      	   
   	   //create json for new pois
   	   	  double longitude = 0.0;
          double latitude = 0.0;
          double vehiclesInDanger = 0.0;
          double peopleInDanger = 0.0;
          String categoryConst = "";
          String subcategoryConst = "";
          String emergencyType = "";
          String outdoor = "";
          String animalsInDanger = "";
          String area = "";
          String riverOvertop = "";
          String infrastructure = "";
          String objectDanger = "";
          
          latitude = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("coordinate").getAsJsonArray().get(0).getAsDouble();
          longitude = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("coordinate").getAsJsonArray().get(1).getAsDouble();
          categoryConst = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("category").getAsString();
          subcategoryConst  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("subcategory").getAsString();
          outdoor  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("outdoor").getAsString();
          emergencyType  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("emergencyType").getAsString();
          peopleInDanger  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("peopleInDanger").getAsDouble();
          vehiclesInDanger  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("vehiclesInDanger").getAsDouble();
          animalsInDanger  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("animalsInDanger").getAsString();
          area  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("area").getAsString();
          riverOvertop  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("riverOvertop").getAsString();
          infrastructure  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("infraInDanger").getAsJsonArray().toString().replace("\"","");
          objectDanger  = jobject.get("shotInfo").getAsJsonArray().get(i).getAsJsonObject().get("objectsInDanger").getAsJsonArray().toString().replace("\"","");
          
 	  
	       //end here code for new pois json
	          
	       //procedure to find project ids
	   	   List<Double> longitudeSE = new ArrayList<Double>();
	   	   for(int j = 1; j < longitudeListFinal.size();  j = j + 2){
	   		   double helper = 0;
	   		   helper = Float.valueOf(longitudeListFinal.get(j));
	   		   longitudeSE.add(helper);
	   		   }
	   	   
	   	   List<Double> longitudeNW = new ArrayList<Double>();
	   	   for(int j = 0; j < longitudeListFinal.size();  j = j + 2){
	   		   double helper = 0;
	   		   helper = Float.valueOf(longitudeListFinal.get(j));
	   		   longitudeNW.add(helper);
	   		   }
	   	   
	   	   List<Double> latitudeSE = new ArrayList<Double>();
	   	   for(int j = 1; j < latitudeListFinal.size();  j = j + 2){
	   		   double helper = 0;
	   		   helper = Float.valueOf(latitudeListFinal.get(j));
	   		   latitudeSE.add(helper);
	   		   }
	   	   
	   	   List<Double> latitudeNW = new ArrayList<Double>();
	   	   for(int j = 0; j < latitudeListFinal.size();  j = j + 2){
	   		   double helper = 0;
	   		   helper = Float.valueOf(latitudeListFinal.get(j));
	   		   latitudeNW.add(helper);
	   		   } 
	   	  
	          
	          List<Integer> clusterIDs = new ArrayList<Integer>();
	   	   for(int j = 0; j < longitudeSE.size(); j++) {
	   		   double nwLat = 0;
	   		   double seLat = 0;
	   		   double nwLon = 0;
	   		   double seLon = 0;
	   		   nwLat = latitudeNW.get(j);
	   		   seLat = latitudeSE.get(j);
	   		   nwLon = longitudeNW.get(j);
	   		   seLon = longitudeSE.get(j);
	   		   if( seLat <= latitude && latitude <= nwLat && nwLon <= longitude && longitude <= seLon ) {
	   			   clusterIDs.add(Integer.valueOf(ids.get(j)));
	   			}
	   	   }//for
	   //end finding project ids
   	   //find pois based on project ids
   	   List<String> idsPOIS = new ArrayList<String>();
   	   List<String> categoryPOIS = new ArrayList<String>();
   	   List<String> subcategoryPOIS = new ArrayList<String>();
   	   List<String> latitudePOIS = new ArrayList<String>();
   	   List<String> longitudePOIS = new ArrayList<String>();
   	   
   	   
   	   for (int t = 0; t < clusterIDs.size(); t++) {
   		   	 String poisResponse = "";
		     int indexMin = 0;
		     indexMin = clusterIDs.get(t);
		     poisResponse = sendGetRequestStr("https://geoservice.xr4drama.up2metric.com:8001/projects/" + indexMin + "/points-of-interest?current_user=%22" + user.replace("\"", "") + "%22&order=ASC&as_point=true");
		     
		     try{
		     String[] poisIDs = poisResponse.split("\"id\":");
		     
		     for(int j = 1; j< poisIDs.length; j++){
		    	 idsPOIS.add(indexMin + "-" + poisIDs[j].split(",\"category\"")[0].toString());
   		   }
		     
		     String[] poisCategory = poisResponse.split("\"category\":");
		     for(int j = 1; j< poisCategory.length; j++){
		    	 categoryPOIS.add(poisCategory[j].split(",\"subcategory\"")[0].toString());
   		   }

		     String[] poisSubcategory = poisResponse.split("\"subcategory\":");
		     for(int j = 1; j< poisSubcategory.length; j++){
		    	 subcategoryPOIS.add(poisSubcategory[j].split(",\"username\"")[0].toString());
   		   }
		     
		     String[] lonPOIS = poisResponse.split("\"coordinates\"");
		     for(int j = 1; j< lonPOIS.length; j++){
		    	 longitudePOIS.add(lonPOIS[j].split(",")[0].toString().replace(":[", ""));
   		   }
		     
		     String[] latPOIS = poisResponse.split("\"coordinates\"");
		     for(int j = 1; j< latPOIS.length; j++){
		    	 latitudePOIS.add(latPOIS[j].split(",")[1].toString().replace(":[", "").replace("]}}", "").replace("]", ""));
   		   }
		     }
		     catch(Exception e){
	               System.err.println(e.getCause());
	          }
		     }

	   //Find the relative pois
       List<String> relativePOI = new ArrayList<String>();
	   for(int j = 0; j < idsPOIS.size(); j++) {
		   double lat = 0;
		   double lon = 0;
		   String category = "";
		   String subcategory = "";
		   lat = Double.parseDouble(latitudePOIS.get(j).replace("}", ""));
		   lon = Double.parseDouble(longitudePOIS.get(j).replace("}", ""));
		   category = categoryPOIS.get(j).replace("\"", "");
		   subcategory = subcategoryPOIS.get(j).replace("\"", "");
		   if (Math.abs(latitude - Math.abs(lat)) < 0.05) {
			   if (Math.abs(longitude - Math.abs(lon)) < 0.05) {
				   if (category.equals(categoryConst)) {
					   if (subcategory.split(",name")[0].equals(subcategoryConst)) {
						   relativePOI.add(idsPOIS.get(j));
					   }
				   }
			   }
		   }
	   }
	   
	   System.out.println(relativePOI.size());
          
	   //ends find relative pois
	   //relativePOI.clear();
	   String imagePOI = "";
	   if (relativePOI.size() > 0) {
		   List<String> allIDS = new ArrayList<String>();
		   for (int j = 0; j < relativePOI.size(); j++) {//change this later to relativePOI list
	    	   
	           JSONObject attributeInstance = new JSONObject();
	           JSONObject attribute = new JSONObject();
	           String pep =String.valueOf(peopleInDanger); 
	           attribute.put("peopleInDanger", pep);
	           String veh =String.valueOf(vehiclesInDanger); 
	           attribute.put("vehiclesInDanger", veh);
	           attribute.put("animalsInDanger", animalsInDanger);
	           attribute.put("outdoor", outdoor);
	           attribute.put("infraInDanger", infrastructure);
	           attribute.put("objectsInDanger", objectDanger);
	           attribute.put("riverOvertop", riverOvertop);
	           attribute.put("area", area);
	           attribute.put("emergencyType", emergencyType);

	           attributeInstance.put("attributes", attribute);
			   String idPOI = "";
			   System.out.println(attributeInstance);
			   idPOI = "https://geoservice.xr4drama.up2metric.com:8001/projects/" + relativePOI.get(j).split("-")[0].toString() + "/points-of-interest/" + relativePOI.get(j).split("-")[1].toString() + "?current_user=" + user.replace("\"", "");
			   sendPutRequestRawStr(idPOI, attributeInstance.toString(), "application/json");
			   allIDS.add(relativePOI.get(j).split("-")[1].toString());
			  
		   }
		   imagePOI = allIDS.toString();		   
	   }
	   else {//create new pois
		   //code for updating a poi
		   
           JSONObject jsonObject = new JSONObject();
           jsonObject.put("category",categoryConst);
           jsonObject.put("subcategory", subcategoryConst);
           jsonObject.put("current_user", user.replace("\"", ""));
           JSONObject geometryObject = new JSONObject();
           geometryObject.put("type","Point");
           JSONArray coordinatesArr = new JSONArray();
           coordinatesArr.put(longitude);//the above are the theoretically correct commands
           coordinatesArr.put(latitude);
       
           geometryObject.put("coordinates",coordinatesArr);
           jsonObject.put("geometry",geometryObject);
           //visual extra
           JSONObject jsonAttribute = new JSONObject();
           String pep =String.valueOf(peopleInDanger);
           String veh =String.valueOf(vehiclesInDanger);
           jsonAttribute.put("peopleInDanger", pep);
           jsonAttribute.put("vehiclesInDanger", veh);
           jsonAttribute.put("animalsInDanger", animalsInDanger);
           jsonAttribute.put("outdoor", outdoor);
           jsonAttribute.put("infraInDanger", infrastructure);
           jsonAttribute.put("objectsInDanger", objectDanger);
           jsonAttribute.put("riverOvertop", riverOvertop);
           jsonAttribute.put("area", area);
           jsonAttribute.put("emergencyType", emergencyType);
           
           jsonObject.put("attributes",jsonAttribute);
           //
           
           String jsonBody = jsonObject.toString();
           
           List<String> allIDS = new ArrayList<String>();
           for (int t = 0; t < clusterIDs.size(); t++) {
        	   String id = "";
           	   int indexMinNew = 0;
           	   indexMinNew = clusterIDs.get(t);
           	   id = "https://geoservice.xr4drama.up2metric.com:8001/projects/" + indexMinNew + "/points-of-interest/";
           	   response = sendPostRequestRawStr(id, jsonBody, "application/json");
           	  
           	   if(response.toString().contains("id")) {
           		   
           		String[] imageIDs = response.split("\"id\":");
           		String[] finalID = imageIDs[1].split(",");
           		allIDS.add(finalID[0]);
           	   }
           	   }
           
          imagePOI = allIDS.toString();
	   }
	   
	   
       flag = 1;
	   allImage = allImage + imagePOI;
	   
 	  }//end big for
 	  	
 	   	  	
 	    if (flag == 1) {return "The POIs id for you message are: " + allImage.replace("[]", "").replace("]", ", ").replace("[", "");}
 	    else {return "The data was not send, so no POIs  were created";}
    }
  
    public static String queriesDSS(String input)
    {	Gson gson = new Gson();
        //code to create and send the json to the DSS
        JsonElement json = gson.fromJson(input, JsonElement.class);
        JsonObject jobject = json.getAsJsonObject();
        //System.out.println(jobject);
        
        for (int i =0; i < jobject.getAsJsonArray("data").size(); i++) {
        	double latitude = jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("latitude").getAsDouble();
        	double longitude = jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("longitude").getAsDouble();
   
    	   String response = "";
       	   response = sendGetRequestStr("https://geoservice.xr4drama.up2metric.com:8001/projects/");
       	   
       	   String[] parts = response.split("\"id\":");
       	   String[] latitudeList = response.split("\"latitude\":");
       	   String[] longitudeList = response.split("\"longitude\":");
       	   
       	   List<String> ids = new ArrayList<String>();
       	   for(int j = 1; j< parts.length; j++){
       		   ids.add(parts[j].split(",\"status\"")[0].toString());
       		   }
              
       	   List<String> latitudeListFinal = new ArrayList<String>();
       	   for(int j = 1; j < latitudeList.length; j++){
       		   latitudeListFinal.add(latitudeList[j].split(",\"longitude\":")[0].toString());
       		   }
       	   
       	   List<String> longitudeListFinal = new ArrayList<String>();
       	   for(int j = 1; j < longitudeList.length; j++){
       		   longitudeListFinal.add(longitudeList[j].split("},\"")[0].toString());
       		   }

        	
 	       //procedure to find project ids
 	   	   List<Double> longitudeSE = new ArrayList<Double>();
 	   	   for(int j = 1; j < longitudeListFinal.size();  j = j + 2){
 	   		   double helper = 0;
 	   		   helper = Float.valueOf(longitudeListFinal.get(j));
 	   		   longitudeSE.add(helper);
 	   		   }
 	   	   
 	   	   List<Double> longitudeNW = new ArrayList<Double>();
 	   	   for(int j = 0; j < longitudeListFinal.size();  j = j + 2){
 	   		   double helper = 0;
 	   		   helper = Float.valueOf(longitudeListFinal.get(j));
 	   		   longitudeNW.add(helper);
 	   		   }
 	   	   
 	   	   List<Double> latitudeSE = new ArrayList<Double>();
 	   	   for(int j = 1; j < latitudeListFinal.size();  j = j + 2){
 	   		   double helper = 0;
 	   		   helper = Float.valueOf(latitudeListFinal.get(j));
 	   		   latitudeSE.add(helper);
 	   		   }
 	   	   
 	   	   List<Double> latitudeNW = new ArrayList<Double>();
 	   	   for(int j = 0; j < latitudeListFinal.size();  j = j + 2){
 	   		   double helper = 0;
 	   		   helper = Float.valueOf(latitudeListFinal.get(j));
 	   		   latitudeNW.add(helper);
 	   		   } 
 	   	  
 	          
 	       List<Integer> clusterIDs = new ArrayList<Integer>();
 	   	   for(int j = 0; j < longitudeSE.size(); j++) {
 	   		   double nwLat = 0;
 	   		   double seLat = 0;
 	   		   double nwLon = 0;
 	   		   double seLon = 0;
 	   		   nwLat = latitudeNW.get(j);
 	   		   seLat = latitudeSE.get(j);
 	   		   nwLon = longitudeNW.get(j);
 	   		   seLon = longitudeSE.get(j);
 	   		   //System.out.println(seLat);
 	   		   //System.out.println(latitude);
 	   		   //System.out.println(nwLat);
 	   		   //System.out.println("//////////////////////");
 	   		   //System.out.println(nwLon);
 	   		   //System.out.println(longitude);
 	   		   //System.out.println(seLon);
 	   		   //System.out.println("/////////NEW//////////");
 	   		   if( seLat <= latitude && latitude <= nwLat && nwLon <= longitude && longitude <= seLon ) {
 	   			   clusterIDs.add(Integer.valueOf(ids.get(j)));
 	   			}
 	   	   }//for
 	   	   //end finding project ids
           System.out.println("Number of Projects: " + clusterIDs.size());
           
       	   List<String> latitudePOIS = new ArrayList<String>();
       	   List<String> longitudePOIS = new ArrayList<String>();
       	   List<String> idsPOIS = new ArrayList<String>();
           //start finding related pois
           if (clusterIDs.size() > 0) {
           	for (int p=0; p < clusterIDs.size(); p++) {
           		String poisResponse = "";
           		System.out.println("Project: " + clusterIDs.get(p));
           		int helperIndex = 0;
           		helperIndex = clusterIDs.get(p);
           		poisResponse = sendGetRequestStr("https://geoservice.xr4drama.up2metric.com:8001/projects/" + helperIndex + "/points-of-interest?current_user=%22KB%22&order=ASC&as_point=true");
           		//here I need some changes based on the user
   		     try{
   			     String[] poisIDs = poisResponse.split("\"id\":");
   			     
   			     for(int j = 1; j< poisIDs.length; j++){
   			    	 idsPOIS.add(helperIndex + "-" + poisIDs[j].split(",\"category\"")[0].toString());
   	   		   }
   			     
   			     
   			     String[] lonPOIS = poisResponse.split("\"coordinates\"");
   			     for(int j = 1; j< lonPOIS.length; j++){
   			    	 longitudePOIS.add(lonPOIS[j].split(",")[0].toString().replace(":[", ""));
   	   		   }
   			     
   			     String[] latPOIS = poisResponse.split("\"coordinates\"");
   			     for(int j = 1; j< latPOIS.length; j++){
   			    	 latitudePOIS.add(latPOIS[j].split(",")[1].toString().replace(":[", "").replace("]}}", "").replace("]", ""));
   	   		   }
   			     }
   			     catch(Exception e){
   		               System.err.println(e.getCause());
   		          }
   		     

   	       List<String> relativePOI = new ArrayList<String>();
   		   for(int j = 0; j < idsPOIS.size(); j++) {
   			   double lat = 0;
   			   double lon = 0;
   			   String category = "";
   			   String subcategory = "";
   			   lat = Double.parseDouble(latitudePOIS.get(j).replace("}", ""));
   			   lon = Double.parseDouble(longitudePOIS.get(j).replace("}", ""));
   			   if (Math.abs(latitude - Math.abs(lat)) < 0.001) {
   				   if (Math.abs(longitude - Math.abs(lon)) < 0.001) {
   					   
   					   relativePOI.add(idsPOIS.get(j));   
   				   }
   			   }
   		   }
   		   
   		   System.out.println("Number of Relative POI: " + relativePOI.size());
   		   
   		   
   		   for (int t=0; t<relativePOI.size(); t++) {
   	           JSONObject jsonDSS = new JSONObject();
   	           jsonDSS.put("type","editPOI");
   	           jsonDSS.put("attached_poi_id", relativePOI.get(t).split("-")[1]);
   	           jsonDSS.put("headline", jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("name").toString().replace("\"", ""));
   	           jsonDSS.put("criticity", jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("criticity").toString().replace("\"", ""));
   	           jsonDSS.put("description", jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("action").toString().replace("\"", ""));
   	           jsonDSS.put("creator", jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("responsibility").toString().replace("\"", ""));//it may need changes
   	           jsonDSS.put("worker", jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("responsibility").toString().replace("\"", ""));//it may need changes
   	           //jsonDSS.put("creator", jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("KB").toString().replace("\"", ""));//it may need changes
   	           //jsonDSS.put("worker", jobject.getAsJsonArray("data").get(i).getAsJsonObject().get("KB").toString().replace("\"", ""));//it may need changes
   	           String id = "";
   	           id = "https://geoservice.xr4drama.up2metric.com:8001/projects/" + relativePOI.get(t).split("-")[0].replace("\"", "") + "/tasks";
   	           String jsonBody = jsonDSS.toString();
   	           
   	           sendPostRequestRawStr(id, jsonBody, "application/json");
   	           break;

   	           
   		   }
   		   
   		   
          }
         }
           //end finding related pois
 
        }
      
    	return "The data was send";
    }
    
}
