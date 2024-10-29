package com.creamakers.usersystem.controller;


import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.service.ViolationRecordService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/users")
public class ViolationRecordController {

    @Autowired
    private ViolationRecordService violationRecordService;

    @GetMapping("/me/violations")
    public GeneralResponse getViolations(HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        return violationRecordService.getViolations(accessToken);
    }

    @GetMapping("/{user_id}/violations")
    public GeneralResponse getViolationsByUserId(@PathVariable("user_id") String userId) {
        return violationRecordService.getViolationsByUserId(Integer.parseInt(userId));
    }
}
