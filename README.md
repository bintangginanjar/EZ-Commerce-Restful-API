# 🚀 EZ-Commerce RESTful API

Welcome to **EZ-Commerce**, a powerful and modular e-commerce backend built with **Spring Boot**. This project offers full-stack-ready REST APIs for product listings, user authentication, shopping cart, and order management.

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-brightgreen.svg)
![License](https://img.shields.io/github/license/bintangginanjar/EZ-Commerce-Restful-API)

---

## ✨ Features

- ✅ User Registration & JWT Authentication
- 🛍️ Product & Category CRUD (Admin only)
- 🛒 Shopping Cart management
- 📦 Order Placement & History
- 🔐 Role-based Access (User/Admin)
- 📖 Swagger Documentation

---

## 📦 Tech Stack

| Layer        | Technology                |
|--------------|---------------------------|
| Language     | Java 17                   |
| Framework    | Spring Boot 3             |
| Security     | Spring Security + JWT     |
| Database     | H2 (dev) / PostgreSQL     |
| ORM          | Spring Data JPA           |
| Docs         | Swagger / OpenAPI         |

---

## 🧭 API Reference

> Base URL: `/api`

### 🔐 Auth

| Method | Endpoint         | Description          |
|--------|------------------|----------------------|
| POST   | `/auth/register` | Register new account |
| POST   | `/auth/login`    | Login & receive JWT  |

### 🛍️ Products

| Method | Endpoint         | Description             |
|--------|------------------|-------------------------|
| GET    | `/products`      | List all products       |
| POST   | `/products`      | Create product (Admin)  |
| GET    | `/products/{id}` | Get product by ID       |
| PUT    | `/products/{id}` | Update product (Admin)  |
| DELETE | `/products/{id}` | Delete product (Admin)  |

### 🗂️ Categories

| Method | Endpoint           | Description              |
|--------|--------------------|--------------------------|
| GET    | `/categories`      | List all categories      |
| POST   | `/categories`      | Create category (Admin)  |
| GET    | `/categories/{id}` | Get category by ID       |
| PUT    | `/categories/{id}` | Update category (Admin)  |
| DELETE | `/categories/{id}` | Delete category (Admin)  |

### 🛒 Cart

| Method | Endpoint              | Description            |
|--------|-----------------------|------------------------|
| GET    | `/cart`               | Get current user's cart |
| POST   | `/cart`               | Add product to cart     |
| DELETE | `/cart/{productId}`   | Remove product from cart|

### 📦 Orders

| Method | Endpoint   | Description               |
|--------|------------|---------------------------|
| GET    | `/orders`  | Get current user's orders |
| POST   | `/orders`  | Place a new order         |

Full API specification can be accessed through (https://github.com/bintangginanjar/EZ-Commerce-Restful-API/blob/main/docs/API.md)

---

## 🔐 Authentication

Use `/auth/login` to retrieve a Bearer token and add this header to protected requests:


---

## 🧪 Running Locally

# Clone repo
git clone https://github.com/bintangginanjar/EZ-Commerce-Restful-API.git
cd EZ-Commerce-Restful-API

# Run
./mvnw spring-boot:run

## 📁 Project Structure

| Folder       | Description                                  |
| ------------ | -------------------------------------------- |
| `controller` | Handles HTTP requests (REST API)             |
| `dto`        | Data Transfer Objects (requests & responses) |
| `model`      | JPA entities (e.g. User, Product)            |
| `repository` | Spring Data JPA interfaces                   |
| `security`   | JWT and security configuration               |
| `service`    | Business logic and use cases                 |

## 🙋‍♂️ Author

Developed by @bintangginanjar
Contributions, issues, and stars ⭐ are welcome!
