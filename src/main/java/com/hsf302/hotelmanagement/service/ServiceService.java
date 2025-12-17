package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.dto.response.overviewServiceDTO;
import com.hsf302.hotelmanagement.entity.Reservation_Service;

import java.util.Date;
import java.util.List;

public interface ServiceService {
    List<overviewServiceDTO> getServiceOverview();
    List<overviewServiceDTO> findByServiceName(String serviceName);
    List<overviewServiceDTO> findByCategory(String category);
    List<overviewServiceDTO> findByServiceNameAndCategory(String serviceName, String category);
}
