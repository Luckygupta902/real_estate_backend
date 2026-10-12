package com.realestate.app.request;


public class UserRequest {

    private Long roleId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String profileImage;
    private String city;
    private String state;

    // Validation method
    public void validate() {

      /*  // Role ID
        if (roleId >0) {
            throw new IllegalArgumentException(
                    "Role ID must be greater than 0");
        }*/

        // Name
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name is required");
        }

        if (name.length() < 3) {
            throw new IllegalArgumentException(
                    "Name must contain at least 3 characters");
        }

        // Email
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Email is required");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException(
                    "Invalid email");
        }

        // Password
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Password is required");
        }

        if (password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters");
        }

        // Phone
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Phone is required");
        }

        if (phone.length() != 10) {
            throw new IllegalArgumentException(
                    "Phone must contain exactly 10 digits");
        }

        for (int i = 0; i < phone.length(); i++) {

            char character = phone.charAt(i);

            if (!Character.isDigit(character)) {
                throw new IllegalArgumentException(
                        "Phone must contain only digits");
            }
        }

        // City
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "City is required");
        }

        // State
        if (state == null || state.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "State is required");
        }
    }

    // Getters and Setters

    public long getRoleId() {
        return roleId;
    }

    public void setRoleId(long roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}