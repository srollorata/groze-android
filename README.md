# Groze - Smart Grocery Tracking App

<p align="center">
  <strong>Smart grocery tracking that adapts to reality.</strong>
</p>

<p align="center">
  <a href="https://github.com/srollorata/groze-android">
    <img src="https://img.shields.io/github/v/release/srollorata/groze-android?include_prereleases&label=latest" alt="GitHub release">
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/github/license/srollorata/groze-android" alt="License">
  </a>
  <img src="https://img.shields.io/badge/status-in--development-orange" alt="Status">
</p>

---

## In Development

Groze is currently **in active development** and not yet available for public download.

**Early access:** Coming soon to Google Play Store.

---

## About Groze

Groze is a realistic, adaptable grocery tracking app that helps users manage spending, track real-world prices, and shop smarter. Unlike traditional checklist apps, Groze acknowledges that plans change once you enter the store.

### The Signature Feature: Reality-Synced Shopping

When you hit "Start Shopping," Groze captures your original plan and tracks every deviation — the "deltas." Whether you switch to name-brand because the generic is out of stock, or grab an unlisted snack, Groze automatically syncs those changes (and new prices) back to your Vault, making the app smarter with every trip.

---

## Core Features

| Feature | Description |
|---------|-------------|
| **The Vault** | Personal item repository that remembers every item you've ever added, including last known price and details. |
| **Flexible Cart Planning** | Build a cart from scratch or pull items from your Vault to establish a baseline budget. |
| **Active Shopping Mode** | Dedicated in-store UI — skip items, add ad-hoc purchases, and update prices on the fly. |
| **Dynamic Budget Engine** | Real-time calculator showing expected total during planning and actual total as you shop. |
| **Finish Trip Summary** | Post-shopping breakdown highlighting skipped items, new additions, and price changes. |

---

## Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material Design 3)
- **Architecture:** MVVM with Clean Architecture
- **Local Database:** Room (offline-first)
- **Dependency Injection:** Hilt

---

## Project Structure

```
app/
 └── src/main/java/com/groze/app/
      ├── data/
      │   ├── local/
      │   │   ├── dao/           # Room DAOs
      │   │   ├── entity/        # Database entities
      │   │   └── GrozeDatabase.kt
      │   ├── preferences/       # User preferences
      │   └── repository/        # Data repositories
      ├── di/                   # Hilt modules
      ├── navigation/           # Navigation components
      └── ui/
          ├── theme/            # Material 3 theming
          ├── tabs/             # Bottom tab screens
          ├── vault/            # Vault management
          ├── tripplan/         # Trip planning
          ├── activetrip/       # Active shopping
          ├── tripsummary/      # Trip summary
          ├── onboarding/       # Onboarding flow
          └── settings/          # Settings screen
```

---

## Screens

| Screen | Route | Description |
|--------|-------|-------------|
| Onboarding | `/onboarding` | 3-page introduction flow |
| Vault | `/vault` | Item repository with search |
| Plan | `/plan` | Create new trip plans |
| Shop | `/shop` | Active shopping trips |
| History | `/history` | Completed trips |
| Settings | `/settings` | App preferences |
| NewTripPlan | `/new_trip_plan/{tripId}` | Trip planning detail |
| ActiveTrip | `/active_trip/{tripId}` | Active shopping mode |
| TripSummary | `/trip_summary/{tripId}` | Post-trip summary |

---

## Database Entities

### VaultItemEntity
- `id` - Primary key
- `name` - Item name
- `category` - Item category
- `lastPrice` - Last known price
- `unit` - Unit of measurement
- `iconName` - Material icon identifier
- `createdAt` / `updatedAt` - Timestamps

### TripEntity
- `id` - Primary key
- `status` - PLANNING, ACTIVE, or COMPLETED
- `expectedTotal` - Planned budget
- `actualTotal` - Actual spending
- `createdAt` / `completedAt` - Timestamps

### CartItemEntity
- `id` - Primary key
- `tripId` - Foreign key to trip
- `vaultItemId` - Reference to vault item (nullable for ad-hoc)
- `name`, `category`, `quantity`, `unit` - Item details
- `plannedPrice` / `actualPrice` - Price tracking
- `status` - PENDING, CHECKED, or SKIPPED
- `isAdHoc` - Whether item was added during shopping

---

## Building

```bash
./gradlew assembleDebug
```

---

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.

---

## Author

**WynRollorata**  
[GitHub](https://github.com/srollorata)