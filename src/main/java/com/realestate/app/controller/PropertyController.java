package com.realestate.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.realestate.app.entity.Property;
import com.realestate.app.entity.PropertyMedia;
import com.realestate.app.service.PropertyService;

@RestController
@RequestMapping("/api/properties")  
public class PropertyController {

    @Autowired
    private PropertyService propertyService;


    // CREATE PROPERTY
    @PostMapping("/create")
    public ResponseEntity<Property> createProperty(
            @RequestBody Property property,
            Authentication authentication) {

        Long userId =
                Long.valueOf(authentication.getName());

        Property savedProperty =
                propertyService.createProperty(
                        property,
                        userId
                );

        return ResponseEntity.ok(savedProperty);
    }


    // GET ALL
    @GetMapping
    public ResponseEntity<List<Property>> getAllProperties() {

        return ResponseEntity.ok(
                propertyService.getAllProperties()
        );
    }


    // GET MY PROPERTIES
    @GetMapping("/my")
    public ResponseEntity<List<Property>> getMyProperties(
            Authentication authentication) {

        Long userId =
                Long.valueOf(authentication.getName());

        return ResponseEntity.ok(
                propertyService.getMyProperties(userId)
        );
    }


    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<Property> getProperty(
            @PathVariable Long id) {

        return propertyService
                .getProperty(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }


    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Property> updateProperty(
            @PathVariable Long id,
            @RequestBody Property property,
            Authentication authentication) {

        Long userId =
                Long.valueOf(authentication.getName());

        Property updated =
                propertyService.updateProperty(
                        id,
                        property,
                        userId
                );

        return ResponseEntity.ok(updated);
    }


    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProperty(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId =
                Long.valueOf(authentication.getName());

        propertyService.deleteProperty(
                id,
                userId
        );

        return ResponseEntity.ok(
                "Property deleted successfully"
        );
    }


    // ADD MEDIA
    @PostMapping("/{propertyId}/media")
    public ResponseEntity<PropertyMedia> addMedia(
            @PathVariable Long propertyId,
            @RequestBody PropertyMedia media,
            Authentication authentication) {

        Long userId =
                Long.valueOf(authentication.getName());

        PropertyMedia saved =
                propertyService.addMedia(
                        propertyId,
                        media,
                        userId
                );

        return ResponseEntity.ok(saved);
    }


    // GET MEDIA
    @GetMapping("/{propertyId}/media")
    public ResponseEntity<List<PropertyMedia>> getMedia(
            @PathVariable Long propertyId) {

        return ResponseEntity.ok(
                propertyService.getPropertyMedia(
                        propertyId
                )
        );
    }


    // DELETE MEDIA
    @DeleteMapping("/media/{mediaId}")
    public ResponseEntity<String> deleteMedia(
            @PathVariable Long mediaId,
            Authentication authentication) {

        Long userId =
                Long.valueOf(authentication.getName());

        propertyService.deleteMedia(
                mediaId,
                userId
        );

        return ResponseEntity.ok(
                "Media deleted successfully"
        );
    }
}
