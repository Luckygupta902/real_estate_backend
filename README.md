<<<<<<< HEAD
# 🏠 Real Estate Application - Backend API

A Spring Boot REST API for a real estate platform with user management, property listings, social features (likes/comments/follows), messaging, lead management, and notifications.

## 🚀 Tech Stack

| Layer          | Technology                                              |
| -------------- | ------------------------------------------------------- |
| **Language**   | Java 17+                                                |
| **Framework**  | Spring Boot 3.x (Spring MVC, Spring Security, Spring Data JPA) |
| **Database**   | MySQL 8.x                                               |
| **Auth**       | JWT (JSON Web Tokens) with HMAC-SHA256                  |
| **Build**      | Maven (pom.xml expected at project root)                |
| **ORM**        | Hibernate 6.x (via Spring Data JPA)                     |
| **Email**      | Spring Mail (Gmail SMTP)                                |
| **Server**     | Apache Tomcat (embedded via Spring Boot)                |
| **Port**       | `8080` (default)                                        |

---

## 📁 Project Structure

```
src/main/java/com/realestate/app/
├── AppApplication.java              # Main entry point
├── configuration/
│   ├── EmailService.java            # Email sending (activation)
│   ├── JwtAuthenticationFilter.java # JWT request filter
│   ├── JwtService.java              # JWT generation & parsing
│   └── SecurityConfig.java          # Spring Security rules
├── controller/
│   ├── AdminUserController.java     # Admin: CRUD users
│   ├── ConversationController.java  # Messaging between users
│   ├── LeadController.java          # Lead management
│   ├── NotificationController.java  # In-app notifications
│   ├── PropertyController.java      # Property CRUD + media
│   ├── SocialController.java        # Likes, comments, follows
│   └── UserController.java          # Registration, login, activation
├── entity/
│   ├── Comment.java
│   ├── Conversation.java
│   ├── ConversationParticipant.java
│   ├── Follow.java
│   ├── Lead.java
│   ├── LeadActivity.java
│   ├── Like.java
│   ├── Message.java
│   ├── Notification.java
│   ├── Property.java
│   ├── PropertyMedia.java
│   ├── Report.java
│   ├── Role.java
│   ├── SavedProperty.java
│   └── User.java
├── repository/                      # Spring Data JPA repositories
├── request/
│   ├── LoginRequest.java            # Login DTO
│   └── UserRequest.java             # Registration DTO
└── service/
    ├── ConversationService.java
    ├── LeadService.java
    ├── NotificationService.java
    ├── PropertyService.java
    ├── SocialService.java
    └── UserService.java
```

---

## 🔐 Authentication & Security

### JWT Token

The app uses **JWT (JSON Web Token)** for stateless authentication.

#### Token Structure

A JWT token contains three base64-encoded parts separated by dots:

```
header.payload.signature
```

**Header:**
```json
{"alg":"HS256"}
```

**Payload (example):**
```json
{
  "sub": "5",                          // User ID
  "email": "user@example.com",          // Email
  "roleId": 1,                         // Role ID
  "iat": 1791744408,                   // Issued at
  "exp": 1792349208                    // Expiry (7 days)
}
```

#### Role Mapping

| Role ID | Role Name | Authority String   |
| ------- | --------- | ------------------ |
| 1       | USER      | `ROLE_USER`        |
| 2       | ADMIN     | `ROLE_ADMIN`       |
| 3       | AGENT     | `ROLE_AGENT`       |
| 4       | BUILDER   | `ROLE_BUILDER`     |
| 5       | BROKER    | `ROLE_BROKER`      |

#### JWT Secret Key

The secret key is hardcoded in `JwtService.java`:
```java
"mySecretKeyForRealEstateApplication123456789"
```
> ⚠️ **For production**: Move this to an environment variable or a secure vault.

#### Authentication Flow

