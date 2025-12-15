
package com.hsf302.hotelmanagement.repository;

import com.hsf302.hotelmanagement.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByUser_UserId(int userUserId);
    List<Task> findAllByUser_UserIdAndStatus(int userUserId, String status);
    List<Task> findAllByStatus(String status);
}
