package com.carrerup.pratice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author guyu
 */
public class TestPost {
	private URL url;

	private URLConnection conn;

	public TestPost() {
	}

	public void setURL(String urlAddr) {

		try {
			url = new URL(urlAddr);
			conn = url.openConnection();

			// conn.setRequestProperty("Pragma:", "no-cache");
			// conn.setRequestProperty("Cache-Control", "no-cache");

		} catch (MalformedURLException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void sendPost(String post) {
		conn.setDoInput(true);
		conn.setDoOutput(true);
		PrintWriter output;
		try {
			output = new PrintWriter(conn.getOutputStream());
			output.print(post);
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String getContent() {
		String line, result = "";
		try {
			conn.connect();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((line = in.readLine()) != null) {
				result += line + "\n";
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {
		// String urlAddr = "http://42.96.186.145:8090/share/ugc/share/add";
		// String post =
		// "userId=33&token=f53a9360b03b033306cf899d3590c072&opinion=test&type=1";

		String urlAddr = "http://42.96.186.145:8090/share/ugc/comment/add";
		String post = "userId=33&token=f53a9360b03b033306cf899d3590c072&content=testcomment&shareId=11&resourceId=11&resourceOwner=33";

		TestPost test = new TestPost();

		test.setURL(urlAddr);
		test.sendPost(post);
		System.out.println(test.getContent());
	}
}