package common.http.sub;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import common.bean.HtmlInfo;
import common.http.SimpleHttpProcess;

public class GoogleHttpProcess extends SimpleHttpProcess {

	public HttpClient httpClient(HtmlInfo html) {
		String key = html.getSite();
		if(clientMap.contains(key)) {
			return clientMap.get(key);
		} else {
			HttpParams params = httpParams(html.getAgent());
			
			HttpClient client = new DefaultHttpClient(new ThreadSafeClientConnManager(), params);
			client = tlsClient(client);
			clientMap.putIfAbsent(key, client);
			return client;
		}
	}
	
}
