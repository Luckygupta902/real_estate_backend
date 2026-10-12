package com.realestate.app.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.realestate.app.entity.Property;
import com.realestate.app.entity.PropertyMedia;
import com.realestate.app.repository.PropertyMediaRepository;
import com.realestate.app.repository.PropertyRepository;

@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private PropertyMediaRepository propertyMediaRepository;

    // CREATE PROPERTY
    public Property createProperty(Property property, Long userId) {

        // Never trust userId from frontend
        property.setUserId(userId);

        return propertyRepository.save(property);
    }

    // GET ALL PROPERTIES
    public List<Property> getAllProperties() {

        return propertyRepository.findAll();
    }

    // GET MY PROPERTIES
    public List<Property> getMyProperties(Long userId) {

        return propertyRepository.findByUserId(userId);
    }

    // GET ONE PROPERTY
    public Optional<Property> getProperty(Long propertyId) {

        return propertyRepository.findById(propertyId);
    }

    // UPDATE PROPERTY
    public Property updateProperty(
            Long propertyId,
            Property updatedProperty,
            Long userId) {

        Optional<Property> optionalProperty = propertyRepository.findByIdAndUserId(
                propertyId,
                userId);

        if (optionalProperty.isEmpty()) {
            throw new RuntimeException(
                    "Property not found or you are not the owner");
        }

        Property property = optionalProperty.get();

        property.setTitle(updatedProperty.getTitle());
        property.setDescription(updatedProperty.getDescription());
        property.setPropertyType(updatedProperty.getPropertyType());
        property.setListingType(updatedProperty.getListingType());
        property.setPrice(updatedProperty.getPrice());
        property.setArea(updatedProperty.getArea());
        property.setBedrooms(updatedProperty.getBedrooms());
        property.setBathrooms(updatedProperty.getBathrooms());
        property.setAddress(updatedProperty.getAddress());
        property.setCity(updatedProperty.getCity());
        property.setState(updatedProperty.getState());
        property.setPincode(updatedProperty.getPincode());
        property.setLatitude(updatedProperty.getLatitude());
        property.setLongitude(updatedProperty.getLongitude());
        property.setStatus(updatedProperty.getStatus());

        return propertyRepository.save(property);
    }

    // DELETE PROPERTY
    public void deleteProperty(
            Long propertyId,
            Long userId) {

        Optional<Property> optionalProperty = propertyRepository.findByIdAndUserId(
                propertyId,
                userId);

        if (optionalProperty.isEmpty()) {
            throw new RuntimeException(
                    "Property not found or you are not the owner");
        }

        // Delete media first
        propertyMediaRepository.deleteByPropertyId(propertyId);

        // Delete property
        propertyRepository.deleteById(propertyId);
    }

    // ADD MEDIA
    public PropertyMedia addMedia(
            Long propertyId,
            PropertyMedia media,
            Long userId) {

        Optional<Property> property = propertyRepository.findByIdAndUserId(
                propertyId,
                userId);

        if (property.isEmpty()) {
            throw new RuntimeException(
                    "Property not found or you are not the owner");
        }

        media.setPropertyId(propertyId);

        return propertyMediaRepository.save(media);
    }

    // GET PROPERTY MEDIA
    public List<PropertyMedia> getPropertyMedia(
            Long propertyId) {

        return propertyMediaRepository
                .findByPropertyId(propertyId);
    }

    // DELETE MEDIA
    public void deleteMedia(
            Long mediaId,
            Long userId) {

        Optional<PropertyMedia> media = propertyMediaRepository.findById(mediaId);

        if (media.isEmpty()) {
            throw new RuntimeException("Media not found");
        }

        Long propertyId = media.get().getPropertyId();

        Optional<Property> property = propertyRepository.findByIdAndUserId(
                propertyId,
                userId);

        if (property.isEmpty()) {
            throw new RuntimeException(
                    "You are not the owner of this property");
        }

        propertyMediaRepository.deleteById(mediaId);
    }
}