1. User registers via `POST /api/auth/user`
2. User activates account via email link (`POST /api/auth/activate?email=...`)
3. User logs in via `POST /api/auth/login` → receives JWT token
4. Client sends `Authorization: Bearer <token>` header with every request
5. `JwtAuthenticationFilter` validates the token and sets the Spring Security context

#### How User ID Flows Through the System

```
JwtService.generateToken()
  └─ sets JWT subject = String.valueOf(user.getId())

JwtAuthenticationFilter.doFilterInternal()
  └─ reads claims.getSubject() → sets as Authentication principal

Controller.currentUserId(Authentication auth)
  └─ Long.valueOf(authentication.getName()) → userId as Long
```

### Security Rules (SecurityConfig)

| Pattern                          | Method   | Required Role                          |
| -------------------------------- | -------- | -------------------------------------- |
| `/api/auth/user`                 | POST     | Public (permitAll)                     |
| `/api/auth/login`                | POST     | Public (permitAll)                     |
| `/api/auth/activate`             | POST     | Public (permitAll)                     |
| `/error`                         | ALL      | Public (permitAll)                     |
| `OPTIONS /**`                    | OPTIONS  | Public (permitAll)                     |
| `/api/admin/users`               | POST     | ADMIN                                  |
| `/api/admin/users/all`           | GET      | ADMIN                                  |
| `/api/admin/users{id}`           | GET      | ADMIN                                  |
| `/api/admin/users**`             | PUT      | ADMIN                                  |
| `/api/admin/users/`              | DELETE   | ADMIN                                  |
| `/api/properties/create`         | POST     | ADMIN, AGENT, BUILDER, BROKER          |
| `/api/properties/my`             | GET      | ADMIN, AGENT, USER, BUILDER, BROKER    |
| `/api/properties/**` (GET)       | GET      | ADMIN, AGENT, USER, BUILDER, BROKER    |
| `/api/properties/**` (PUT)       | PUT      | ADMIN, AGENT, BUILDER, BROKER          |
| `/api/properties/**` (DELETE)    | DELETE   | ADMIN, AGENT, BUILDER, BROKER          |
| `/api/properties/*/media` (POST) | POST     | ADMIN, AGENT, BUILDER, BROKER          |
| `/api/properties/*/media` (GET)  | GET      | ADMIN, AGENT, USER, BUILDER, BROKER    |
| `/api/properties/**` (remaining) | ALL      | Authenticated                          |
| `/api/conversations/admin/all`   | GET      | ADMIN                                  |
| `/api/conversations/**` (DELETE) | DELETE   | ADMIN                                  |
| `/api/conversations/*/messages`  | POST     | ADMIN, AGENT, USER, BUILDER, BROKER    |
| `/api/conversations/**`          | ALL      | ADMIN, AGENT, USER                     |
| `/**` (any other)                | ALL      | Authenticated                          |

---

## 🗄️ Database Schema

### Database: `real_estate` (MySQL)

Hibernate `ddl-auto=update` auto-creates/updates tables on startup.

### Entity Relationship Diagram (Text)

```
User ──1:N──> Property       (userId)
User ──1:N──> Comment         (userId)
User ──1:N──> Like            (userId)
User ──1:N──> Follow          (followerId / followingId)
User ──1:N──> Conversation    (via ConversationParticipant)
User ──1:N──> Message         (senderId)
User ──1:N──> Notification    (userId)
User ──1:N──> Lead            (customerId / assignedTo)
User ──1:N──> Report          (reportedBy / reportedUserId)
User ──1:N──> SavedProperty   (userId)

Property ──1:N──> PropertyMedia  (propertyId)
Property ──1:N──> Like           (propertyId)
Property ──1:N──> Comment        (propertyId)
Property ──1:N──> Conversation   (propertyId)
Property ──1:N──> Lead           (propertyId)
Property ──1:N──> SavedProperty  (propertyId)
Property ──1:N──> Report         (propertyId)

Conversation ──1:N──> ConversationParticipant  (conversationId)
Conversation ──1:N──> Message                   (conversationId)

Lead ──1:N──> LeadActivity   (leadId)
```

---

