
package com.hsf302.hotelmanagement.service.impl;

import com.hsf302.hotelmanagement.entity.Task;
import com.hsf302.hotelmanagement.repository.TaskRepository;
import com.hsf302.hotelmanagement.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public List<Task> findByUserId(int userId) {
        return taskRepository.findAllByUser_UserId(userId);
    }

    @Override
    public List<Task> findByUserIdAndStatus(int userId, String status) {
        return taskRepository.findAllByUser_UserIdAndStatus(userId, status);
    }

    @Override
    public List<Task> findByStatus(String status) {
        return taskRepository.findAllByStatus(status);
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public Task findById(int id) {
        Optional<Task> task = taskRepository.findById(id);
        return task.orElse(null);
    }

    @Override
    public Task updateTaskStatus(int taskId, String status) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setStatus(status);
            task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return taskRepository.save(task);
        }
        return null;
    }
}
