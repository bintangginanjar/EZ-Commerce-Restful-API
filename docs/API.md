# RESTFUL API for Food Order

Feel free to use & modify this API for creating food order.

## 1. User Management

### a. Create User
Endpoint : POST /api/users

Request Header : None

Request Body:
```json
{
    "username" : "bintang.ginanjar",
    "password" : "password"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "User registration success",
    "errors": null,
    "data": {
        "username": "bintang.ginanjar",
        "name": "Bintang Ginanjar"
    }
}
```

### b. Get Current User
Endpoint : GET /api/users/current

Request Header :

* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "username" : "username",
    "password" : "password",
    "name" : "name"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "User fetching success",
    "errors": null,
    "data": {
        "username": "bintang.ginanjar",
        "name": "Bintang Ginanjar"
    }
}
```

### c. Update User
Endpoint : PATCH /api/users/current

Request Header :

* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "username" : "username",
    "password" : "password"    
}
```

Response Body:
```json
{
    "status": true,
    "messages": "User update success",
    "errors": null,
    "data": {
        "username": "bintang.ginanjar",
        "name": "Bintang Ginanjar"
    }
}
```

## 2. Auth Management

### a. User Login
Endpoint : POST /api/auth/login

Request Header : None

Request Body:
```json
{
    "username" : "username",
    "password" : "password"
}
```

Response Body:
```json
{
    "status": null,
    "messages": null,
    "errors": null,
    "data": {
        "token": "05072151-1c79-48de-b1bb-2d5eaf6f1912",
        "expiredAt": 1734404530947
    }
}
```

### b. User Logout
Endpoint : DELETE /api/auth/logout

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "username" : "username",
    "password" : "password"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "User logout success",
    "errors": null,
    "data": null
}
```
## 3. Profile Management

### a. Create Profile
Endpoint : POST /api/profiles

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "firstname" : "Bintang",
    "lastname" : "Ginanjar",
    "email" : "email@mail.com",
    "address" : "address",
    "phoneNumber" : "1234567890",
    "city" : "Bandung",
    "province" : "West Java",
    "postalCode" : "40254"

}
```

Response Body:
```json
{
    "status": true,
    "messages": "Profile registration success",
    "errors": null,
    "data": {
        "id": 1,
        "firstname": "Bintang",
        "lastname": "Ginanjar",
        "email": "email@mail.com",
        "address": "address",
        "phoneNumber": "1234567890",
        "city": "Bandung",
        "province": "West Java",
        "postalCode": "40254"
    }
}
```

### b. Update Profile
Endpoint : PATCH /api/profiles

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "firstname" : "Bintang",
    "lastname" : "Ginanjar",
    "email" : "email@mail.com",
    "address" : "address",
    "phoneNumber" : "1234567890",
    "city" : "Bandung",
    "province" : "West Java",
    "postalCode" : "40254"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "Profile update success",
    "errors": null,
    "data": {
        "id": 1,
        "firstname": "Bintang",
        "lastname": "Ginanjar",
        "email": "email@mail.com",
        "address": "address",
        "phoneNumber": "1234567890",
        "city": "Bandung",
        "province": "West Java",
        "postalCode": "40254"
    }
}
```

## 4. Category Management

### a. Create Category
Endpoint : POST /api/categories

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "name" : "Side Dish"    

}
```

Response Body:
```json
{
    "status": true,
    "messages": "Category registration success",
    "errors": null,
    "data": {
        "id": 26,
        "name": "Side Dish"
    }
}
```

### b. Get Category
Endpoint : GET /api/category/{categoryId}

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Category fetching success",
    "errors": null,
    "data": {
        "id": 26,
        "name": "Side Dish"
    }
}
```

### c. Update Category
Endpoint : PUT /api/category/{categoryId}

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "id": "26",
    "name": "Appetizer"    
}
```

Response Body:
```json
{
    "status": true,
    "messages": "Category update success",
    "errors": null,
    "data": {
        "id": 26,
        "name": "Appetizer"
    }
}
```

### d. Get Category with its Foods
Endpoint : GET /api/category/{categoryId}/foods

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Category fetching success",
    "errors": null,
    "data": {
        "id": 25,
        "name": "Main Course",
        "foods": [
            {
                "id": 27,
                "code": "af1b7c05-a108-428b-8feb-53fce3955daa",
                "name": "Fried rice",
                "price": 40,
                "isReady": true,
                "photoUrl": "https://img.freepik.com/free-photo/american-shrimp-fried-rice-served-with-chili-fish-sauce-thai-food_1150-26576.jpg"
            },
            {
                "id": 28,
                "code": "3074784c-c7c9-4e65-8f19-6f09e4de0f0c",
                "name": "Burger",
                "price": 10,
                "isReady": true,
                "photoUrl": "https://img.freepik.com/free-photo/american-shrimp-fried-rice-served-with-chili-fish-sauce-thai-food_1150-26576.jpg"
            }
        ]
    }
}
```

## 5. Food Management

### a. Create Food
Endpoint : POST /api/categories/{categoryId}/foods

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "name" : "Spaghetti Bolognese",
    "price" : 40,
    "photoUrl" : "https://img.freepik.com/free-photo/spaghetti-with-bolognese-sauce-wooden-tablexa_123827-22962.jpg"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "Food registration success",
    "errors": null,
    "data": {
        "id": 29,
        "code": "7360fb86-ff28-4078-b8c7-709caaef2ddf",
        "name": "Spaghetti Bolognese",
        "price": 40,
        "isReady": true,
        "photoUrl": "https://img.freepik.com/free-photo/spaghetti-with-bolognese-sauce-wooden-tablexa_123827-22962.jpg"
    }
}
```

