package com.example.AndroidNearestStreet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.openstreetmap.osmosis.core.OsmosisRuntimeException;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.xml.common.CompressionActivator;
import org.openstreetmap.osmosis.core.xml.common.CompressionMethod;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import android.util.Log;



/**
 * An OSM data source reading from an xml file. The entire contents of the file
 * are read.
 * 
 * @author Brett Henderson
 */
public class XmlReader implements RunnableSource {
	
	private static Logger log = Logger.getLogger(XmlReader.class.getName());
	
	private Sink sink;
	private File file;
	private boolean enableDateParsing;
	private CompressionMethod compressionMethod;
	
	
	/**
	 * Creates a new instance.
	 * 
	 * @param file
	 *            The file to read.
	 * @param enableDateParsing
	 *            If true, dates will be parsed from xml data, else the current
	 *            date will be used thus saving parsing time.
	 * @param compressionMethod
	 *            Specifies the compression method to employ.
	 */
	public XmlReader(File file, boolean enableDateParsing, CompressionMethod compressionMethod) {
		this.file = file;
		this.enableDateParsing = enableDateParsing;
		this.compressionMethod = compressionMethod;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	public void setSink(Sink sink) {
		this.sink = sink;
	}
	
	
	/**
	 * Creates a new SAX parser.
	 * 
	 * @return The newly created SAX parser.
	 */
	private SAXParser createParser() {
		try {
			return SAXParserFactory.newInstance().newSAXParser();
			
		} catch (ParserConfigurationException e) {
			throw new OsmosisRuntimeException("Unable to create SAX Parser.", e);
		} catch (SAXException e) {
			throw new OsmosisRuntimeException("Unable to create SAX Parser.", e);
		}
	}
	
	
	/**
	 * Reads all data from the file and send it to the sink.
	 */
	public void run() {
		InputStream inputStream = null;
		
		try {
			SAXParser parser;
			
			// make "-" an alias for /dev/stdin
			if(file.getName().equals("-"))
			{
				inputStream = System.in;
			} else {
				inputStream = new FileInputStream(file);
			}
			
			
			inputStream =
				new CompressionActivator(compressionMethod).
					createCompressionInputStream(inputStream);
			
			
			//out.println((new BufferedReader(new InputStreamReader(inputStream))).readLine());
			Log.v("MyTag", "My Message: file.getName() = " + file.getName());
			
			/*
			try {
				if (inputStream != null) {
					Log.v("MyTag", "My Message: readLine() = "
							+ (new BufferedReader(new InputStreamReader(
									inputStream))).readLine());
				} else {
					Log.v("MyTag", "My Message: inputStream == null");
				}
			} catch (Exception e) {
				Log.v("MyTag", "MyMessage: Exception = " + e.toString());
			}
			*/
			
			parser = createParser();
			
			parser.parse(inputStream, new OsmHandler(sink, enableDateParsing));
			
			sink.complete();
			
		} catch (SAXParseException e) {
			throw new OsmosisRuntimeException(
				"Unable to parse xml file " + file
				+ ".  publicId=(" + e.getPublicId()
				+ "), systemId=(" + e.getSystemId()
				+ "), lineNumber=" + e.getLineNumber()
				+ ", columnNumber=" + e.getColumnNumber() + ".",
				e);
		} catch (SAXException e) {
			throw new OsmosisRuntimeException("Unable to parse XML.", e);
		} catch (IOException e) {
			throw new OsmosisRuntimeException("Unable to read XML file " + file + ".", e);
		} finally {
			sink.release();
			
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.log(Level.SEVERE, "Unable to close input stream.", e);
				}
				inputStream = null;
			}
		}
	}
}
