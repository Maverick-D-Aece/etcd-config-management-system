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

## Examples

### Example HTTP Request (Using `.http` file)

```http
### Put request to put configuration to etcd server
POST http://localhost:8080/api/config?
    key=/DAILY_LIMIT&
    value=500.00
Accept: text/plain

### Get request to get a configuration from etcd server
GET http://localhost:8080/api/config?
    key=/DAILY_LIMIT
Accept: text/plain  
```

## Troubleshooting
- **etcd Connection Problems**: Verify etcd configuration and connectivity.
