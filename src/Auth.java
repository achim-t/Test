import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Auth {

	private static final String REDIRECT_URI = "http://localhost:8080/Test/callback";
	private static final String CLIENT_SECRET = "qq14xBzl0IjKmzKVI2HWUrT0";
	private static final String CLIENT_ID = "331645637746-72r1bogrn14ud43p02cqhoq53vopi9ft.apps.googleusercontent.com";
	private static final String URL = "https://accounts.google.com/o/oauth2/token";

	public static JSONObject makeApiCall(HttpSession session, String url) throws ClientProtocolException, IOException {

		HttpGet httpGet = new HttpGet(url);
		String token = (String) session.getAttribute("access_token");
		httpGet.addHeader("Authorization", "Bearer " + token);
		return makeHttpRequest(httpGet);
	}

	private static JSONObject makeHttpRequest(HttpRequestBase httpRequest)
			throws IOException, ClientProtocolException {
		CloseableHttpResponse httpResponse = HttpClients.createDefault()
				.execute(httpRequest);
		HttpEntity entity = httpResponse.getEntity();
		JSONTokener jsonTokener = new JSONTokener(entity.getContent());
		JSONObject jsonResponse = new JSONObject(jsonTokener);

		return jsonResponse;
	}

	public static void authorize(HttpServletResponse response)
			throws IOException {
		URI uri;
		try {
			uri = new URIBuilder()
					.setScheme("https")
					.setHost("accounts.google.com")
					.setPath("/o/oauth2/auth")
					.setParameter("response_type", "code")
					.setParameter("client_id", CLIENT_ID)

					.setParameter("redirect_uri", REDIRECT_URI)
					.setParameter("scope",
							"https://www.googleapis.com/auth/youtube.readonly")
					.setParameter("access_type", "offline")
					.setParameter("approval_prompt", "force").build();
			response.sendRedirect(uri.toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void aquireAccessTokenFromCode(HttpSession session,
			String code)
			// TODO save the tokens
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("code", code));
		params.add(new BasicNameValuePair("client_id", CLIENT_ID));
		params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
		params.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
		params.add(new BasicNameValuePair("grant_type", "authorization_code"));
		JSONObject jsonResponse = getAccessTokenAsJSON(params);

		String access_token = jsonResponse.getString("access_token");
		String refresh_token = jsonResponse.getString("refresh_token");
		session.setAttribute("access_token", access_token);
		session.setAttribute("refresh_token", refresh_token);
	}

	public static void refreshAccessToken(HttpSession session,
			String refresh_token) throws UnsupportedEncodingException,
			IOException, ClientProtocolException {
		List<NameValuePair> params;
		params = new ArrayList<>();
		params.add(new BasicNameValuePair("client_id", CLIENT_ID));
		params.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
		params.add(new BasicNameValuePair("refresh_token", refresh_token));
		params.add(new BasicNameValuePair("grant_type", "refresh_token"));

		JSONObject jsonResponse = getAccessTokenAsJSON(params);
		String access_token = jsonResponse.getString("access_token");
		session.setAttribute("access_token", access_token);
	}

	private static JSONObject getAccessTokenAsJSON(List<NameValuePair> params)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
		HttpPost httpPost = new HttpPost(URL);
		httpPost.setEntity(new UrlEncodedFormEntity(params));

		return makeHttpRequest(httpPost);
	}
}
