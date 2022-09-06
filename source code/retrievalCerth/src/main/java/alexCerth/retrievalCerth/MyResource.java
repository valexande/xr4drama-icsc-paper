
package alexCerth.retrievalCerth;

import java.io.IOException;
import java.text.ParseException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;

@Path("/{type}")
public class MyResource {
    
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response retrieval(@PathParam("type") String type, String object) throws JSONException, IOException, ParseException {
		if (type.equals("text-generation")) {
			return Response.status(200).entity(Retrieve.queriesExecution(object).toString()).build();
		}
		else if(type.equals("backend-api")) {
			return Response.status(200).entity(Retrieve.queriesExecutionForBackend(object).toString()).build();
		}
		else if(type.equals("poiText")) {
			return Response.status(200).entity(Retrieve.queriesPOItext(object).toString()).build();
		}
		else if(type.equals("poiVisual")) {
			return Response.status(200).entity(Retrieve.queriesPOIvisual(object).toString()).build();
		}
		else if(type.equals("dss")) {
			return Response.status(200).entity(Retrieve.queriesDSS(object).toString()).build();
		}
		return Response.status(200).entity("Unexpected parameter {type}").build();
    }
}