### Table: `user`

| Column         | Type           | Constraints                |
| -------------- | -------------- | -------------------------- |
| `id`           | BIGINT         | PK, AUTO_INCREMENT         |
| `role_id`      | BIGINT         | NOT NULL                   |
| `name`         | VARCHAR(255)   |                            |
| `email`        | VARCHAR(255)   | NOT NULL, UNIQUE           |
| `password`     | VARCHAR(255)   | NOT NULL, UNIQUE (bcrypt)  |
| `phone`        | VARCHAR(255)   | NOT NULL, UNIQUE           |
| `profile_image`| VARCHAR(255)   |                            |
| `city`         | VARCHAR(255)   |                            |
| `state`        | VARCHAR(255)   |                            |
| `is_active`    | BIT(1)         | Default false              |
| `created_at`   | DATETIME       |                            |
| `updated_at`   | DATETIME       |                            |

**Notes:**
- Passwords are stored as bcrypt hashes
- New users must activate their account via email before logging in
- `role_id` references the `role` table (1=USER, 2=ADMIN, 3=AGENT, 4=BUILDER, 5=BROKER)

---

### Table: `role`

| Column | Type           | Constraints        |
| ------ | -------------- | ------------------ |
| `id`   | BIGINT         | PK, AUTO_INCREMENT |
| `name` | VARCHAR(255)   |                    |

**Seed data expected:**
| id | name    |
| -- | ------- |
| 1  | USER    |
| 2  | ADMIN   |
| 3  | AGENT   |
| 4  | BUILDER |
| 5  | BROKER  |

---

### Table: `property`

| Column         | Type           | Constraints        |
| -------------- | -------------- | ------------------ |
| `id`           | BIGINT         | PK, AUTO_INCREMENT |
| `user_id`      | BIGINT         | FK → user(id)      |
| `title`        | VARCHAR(255)   |                    |
| `description`  | TEXT / VARCHAR |                    |
| `property_type`| VARCHAR(255)   | e.g. Apartment, House, Plot, Commercial |
| `listing_type` | VARCHAR(255)   | e.g. Sell, Rent |
| `price`        | DECIMAL        |                    |
| `area`         | DECIMAL        | sq. ft. / sq. m.   |
| `bedrooms`     | INT            |                    |
| `bathrooms`    | INT            |                    |
| `address`      | VARCHAR(255)   |                    |
| `city`         | VARCHAR(255)   |                    |
| `state`        | VARCHAR(255)   |                    |
| `pincode`      | VARCHAR(255)   |                    |
| `latitude`     | DECIMAL        |                    |
| `longitude`    | DECIMAL        |                    |
| `status`       | VARCHAR(255)   | e.g. Available, Sold, Rented, Pending |
| `created_at`   | DATETIME       |                    |
| `updated_at`   | DATETIME       |                    |

---

### Table: `property_media`

| Column          | Type           | Constraints            |
| --------------- | -------------- | ---------------------- |
| `id`            | BIGINT         | PK, AUTO_INCREMENT     |
| `property_id`   | BIGINT         | FK → property(id)      |
| `media_type`    | VARCHAR(255)   | e.g. Image, Video, Tour|
| `media_url`     | VARCHAR(255)   |                        |
| `thumbnail_url` | VARCHAR(255)   |                        |
| `created_at`    | DATETIME       |                        |

---

### Table: `likes`

| Column        | Type           | Constraints        |
| ------------- | -------------- | ------------------ |
| `id`          | BIGINT         | PK, AUTO_INCREMENT |
| `user_id`     | BIGINT         | FK → user(id)      |
| `property_id` | BIGINT         | FK → property(id)  |
| `created_at`  | DATETIME       |                    |

**Unique constraint:** A user can like a property only once (enforced in code, not DB).

---

### Table: `comment`

