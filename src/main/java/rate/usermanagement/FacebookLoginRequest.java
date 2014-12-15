package rate.usermanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;

public class FacebookLoginRequest {
	static String appid = "732083976882116";
	static String appsecret = "eacc38539438a41c127ec7e1c6662994";
	static String authUrl = "https://www.facebook.com/dialog/oauth";
	//static String redirectUrl = "http://localhost:8080/welcome.jsp";
	//static String redirectUrl = "http://lyumingrui1.appspot.com/rest/facebook/login";
	static String redirectUrl = "http://localhost:8080/rest/facebook/login";
	static String tokenUrl = "https://graph.facebook.com/oauth/access_token";
	static String permission = "email,user_friends,user_birthday,user_hometown";
	static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();	
	
	public static String getFacebookAuthURL() {
		return authUrl + "?client_id=" + appid + "&redirect_uri=" + redirectUrl
				+ "&scope=" + permission + "&auth_type=reauthenticate";
	}

	static public String getFacebookAccessToken(String faceCode)
			throws IOException {
		String token = null;
		if (faceCode != null && !"".equals(faceCode)) {
			String newUrl = tokenUrl + "?client_id=" + appid + "&redirect_uri="
					+ redirectUrl + "&client_secret=" + appsecret + "&code=" + faceCode;
			GenericUrl redirecturl = new GenericUrl(newUrl);
			HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
			HttpRequest request = requestFactory.buildGetRequest(redirecturl);
			//String index = request.execute().parseAsString();
			HttpResponse response = request.execute();
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null)
					result.append(line);
			String temptoken = StringUtils.removeStart(result.toString(), "access_token=");
			token = temptoken.substring(0, temptoken.indexOf('&'));
		}
		return token;
	}
}
