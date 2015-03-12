import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Subscription;
import com.google.api.services.youtube.model.SubscriptionListResponse;

/**
 * Servlet implementation class Home
 */
@WebServlet("/home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String userId = request.getSession().getId();
		Credential credential = Auth.getCredential(userId);
		if (credential == null) {
			String url = Auth.getAuthorizationUrl();
			response.sendRedirect(url);
			return;
		}
		YouTube youtube = new YouTube.Builder(Auth.HTTP_TRANSPORT,
				Auth.JSON_FACTORY, credential).build();
		SubscriptionListResponse subscriptionListResponse = null;
		try {
			subscriptionListResponse = youtube.subscriptions().list("snippet")
					.setMine(true).execute();
		} catch (TokenResponseException | GoogleJsonResponseException e) {
			// TODO Auto-generated catch block
			System.out.println(e);
			Auth.deleteUserFromCredentialDataStore(userId);
			response.sendRedirect("/Test/home");
			return;
		}
		for (Subscription sub : subscriptionListResponse.getItems()) {
			String id = sub.getSnippet().getResourceId().getChannelId();
			System.out.println(id);

		}
		response.getWriter().print(subscriptionListResponse.toPrettyString());
		
	}

}
