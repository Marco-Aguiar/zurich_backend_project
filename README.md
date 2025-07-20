# Book Reader: Your Personal Book Management API

![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Lombok](https://img.shields.io/badge/Lombok-333333?style=for-the-badge&logo=lombok&logoColor=white)

BookReader is a robust Spring Boot RESTful API designed to help users manage their personal book collection. It integrates seamlessly with the Google Books API, allowing users to discover new books, track their reading progress, and maintain a comprehensive personal library.

---

### ✨ Features

* **User Authentication & Authorization:** Secure user registration, login, and JWT-based authentication for protected endpoints.
* **Google Books API Integration:**
    * **Flexible Book Search:** Search for books by title, author, or subject using the powerful Google Books API.
    * **Price Information:** Retrieve sale and pricing details for books via ISBN.
    * **Personalized Recommendations:** Get book recommendations based on categories of books you've already interacted with.
* **Personal Book Collection Management:**
    * **Add Books:** Easily save books found via Google Books API to your personal collection.
    * **Manage Status:** Update the reading status of your books (e.g., `WISHLIST`, `PLAN_TO_READ`, `READING`, `PAUSED`, `DROPPED`, `READ`, `RECOMMENDED`).
    * **View Collection:** Retrieve all books in your personal library.
    * **Delete Books:** Remove books from your collection.
* **Global Error Handling:** Centralized exception handling to provide consistent and informative error responses.
* **API Documentation:** Comprehensive API documentation generated with Swagger (OpenAPI 3) for easy endpoint exploration and testing.
* **Structured Logging:** Professional logging with SLF4J and Logback for improved application monitoring and debugging.

### ✨ Coming soon
* **Tracker book progress:** A dedicated feature to track your reading progress for each book. For example, easily see "Lord of the Rings - 80% read in two weeks.
* **Co Read:** Connect with friends and share your reading journey! This feature will let you see your friends' book management activities. 


---

### 🚀 Technologies Used

* **Spring Boot:** Framework for building robust and scalable Java applications.
* **Spring Security:** Provides authentication, authorization, and other security features.
* **JWT (JSON Web Tokens):** For secure, stateless authentication.
* **Spring Data JPA:** Simplifies data access with Hibernate as the ORM.
* **PostgreSQL:** Relational database for persistent storage.
* **Lombok:** Reduces boilerplate code (getters, setters, constructors).
* **Swagger (OpenAPI 3):** For interactive API documentation.
* **SLF4J & Logback:** For logging.
* **RestTemplate:** For consuming external REST APIs (Google Books API).
* **Maven:** Dependency management and build automation.
* **Docker & Docker Compose:** For containerization, consistent environments, and simplified local setup.

---

### ⚙️ Setup and Run Locally (using Docker Compose)

To get BookReader up and running on your local machine using Docker Compose, follow these steps:

**Prerequisites:**

* **Docker Desktop** (includes Docker Engine and Docker Compose) installed and running.
* Git

1.  **Clone the repository**

    ```bash
    git clone https://github.com/Marco-Aguiar/zurich_backend_project.git
    cd BookReader
    ```

2.  **Configure Environment Variables**

    The file named `.env` in the root of your project already contains the necessary variables to run the project, unless you donwloaded the project by Github. Fell free to customize them (the same directory as `docker-compose.yml`) and add your sensitive environment variables there. This keeps your secrets out of `docker-compose.yml`.

    ```dotenv
    # .env file example
    DB_USERNAME=your_db_user_for_docker
    DB_PASSWORD=your_db_password_for_docker
    GOOGLE_BOOKS_API_KEY=YOUR_GOOGLE_BOOKS_API_KEY_HERE
    JWT_SECRET=YOUR_ULTRA_SECRET_JWT_KEY_HERE
    ```

    Then, modify your `docker-compose.yml` to *read* these variables from the `.env` file using `${VAR_NAME}`.
    (If you've already set them directly in `docker-compose.yml`, you can replace the hardcoded values with `${VAR_NAME}`).

    Example snippet from `docker-compose.yml` (showing how it would reference the .env file):
    ```yaml
    services:
      db:
        # ...
        environment:
          POSTGRES_DB: booktracker_db
          POSTGRES_USER: ${DB_USERNAME} # Read from .env
          POSTGRES_PASSWORD: ${DB_PASSWORD} # Read from .env
      app:
        # ...
        environment:
          DB_HOST: db
          DB_NAME: booktracker_db
          DB_USERNAME: ${DB_USERNAME} # Read from .env
          DB_PASSWORD: ${DB_PASSWORD} # Read from .env
          GOOGLE_BOOKS_API_KEY: ${GOOGLE_BOOKS_API_KEY} # Read from .env
          JWT_SECRET: ${JWT_SECRET} # Read from .env
    ```

    > **Note:** Your `src/main/resources/application.properties` is already configured to read these environment variables, making it flexible for Dockerized and non-Dockerized runs. You just need to add them as environment variables in your Intellij/Preferred IDE.

3.  **Build and Run the Containers**

    From the project root directory, run the following command to build your application's Docker image and start both the application and database containers:

    ```bash
    docker-compose up -d --build
    ```
    * `--build`: Ensures your application image is rebuilt if changes were made since the last build.
    * `-d`: Runs the containers in detached mode (in the background).

    Wait a few moments for the containers to fully start. You can check their status with `docker-compose ps` or view application logs with `docker-compose logs -f app`.

---

### 🚀 API Usage Examples

Once the application is running, you can interact with the API:

1.  **Register a New User:**

    ```bash
    curl -X POST \
      http://localhost:8080/api/users \
      -H 'Content-Type: application/json' \
      -d '{
        "username": "testuser",
        "email": "test@example.com",
        "password": "StrongPassword123"
      }'
    ```

2.  **Login to Get a JWT:**

    ```bash
    curl -X POST \
      http://localhost:8080/api/auth/login \
      -H 'Content-Type: application/json' \
      -d '{
        "email": "test@example.com",
        "password": "StrongPassword123"
      }'
    ```
    *(Save the `token` from the response for authenticated requests)*

3.  **Search for Books (Authenticated):**

    ```bash
    curl -X GET \
      "http://localhost:8080/api/external/books/search?title=The%20Hobbit" \
      -H 'Authorization: Bearer YOUR_JWT_TOKEN_HERE' \
      -H 'Content-Type: application/json'
    ```

---

### 📄 API Documentation (Swagger UI)

For a comprehensive and interactive exploration of all API endpoints, access the Swagger UI at:

`http://localhost:8080/documentation`

---

### 🛑 Stopping and Cleaning Up

To stop the running containers (and keep the database data):

```bash
docker-compose down
```

To stop and remove containers and delete the persistent database data (for a clean start):

```bash
docker-compose down -v
```

---

###  Full experience Setup

For a full experience, you can download the frontend application that connects with this backend server.
If you got interested, here's the link:<br>
[https://github.com/Marco-Aguiar/zurich_frontend_project.git](https://github.com/Marco-Aguiar/zurich_frontend_project)


🤝 Contributing

Contributions are welcome! If you have any suggestions, bug reports, or want to contribute to the codebase, please feel free to open an issue or submit a pull request.