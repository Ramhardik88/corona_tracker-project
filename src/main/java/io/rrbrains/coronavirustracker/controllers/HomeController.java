package io.rrbrains.coronavirustracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.rrbrains.coronavirustracker.models.LocationStats;
import io.rrbrains.coronavirustracker.services.CoronavirusDataService;


@Controller
public class HomeController {
	@Autowired
	CoronavirusDataService coronavirusDataService;
	
	@GetMapping("/")
	public String home(Model model) {
		 List<LocationStats> allStats=coronavirusDataService.getAllStats();
		 int totalreportedcases=allStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
		 int totalnewcases=allStats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalreportedcases",totalreportedcases);
		model.addAttribute("totalnewcases",totalnewcases);

		
		return "home";
	}

}
