package com.hsf302.hotelmanagement.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendBookingConfirmationEmail(String to, String guestName, int reservationId, 
                                            String roomTypeName, int quantity, String checkInDate, 
                                            String checkOutDate, double totalAmount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@hotelmanagement.com");
            helper.setTo(to);
            helper.setSubject("Xác nhận đặt phòng - Mã đặt phòng: " + reservationId);

            // Format ngày tháng theo định dạng tiếng Việt
            String formattedCheckIn = formatDate(checkInDate);
            String formattedCheckOut = formatDate(checkOutDate);

            // Tạo context cho Thymeleaf template
            Context context = new Context();
            context.setVariable("guestName", guestName);
            context.setVariable("reservationId", reservationId);
            context.setVariable("roomTypeName", roomTypeName);
            context.setVariable("quantity", quantity);
            context.setVariable("checkInDate", formattedCheckIn);
            context.setVariable("checkOutDate", formattedCheckOut);
            context.setVariable("totalAmount", totalAmount);

            // Render template
            String htmlContent = templateEngine.process("email/booking-confirmation", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Log error nhưng không throw để không ảnh hưởng đến luồng đặt phòng
        }
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("vi-VN"));
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (ParseException e) {
            return dateStr; // Trả về chuỗi gốc nếu không parse được
        }
    }
}

