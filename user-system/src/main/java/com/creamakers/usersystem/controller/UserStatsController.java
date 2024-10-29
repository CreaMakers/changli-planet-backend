package com.creamakers.usersystem.controller;


import com.creamakers.usersystem.dto.request.BindStudentNumberRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.service.UserStatsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class UserStatsController {

    @Autowired
    private UserStatsService userStatsService;


    @GetMapping("/me/stats")
    public GeneralResponse getStats(HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        return userStatsService.getStats(accessToken);
    }

    @GetMapping("/{user_id}/stats")
    public GeneralResponse getStatsByUserId(@PathVariable("user_id") String userId) {
        return userStatsService.getStatsById(userId);
    }



    @PostMapping("/me/student-number")
    public GeneralResponse bindStudentNumber(@RequestBody BindStudentNumberRequest bindStudentNumberRequest, HttpServletRequest request) {
        String accessToken = request.getHeader("token");
        String studentNumber = bindStudentNumberRequest.getStudentNumber();
        return userStatsService.setStudentNumber(studentNumber, accessToken);
    }
}
