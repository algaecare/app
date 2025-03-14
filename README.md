# Algae Care Application

## Running the application

- Locally: 
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