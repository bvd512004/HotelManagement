package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.dto.response.overviewServiceDTO;
import com.hsf302.hotelmanagement.entity.Reservation_Service;
import com.hsf302.hotelmanagement.repository.ReservationServiceRepository;
import com.hsf302.hotelmanagement.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ReservationServiceRepository reservationServiceRepository;

    private List<overviewServiceDTO> convertToDtoList(List<Reservation_Service> serviceReservations) {
        List<overviewServiceDTO> dtoList = new ArrayList<>();

        for (Reservation_Service rs : serviceReservations) {
            int reservationId = 0;
            String serviceName = "N/A";
            String category = "N/A";
            String status = "N/A";
            Double servicePrice = 0.0;

            if (rs.getService() != null) {
                reservationId = rs.getReservation().getReservationId();
                serviceName = rs.getService().getServiceName();
                category = rs.getService().getCategory();
                status = rs.getService().getStatus();
                servicePrice = rs.getService().getPrice();
            }

            int quantity = rs.getQuantity();
            double totalPrice = servicePrice * quantity;

            overviewServiceDTO dto = new overviewServiceDTO(
                    reservationId,
                    serviceName,
                    category,
                    status,
                    quantity,
                    totalPrice
            );
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Override
    public List<overviewServiceDTO> getServiceOverview() {
        List<Reservation_Service> serviceReservations = reservationServiceRepository.findAll();
        return convertToDtoList(serviceReservations);
    }

    @Override
    public List<overviewServiceDTO> findByServiceName(String serviceName) {
        List<Reservation_Service> serviceReservations = reservationServiceRepository.findAll();
        List<Reservation_Service> sList = new ArrayList<>();
        for(Reservation_Service rs : serviceReservations){
            if(rs.getService().getServiceName().contains(serviceName)){
                sList.add(rs);
            }
        }
        return convertToDtoList(sList);
    }

    public List<overviewServiceDTO> findByCategory(String category) {
        List<Reservation_Service> serviceReservations = reservationServiceRepository.findAll();
        List<Reservation_Service> sList = new ArrayList<>();
        for(Reservation_Service rs : serviceReservations){
            if(rs.getService().getCategory().contains(category)){
                sList.add(rs);
            }
        }
        return convertToDtoList(sList);
    }

    @Override
    public List<overviewServiceDTO> findByServiceNameAndCategory(String serviceName, String category) {
        List<Reservation_Service> serviceReservations = reservationServiceRepository.findAll();
        List<Reservation_Service> sList = new ArrayList<>();
        for(Reservation_Service rs : serviceReservations){
            if(rs.getService().getCategory().contains(category) && rs.getService().getServiceName().contains(serviceName)){
                sList.add(rs);
            }
        }
        return convertToDtoList(sList);
    }
}