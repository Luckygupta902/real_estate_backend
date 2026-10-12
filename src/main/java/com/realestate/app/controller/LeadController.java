package com.realestate.app.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.realestate.app.entity.Lead;
import com.realestate.app.entity.LeadActivity;
import com.realestate.app.service.LeadService;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    // Create lead
    @PostMapping  
    public ResponseEntity<?> createLead(@RequestBody Lead lead) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(leadService.createLead(lead));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all leads
    @GetMapping
    public ResponseEntity<List<Lead>> getAllLeads() {
        return ResponseEntity.ok(leadService.getAllLeads());
    }

    // Get lead by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLeadById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(
                    leadService.getLeadById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get leads by customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Lead>> getLeadsByCustomer(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                leadService.getLeadsByCustomer(customerId));
    }

    // Get leads by property
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<Lead>> getLeadsByProperty(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                leadService.getLeadsByProperty(propertyId));
    }

    // Get leads assigned to an employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Lead>> getLeadsByEmployee(
            @PathVariable Long employeeId) {

        return ResponseEntity.ok(
                leadService.getLeadsByEmployee(employeeId));
    }

    // Update lead details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLead(
            @PathVariable Long id,
            @RequestBody Lead lead) {

        try {
            return ResponseEntity.ok(
                    leadService.updateLead(id, lead));
        } catch (IllegalArgumentException e) {
            HttpStatus status = e.getMessage().startsWith("Lead not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(status)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Assign lead to employee
    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignLead(
            @PathVariable Long id,
            @RequestBody Map<String, Long> request) {

        try {
            Long employeeId = request.get("employeeId");
            Long actingUserId = request.get("actingUserId");

            return ResponseEntity.ok(
                    leadService.assignLead(
                            id, employeeId, actingUserId));
        } catch (IllegalArgumentException e) {
            HttpStatus status = e.getMessage().startsWith("Lead not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(status)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update lead status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateLeadStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        try {
            String status = request.get("status");
            String actingUserValue = request.get("actingUserId");

            Long actingUserId = actingUserValue == null
                    ? null
                    : Long.valueOf(actingUserValue);

            return ResponseEntity.ok(
                    leadService.updateLeadStatus(
                            id, status, actingUserId));
        } catch (IllegalArgumentException e) {
            HttpStatus httpStatus = e.getMessage().startsWith("Lead not found")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity.status(httpStatus)
                    .body(Map.of("error", e.getMessage()));
        }
    }
  
    // Get activity history for a lead
    @GetMapping("/{id}/activities")
    public ResponseEntity<?> getLeadActivities(
            @PathVariable Long id) {

        try {
            List<LeadActivity> activities = leadService.getLeadActivities(id);

            return ResponseEntity.ok(activities);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all lead activities
    @GetMapping("/activities/all")
    public ResponseEntity<List<LeadActivity>> getAllActivities() {
        return ResponseEntity.ok(
                leadService.getAllActivities());
    }
}
