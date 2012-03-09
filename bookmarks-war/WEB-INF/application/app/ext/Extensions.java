package ext;

import play.templates.JavaExtensions;
import java.text.*;
import java.util.*;
import java.lang.*;
import java.net.*;

public class Extensions extends JavaExtensions {

public static String domain(String urlAddress) {
		String domain = null;
		try {
			URL url = new URL(urlAddress);
			domain = url.getHost();

		}catch(Exception e) {
		}
		return domain;
  }
}
