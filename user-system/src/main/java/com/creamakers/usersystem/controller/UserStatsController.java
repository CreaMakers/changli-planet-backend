package com.creamakers.usersystem.controller;


import com.creamakers.usersystem.dto.request.BindStudentNumberRequest;
import com.creamakers.usersystem.dto.response.GeneralResponse;
import com.creamakers.usersystem.service.UserStatsService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class UserStatsController {

    @Autowired
    private UserStatsService userStatsService;


    @GetMapping("/me/stats")
    public ResponseEntity<GeneralResponse> getStats(@RequestHeader(value = "Authorization") String authorization) {
        String accessToken = authorization.substring(7);
        return userStatsService.getStats(accessToken);
    }

    @GetMapping("/{user_id}/stats")
    public ResponseEntity<GeneralResponse> getStatsByUserId(@PathVariable("user_id") String userId) {
        return userStatsService.getStatsById(userId);
    }



    @PostMapping("/me/student-number")
    public ResponseEntity<GeneralResponse> bindStudentNumber(@RequestBody BindStudentNumberRequest bindStudentNumberRequest,
                                                             @RequestHeader(value = "Authorization") String authorization){
        String accessToken = authorization.substring(7);
        String studentNumber = bindStudentNumberRequest.getStudentNumber();
        return userStatsService.setStudentNumber(studentNumber, accessToken);
    }
}
