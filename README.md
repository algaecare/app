# Algae Care Application

## Running the application

- Locally without Testing:
```bash
mvn clean package -P release -DskipTests
mvn verify -P run-local -DskipTests
```

- Locally with Testing: 
```bash
mvn clean package -P release
mvn verify -P run-local
```
- On Raspberry Pi:
```bash
mvn clean package -P release
mvn install -P run-on-Pi
```

### Restart the application on Raspberry Pi
```bash
mvn validate -P restart-on-Pi
```

### Debugging on Raspberry Pi
```bash
mvn clean package -P release,debug
mvn install -P run-on-Pi
```

## Testing the application

### Unit tests
```bash
mvn test
```