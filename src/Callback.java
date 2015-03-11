import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/callback")
public class Callback extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String code = request.getParameter("code");
		HttpSession session = request.getSession();

		Auth.aquireAccessTokenFromCode(session, code);
		String access_token = (String) session.getAttribute("access_token");
		System.out.println("1st Access Token: " + access_token);
		String refresh_token = (String) session.getAttribute("refresh_token");
		System.out.println("Refresh Token: " + refresh_token);
		Auth.refreshAccessToken(session, refresh_token);
		String access_token_2 = (String) session.getAttribute("access_token");
		System.out.println("2nd Access Token: " + access_token_2);

		response.sendRedirect("./home");

	}

}
