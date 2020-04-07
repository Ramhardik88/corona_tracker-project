package io.rrbrains.coronavirustracker.services;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.rrbrains.coronavirustracker.models.LocationStats;

@Service
public class CoronavirusDataService {
	private static String Virus_Data_url = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private List<LocationStats> allStats = new ArrayList<>();

	@PostConstruct
	@Scheduled(cron = "* * 1 * * *") // update every hour suppose in aws Update in every second we can change
	public void fetchVirusData() throws IOException, InterruptedException {
		List<LocationStats> newStats = new ArrayList<>();
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(Virus_Data_url)).build();

		HttpResponse<String> httpresponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		StringReader csvBodyReader = new StringReader(httpresponse.body());
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
		for (CSVRecord record : records) {
			LocationStats locationStats = new LocationStats();
			locationStats.setState(record.get("Province/State"));
			locationStats.setCountry(record.get("Country/Region"));
			int latestCases=Integer.parseInt(record.get(record.size() - 1));
			int prevDayCases=Integer.parseInt(record.get(record.size() - 2));
			
			locationStats.setLatestTotalCases(latestCases);
			locationStats.setDiffFromPrevDay(latestCases-prevDayCases);
		
			newStats.add(locationStats);

		}
		this.allStats = newStats;

	}

	public List<LocationStats> getAllStats() {
		return allStats;
	} 

	public void setAllStats(List<LocationStats> allStats) {
		this.allStats = allStats;
	}

}
