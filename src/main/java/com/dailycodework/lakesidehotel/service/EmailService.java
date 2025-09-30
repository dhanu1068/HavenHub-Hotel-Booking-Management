package com.dailycodework.lakesidehotel.service;
import com.dailycodework.lakesidehotel.model.BookedRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("HavenHub - Verify Your Email");
        message.setText(
                "Hello,\n\n" +
                        "Your OTP for email verification is: " + otp + "\n\n" +
                        "This code is valid for 5 minutes.\n\n" +
                        "Thank you for registering with HavenHub !"
        );
        mailSender.send(message);
    }
    @Async
    public void sendBookingConfirmation(BookedRoom booking) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(booking.getGuestEmail());
        message.setSubject("HavenHub - Booking Confirmation");

        message.setText(
                "Hello " + booking.getGuestFullName() + ",\n\n" +
                        "Your booking has been successfully confirmed!\n\n" +

                        "Booking Details:\n" +
                        "Room Type: " + booking.getRoom().getRoomType() + "\n" +
                        "Check-in Date: " + booking.getCheckInDate() + "\n" +
                        "Check-out Date: " + booking.getCheckOutDate() + "\n" +
                        "Adults: " + booking.getNumOfAdults() + "\n" +
                        "Children: " + booking.getNumOfChildren() + "\n" +
                        "Total Guests: " + booking.getTotalNumOfGuest() + "\n\n" +

                        "Confirmation Code: " + booking.getBookingConfirmationCode() + "\n\n" +

                        "Thank you for choosing HavenHub. We look forward to hosting you!\n\n" +
                        "Best Regards,\n" +
                        "HavenHub Team"
        );

        mailSender.send(message);
    }

}


