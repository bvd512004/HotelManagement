package com.hsf302.hotelmanagement.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="services")
public class Service {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ServiceId")
    private int serviceId;

    @Column(name="ServiceName", columnDefinition = "NVARCHAR(MAX)")
    private String serviceName;

    @Column(name="Description", length=500, columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Column(name="Price")
    private double price;

    @Column(name="Unit", length=50, columnDefinition = "NVARCHAR(50)")
    private String unit;

    @Column(name="Category", length=50, columnDefinition = "NVARCHAR(50)")
    private String category;

    @Column(name="Status", length=20, columnDefinition = "NVARCHAR(20)")
    private String status;

    @CreationTimestamp
    @Column(name="CreatedAt",  updatable=false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation_Service> reservation_services;

    public Service() {
    }

    public Service(String serviceName, String description, double price, String unit, String category, String status, LocalDateTime createdAt, LocalDateTime updatedAt, List<Reservation_Service> reservation_services) {
        this.serviceName = serviceName;
        this.description = description;
        this.price = price;
        this.unit = unit;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reservation_services = reservation_services;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Reservation_Service> getReservation_services() {
        return reservation_services;
    }

    public void setReservation_services(List<Reservation_Service> reservation_services) {
        this.reservation_services = reservation_services;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", serviceName='" + serviceName + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", unit='" + unit + '\'' +
                ", category='" + category + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", reservation_services=" + reservation_services +
                '}';
    }
}
