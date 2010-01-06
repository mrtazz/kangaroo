package com.example.AndroidNearestStreet;


import java.io.File;
import java.net.URL;

import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.io.DataSetSink;

import org.openstreetmap.osmosis.core.migrate.MigrateV05ToV06;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.xml.common.CompressionMethod;
//import org.openstreetmap.osmosis.core.xml.v0_6.XmlReader;

/**
 * This class exists to make loading an <a hef="http://wiki.openstreetmap.org/index.php/Develop">OpenStreetMap</a>-file
 * via program-code easier. It wraps the existing functionality
 * in <a href="http://wiki.openstreetmap.org/index.php/Osmosis">Osmosis</a>.
 * @author <a href="mailto:Marcus@Wolschon.biz">Marcus Wolschon</a>
 */
public class FileLoader {

    /**
     * The filename we are reading from.
     */
    private File myFileName;

    /**
     * The filename we are reading from.
     */
    //private URL myFileURL;


    /**
     * @param aFileName The filename we are reading from.
     */
    public FileLoader(final File aFileName) {
        super();
        this.myFileName = aFileName;
//        this.myFileURL = null;
    }

    /**
     * @param aFileName The filename we are reading from.
     */
    public FileLoader(final URL aFileName) {
        super();
        this.myFileName = null;
  //      this.myFileURL = aFileName;
    }


    /**
     * @return the file-content or null
     */
    public MemoryDataSet parseOsm() {
        DataSetSink sink = new DataSetSink();

        parseOsm(sink);

        return (MemoryDataSet) sink.getDataSet();
    }


    /**
     * @param sink where to give the file-content for processing
     */
    public void parseOsm(final Sink sink) {
        CompressionMethod compr = CompressionMethod.None;
        if (myFileName.getName().toLowerCase().endsWith(".gz")) {
            compr = CompressionMethod.GZip;
        } else {
            if (myFileName.getName().toLowerCase().endsWith(".bz2")) {
                compr = CompressionMethod.BZip2;
            }
        }

        XmlReader task = new XmlReader(this.myFileName, true, compr);


        task.setSink(sink);
        try {
            task.run();
        } catch (java.lang.NumberFormatException e) {
            if (e.getMessage().equals("null")) {
                // this seems to be an api0.5-file.
                org.openstreetmap.osmosis.core.xml.v0_5.XmlReader oldTask = new org.openstreetmap.osmosis.core.xml.v0_5.XmlReader(this.myFileName, true, compr);
                MigrateV05ToV06 migrate = new MigrateV05ToV06();
                oldTask.setSink(migrate);
                migrate.setSink(sink);
                oldTask.run();
            } else {
                throw e;
            }
        }

    }

}

