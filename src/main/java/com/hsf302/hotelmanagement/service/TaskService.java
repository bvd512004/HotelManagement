package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Task;

import java.util.List;

public interface TaskService {
    Task save(Task task);
    Task findById(int id);
    List<Task> findByUserId(int userId);
    Task updateTaskStatus(int taskId, String status);
}