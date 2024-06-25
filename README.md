# Vert.x with etcd Configuration Management and WebSocket Example

This project demonstrates how to integrate Vert.x with etcd for configuration management and WebSocket handling in a Java application.

## Overview

Vert.x is a polyglot event-driven application framework that runs on the Java Virtual Machine (JVM). It provides a powerful toolkit for building reactive applications and supports various protocols and integrations, including WebSocket handling.

etcd is a distributed key-value store that is widely used for shared configuration and service discovery in distributed systems. This project showcases how to use etcd for dynamic configuration updates in a Vert.x application, leveraging its watch feature to reactively handle changes.

## Features

- **Configuration Management**: Utilize etcd to manage application configuration dynamically.
- **WebSocket Integration**: Implement WebSocket endpoints to provide real-time communication capabilities.
- **Asynchronous Handling**: Ensure non-blocking operations using Vert.x's asynchronous model.

## Requirements

- Java Development Kit (JDK) 8 or higher
- Maven for dependency management
- Docker (optional, for running etcd in a containerized environment)

## Installation and Setup

### 1. Clone the Repository

```bash
git clone https://github.com/Maverick-D-Aece/etcd-config-management-system.git
cd etcd-config-management-system
```

### 2. Build the Project

```bash
mvn clean package
```

### 3. Run etcd (Docker)

If etcd is not available in your environment, you can run it using Docker Compose directly from IntelliJ or:

```bash
# need to re-check
docker-compose -d -f /infra/docker-compose.yml up 
```

### 4. Configure Environment Variables

Create a `.env` file in the project root and configure necessary environment variables:

```dotenv
ecms.etcd.endpoint=localhost
ecms.etcd.port=2379
ecms.config-mgmt-server.port=8080
```

### 5. Run the Application

```bash
java -jar target/<jar_file_name>.jar
```

## Usage

### API Endpoints

- **PUT /api/config**: Update a configuration value in etcd.
    - Parameters:
        - `key`: The key of the configuration.
        - `value`: The new value to set.

- **GET /api/config**: Retrieve a configuration value from etcd.
    - Parameters:
        - `key`: The key of the configuration.

### WebSocket Endpoint

- **/api/watchConfig**: Establish a WebSocket connection to watch changes in a configuration key.

## Examples

### Example HTTP Request (Using `.http` file)

```http
### PUT request to update a configuration value in etcd
PUT http://localhost:8080/api/config?key=/config/myapp
Content-Type: application/json

"new_value"

### GET request to retrieve a configuration value from etcd
GET http://localhost:8080/api/config?key=/config/myapp
Accept: text/plain

### WebSocket request to watch changes in a configuration key
WS ws://localhost:8080/api/watchConfig?key=/config/myapp
```

## Troubleshooting

- **WebSocket Issues**: Ensure proper WebSocket handling in your client and server implementations.
- **etcd Watch Problems**: Verify etcd configuration and connectivity.
- **General Issues**: Check logs (`application.log`) for errors and debug information.

## Contributing

Contributions are welcome! Fork the repository and submit a pull request with your enhancements.

### Notes:

- Replace `<repository_url>`, `<repository_directory>`, `<jar_file_name>`, and other placeholders with actual values relevant to your project.
- Provide detailed steps for installation, setup, and usage to help users understand how to run and interact with your application.
- Include examples of HTTP requests using `.http` file format for testing API endpoints.
- Mention troubleshooting tips to assist users in resolving common issues.
- Adjust the structure and content based on the specific features and nuances of your implementation.

This README.md file structure aims to provide comprehensive information to users and potential contributors about your Vert.x and etcd integration project, facilitating smooth setup, usage, and troubleshooting.