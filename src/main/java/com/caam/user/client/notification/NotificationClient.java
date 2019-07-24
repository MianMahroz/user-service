package com.caam.user.client.notification;

import com.caam.user.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "CAAM-NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/activationEmail")
    ResponseEntity<?> sendActivationEmail(@RequestBody UserDto dto);

    @PostMapping("/api/notification/forgotPassword")
    ResponseEntity<?> sendForgotPasswordEmail(@RequestBody UserDto dto);
    @PostMapping("/api/sendTextMessage")
    ResponseEntity sendTextMessage(@RequestParam String number,@RequestParam String message);
    @PostMapping("/api/sendToDevice")
    ResponseEntity sendPushMessage(@RequestParam Object message,@RequestParam String[] deviceId, @RequestParam String title);
    
}
