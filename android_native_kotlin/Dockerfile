# Build stage
FROM gradle:8.5.0-jdk17 AS build

# Set working directory
WORKDIR /app

# Copy gradle files
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle
COPY app/build.gradle.kts app/

# Copy source code
COPY app/src app/src

# Build the APK (Release mode)
RUN gradle assembleRelease --no-daemon

# Verification stage (Optional: helps CI identify where the apk went)
RUN ls -la app/build/outputs/apk/release/

# Output stage
# In a real CI, you would grab the artifact from the build stage.
# For a container that just builds artifacts, this is sufficient.
