package roart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import roart.exception.NotAllowedException;
import roart.exception.NotFoundException;
import roart.exception.OtherException;
import roart.model.BysykkelStativ;
import roart.model.availability.Availability;
import roart.model.availability.AvailabilityOuter;
import roart.model.availability.AvailabilityStation;
import roart.model.stations.Station;
import roart.model.stations.StationsOuter;
import roart.model.status.Status;
import roart.model.status.StatusOuter;

public class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    static final String URL = "https://oslobysykkel.no/api/v1";

    public static void main(String argv[]) {
        String identifier = System.getProperty("IDENTIFIER");
        if (identifier == null) {
            log.info("No identifier given");
            System.exit(1);
        }
        
        StatusOuter statusOuter = null;
        try {
            statusOuter = RestUtil.sendMe(StatusOuter.class, URL, "status", identifier);
        } catch (NotAllowedException | NotFoundException | OtherException e) {
            log.info("Exception, quitting because {}", e.getMessage());
            System.exit(1);
        }
        if (statusOuter == null) {
            log.info("Did not get overall status, quitting");
            System.exit(1);
        }
        Status status = statusOuter.getStatus();
        if (status == null) {
            log.info("Did not get overall status, quitting");
            System.exit(1);            
        }
        if (status.getAllStationsClosed() != null && status.getAllStationsClosed()) {
            log.info("All stations are closed");
            System.exit(0);            
        }
        List<Integer> closedStations = new ArrayList<>();
        if (status.getStationsClosed() != null) {
            closedStations = status.getStationsClosed();
        }
        StationsOuter stationsOuter = null;
        try {
            stationsOuter = RestUtil.sendMe(StationsOuter.class, URL, "stations", identifier);
        } catch (NotAllowedException | NotFoundException | OtherException e) {
            log.info("Exception, quitting because {}", e.getMessage());
            System.exit(1);
        }
        if (stationsOuter == null) {
            log.info("Did not get overall stations, quitting");
            System.exit(1);
        }
        List<Station> stations = stationsOuter.getStations();
        if (stations == null) {
            log.info("Did not get stations, quitting");
            System.exit(1);
        }

        AvailabilityOuter availOuter = null;
        try {
            availOuter = RestUtil.sendMe(AvailabilityOuter.class, URL, "stations/availability", identifier);
        } catch (NotAllowedException | NotFoundException | OtherException e) {
            log.info("Exception, quitting because {}", e.getMessage());
            System.exit(1);
        }
        if (availOuter == null) {
            log.info("Did not get overall availability, quitting");
            System.exit(1);
        }
        List<AvailabilityStation> availStations = availOuter.getStations();
        if (availStations == null) {
            log.info("Did not get availability stations, quitting");
            System.exit(1);
        }
        
        List<BysykkelStativ> bysykkelStativListe = merge(stations, availStations, closedStations);
        for (BysykkelStativ bysykkelStativ : bysykkelStativListe) {
            System.out.println(bysykkelStativ);
        }
    }

    private static List<BysykkelStativ> merge(List<Station> stations, List<AvailabilityStation> availStations,
            List<Integer> closedStations) {
        List<BysykkelStativ> bysykkelStativListe = new ArrayList<>();
        Map<Integer, AvailabilityStation> availMap = new HashMap<>();
        for (AvailabilityStation availStation : availStations) {
            Integer id = availStation.getId();
            if (id == null) {
                log.info("No id for availability station, continuing anyway");
                continue;
            }
            AvailabilityStation old = availMap.put(id, availStation);
            if (old != null) {
                log.info("Duplicate id for availability station, continuing anyway");
                continue;
            }
        }
        for (Station station : stations) {
            Integer id = station.getId();
            if (id == null) {
                log.info("No id for station, continuing anyway");
                continue;
            }
            if (closedStations.indexOf(id) >= 0) {
                continue;
            }
            
            if (!station.getInService()) {
                continue;
            }
            
            AvailabilityStation availStation = availMap.get(id);
            if (availStation == null) {
                log.info("No availability station, continuing anyway");
                continue;
            }
                        
            Availability avail = availStation.getAvailability();
            if (avail == null) {
                log.info("No availability for station, continuing anyway");
                continue;
            }
            
            if (station.getNumberOfLocks() != (avail.getBikes() + avail.getLocks())) {
                log.info("Ignoring: inconsistency of lock number for id : {}", id);
            }

            BysykkelStativ bysykkelStativ = new BysykkelStativ();
            bysykkelStativ.setId(id);
            bysykkelStativ.setNavn(station.getTitle());
            bysykkelStativ.setAntallLaaser(station.getNumberOfLocks());
            bysykkelStativ.setAntallLedigeSykler(avail.getBikes());
            bysykkelStativListe.add(bysykkelStativ);
        }
        return bysykkelStativListe;
    }

}
