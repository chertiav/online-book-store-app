# Online Book Store App

## 📚 Description

The **Online Book Store App** is a robust and user-friendly Java web application designed to manage book inventory and facilitate online book purchases. This project showcases how modern Java frameworks and libraries can come together to create a scalable and efficient software solution.

The application includes user authentication, API documentation, database integrations, and controller-based functionalities tailored to enhance user experience. The project demonstrates strong coding practices, making it a great example for presenting technical proficiency.

---

## 🚀 Features

- **User Authentication**: Secure user login and registration using Spring Security and JWT.
- **RESTful API**: Well-structured APIs for CRUD operations on books, users, and orders.
- **Database Management**: Integrated with MySQL and supported by Liquibase for database migrations.
- **OpenAPI Documentation**: Explore the API endpoints conveniently using Springdoc Swagger.
- **Validation and Error Handling**: Input validation with detailed error messages.
- **Scalability**: Modular and extendable architecture with Spring Boot's framework.
- **Development Efficiency**: Features hot reloading through Spring Boot DevTools.

---

## 🛠️ Technologies Used

This project leverages the following technologies and tools

- **Java 17**: The core programming language.
- **Spring Framework**:
    - Spring Boot
    - Spring Data JPA
    - Spring Security
    - Spring MVC
- **Database**:
    - MySQL Connector (JDBC)
    - Liquibase (Database migration tool)
- **API Documentation**:
    - SpringDoc OpenAPI & Swagger UI
- **JWT**: JSON Web Tokens for authentication.
- **Lombok**: Simplified Java development with reduced boilerplate.
- **MapStruct**: For converting entities to DTOs and vice versa.
- **Docker Compose**: For containerized and simplified deployment.
- Additional tools like Maven for builds and Git for version control.

---

## Unique Functionalities
### 👤 Auth Controller
The Auth Controller handles registration and authentication processes for users.
- **Endpoints**:
    - **POST /auth/registration** — Allows new users to sign up by providing their email, password, and personal details.  
    - **POST /auth/login** — Authenticates a user and provides a JWT for accessing protected endpoints.  


### 📖 Book Controller
The Book Controller manages the creation, retrieval, search, update, and deletion of books with role-based access control.
- **Endpoints**:
    - **GET /books** — Retrieves a paginated list of all books.
      **Access**: Accessible to users with the role `ROLE_USER`.
    - **GET /books/{id}** — Retrieves detailed information for a specific book by its ID.
      **Access**: Accessible to users with the role `ROLE_USER`.
    - **GET /books/search** — Searches for books based on specific parameters (e.g., category, author) and returns a paginated list of results.
      **Access**: Accessible to users with the role `ROLE_USER`.
    - **POST /books** — Creates a new book in the system.
      **Access**: Restricted to administrators with the role `ROLE_ADMIN`.
    - **PUT /books/{id}** — Updates an existing book's details.
      **Access**: Restricted to administrators with the role `ROLE_ADMIN`.
    - **DELETE /books/{id}** — Deletes a specific book by its ID.
      **Access**: Restricted to administrators with the role `ROLE_ADMIN`.

### 📂 Category Controller
Category Controller provides endpoints for managing book categories, including CRUD operations and listing books associated with specific categories. Access is role-based to ensure security.
- **Endpoints**:
    - **GET /categories** — Retrieve a paginated list of all categories.
      **Access**: Accessible to users with the role `ROLE_USER`.
    - **GET /categories/{id}** — Retrieve details of a specific category by its ID.
      **Access**: Accessible to users with the role `ROLE_USER`.
    - **GET /categories/{id}/books** — Retrieves a paginated list of all books belonging to a specific category.
      **Access**: Accessible to users with the role `ROLE_USER`.
    - **POST /categories** — Create a new category.
      **Access**: Restricted to administrators with the role `ROLE_ADMIN`.
    - **PUT /categories/{id}** — Update an existing category by its ID.
      **Access**: Restricted to administrators with the role `ROLE_ADMIN`.
    - **DELETE /categories/{id}** — Delete a category by its ID.
      **Access**: Restricted to administrators with the role `ROLE_ADMIN`.