| Column         | Type           | Constraints        |
| -------------- | -------------- | ------------------ |
| `id`           | BIGINT         | PK, AUTO_INCREMENT |
| `user_id`      | BIGINT         | FK → user(id)      |
| `property_id`  | BIGINT         | FK → property(id)  |
| `comment_text` | VARCHAR(255)   | Max 2000 chars     |
| `created_at`   | DATETIME       |                    |
| `updated_at`   | DATETIME       |                    |

---

### Table: `follow`

| Column         | Type           | Constraints        |
| -------------- | -------------- | ------------------ |
| `id`           | BIGINT         | PK, AUTO_INCREMENT |
| `follower_id`  | BIGINT         | FK → user(id)      |
| `following_id` | BIGINT         | FK → user(id)      |
| `created_at`   | DATETIME       |                    |

**Business rule:** A user cannot follow themselves.

---

### Table: `conversation`

| Column        | Type           | Constraints        |
| ------------- | -------------- | ------------------ |
| `id`          | BIGINT         | PK, AUTO_INCREMENT |
| `property_id` | BIGINT         | FK → property(id)  |
| `created_at`  | DATETIME       |                    |
| `updated_at`  | DATETIME       | updated on new msg |

---

### Table: `conversation_participant`

| Column            | Type   | Constraints                  |
| ----------------- | ------ | ---------------------------- |
| `id`              | BIGINT | PK, AUTO_INCREMENT           |
| `conversation_id` | BIGINT | FK → conversation(id)        |
| `user_id`         | BIGINT | FK → user(id)                |

**Note:** Each conversation has exactly 2 participants (the property owner and the interested user).

---

### Table: `message`

| Column            | Type           | Constraints              |
| ----------------- | -------------- | ------------------------ |
| `id`              | BIGINT         | PK, AUTO_INCREMENT       |
| `conversation_id` | BIGINT         | FK → conversation(id)    |
| `sender_id`       | BIGINT         | FK → user(id)            |
| `message_text`    | VARCHAR(255)   | Max 5000 chars           |
| `is_read`         | BIT(1)         | Default false            |
| `created_at`      | DATETIME       |                          |

---

### Table: `leads`

| Column        | Type           | Constraints            |
| ------------- | -------------- | ---------------------- |
| `id`          | BIGINT         | PK, AUTO_INCREMENT     |
| `customer_id` | BIGINT         | FK → user(id)          |
| `property_id` | BIGINT         | FK → property(id)      |
| `assigned_to` | BIGINT         | FK → user(id), nullable|
| `status`      | VARCHAR(255)   | Default: "NEW"         |
| `source`      | VARCHAR(255)   |                        |
| `message`     | VARCHAR(255)   |                        |
| `created_at`  | DATETIME       |                        |
| `updated_at`  | DATETIME       |                        |

**Allowed statuses:** NEW, CONTACTED, QUALIFIED, VISIT_SCHEDULED, NEGOTIATION, CONVERTED, LOST

---

### Table: `lead_activity`

| Column          | Type           | Constraints        |
| --------------- | -------------- | ------------------ |
| `id`            | BIGINT         | PK, AUTO_INCREMENT |
| `lead_id`       | BIGINT         | FK → leads(id)     |
| `user_id`       | BIGINT         | FK → user(id)      |
| `activity_type` | VARCHAR(255)   |                    |
| `description`   | VARCHAR(255)   |                    |
| `created_at`    | DATETIME       |                    |

---

### Table: `notification`

| Column         | Type           | Constraints        |
| -------------- | -------------- | ------------------ |
| `id`           | BIGINT         | PK, AUTO_INCREMENT |
| `user_id`      | BIGINT         | FK → user(id)      |
| `type`         | VARCHAR(255)   |                    |
| `title`        | VARCHAR(255)   |                    |
| `message`      | TEXT           |                    |
| `reference_id` | BIGINT         | (polymorphic ref)  |
| `is_read`      | BIT(1)         | Default false      |
| `created_at`   | DATETIME       |                    |

---

### Table: `report`

