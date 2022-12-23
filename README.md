# THIS PROJECT IS JUST A CONCEPT

## VertxToSpring

This project is a proof of concept for a few things:
1. That Codemods can be done in Java
2. That ChatGPT + GitHub CoPilot can greatly increase the speed of development
3. That it could be possible to migrate a codebase with a combination of these tools

### Usage

```bash
./gradlew run --args='-d="/path/to/vertx-examples"'
```

Optionally, you can add the `-w or --write` flag to overwrite the files in place.

```bash
./gradlew run --args='-d="/path/to/vertx-examples" -w'
```