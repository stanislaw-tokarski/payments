# Payments
RESTful API for money transfers between accounts

##API
* **POST: http://localhost:4567/accounts/create**

Consumes POST requests. When one is received, new account with random UUID and default balance (100.0) is created.

Produces JSON with new account details. 

```json
{
    "id": "80bba8a6-12aa-433c-b9d3-fc2fa88182fa",
    "balance": 100
}
```

* **POST: http://localhost:4567/accounts/create/:UUID**

Consumes POST requests with given UUID in a path. When one is received, new account with given UUID and default balance (100.0) is created. 

Produces JSON with new account details. If provided UUID is already in use, returns error code with error message.

````json
{
    "id": "f5ebb38b-36ac-4e77-b028-6ab326cb09db",
    "balance": 100
}
````
````json
{
    "status": 409,
    "message": "Account with given ID already exists"
}
````

* **GET: http://localhost:4567/accounts/:UUID**

Consumes GET requests with UUID provided in a path.

Returns either details of given account or error code with error message when account does not exist.

````json
{
    "id": "80bba8a6-12aa-433c-b9d3-fc2fa88182fa",
    "balance": 100
}
````
````json
{
    "status": 404,
    "message": "Account with given ID does not exist"
}
````
* **POST: http://localhost:4567/payments**

Consumes POST requests with body containing transaction amount and UUIDs of both sender and receiver accounts:

````json
{
	"amount":"10.0",
	"originId":"f5ebb38b-36ac-4e77-b028-6ab326cb09db",
	"destinationId":"3a146b5e-f964-4632-a11b-3fd1a9a47d69"
}
````

Produces transaction details or response with proper error code and corresponding error message, e.g.

````json
{
	"amount":"10.0",
	"originId":"f5ebb38b-36ac-4e77-b028-6ab326cb09db",
	"destinationId":"3a146b5e-f964-4632-a11b-3fd1a9a47d69"
}
````
````json
{
    "status": 400,
    "message": "Payment cannot be processed because of insufficient funds amount"
}
````
````json
{
    "status": 404,
    "message": "Account with given ID does not exist"
}
````

## Getting application up and running
In order to build a project:
````
mvn clean install -DskipTests
````
Skipping tests is necessary as there are calls to real endpoints and therefore application has to be able to accept requests, which can be achieved by running a previously build .jar.
````
java -jar target\payments-1.0-SNAPSHOT.jar (on Windows)
java -jar target/payments-1.0-SNAPSHOT.jar (on Linux)
````
Once the application is ready to serve requests on port 4567, tests can be executed with:
````
mvn test
````