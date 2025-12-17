package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.dto.response.overviewServiceDTO;
import com.hsf302.hotelmanagement.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    /**
     * Handles the initial GET request to load the service list fragment.
     * Displays all services by default.
     */
    @GetMapping("/manager/services-fragment")
    public String showServiceList(Model model) {
        // As per your request, get the initial list of services
        List<overviewServiceDTO> serviceList = serviceService.getServiceOverview();
        model.addAttribute("serviceList", serviceList);
        return "manager/serviceList :: service-content";
    }

    /**
     * Handles the POST request from the search form.
     * Displays a filtered list of services.
     */
    @PostMapping("/manager/services-fragment")
    public String searchServiceList(
            @RequestParam(value = "serviceName", required = false) String serviceName,
            @RequestParam(value = "category", required = false) String category,
            Model model) {

        List<overviewServiceDTO> serviceList;

        boolean hasServiceName = serviceName != null && !serviceName.trim().isEmpty();
        boolean hasCategory = category != null && !category.trim().isEmpty();

        if (hasServiceName) {
            serviceList = serviceService.findByServiceName(serviceName);
        } else if (hasCategory) {
            serviceList = serviceService.findByCategory(category);
        } else {
            // If no filter is provided, show the default list again
            serviceList = serviceService.getServiceOverview();
        }

        model.addAttribute("serviceList", serviceList);
        // Add filter values back to the model to keep them in the form
        model.addAttribute("serviceName", serviceName);
        model.addAttribute("category", category);

        return "manager/serviceList :: service-content";
    }
}
