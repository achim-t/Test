import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

@WebServlet("/callback")
public class Callback extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("code");
		HttpSession session = request.getSession();
		String userId = session.getId();
		GoogleAuthorizationCodeTokenRequest tokenRequest = Auth.getFlow().newTokenRequest(code);
		GoogleTokenResponse tokenResponse = tokenRequest.setRedirectUri(Auth.REDIRECT_URI).execute();
		Auth.getFlow().createAndStoreCredential(tokenResponse, userId);
		response.sendRedirect("./home");
	}
}
