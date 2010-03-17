package no.ctryti.dagensatuio;

import java.net.URI;
import java.net.URISyntaxException;

import android.util.Log;

public abstract class Settings {
	
	private Settings() {} /* Prevent instantiation */
	
	public static final String TAG = "DagensAtUiO";
	
	public enum Place {
		/*
		Need to write a different parser for these. They have DAGENS and VEGETAR.
		OLE ("Kafe Ole","http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/kafe+ole"),
		MHS ("Musikkhøgskolens kafe","http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/musikkhogskolens+kafe+ny"),
		*/
		AHO        ("AHO-kafeen",                  "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/aho"), 
		FORSKNINGSV("Forskningsveien",             "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/forskningsveien"), 
		FREDERIKKE ("Frederikke kafé",             "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/frederikke+kafe"), 
		IFI        ("Informatikkafeen",            "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/informatikkafeen+ny"), 
		HELGA      ("Kafe Helga",                  "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/kafe+helga"), 
		NIH        ("Norges idrettshøgskoles kafe","http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/nih"),
		NOVA       ("Kafe Nova",                   "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/kafe+nova"), 
		NVH        ("Norges veterinørhøgskole",    "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/veterinerhogskolen"),
		SEILDUKEN  ("Kafe Seilduken",              "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/kafe+seilduken"), 
		SV         ("SV Kafeen",                   "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/sv+kafeen+ny"),
		ODONTOLOGI ("Odontologikafeen",            "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/odontologikafeen"), 
		PREKLINISK ("Preklinisk kafe",             "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/preklinisk+kafe"), 
		;

		private final String name;
		private final URI uri;

		Place(String name, String uri) {
			URI tmp = null;
			try {
				tmp = new URI(uri);
			} catch (URISyntaxException e) {
				Log.e(TAG, "Could not create URI: " + uri);
			}
			this.uri = tmp;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public URI getURI() {
			return uri;
		}
	}
}
