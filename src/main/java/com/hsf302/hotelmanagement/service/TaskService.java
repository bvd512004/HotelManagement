
package com.hsf302.hotelmanagement.service;

import com.hsf302.hotelmanagement.entity.Task;
import java.util.List;

public interface TaskService {
    List<Task> findAll();
    List<Task> findByUserId(int userId);
    List<Task> findByUserIdAndStatus(int userId, String status);
    List<Task> findByStatus(String status);
    Task save(Task task);
    Task findById(int id);
    Task updateTaskStatus(int taskId, String status);
}