| Column            | Type           | Constraints        |
| ----------------- | -------------- | ------------------ |
| `id`              | BIGINT         | PK, AUTO_INCREMENT |
| `reported_by`     | BIGINT         | FK → user(id)      |
| `property_id`     | BIGINT         | FK → property(id)  |
| `reported_user_id`| BIGINT         | FK → user(id)      |
| `reason`          | VARCHAR(255)   |                    |
| `description`     | VARCHAR(255)   |                    |
| `status`          | VARCHAR(255)   |                    |
| `created_at`      | DATETIME       |                    |

---

### Table: `saved_property`

| Column        | Type           | Constraints        |
| ------------- | -------------- | ------------------ |
| `id`          | BIGINT         | PK, AUTO_INCREMENT |
| `user_id`     | BIGINT         | FK → user(id)      |
| `property_id` | BIGINT         | FK → property(id)  |
| `created_at`  | DATETIME       |                    |

---

## 🌐 API Endpoints

### Authentication & Users

| Method | URL                        | Description              | Auth Required |
| ------ | -------------------------- | ------------------------ | ------------- |
| POST   | `/api/auth/user`           | Register new user        | ❌ No         |
| POST   | `/api/auth/login`          | Login → returns JWT      | ❌ No         |
| POST   | `/api/auth/activate`       | Activate account by email| ❌ No         |

**Registration Request Body:**
```json
{
  "roleId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "9876543210",
  "profileImage": "url",
  "city": "Mumbai",
  "state": "Maharashtra"
}
```

**Login Response:**
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### Admin User Management

| Method | URL                          | Description          | Auth     |
| ------ | ---------------------------- | -------------------- | -------- |
| POST   | `/api/admin/users`           | Create user (admin)  | ADMIN    |
| GET    | `/api/admin/users/all`       | List all users       | ADMIN    |
| GET    | `/api/admin/users/{id}`      | Get user by ID       | ADMIN    |
| PUT    | `/api/admin/users/{id}`      | Update user          | ADMIN    |
| DELETE | `/api/admin/users/{id}`      | Delete user          | ADMIN    |

### Property Management

| Method | URL                              | Description                     | Auth Required              |
| ------ | -------------------------------- | ------------------------------- | -------------------------- |
| POST   | `/api/properties/create`         | Create a property               | ADMIN, AGENT, BUILDER, BROKER |
| GET    | `/api/properties`                | List all properties             | ADMIN, AGENT, USER, BUILDER, BROKER |
| GET    | `/api/properties/my`             | Get current user's properties   | ADMIN, AGENT, USER, BUILDER, BROKER |
| GET    | `/api/properties/{id}`           | Get property by ID              | ADMIN, AGENT, USER, BUILDER, BROKER |
| PUT    | `/api/properties/{id}`           | Update property                 | ADMIN, AGENT, BUILDER, BROKER |
| DELETE | `/api/properties/{id}`           | Delete property                 | ADMIN, AGENT, BUILDER, BROKER |
| POST   | `/api/properties/{id}/media`     | Add media to property           | ADMIN, AGENT, BUILDER, BROKER |
| GET    | `/api/properties/{id}/media`     | Get property media              | ADMIN, AGENT, USER, BUILDER, BROKER |
| DELETE | `/api/properties/media/{mediaId}`| Delete media                    | ADMIN, AGENT, BUILDER, BROKER |

### Social Features (Likes, Comments, Follows)

