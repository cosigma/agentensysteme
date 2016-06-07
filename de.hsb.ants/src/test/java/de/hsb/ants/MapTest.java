package de.hsb.ants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hsb.ants.map.ChunkMap;

public class MapTest {

	static final Logger LOG = LoggerFactory.getLogger(MapTest.class);

	public static void main(String[] args) {
		
		ChunkMap<String> map = new ChunkMap<String>(2);
		
		

		for (int x = -20; x <= 20; ++x) {
			for (int y = -20; y <= 20; ++y) {
				map.set(x, y, "TEST_" + x + "_" + y);
			}
		}

		boolean allOk = true;
		for (int x = -20; x <= 20; ++x) {
			for (int y = -20; y <= 20; ++y) {
				String expected = "TEST_" + x + "_" + y;
				String found = map.get(x, y);
				if (!expected.equals(found)) {
					allOk = false;
					LOG.error("wrong return value: '{}' expected but '{}' found", expected, found);
				}
			}
		}
		if(allOk){
			LOG.info("all ok!");
		}

	}

}
