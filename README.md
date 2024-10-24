<div align="center">
  <a href="https://notevino.com/" style="display:inline-block; text-decoration:none; color:inherit;">
    <img src="./notevino/nv-logo-300.png" alt="Logo" width="200px">
  </a>
  <h1 style="margin: 0; text-align:center;">NoteVino</h1>
</div>

## About
<a href="https://notevino.com">NoteVino</a> is a personalized recommendation system and wine tasting notes platform designed for wine enthusiasts. It helps users manage wine information and write tasting notes. Based on users' past tasting records and preferences, NoteVino recommends suitable wines, assisting users in discovering new selections.

## Repositories
This project is divided into two main parts: **Backend** and **Frontend**. You can find the source code for both parts below:

- **Backend Repository**: [View Backend Repo](https://github.com/rzh12/NoteVino-Backend)
- **Frontend Repository**: [View Frontend Repo](https://github.com/rzh12/NoteVino-Frontend)

## Built with
### **Backend**
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?style=for-the-badge&logo=amazon-ec2&logoColor=white)
![Elastic Load Balancing](https://img.shields.io/badge/AWS%20ELB-FF9900?style=for-the-badge&logo=amazon-aws&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS%20RDS-527FFF?style=for-the-badge&logo=amazon-aws&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazon-s3&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![AWS ElastiCache](https://img.shields.io/badge/AWS%20ElastiCache-1488C6?style=for-the-badge&logo=amazon-aws&logoColor=white)
![OpenAI](https://img.shields.io/badge/OpenAI-412991?style=for-the-badge&logo=openai&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

### **Frontend**
![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)
![React Router](https://img.shields.io/badge/React%20Router-CA4245?style=for-the-badge&logo=react-router&logoColor=white)
![Recharts](https://img.shields.io/badge/Recharts-FF4500?style=for-the-badge&logo=Recharts&logoColor=white)
![Markdown](https://img.shields.io/badge/Markdown-000000?style=for-the-badge&logo=markdown&logoColor=white)
![AWS CloudFront](https://img.shields.io/badge/AWS%20CloudFront-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)

## Features
### **Recommendation System**
- Implemented using the k-Nearest Neighbors (k-NN) algorithm for content-based filtering.
- Analyzes user wine preferences based on attributes like region and grape variety to recommend wines with similar characteristics.

### **Tasting Notes Assistance and Formats**
- Supports Markdown syntax and provides both Free-Form and SAT templates for structured note-taking.
- Users can create, save, and edit tasting notes.
- Uses the OpenAI API to generate sample notes based on keywords for user assistance.

### **Wine Database and Search**
- Scraped wine data using Python with Selenium WebDriver and BeautifulSoup.
- Autocomplete suggestions for wine names are cached using Redis for faster search functionality.

### **Data Storage and Collection**
- Users can upload wine information and photos, with images stored in AWS S3 and data saved in AWS RDS (MySQL).
- Automated data collection from external sources ensures accurate and up-to-date wine records.

### **Data Visualization**
- Implements Recharts for visualizing user wine tasting statistics and personal trends.

### **User Authentication**
- Secured using Spring Security with JWT tokens, ensuring protected routes and actions for logged-in users only.






