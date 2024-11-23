# Store Backend

This is a Spring Boot project for managing a store's backend. It handles products, stock, orders, payments, and provides role-based access control using Spring Security.

## Features

- **Product Management**: Add, view, and delete products.
- **Order Management**: Create and view orders.
- **Cart Management**: Add, view, and remove items in a cart.
- **Authentication and Authorization**: Role-based access control for admin and user functionalities.
- **Dummy Payments**: Simulated payment processing for orders.

## Technologies Used

- Spring Boot
- Spring Data JPA
- Spring Security
- MySQL
- Maven

## Getting Started

### Prerequisites

- Java 11 or later
- Maven
- MySQL

### Setup

1. **Clone the repository**:

   ```bash
   git clone https://github.com/Samah-00/store-management-backend.git
   cd store-backend
   ```
2. **Set up MySQL database**:

    Create a MySQL database named ex5 and ensure that it is running.

3. **Configure database connection**:

    Update the src/main/resources/application.properties file with your database credentials:
   ```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/ex5
   spring.datasource.username=yourusername
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```
4. **Build and run the project**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Testing Endpoints

You can test the endpoints using Postman or any other API testing tool. Below are the endpoints available:

#### Admin Endpoints (Require Role: ADMIN)

- **Add Product**:
    - **Method**: `POST`
    - **URL**: `http://localhost:8080/admin/products`
    - **Headers**:
        - `Authorization`: Basic `admin:admin_password`
    - **Body**: JSON
      ```json
      {
        "name": "New Product",
        "price": 100.0,
        "stock": 50
      }
      ```

- **Delete Product**:
    - **Method**: `DELETE`
    - **URL**: `http://localhost:8080/admin/products/{productId}`
    - **Headers**:
        - `Authorization`: Basic `admin:admin_password`

- **Get Orders**:
    - **Method**: `GET`
    - **URL**: `http://localhost:8080/admin/orders`
    - **Headers**:
        - `Authorization`: Basic `admin:admin_password`

#### User Endpoints (Require Role: USER)

- **Add Item to Cart**:
    - **Method**: `POST`
    - **URL**: `http://localhost:8080/cart/add`
    - **Headers**:
        - `Authorization`: Basic `user:user_password`
    - **Body**: JSON
      ```json
      {
        "productId": 1,
        "quantity": 2
      }
      ```

- **View Cart**:
    - **Method**: `GET`
    - **URL**: `http://localhost:8080/cart`
    - **Headers**:
        - `Authorization`: Basic `user:user_password`

- **Remove Item from Cart**:
    - **Method**: `DELETE`
    - **URL**: `http://localhost:8080/cart/remove?productId={productId}`
    - **Headers**:
      - `Authorization`: Basic `user:user_password`
    - **Params**:
      - productId: ID of the product to be removed


- **Create Order**:
    - **Method**: `POST`
    - **URL**: `http://localhost:8080/orders?userId={userId}`
    - **Headers**:
        - `Authorization`: Basic `user:user_password`
    - **Body**: JSON
      ```json
      [
          {
            "productId": 1,
            "quantity": 2
          },
          {
            "productId": 2,
            "quantity": 1
          }
      ]
      ```

- **Get Order by ID**:
    - **Method**: `GET`
    - **URL**: `http://localhost:8080/orders/{orderId}`
    - **Headers**:
        - `Authorization`: Basic `user:user_password`

- **View User Orders**:
    - **Method**: `GET`
    - **URL**: `http://localhost:8080/orders?userId={userId}`
    - **Headers**:
        - `Authorization`: Basic `user:user_password`

- **Get All Products**:
    - **Method**: `GET`
    - **URL**: `http://localhost:8080/products`
    - **Headers**:
        - `Authorization`: Basic `user:user_password`

- **Get Product by ID**:
    - **Method**: `GET`
    - **URL**: `http://localhost:8080/products/{productId}`
    - **Headers**:
        - `Authorization`: Basic `user:user_password`

## Authentication

This project uses basic authentication with the following credentials:

- **Admin**:
    - **Username**: `admin`
    - **Password**: `admin_password`
- **User**:
    - **Username**: `user`
    - **Password**: `user_password`

## Logging

This project uses **SLF4J** with the Logback implementation for logging. Logging is used to capture key application events, errors, and debugging information to assist in development, debugging, and monitoring.

### Key Logging Features

- **Info Logs**: 
    - Informational messages about the application's regular operations.
    - Example: Successful payment processing.
  
- **Error Logs**:
    - Captures unexpected issues, such as database access errors or exceptions during processing.

### Examples of Logging in the Codebase

**Logs insufficient stock or other validation issues**:
```java
log.warn(String.format("Not enough stock for product ID %d. Requested: %d, Available: %d",
                       requestedProduct.getId(), cartItem.getQuantity(), requestedProduct.getStock()));
```

**Captures database-related exceptions when retrieving orders**:
```java
log.error(String.format("Error retrieving order with ID %d: %s", orderId, e.getMessage()), e);
```

### How to View Logs

Logs are printed to the console by default. You can customize the logging configuration in the `src/main/resources/application.properties` file.
