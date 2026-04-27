> **ARCHIVE NOTE (2026-04-27, WO-V180-CLEANUP-009-E)**
>
> 본 파일은 원래 `docs/00_governance/docs-index.md`에 위치했으나
> 파일명과 내용이 불일치 (파일명: docs-index, 내용: Android 모듈 안내).
>
> 모듈 안내 내용은 레포 루트 `README.md`로 이동·통합됨.
> 본 파일은 역사 기록용으로 archive에 보존.

---
# MyPhoneCheck Android Project

A multi-module Android application for intelligent call verification and decision making using Kotlin, Jetpack Compose, and Hilt dependency injection.

## Project Structure

### Core Modules
- **core:model** - Domain models and data classes
- **core:util** - Utility functions (phone number normalization, time utilities, Result wrapper)

### Feature Modules
- **feature:call-intercept** - Call interception and screening logic
- **feature:device-evidence** - Device-based evidence collection
- **feature:search-enrichment** - Search-based enrichment logic
- **feature:decision-engine** - Core decision making engine
- **feature:decision-ui** - UI for displaying decisions
- **feature:settings** - User settings screens
- **feature:billing** - In-app billing and subscription management
- **feature:country-config** - Country-specific configurations

### Data Modules
- **data:contacts** - Contact data access
- **data:calllog** - Call log data access
- **data:sms** - SMS data access
- **data:search** - Search results data access
- **data:local-cache** - Local database caching with Room

### Build Logic
- **build-logic:convention** - Gradle convention plugins for consistent configuration

## Technology Stack

- **Language**: Kotlin 2.0.0
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt 2.51.1
- **Database**: Room 2.6.1
- **Async**: Coroutines 1.8.1
- **Phone Numbers**: libphonenumber 8.13.49
- **Billing**: Google Play Billing 7.0.0
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Compile SDK**: 34

## Setup

### Prerequisites
- Android Studio Jellyfish or later
- Java 11 or later
- Android SDK with API level 34

### Configuration

1. Clone the repository
2. Open the project in Android Studio
3. Create `local.properties` from the template:
   ```bash
   cp local.properties.template local.properties
   ```
4. Update `local.properties` with your SDK path:
   ```properties
   sdk.dir=/path/to/android/sdk
   ```

### Build

```bash
./gradlew build
```

### Debug

```bash
./gradlew assembleDebug
```

### Release

```bash
./gradlew assembleRelease
```

## Permissions

The app requires the following permissions:

- `READ_PHONE_STATE` - To monitor incoming calls
- `READ_CALL_LOG` - To access call history
- `READ_CONTACTS` - To verify against contacts
- `READ_SMS` - To analyze SMS patterns
- `INTERNET` - For API calls and search enrichment
- `POST_NOTIFICATIONS` - To send notifications
- `ANSWER_PHONE_CALLS` - For call screening
- `MODIFY_PHONE_STATE` - For call control
- `ACCESS_NETWORK_STATE` - To check connectivity

## Architecture

The project follows a modular, layered architecture:

- **Models**: Core domain objects
- **Services**: Business logic and use cases
- **Data**: Repository pattern for data access
- **UI**: Jetpack Compose-based user interface
- **DI**: Hilt for dependency management

## Testing

```bash
./gradlew test
./gradlew androidTest
```

## Code Style

The project follows Kotlin coding conventions and uses Kotlin DSL for Gradle build files.

## Version Management

Dependencies and versions are managed through `gradle/libs.versions.toml` for centralized control.
