package com.app.android.hashmap.util;
/**
 * JSON Parser for results for # search term suggestions
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class AutoCompleteUtil {
	
	static public class AutoCompletePullParser extends DefaultHandler
	{
		ArrayList<String> autoList;
				
		public static ArrayList<String> parseSearch(InputStream in) throws XmlPullParserException, IOException
		{
			XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
			parser.setInput(in, "UTF-8");
			ArrayList<String> autoList = new ArrayList<String>();
			int event  = parser.getEventType();
			String data;
			
			while(event != XmlPullParser.END_DOCUMENT)
			{
				
				switch (event) {
				case XmlPullParser.START_TAG:
					if(parser.getName().equals("suggestion"))
					{
						data = parser.getAttributeValue(null, "data");
						if(data.substring(0, 1).equals("#"))
						{
							autoList.add(data.substring(1));
						}
						else
						{
							break;
						}
					}
					
							
				}
				
				event = parser.next();
			}
			
			
			
			
			return autoList;
		}

		
		
	}

}
