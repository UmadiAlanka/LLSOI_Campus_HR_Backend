package com.klef.fsad.sdp.service;

import com.klef.fsad.sdp.model.Admin;
import com.klef.fsad.sdp.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    // Create new admin
    public Admin createAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    // Get all admins
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    // Get admin by ID
    public Optional<Admin> getAdminById(int id) {
        return adminRepository.findById(id);
    }

    // Update admin
    public Admin updateAdmin(int id, Admin adminDetails) {
        Optional<Admin> adminOpt = adminRepository.findById(id);
        if (adminOpt.isEmpty()) {
            throw new RuntimeException("Admin not found");
        }

        Admin admin = adminOpt.get();
        admin.setUsername(adminDetails.getUsername());
        admin.setEmail(adminDetails.getEmail());
        if (adminDetails.getPassword() != null && !adminDetails.getPassword().isEmpty()) {
            admin.setPassword(adminDetails.getPassword());
        }

        return adminRepository.save(admin);
    }

    // Delete admin
    public void deleteAdmin(int id) {
        adminRepository.deleteById(id);
    }

    // Check if admin exists by username
    public boolean existsByUsername(String username) {
        return adminRepository.findAll().stream()
                .anyMatch(admin -> admin.getUsername().equals(username));
    }

    // Check if admin exists by email
    public boolean existsByEmail(String email) {
        return adminRepository.findAll().stream()
                .anyMatch(admin -> admin.getEmail().equals(email));
    }

    // Get admin by username
    public Optional<Admin> getAdminByUsername(String username) {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getUsername().equals(username))
                .findFirst();
    }

    // Verify admin credentials
    public Optional<Admin> verifyCredentials(String username, String password) {
        return adminRepository.findAll().stream()
                .filter(admin -> admin.getUsername().equals(username) &&
                        admin.getPassword().equals(password))
                .findFirst();
    }
}