# **Brain Dissecting**

![Indexpage Screenshot](BrainDissecting-SSR-version/screenshots/indexpage.png)

**Explore, manage, and moderate a diverse collection of scientific articles.**

---  

![Users-register Screenshot](BrainDissecting-SSR-version/screenshots/users-register.png)

## **Overview**

*Brain Dissecting* is a full-stack web application built with **Spring Boot**, **Thymeleaf**, and **MySQL**, showcasing a seamless integration of **Gradle** and some **JavaScript** for enhanced user experience. It is designed for tech enthusiasts, science lovers, and curious minds, enabling exploration, engagement, and moderation of scientific content.  

![Login Screenshot](BrainDissecting-SSR-version/screenshots/login.png)

This project serves as a portfolio application, highlighting modern web development practices, role-based access control, and content management features.  

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
  - Review and approve/reject suggestions for new topics submitted by moderators.
- **Content Management**:
  - Fetch articles by specific topics from an external API.
  - Approve or reject fetched pending articles.
  - Delete articles permanently.

---

## **Tech Stack**

- **Backend**: Spring Boot, Spring Security, Hibernate, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap, JavaScript
- **Database**: MySQL
- **Build Tool**: Gradle
- **External API**: Integration with [DOAJ API](https://doaj.org/) for fetching articles
- **Testing**: JUnit and MockMvc for unit and integration testing

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

3. **Configure Environment Variables**
- Set up the following environment variables for database credentials:


db_username: Your MySQL username  
db_password: Your MySQL password  


Example:  

bash:  
export db_username=yourUsername  
export db_password=yourPassword  

4. **Run the Application**  

- Using Gradle:  
bash:  
./gradlew bootRun  

- Alternatively, open the project in IntelliJ IDEA and run the BrainDissectingSsrVersionApplication class.  

5. **Access the application**  

Open your browser and navigate to: http://localhost:8080  

---

### Testing the Application  

The project includes integration and unit tests.  

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