### 🛒 Order Controller
The Order Controller manages the creation, retrieval, and updating of orders and their items. It supports both user-specific operations and administrative tasks for managing orders.
- **Endpoints**:
    - **POST /orders** — Places an order. Accessible to authenticated users with the role `ROLE_USER`.
      Example Use: Provide necessary details (e.g., book IDs, quantity) to create a new order.
    - **GET /orders** — Retrieve a paginated list of all orders placed by the currently logged-in user.
      Accessible to `ROLE_USER`.
    - **PATCH /orders/{id}** — Update the status of an order (e.g., to "shipped" or "delivered").
      Accessible only to administrators with the `ROLE_ADMIN` role.
    - **GET /orders/{orderId}/items** — Retrieve a detailed list of order items for a specific order by its ID. Provides a paginated response.
      Accessible to the owner of the order (`ROLE_USER`).
    - **GET /orders/{orderId}/items/{itemId}** — Fetch details of a specific item from an order.
      Accessible to the owner of the order (`ROLE_USER`).


### 🛒 Shopping Cart Controller
The Shopping Cart Controller manages the retrieval, modification, and deletion of items in a user's shopping cart. It ensures that only authenticated users can interact with their shopping cart.
- **Endpoints**:
    - **GET /cart** — Retrieve the shopping cart of the currently authenticated user.
      Accessible to authenticated users with the role `ROLE_USER`.
      Example Use: View all items and their quantities in the shopping cart.
    - **POST /cart** — Add a book to the shopping cart.
      Accessible to authenticated users with the role `ROLE_USER`.
      Example Use: Add a specific book to the shopping cart with the desired quantity.
    - **PUT /cart/{cartItemId}** — Update the details (e.g., quantity) of an existing item in the cart.
      Accessible to authenticated users with the role `ROLE_USER`.
    Example Use: Modify the quantity of a specific item in the shopping cart.
    - **DELETE /cart/{cartItemId}** — Remove an item from the shopping cart by its ID.
      Accessible to authenticated users with the role `ROLE_USER`.
      Example Use: Delete an item from the cart when it's no longer needed.

---

## 📋 Prerequisites

Ensure the following are installed on your machine:

- **Java**: JDK 17+.
- **Build Tool**: Maven or Gradle (Maven used in this project).
- **Docker** version 20+
- **Docker Compose** version 2.5+

---

## 📝 Setup Instructions

### Step 1: Clone the repository
```shell script
  git clone git@github.com:chertiav/online-book-store-app.git
  cd online-book-store-app
```

### Step 2: Configure the `.env` File

1. In the root directory of the project, create a new file named `.env`.  
   This file will store the environment variables necessary to configure the database and application.

2. Add the following variables to the `.env` file based on the provided template:

```dotenv
    MYSQLDB_USER=<your_database_username>
    MYSQLDB_PASSWORD=<your_database_password>
    MYSQLDB_DATABASE=online_book_store
    MYSQL_LOCAL_PORT=3308
    MYSQL_DOCKER_PORT=3306

    JWT_EXPIRATION=3600000
    JWT_SECRET=<your_jwt_secret>

    SPRING_LOCAL_PORT=8088
    SPRING_DOCKER_PORT=8080
    DEBUG_PORT=5005
```

3. Replace the placeholders `<your_database_username>`, `<your_database_password>`, and `<your_jwt_secret>` with actual values. For example:

```dotenv
    MYSQLDB_USER=admin
    MYSQLDB_PASSWORD=qwerty
    JWT_SECRET=abcdefghijklmnopABCDEFGHIJKLMNOP12345678
```

- **Descriptions of Required Variables**:
    - **`MYSQLDB_USER`** — The username for the MySQL database.
    - **`MYSQLDB_PASSWORD`** — Password for the MySQL database user.
    - **`MYSQLDB_DATABASE`** — The name of the database used by the application (defaults to `online_book_store`).
    - **`JWT_SECRET`** — A secure key used to sign JWT tokens. Ensure this is a strong and unique value.

