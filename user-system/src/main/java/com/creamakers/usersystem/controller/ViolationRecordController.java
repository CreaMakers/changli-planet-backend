package com.creamakers.usersystem.controller;


import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.service.ViolationRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class ViolationRecordController {

    @Autowired
    private ViolationRecordService violationRecordService;

    @GetMapping("/me/violations")
    public ResponseEntity<GeneralResponse> getViolations(@RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return violationRecordService.getViolations(accessToken);
    }

    @GetMapping("/{user_id}/violations")
    public ResponseEntity<GeneralResponse> getViolationsByUserId(@PathVariable("user_id") String userId) {
        return violationRecordService.getViolationsByUserId(Integer.parseInt(userId));
    }
}