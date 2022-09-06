
package alexCerthRest.rest_apiCerth;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;



import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
public class SecurtiyFilter implements ContainerRequestFilter {
	
	private static final String AUTHORIZATION_HEADER_KEY="Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX= "Basic ";
	private static final String SECURED_URL_PREFIX= "secured";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// TODO Auto-generated method stub
		if (requestContext.getUriInfo().getPath().contains(SECURED_URL_PREFIX)) {
			List<String> authHeader=requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
			if (authHeader !=null && authHeader.size()>0) {
				String authToken = authHeader.get(0);
				authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
				byte[] decodedBytes = Base64.getDecoder().decode(authToken);
				String decodedString = new String(decodedBytes);
				StringTokenizer tokenizer = new StringTokenizer(decodedString, ":");
				String username = tokenizer.nextToken();
				String password = tokenizer.nextToken();
				
				
				if ("admin".equals(username) && "12345a".equals(password)) {
					return;
				}
		}
		
			Response unauthirizedStatus= Response
										.status(Response.Status.UNAUTHORIZED)
										.entity("user cannot access")
										.build();
			requestContext.abortWith(unauthirizedStatus);
	} 
  }
}
