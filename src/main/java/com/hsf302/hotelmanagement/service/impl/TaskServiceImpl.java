package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Task;
import com.hsf302.hotelmanagement.repository.TaskRepository;
import com.hsf302.hotelmanagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task findById(int id) {
        return taskRepository.findById(id).orElse(null);
    }

    @Override
    public List<Task> findByUserId(int userId) {
        return taskRepository.findByUserId(userId);
    }

    @Override
    public Task updateTaskStatus(int taskId, String status) {
        Task task = findById(taskId);
        if (task != null) {
            task.setStatus(status);
            task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return save(task);
        }
        return null;
    }
}