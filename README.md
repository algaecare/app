# Algae Care Application

## Copy ressources from OneDrive

GitLab does not allow to upload files larger than 100MB. Therefore, the resources are not included in the repository. You can copy them from OneDrive.
1. Go to the OneDrive folder: [OneDrive]()
2. Download the folder `resources` and copy it to `src > main > resources` in the project folder.
3. Make sure the folder structure is correct:
```
src
└── main
    └── resources
        ├── dynamic
        │   └── axolotl
        │       ├── idle
        │       ├── intro
        │       └── images 
        └── static
```

## Running the application

- Really locally:
```bash
mvn clean javafx:run -P run-local
```

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
mvn clean package -P release -DskipTests
mvn install -P run-on-Pi -DskipTests
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