package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats = new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    //indicates a method that should be executed after dependency injection is done.
    @PostConstruct
    //marks a method as a scheduled task.The method will be executed once every hour.
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        // Create a new list to hold the updated location statistics
        List<LocationStats> newStats = new ArrayList<>();

        // Create an instance of HttpClient to send HTTP requests
        HttpClient client = HttpClient.newHttpClient();

        // Build a HttpRequest with the URL for the virus data
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();

        // Send the HTTP request and obtain the response as a string
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Create a StringReader from the response body to parse the CSV data
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        // Parse the CSV data using CSVFormat, assuming the first record as the header
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        // Iterate over each CSV record
        for (CSVRecord record : records) {
            // Create a new LocationStats object for each record
            LocationStats locationStat = new LocationStats();

            // Extract the relevant fields from the record and set them in the LocationStats object
            locationStat.setState(record.get("Province/State"));
            locationStat.setCountry(record.get("Country/Region"));
            int latestCases = Integer.parseInt(record.get(record.size() - 1));
            int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
            locationStat.setLatestTotalCases(latestCases);
            locationStat.setDiffFromPrevDay(latestCases - prevDayCases);

            // Add the LocationStats object to the newStats list
            newStats.add(locationStat);
        }

        // Update the allStats list with the newStats list
        this.allStats = newStats;
    }
}