| Method | URL                                     | Description                        | Auth Required |
| ------ | --------------------------------------- | ---------------------------------- | ------------- |
| POST   | `/api/properties/{propertyId}/likes`    | Like a property                    | Authenticated |
| DELETE | `/api/properties/{propertyId}/likes`    | Unlike a property                  | Authenticated |
| GET    | `/api/properties/{propertyId}/likes`    | Get likes for a property           | Authenticated |
| GET    | `/api/properties/{propertyId}/likes/count` | Get like count                 | Authenticated |
| GET    | `/api/likes/my`                         | Get properties I liked             | Authenticated |
| POST   | `/api/properties/{propertyId}/comments` | Add comment to property            | Authenticated |
| GET    | `/api/properties/{propertyId}/comments` | Get property comments              | Authenticated |
| PUT    | `/api/comments/{commentId}`             | Update comment (owner or admin)    | Authenticated |
| DELETE | `/api/comments/{commentId}`             | Delete comment (owner or admin)    | Authenticated |
| GET    | `/api/comments/my`                      | Get my comments                    | Authenticated |
| POST   | `/api/users/{userId}/follow`            | Follow a user                      | Authenticated |
| DELETE | `/api/users/{userId}/follow`            | Unfollow a user                    | Authenticated |
| GET    | `/api/users/{userId}/following`         | Get who a user follows             | Authenticated |
| GET    | `/api/users/{userId}/followers`         | Get a user's followers             | Authenticated |
| GET    | `/api/users/{userId}/followers/count`   | Count followers                    | Authenticated |
| GET    | `/api/users/{userId}/following/count`   | Count following                    | Authenticated |

### Conversations & Messages

| Method | URL                                                        | Description                       | Auth Required              |
| ------ | ---------------------------------------------------------- | --------------------------------- | -------------------------- |
| POST   | `/api/conversations`                                       | Start conversation about property | ADMIN, AGENT, USER         |
| GET    | `/api/conversations/my`                                    | Get my conversations              | ADMIN, AGENT, USER         |
| GET    | `/api/conversations/admin/all`                             | Admin: get all conversations      | ADMIN                      |
| GET    | `/api/conversations/{id}`                                  | Get conversation details          | ADMIN, AGENT, USER         |
| POST   | `/api/conversations/{id}/messages`                         | Send message in conversation      | ADMIN, AGENT, USER, BUILDER, BROKER |
| GET    | `/api/conversations/{id}/messages`                         | Get conversation messages         | ADMIN, AGENT, USER         |
| PUT    | `/api/conversations/{convId}/messages/{msgId}/read`        | Mark message as read              | ADMIN, AGENT, USER         |
| DELETE | `/api/conversations/{id}`                                  | Delete conversation (admin only)  | ADMIN                      |

**Start Conversation Request Body:**
```json
{
  "propertyId": 1
}
```

**Send Message Request Body:**
```json
{
  "messageText": "Hi, I'm interested in this property"
}
```

### Lead Management

| Method | URL                                       | Description                | Auth Required |
| ------ | ----------------------------------------- | -------------------------- | ------------- |
| POST   | `/api/leads`                              | Create a new lead          | Authenticated |
| GET    | `/api/leads`                              | Get all leads              | Authenticated |
| GET    | `/api/leads/{id}`                         | Get lead by ID             | Authenticated |
| GET    | `/api/leads/customer/{customerId}`        | Get leads by customer      | Authenticated |
| GET    | `/api/leads/property/{propertyId}`        | Get leads for property     | Authenticated |
| GET    | `/api/leads/employee/{employeeId}`        | Get leads assigned to emp  | Authenticated |
| PUT    | `/api/leads/{id}`                         | Update lead details        | Authenticated |
| PATCH  | `/api/leads/{id}/assign`                  | Assign lead to employee    | Authenticated |
| PATCH  | `/api/leads/{id}/status`                  | Update lead status         | Authenticated |
| GET    | `/api/leads/{id}/activities`              | Get lead activity history  | Authenticated |
| GET    | `/api/leads/activities/all`               | Get all activities         | Authenticated |

**Lead Status Values:** `NEW`, `CONTACTED`, `QUALIFIED`, `VISIT_SCHEDULED`, `NEGOTIATION`, `CONVERTED`, `LOST`

### Notifications

| Method | URL                                           | Description                    | Auth Required |
| ------ | --------------------------------------------- | ------------------------------ | ------------- |
| POST   | `/api/notifications`                          | Create notification            | Authenticated |
| GET    | `/api/notifications/user/{userId}`            | Get user notifications         | Authenticated |
| GET    | `/api/notifications/user/{userId}/unread`     | Get unread notifications       | Authenticated |
| GET    | `/api/notifications/user/{userId}/unread/count`| Count unread                  | Authenticated |
| PUT    | `/api/notifications/{id}/read`                | Mark notification as read      | Authenticated |
| PUT    | `/api/notifications/user/{userId}/read-all`   | Mark all as read               | Authenticated |
| DELETE | `/api/notifications/{id}`                     | Delete notification            | Authenticated |

