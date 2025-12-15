
package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.Task;
import com.hsf302.hotelmanagement.entity.User;
import com.hsf302.hotelmanagement.service.TaskService;
import com.hsf302.hotelmanagement.service.RoomService;
import com.hsf302.hotelmanagement.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private HttpSession session;

    // Màn hình danh sách task cho housekeeping staff
    @GetMapping
    public String listTasks(Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        List<Task> tasks = taskService.findByUserId(currentUser.getUserId());
        model.addAttribute("tasks", tasks);
        model.addAttribute("activePage", "tasks");
        model.addAttribute("user", currentUser);

        return "task-list";
    }

    // API lấy danh sách task của user hiện tại (JSON)
    @GetMapping("/api/my-tasks")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getMyTasks() {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(401).body(new java.util.ArrayList<>());
        }

        List<Task> tasks = taskService.findByUserId(currentUser.getUserId());
        List<Map<String, Object>> result = tasks.stream()
                .map(task -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("taskId", task.getTaskId());

                    // Nếu task có nhiều phòng, hiển thị danh sách các phòng
                    if (task.getRooms() != null && !task.getRooms().isEmpty()) {
                        // Lấy phòng đầu tiên để hiển thị
                        Room firstRoom = task.getRooms().iterator().next();
                        map.put("roomName", firstRoom.getRoomName());
                        map.put("floor", firstRoom.getFloor() != null ? firstRoom.getFloor().getFloorNumber() : "N/A");
                        map.put("roomType", firstRoom.getRoomType() != null ? firstRoom.getRoomType().getTypeName() : "N/A");

                        // Thêm danh sách tất cả phòng
                        List<String> roomNames = task.getRooms().stream()
                                .map(Room::getRoomName)
                                .collect(Collectors.toList());
                        map.put("roomCount", task.getRooms().size());
                        map.put("roomList", String.join(", ", roomNames));
                    } else {
                        map.put("roomName", "N/A");
                        map.put("floor", "N/A");
                        map.put("roomType", "N/A");
                        map.put("roomCount", 0);
                        map.put("roomList", "");
                    }

                    map.put("status", task.getStatus());
                    map.put("priority", task.getPriority());
                    map.put("deadline", task.getDeadline() != null ? task.getDeadline().toString() : null);
                    map.put("description", task.getDescription());
                    map.put("createdAt", task.getCreatedAt() != null ? task.getCreatedAt().toString() : null);
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // API để update trạng thái task
    @PostMapping("/api/update-status/{taskId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateTaskStatus(
            @PathVariable int taskId,
            @RequestParam String status) {

        try {
            Task task = taskService.updateTaskStatus(taskId, status);

            if (task == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Task không tồn tại"));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật trạng thái thành công");
            response.put("status", task.getStatus());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Lỗi: " + e.getMessage()));
        }
    }

    // API để cập nhật trạng thái phòng từ Dirty thành Clean
    @PostMapping("/api/mark-room-clean/{taskId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markRoomAsClean(@PathVariable int taskId) {
        try {
            Task task = taskService.findById(taskId);

            if (task == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Task không tồn tại"));
            }

            // Cập nhật trạng thái tất cả các phòng trong task thành Available
            if (task.getRooms() != null && !task.getRooms().isEmpty()) {
                for (Room room : task.getRooms()) {
                    if (room.getRoomStatus() != null) {
                        room.getRoomStatus().setRoomStatus("Available");
                        roomService.save(room);
                    }
                }
            }

            // Cập nhật trạng thái task thành Completed
            task.setStatus("Completed");
            task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            taskService.save(task);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", task.getRooms().size() + " phòng đã được đánh dấu là sạch sẽ");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Lỗi: " + e.getMessage()));
        }
    }
}
