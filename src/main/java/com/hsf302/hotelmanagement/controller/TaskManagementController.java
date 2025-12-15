package com.hsf302.hotelmanagement.controller;

import com.hsf302.hotelmanagement.entity.Room;
import com.hsf302.hotelmanagement.entity.Task;
import com.hsf302.hotelmanagement.entity.User;
import com.hsf302.hotelmanagement.service.RoomService;
import com.hsf302.hotelmanagement.service.TaskService;
import com.hsf302.hotelmanagement.service.UserService;
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
@RequestMapping("/tasks-management")
public class TaskManagementController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    // Màn hình giao task (cho admin/receptionist)
    @GetMapping("/assign")
    public String assignTaskPage(Model model) {
        // Lấy danh sách phòng có status là "Dirty"
        List<Room> dirtyRooms = roomService.findAll().stream()
                .filter(room -> room.getRoomStatus() != null && "Dirty".equals(room.getRoomStatus().getRoomStatus()))
                .collect(Collectors.toList());

        // Lấy danh sách housekeeping staff (role = "Housekeeping Staff")
        List<User> housekeepingStaff = userService.findAll().stream()
                .filter(user -> user.getRole() != null && "HouseKeeping Staff".equals(user.getRole()))
                .collect(Collectors.toList());

        model.addAttribute("dirtyRooms", dirtyRooms);
        model.addAttribute("housekeepingStaff", housekeepingStaff);
        model.addAttribute("activePage", "task-assign");
        model.addAttribute("view", "task-assign");

        // Return the main layout
        return "dashboard-layout";
    }

    // API lấy danh sách phòng bẩn
    @GetMapping("/api/dirty-rooms")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDirtyRooms() {
        List<Room> dirtyRooms = roomService.findAll().stream()
                .filter(room -> room.getRoomStatus() != null && "Dirty".equals(room.getRoomStatus().getRoomStatus()))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = dirtyRooms.stream()
                .map(room -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("roomId", room.getRoomId());
                    map.put("roomName", room.getRoomName());
                    map.put("floor", room.getFloor() != null ? room.getFloor().getFloor_number() : "N/A");
                    map.put("roomType", room.getRoomType() != null ? room.getRoomType().getTypeName() : "N/A");
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // API lấy danh sách housekeeping staff
    @GetMapping("/api/housekeeping-staff")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getHousekeepingStaff() {
        List<User> staff = userService.findAll().stream()
                .filter(user -> user.getRole() != null && "HouseKeeping Staff".equals(user.getRole()))
                .collect(Collectors.toList());

        List<Map<String, Object>> result = staff.stream()
                .map(user -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", user.getUserId());
                    map.put("fullName", user.getUserName());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    // API tạo task với nhiều phòng
    @PostMapping("/api/create-task")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTask(
            @RequestParam List<Integer> roomIds,
            @RequestParam int userId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String deadline) {

        try {
            User user = userService.findById(userId);

            if (user == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "User không tồn tại"));
            }

            if (roomIds == null || roomIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Vui lòng chọn ít nhất 1 phòng"));
            }

            Task task = new Task();
            task.setUser(user);
            task.setDescription("Vệ sinh " + roomIds.size() + " phòng");
            task.setPriority(priority != null ? priority : "Normal");
            task.setStatus("Pending");
            task.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            task.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (deadline != null && !deadline.isEmpty()) {
                task.setDeadline(Timestamp.valueOf(deadline.replace("T", " ") + ":00"));
            }

            // Thêm tất cả phòng vào task
            for (Integer roomId : roomIds) {
                Room room = roomService.findById(roomId);
                if (room == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Phòng ID " + roomId + " không tồn tại"));
                }
                // Kiểm tra phòng có status là Dirty không
                if (room.getRoomStatus() == null || !"Dirty".equals(room.getRoomStatus().getRoomStatus())) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("success", false, "message", "Phòng " + room.getRoomName() + " không phải Dirty"));
                }
                task.addRoom(room);
            }

            Task savedTask = taskService.save(task);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("taskId", savedTask.getTaskId());
            response.put("message", "Task đã được tạo thành công cho " + roomIds.size() + " phòng");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "Lỗi: " + e.getMessage()));
        }
    }
}

