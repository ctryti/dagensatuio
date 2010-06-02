package no.ctryti.dagensatuio;

import java.net.URI;
import java.net.URISyntaxException;

import android.util.Log;

public abstract class Settings {
	
	private static String[] places;
	
	private Settings() {} /* Prevent instantiation */
	
	public static final String TAG = "DagensAtUiO";
	
	public enum Place {
		AHO        ("AHO-kafeen",                  "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/aho"), 
		FORSKNINGSV("Forskningsveien",             "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/forskningsveien"), 
		FREDERIKKE ("Frederikke kaf\u00e9",             "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/frederikke+kafe"), 
		IFI        ("Informatikkafeen",            "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/informatikkafeen+ny"), 
		HELGA      ("Kafe Helga",                  "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/kafe+helga"), 
		NIH        ("Norges idrettsh\u00f8gskoles kafe","http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/nih"),
		NVH        ("Norges veterin\u00e6rh\u00f8gskole",    "http://www.sio.no/wps/wcm/connect/migration/sio/mat+og+drikke/dagens+middag/veterinerhogskolen"),
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
		
		static public String[] getPlaces() {
			if(places == null) {
				places = new String[Place.values().length];
				int i = 0;
				for(Place p : Place.values()) {
					places[i++] = p.getName();
				}
			}
			return places;
		}

		public String getName() {
			return name;
		}

		public URI getURI() {
			return uri;
		}
	}
}
