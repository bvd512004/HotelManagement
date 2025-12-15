package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
}