### b. Get Food
Endpoint : GET /api/categories/{categoryId}/foods/{foodId}

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Food fetching success",
    "errors": null,
    "data": {
        "id": 30,
        "code": "ef6c1466-3b2e-4976-9699-1c17b435645e",
        "name": "Croissant",
        "price": 15,
        "isReady": true,
        "photoUrl": "https://img.freepik.com/free-photo/cooking-heap-croissants-table_144627-12959.jpg"
    }
}
```

### c. Update Food
Endpoint : PATCH /api/categories/{categoryId}/foods

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "id" : "30",
    "name" : "Cheese Croissant",
    "price" : 15,
    "photoUrl" : "https://img.freepik.com/free-photo/cooking-heap-croissants-table_144627-12959.jpg"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "Food update success",
    "errors": null,
    "data": {
        "id": 30,
        "code": "ef6c1466-3b2e-4976-9699-1c17b435645e",
        "name": "Cheese Croissant",
        "price": 15,
        "isReady": true,
        "photoUrl": "https://img.freepik.com/free-photo/cooking-heap-croissants-table_144627-12959.jpg"
    }
}
```

### d. Delete Food
Endpoint : DELETE /api/categories/{categoryId}/foods

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Food delete success",
    "errors": null,
    "data": null
}
```

## 6. Order Management

### a. Create Order
Endpoint : POST /api/orders

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Order creation success",
    "errors": null,
    "data": {
        "id": 18,
        "orderId": "b3b7f8c1-92a5-4be6-9617-7880bc092358",
        "date": "Tue Dec 17 23:13:33 WIB 2024",
        "totalPrice": 0,
        "status": "Pending"
    }
}
```

### b. Get Order
Endpoint : GET /api/orders

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Order list fetching success",
    "errors": null,
    "data": [
        {
            "id": 18,
            "orderId": "b3b7f8c1-92a5-4be6-9617-7880bc092358",
            "date": "Tue Dec 17 23:13:33 WIB 2024",
            "totalPrice": 0,
            "status": "Pending"
        }
    ]
}
```

### c. Get Order
Endpoint : GET /api/orders/{orderId}/food/{foodId}

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Order list fetching success",
    "errors": null,
    "data": [
        {
            "id": 18,
            "orderId": "b3b7f8c1-92a5-4be6-9617-7880bc092358",
            "date": "Tue Dec 17 23:13:33 WIB 2024",
            "totalPrice": 30,
            "status": "Pending"
        }
    ]
}
```

### d. Update Order
Endpoint : PATCH /api/orders/{orderId}/food/{foodId}

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "orderId" : "2",
    "foodId" : "2",
    "quantity" : "2"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "Update order item success",
    "errors": null,
    "data": {
        "id": 2,
        "orderId": "90f5f3dd-8309-4cde-95fd-fd321ddcb72f",
        "date": "Tue Dec 17 23:40:37 WIB 2024",
        "totalPrice": 20,
        "status": "Pending"
    }
}
```

### e. Delete Order
Endpoint : DELETE /api/orders/{orderId}/food/{itemId}

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body:
```json
{
    "orderId" : "2",
    "foodId" : "2",
    "quantity" : "2"
}
```

Response Body:
```json
{
    "status": true,
    "messages": "Update order item success",
    "errors": null,
    "data": {
        "id": 2,
        "orderId": "90f5f3dd-8309-4cde-95fd-fd321ddcb72f",
        "date": "Tue Dec 17 23:40:37 WIB 2024",
        "totalPrice": 0,
        "status": "Pending"
    }
}
```

### f. Get Order List with its Items
Endpoint : GET /api/orders/{orderId}/foods

Request Header :
* X-API-TOKEN : Token (mandatory)

Request Body: None

Response Body:
```json
{
    "status": true,
    "messages": "Fetching order success",
    "errors": null,
    "data": {
        "id": 1,
        "orderId": "f7fa31e1-4450-478e-abab-cfbc762a2bf4",
        "date": "Tue Dec 17 23:39:56 WIB 2024",
        "totalPrice": 20,
        "status": "Pending",
        "items": [
            {
                "id": 1,
                "quantity": 2,
                "subTotal": 20
            }
        ]
    }
}
```
