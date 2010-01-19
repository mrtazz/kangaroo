/**
 * 
 */
package com.kangaroo.tsm.osm.io;

import java.io.File;

import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.io.DataSetSink;

import com.kangaroo.tsm.osm.data.KangarooTSMMemoryDataSet;

/**
 * @author andreaswalz
 *
 */
public class KangarooTSMFileLoader extends FileLoader {

	public KangarooTSMFileLoader(File aFileName) {
		super(aFileName);
	}
	
	
	public KangarooTSMMemoryDataSet parseOsmKangarooTSM() {
		KangarooTSMDataSetSink sink = new KangarooTSMDataSetSink();

        parseOsm(sink);

        return (KangarooTSMMemoryDataSet) sink.getDataSet();
    }

}
