# Algae Care

A JavaFX application for monitoring and managing algae cultivation systems. Built using the Model-View-Controller (MVC) design pattern to ensure maintainable and scalable code.

## Project Structure

```
algae-care
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   ├── algaecare
│   │   │   │   │   ├── app
│   │   │   │   │   │   ├── Main.java
│   │   │   │   │   │   ├── controller
│   │   │   │   │   │   ├── model
│   │   │   │   │   │   └── view
│   │   └── resources
│   │       ├── fxml
│   │       └── styles
├── pom.xml
└── README.md
```

## Setup Instructions

1. **Clone the repository**:
   ```bash
   git clone https://github.com/PrimeoEnergy/algae-care.git
   cd algae-care
   ```

2. **Build the project**:
   Ensure Maven is installed, then run:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn javafx:run
   ```

## Overview

- **Main.java**: Application entry point and JavaFX initialization
- **Controller**: Handles user interactions and system logic
- **Model**: Manages data structures and business logic
- **View**: FXML-based UI layouts
- **Resources**: Contains FXML layouts and CSS styles

## Dependencies

- JavaFX 17
- Maven
- JUnit 5 for testing

For detailed documentation and contribution guidelines, please refer to the project wiki.
