# **Brain Dissecting** 

---  

***Brain Dissecting Website***:  [BrainDissecting](https://braindissecting-ssr-version-production.up.railway.app)

link to -> [Video Preview](https://youtu.be/e7vQ6qeueW4?si=4O9dF8BON2g5R24B)

---

![Indexpage Screenshot](BrainDissecting-SSR-version/screenshots/indexpage.png)

**Explore, manage, and moderate a diverse collection of scientific articles.**

---  

![Registration Screenshot](BrainDissecting-SSR-version/screenshots/registration.png)

![Registration validation Screenshot](BrainDissecting-SSR-version/screenshots/registration-validation.png)

![Users-register Screenshot](BrainDissecting-SSR-version/screenshots/users-register.png)

---

## **Overview**

*Brain Dissecting* is a full-stack web application built with -> **Tech Stack**:
- Java 21: The core programming language used for backend development.
- Spring Boot 3.3.2: For building the web application, including:
- Spring Boot Starter Web: Provides core features for developing web applications.
- Spring Boot Starter Data JPA: For database interactions and ORM with Hibernate.
- Spring Boot Starter Security: For securing the application.
- Spring Boot Starter Validation: For input validation.
- Spring Boot DevTools: For rapid development with hot reload.
---
- **External API**: Integration with <a href="https://doaj.org/api/v3/docs" target="_blank">DOAJ API</a> for fetching articles
---
- Thymeleaf: Used as the templating engine for rendering HTML, enhanced with:
- Thymeleaf Extras Spring Security 6: For integrating security with Thymeleaf views.
- Bootstrap: Ensuring a consistent and user-friendly front-end experience.
- ModelMapper: For mapping DTOs to entities and vice versa.
- JSONPath: For extracting data from JSON responses.
- JSoup: For parsing and cleaning HTML.
- MySQL: The database used in production.
- HSQLDB: An in-memory database used for testing purposes.
- JUnit 5: For writing and running unit and integration tests.
- Spring Security Test: For testing security configurations.

It is designed for tech enthusiasts, science lovers, and curious minds, enabling exploration, engagement, and moderation of scientific content.  

![Login wrong data Screenshot](BrainDissecting-SSR-version/screenshots/login-wrong-data.png)  

![Login validation](BrainDissecting-SSR-version/screenshots/login-validation.png)

![Login Screenshot](BrainDissecting-SSR-version/screenshots/login.png)  

![Homepage Screenshot](BrainDissecting-SSR-version/screenshots/homepage.png)

---

## **Key Features**

### **For All Users**
- **Browse Articles**: Discover and explore curated scientific articles by topic or category.
  
![All articles Screenshot](BrainDissecting-SSR-version/screenshots/all-articles.png)  

---

- **Save Favorites**: Manage a personalized list of favorite articles.

![My favourites Screenshot](BrainDissecting-SSR-version/screenshots/my-favourites.png)  

--- 

- **Engage via Comments**: Comment on articles and interact with other users.

![Writing comment Screenshot](BrainDissecting-SSR-version/screenshots/writing-comment.png)  

![Comment Screenshot](BrainDissecting-SSR-version/screenshots/comment.png)

--- 

- **Profile Management**: Update username and email directly from the profile page.

![My profile page Screenshot](BrainDissecting-SSR-version/screenshots/my-profile.png)  

![Updating profile Screenshot](BrainDissecting-SSR-version/screenshots/updating-profile.png)  

![Profile updated Screenshot](BrainDissecting-SSR-version/screenshots/profile-updated.png)

--- 

### **For Moderators**
- **Suggest Topics**: Propose new topics for future article collections.

![Moderator homepage Screenshot](BrainDissecting-SSR-version/screenshots/moderator-homepage.png)  

![Suggest theme page Screenshot](BrainDissecting-SSR-version/screenshots/suggest-theme.png)  

![Suggesting theme Screenshot](BrainDissecting-SSR-version/screenshots/suggesting-theme.png)  

![Theme suggested Screenshot](BrainDissecting-SSR-version/screenshots/theme-suggested.png)

--- 
  
- **Moderate Articles**: Approve or reject pending articles submitted for publication.

![Pending for approval page Screenshot](BrainDissecting-SSR-version/screenshots/moderator-pending-for-approval.png)  

![Approving article Screenshot](BrainDissecting-SSR-version/screenshots/approving-article.png)  

![Article approved Screenshot](BrainDissecting-SSR-version/screenshots/article-approved.png)

--- 

### **For Admins**
- **User Management**:
  - Promote/demote users to/from the moderator role.

![Admin manage users Screenshot](BrainDissecting-SSR-version/screenshots/admin-manage-users.png)

![Promoting to moderator Screenshot](BrainDissecting-SSR-version/screenshots/promoting-to-moderator.png)

![User promoted Screenshot](BrainDissecting-SSR-version/screenshots/user-promoted.png)

--- 

  - Ban/unban users to maintain a safe environment.

![Banning user Screenshot](BrainDissecting-SSR-version/screenshots/banning-user.png)

![Banned user Screenshot](BrainDissecting-SSR-version/screenshots/user-banned.png)

![Banned user login Screenshot](BrainDissecting-SSR-version/screenshots/banned-user-login.png)

![Account banned Screenshot](BrainDissecting-SSR-version/screenshots/account-banned.png)

--- 

- **Topic Management**:
  - Create new topics for articles.

![Admin manage themes page Screenshot](BrainDissecting-SSR-version/screenshots/admin-manage-themes.png)

![Admin adding theme Screenshot](BrainDissecting-SSR-version/screenshots/admin-adding-theme.png)

![Admin theme added Screenshot](BrainDissecting-SSR-version/screenshots/admin-theme-added.png)

---

  - Review and approve/reject suggestions for new topics submitted by moderators.

![Admin approving moderator suggestion Screenshot](BrainDissecting-SSR-version/screenshots/admin-approving-moderator-suggestion.png)

![Suggestion approved Screenshot](BrainDissecting-SSR-version/screenshots/suggestion-approved.png)  

---

- **Content Management**:
  - Fetch articles by specific topics from an external API.

![Admin updating articles Screenshot](BrainDissecting-SSR-version/screenshots/admin-updating-art.png)  

![Admin articles updated Screenshot](BrainDissecting-SSR-version/screenshots/admin-art-updated.png) 

---

  - Approve or reject fetched pending articles.

![Admin approve/reject fetched articles Screenshot](BrainDissecting-SSR-version/screenshots/admin-approve-fetched-articles.png)  

---

  - Delete articles permanently.

![Admin delete article Screenshot](BrainDissecting-SSR-version/screenshots/admin-delete-article.png)  

---

---

---

## **Setup Instructions**

### **Prerequisites**
- Java 17 or higher
- MySQL
- Gradle

---

### Steps to Run the Application Locally

1. **Clone the Repository**

2. **Set Up the Database**
- Create a MySQL database named brain-dissecting-ssr (or modify the application.yaml to use your preferred database name).
- Ensure your MySQL server is running and accessible.

3. Configure Environment Variables
Set up the following environment variables for database connection:


Variable Name	Description
MYSQLHOST	Your MySQL server host (usually localhost for local runs)
MYSQLPORT	MySQL server port (default is 3306)
MYSQLUSER	Your MySQL username
MYSQLPASSWORD	Your MySQL password
MYSQLDATABASE	Your database name (e.g., brain-dissecting-ssr)

4. **Run the Application**  

- Using Gradle:  
bash:  
./gradlew bootRun  

- Alternatively, open the project in IntelliJ IDEA and run the BrainDissectingSsrVersionApplication class.  

5. **Access the application**  

Open your browser and navigate to: http://localhost:8080  

# **The first user you register will become an admin so that you can explore the full range of functionalities**  

---

### Testing the Application  

The project includes integration and unit tests.  

![Test Screenshot](BrainDissecting-SSR-version/screenshots/test.png)

To run the tests, execute:  

bash:  
./gradlew test  
View the test results in the build/reports/tests/test directory.  

**Optional:**  
  
Use Schema initialization Script  
If you prefer not to rely on Hibernate to generate the schema:  

**Disable Schema Generation:**  
Update application.yaml:  

spring: 
  jpa: 
    hibernate: 
      ddl-auto: none  
  sql: 
    init: 
      mode: always 
