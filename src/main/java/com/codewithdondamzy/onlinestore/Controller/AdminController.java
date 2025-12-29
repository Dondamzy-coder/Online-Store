package com.codewithdondamzy.onlinestore.Controller;

import com.codewithdondamzy.onlinestore.Dtos.Request.AdminRequest;
import com.codewithdondamzy.onlinestore.Dtos.Response.AdminResponse;
import com.codewithdondamzy.onlinestore.Models.Admin;
import com.codewithdondamzy.onlinestore.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/OnlineStore")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService){
        this.adminService = adminService;
    }
    @PostMapping("/createAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody AdminRequest adminRequest) {
        return ResponseEntity.ok(adminService.createAdmin(adminRequest));
    }

    @GetMapping("/getAdminByUsername")
    public ResponseEntity<?> getAdminByUsername(@RequestParam String userName) {
        return ResponseEntity.ok(adminService.getAdminByUserName(userName));
    }

    @PutMapping("/updateAdminByUserName{userName}")
    public ResponseEntity<?> updateAdmin(@RequestBody AdminRequest adminRequest, @RequestParam String userName) {
        return ResponseEntity.ok(adminService.updateAdmin(adminRequest,userName));
    }

    @GetMapping("/getAllAdmins")
    public ResponseEntity<?> getAllAdmins() {
       return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @DeleteMapping("/deleteAdminByUserName{userName}")
    public ResponseEntity<?> deleteAdmin(@RequestParam String userName) {
        return ResponseEntity.ok(adminService.deleteAdminByUserName(userName));
    }
}
