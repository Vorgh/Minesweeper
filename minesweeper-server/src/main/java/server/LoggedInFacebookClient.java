package server;

import com.restfb.DefaultFacebookClient;
import com.restfb.Version;

/**
 * Helper class for the restFB {@link DefaultFacebookClient}. Without this, the client has to be created twice, once for
 * obtaining the access token, and once for setting it (through the constructor).
 * 
 * @author Eperjesi Ádám
 *
 */
public class LoggedInFacebookClient extends DefaultFacebookClient
{
	public LoggedInFacebookClient(String appId, String appSecret, String redirectURI, String verificationCode, Version version)
	{
		super(version);
		AccessToken accessToken = this.obtainUserAccessToken(appId, appSecret, redirectURI, verificationCode);
		this.accessToken = accessToken.getAccessToken();
		this.apiVersion = version;
	}

}
