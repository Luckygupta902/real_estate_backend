package com.realestate.app.service;



import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.app.entity.Lead;
import com.realestate.app.entity.LeadActivity;
import com.realestate.app.repository.LeadActivityRepository;
import com.realestate.app.repository.LeadRepository;

@Service
@Transactional
public class LeadService {

    private final LeadRepository leadRepository;
    private final LeadActivityRepository activityRepository;

    public LeadService(
            LeadRepository leadRepository,
            LeadActivityRepository activityRepository) {
        this.leadRepository = leadRepository;
        this.activityRepository = activityRepository;
    }

    // Create a new lead
    public Lead createLead(Lead lead) {

        if (lead.getCustomerId() == null) {
            throw new IllegalArgumentException(
                    "Customer ID is required");
        }

        if (lead.getPropertyId() == null) {
            throw new IllegalArgumentException(
                    "Property ID is required");
        }

        LocalDateTime now = LocalDateTime.now();

        
        lead.setStatus("NEW");
        lead.setCreatedAt(now);
        lead.setUpdatedAt(now);

        Lead savedLead = leadRepository.save(lead);

        saveActivity(
                savedLead.getId(),
                null,
                "LEAD_CREATED",
                "New lead created");

        return savedLead;
    }

    // Get all leads
    @Transactional(readOnly = true)
    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    // Get lead by ID
    @Transactional(readOnly = true)
    public Lead getLeadById(Long id) {
        return leadRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lead not found with ID: " + id));
    }

    // Get leads belonging to a customer
    @Transactional(readOnly = true)
    public List<Lead> getLeadsByCustomer(Long customerId) {
        return leadRepository.findByCustomerId(customerId);
    }

    // Get leads for a property
    @Transactional(readOnly = true)
    public List<Lead> getLeadsByProperty(Long propertyId) {
        return leadRepository.findByPropertyId(propertyId);
    }

    // Get leads assigned to an employee
    @Transactional(readOnly = true)
    public List<Lead> getLeadsByEmployee(Long employeeId) {
        return leadRepository.findByAssignedTo(employeeId);
    }

    // Update lead details
    public Lead updateLead(Long id, Lead updatedLead) {

        Lead existingLead = getLeadById(id);

        if (updatedLead.getCustomerId() != null) {
            existingLead.setCustomerId(
                    updatedLead.getCustomerId());
        }

        if (updatedLead.getPropertyId() != null) {
            existingLead.setPropertyId(
                    updatedLead.getPropertyId());
        }

        if (updatedLead.getSource() != null) {
            existingLead.setSource(updatedLead.getSource());
        }

        if (updatedLead.getMessage() != null) {
            existingLead.setMessage(updatedLead.getMessage());
        }

        existingLead.setUpdatedAt(LocalDateTime.now());

        Lead savedLead = leadRepository.save(existingLead);

        saveActivity(
                id,
                null,
                "LEAD_UPDATED",
                "Lead details updated");

        return savedLead;
    }

    // Assign lead to an employee
    public Lead assignLead(Long leadId, Long employeeId,
                           Long actingUserId) {

        if (employeeId == null) {
            throw new IllegalArgumentException(
                    "Employee ID is required");
        }

        Lead lead = getLeadById(leadId);

        lead.setAssignedTo(employeeId);
        lead.setUpdatedAt(LocalDateTime.now());

        Lead savedLead = leadRepository.save(lead);

        saveActivity(
                leadId,
                actingUserId,
                "LEAD_ASSIGNED",
                "Lead assigned to employee ID: " + employeeId);

        return savedLead;
    }

    // Update lead status
    public Lead updateLeadStatus(Long leadId, String status,
                                 Long actingUserId) {

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Lead status is required");
        }

        String normalizedStatus =
                status.trim().toUpperCase();

        List<String> allowedStatuses = List.of(
                "NEW",
                "CONTACTED",
                "QUALIFIED",
                "VISIT_SCHEDULED",
                "NEGOTIATION",
                "CONVERTED",
                "LOST");

        if (!allowedStatuses.contains(normalizedStatus)) {
            throw new IllegalArgumentException(
                    "Invalid status. Allowed statuses: "
                            + allowedStatuses);
        }

        Lead lead = getLeadById(leadId);

        String oldStatus = lead.getStatus();

        lead.setStatus(normalizedStatus);
        lead.setUpdatedAt(LocalDateTime.now());

        Lead savedLead = leadRepository.save(lead);

        saveActivity(
                leadId,
                actingUserId,
                "STATUS_CHANGED",
                "Status changed from " + oldStatus
                        + " to " + normalizedStatus);

        return savedLead;
    }

    // Get activity history for a lead
    @Transactional(readOnly = true)
    public List<LeadActivity> getLeadActivities(Long leadId) {

        getLeadById(leadId);

        return activityRepository
                .findByLeadIdOrderByCreatedAtDesc(leadId);
    }

    // Get all activities
    @Transactional(readOnly = true)
    public List<LeadActivity> getAllActivities() {
        return activityRepository.findAll();
    }

    // Internal helper to save lead activity
    private void saveActivity(
            Long leadId,
            Long userId,
            String activityType,
            String description) {

        LeadActivity activity = new LeadActivity();

        activity.setLeadId(leadId);
        activity.setUserId(userId);
        activity.setActivityType(activityType);
        activity.setDescription(description);
        activity.setCreatedAt(LocalDateTime.now());

        activityRepository.save(activity);
    }
}