### Additional Notes:
- The variable `MYSQL_LOCAL_PORT` specifies the port used locally to connect to the database, while `MYSQL_DOCKER_PORT` specifies the port inside the Docker container. These generally do not need modification.
- `SPRING_LOCAL_PORT`, `SPRING_DOCKER_PORT`, and `DEBUG_PORT` define the ports for the application and debugging and should match your local and containerized setup.

Make sure to save the `.env` file and avoid committing it to version control to keep sensitive information secure.

### Step 3: Start the Application

1. Once the `.env` file is configured, navigate to the project’s root directory using the terminal.

2. Use the following command to start the application and all required services via Docker Compose:

```shell script
  docker compose up --build
```

3. **What this does**:
    - This command builds the Docker images for the application and required services.
    - It starts the application, database, and any other dependencies inside containers.

4. Wait for the services to start. You should see logs indicating that the application and database are running.

---

### Step 4: Access the Application
1. If you are accessing the application **locally (outside the container)**, use the port defined in `SPRING_LOCAL_PORT`. Since the `context-path` is set to `/api`, the application will be accessible at:
``` 
    http://localhost:8088/api
```
2. To view the **Swagger API documentation**, navigate to:
``` 
    http://localhost:8088/api/swagger-ui/index.html
```
3. If you are working **inside a container**, the application will be accessible on the port defined in `SPRING_DOCKER_PORT`. Use the following address:
``` 
    http://localhost:8080/api
```
4. Similarly, the Swagger documentation inside the container will be available at:
``` 
    http://localhost:8080/api/swagger-ui.html
```

### Note:
- The separation of ports (`SPRING_LOCAL_PORT` and `SPRING_DOCKER_PORT`) allows you to work locally on one port and expose another port for Docker containers.
- Ensure you are accessing the correct port depending on your working environment.

---

## 🔄 Managing the Application

### Build Containers
Build the Docker containers if there are changes in the application or its dependencies:

```shell script
  docker compose build
```

### Run In Detached Mode
Run the application in the background:

```shell script
  docker compose up -d
```

### Stop the Containers
Stop all running containers:

```shell script
  docker compose down
```

### Clean Up
If you want to clear all containers and volumes, use:

```shell script
  docker compose down --volumes
```

### Stop Running Containers Without Removing Them
If you only want to stop the running containers but keep them without removing resources, you can use:
```shell script
  docker compose stop
```

---

## 🛠 Obstacles & Solutions

### **Challenge: Running Locally with Docker Compose**
- **Problem**: Integrating the application with Docker Compose while ensuring smooth database connections.
- **Solution**: Used `spring-boot-docker-compose` starter dependency to simplify integration and manage service dependencies automatically.

### **Challenge: Managing Schema Changes**
- **Problem**: Updating the database schema consistently during development.
- **Solution**: Adopted Liquibase for schema versioning and included Liquibase scripts in `docker-compose`.

---

## 📂 Postman Collection

You can find a Postman collection in `docs/online-book-store-app-api.postman_collection.json`. To use this:

1. Import the file into Postman.
2. Adjust Authorization headers (use JWT obtained from login endpoint).
3. Test all exposed APIs such as authentication, book management, and more.

---

## 📊 Architecture Diagram

Below is a system overview of the application and its setup with Docker Compose:

```
+-------------------+
|  REST API Client  |
+-------------------+
        |
        ↓
+------------------------+
|    Spring Boot App     |
|                        |
| - Controllers          |
| - Services             |
| - Repositories         |
+------------------------+
        |
        ↓
+------------------+      +-------------------+
|  MySQL Database  | <--> | Liquibase Scripts |
+------------------+      +-------------------+
        |
 Docker Compose (Container Management)
```

---

## 👥 Contributions

We welcome contributions to enhance the project! To contribute:

1. **Fork the repository**.
2. **Create a branch**:
```shell script
  git checkout -b new-feature
```
3. **Commit your changes**:
```shell script
  git commit -m "Add new feature"
```
4. **Test your changes**.
5. **Submit a pull request**.

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

## 📧 Contact

Feel free to reach out for feedback or questions:

- **Email**: [chertaiv@gmail.com]
- **GitHub**: [GitHub Profile](https://github.com/chertiav)
