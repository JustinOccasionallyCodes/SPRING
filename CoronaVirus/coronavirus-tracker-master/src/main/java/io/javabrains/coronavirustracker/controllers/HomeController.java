package io.javabrains.coronavirustracker.controllers;

import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model) {
        // Retrieve the list of LocationStats objects from the CoronaVirusDataService
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();

        // Calculate the total reported cases by summing the latestTotalCases property of each LocationStats object
        int totalReportedCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();

        // Calculate the total new cases by summing the diffFromPrevDay property of each LocationStats object
        int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();

        // Add the list of LocationStats objects, total reported cases, and total new cases to the Model object
        model.addAttribute("locationStats", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalNewCases", totalNewCases);

        // Return the name of the view template that will be rendered
        return "home";
    }
}

