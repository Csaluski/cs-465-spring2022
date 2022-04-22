package appserver.server;

import java.util.ArrayList;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class LoadManager {

    static ArrayList<String> satellites = null;
    static int lastSatelliteIndex = -1;

    public LoadManager() {
        satellites = new ArrayList<String>();
    }

    public void satelliteAdded(String satelliteName) {
        // add satellite
        // ...
        satellites.add(satelliteName);
    }


    public String nextSatellite() throws Exception {
        
        int numberSatellites;
        String satelliteName;
        
        synchronized (satellites) {
            // implement policy that returns the satellite name according to a round robin methodology
            // ...
            numberSatellites = (lastSatelliteIndex + 1) % satellites.size();
            satelliteName = satellites.get(numberSatellites);
            lastSatelliteIndex = numberSatellites;
        }
        // ... name of satellite who is supposed to take job
        return satelliteName;
    }
}
