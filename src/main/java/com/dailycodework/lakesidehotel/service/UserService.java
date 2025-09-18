package com.dailycodework.lakesidehotel.service;

import com.dailycodework.lakesidehotel.exception.UserAlreadyExistsException;
import com.dailycodework.lakesidehotel.model.Role;
import com.dailycodework.lakesidehotel.model.User;
import com.dailycodework.lakesidehotel.repository.RoleRepository;
import com.dailycodework.lakesidehotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * @author Simpson Alfred
 */

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final EmailService emailService;

    @Override
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(user.getEmail() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        Role userRole = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(userRole));
        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        //  Set OTP expiry time (valid for 5 minutes)
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        // Save user with OTP
        User savedUser = userRepository.save(user);
        // Send OTP email
        emailService.sendOtpEmail(savedUser.getEmail(), otp);
        return savedUser;
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        User theUser = getUser(email);
        if (theUser != null){
            userRepository.deleteByEmail(email);
        }

    }

    @Override
    public User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        User user = getUser(email);

        if (user.getOtp() != null
                && user.getOtp().equals(otp)
                && user.getOtpExpiry() != null
                && user.getOtpExpiry().isAfter(LocalDateTime.now())) {

            // Mark user as verified
            user.setVerified(true);
            user.setOtp(null); // clear OTP after success
            user.setOtpExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    @Override
    public void resendOtp(String email) {
        User user = getUser(email);

        // Generate new OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));

        // Save updated user
        userRepository.save(user);

        // Send new OTP email
        emailService.sendOtpEmail(user.getEmail(), otp);

    }
}