---

## ⚙️ Configuration (application.properties)

```properties
spring.application.name=app
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/real_estate
spring.datasource.username=root
spring.datasource.password=root@123
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=lucky2301196@gmail.com
spring.mail.password=osex azlz qfbo pwon
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🧪 Getting Started

### Prerequisites

1. **Java 17+** installed
2. **MySQL 8.x** installed and running
3. **Maven** installed (or use the Maven wrapper)

### Setup Steps

```bash
# 1. Create the database
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS real_estate;"

# 2. Navigate to project directory
cd app

# 3. Build the project
mvn clean install

# 4. Run the application
mvn spring-boot:run
```

The server starts on `http://localhost:8080`.

### Seed the Roles Table

If the `role` table wasn't auto-created with seed data, insert manually:

```sql
INSERT INTO role (id, name) VALUES
(1, 'USER'),
(2, 'ADMIN'),
(3, 'AGENT'),
(4, 'BUILDER'),
(5, 'BROKER');
```

---

## 🔌 Postman Usage

### 1. Register a User
```http
POST http://localhost:8080/api/auth/user
Content-Type: application/json

{
  "roleId": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phone": "9876543210",
  "city": "Mumbai",
  "state": "Maharashtra"
}
```

### 2. Activate Account
```http
POST http://localhost:8080/api/auth/activate?email=john@example.com
```

### 3. Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

Copy the `token` from the response.

### 4. Use the Token

Set a header on all subsequent requests:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### 5. Like a Property
```http
POST http://localhost:8080/api/properties/1/likes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## ❗ Troubleshooting Common Issues

| Problem                          | Likely Cause                                    | Solution                                          |
| -------------------------------- | ----------------------------------------------- | ------------------------------------------------- |
| 404 Not Found on API calls       | Server not running / MySQL not running           | Start MySQL, then run `mvn spring-boot:run`       |
| 401 Unauthorized                 | Missing / expired / invalid JWT                  | Re-login to get fresh token                       |
| 403 Forbidden                    | User role doesn't have permission                | Check roleId in token matches required roles      |
| "Email already exists"           | Duplicate registration                           | Use a different email                             |
| "Phone number already exists"    | Duplicate phone                                  | Use a different phone number                      |
| "Please activate your account"   | Account not activated via email                  | Call `/api/auth/activate?email=...`               |
| "Property not found"             | Property ID doesn't exist in database            | Create the property first                         |
| "You have already liked this"    | Cannot like the same property twice              | Use DELETE endpoint to unlike first               |
| Email not sending                | Gmail SMTP credentials incorrect                 | Update app password in application.properties     |
| Connection refused (database)    | MySQL not running                                | Start MySQL service (e.g., `net start MySQL80`)   |

---

## 📝 Key Business Logic Notes

- **Property Ownership**: Only the owner (or an admin) can update/delete a property
- **Self-follow Prevention**: Users cannot follow themselves
- **Self-messaging Prevention**: Users cannot start a conversation with themselves about their own property
- **Like Uniqueness**: One like per user per property (enforced at service level)
- **Comment Editing**: Only the comment author or an admin can edit/delete
- **Message Read**: A sender cannot mark their own message as read
- **Lead Status**: Validated against an allowed list; status changes are logged as activities
- **Password Storage**: Passwords stored as bcrypt hashes via `BCryptPasswordEncoder`
- **Account Activation**: New accounts are inactive until the activation link is visited
- **CORS**: Configured to allow all origins (development setting; restrict for production)
=======
# real_estate_backend
>>>>>>> 3b4715cbb0475f663003be57fd6ed11fb1715092
