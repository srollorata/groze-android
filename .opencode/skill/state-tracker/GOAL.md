# Groze Development Goals

---

## Phase 1: MVP Release (v1.0)

### Current Status: 🟡 In Progress

---

## ✅ Completed Features

- [x] Onboarding flow
- [x] Vault (item repository)
- [x] Cart planning (Trip Plan Screen)
- [x] Active shopping mode
- [x] Trip summary with delta tracking
- [x] History view

---

## 🐛 Bugs to Fix

### Bug #1: Active Shopping persists after trip completion
- **Issue:** After finishing a trip, the trip still appears in Shop tab as "Active Shopping"
- **Behavior:** When opened, no items or prices are displayed
- **Root cause:** Trip status not properly updated to COMPLETED after confirmation
- **Fix needed:** Ensure trip status transitions to COMPLETED in TripSummaryViewModel

### Bug #2: Add new item to Vault in Trip Plan not functional
- **Issue:** When searching for a product that doesn't exist in Vault, "Add [product] as new item" option appears but is non-functional
- **Fix needed:** Implement add-new-item functionality in TripPlanScreen/TripPlanViewModel

---

## 🔄 Changes

### Change #1: Remove non-functional UI elements from Vault
- **Issue:** Hamburger menu and search icon have no functionality
- **Fix needed:** Remove TopAppBar navigationIcon and actions in VaultScreen

---

## ✨ New Features

### Feature #1: Settings Menu in Vault
- **Description:** Add settings access point in Vault screen
- **Location:** TopAppBar or overflow menu

### Feature #2: Currency Selection
- **Options:** PHP, USD
- **Storage:** UserPreferences for persistence
- **Display:** Format prices based on selected currency

### Feature #3: Dark/Light Mode Toggle
- **Implementation:** Compose theme switching following system settings
- **Storage:** UserPreferences
- **Default:** Follow system settings

### Feature #4: Category Templates
- **Pre-defined categories (match new template):**
  - Spices and Condiments
  - Meat and Poultry
  - Canned Goods
  - Seafood
  - Fresh Produce
  - Dairy
  - Beverages
  - Households
- **Implementation:** Update category dropdown in AddEditVaultItemSheet and migrate existing categories

### Feature #5: Settings Screen (based on settings/code.html)
- **Location:** Accessible from Vault TopAppBar
- **UI Components:**
  - **App Preferences Section:**
    - Appearance (Dark/Light mode toggle - currently a switch)
    - Currency (PHP, USD - currently shows USD)
  - **About Section:**
    - Version (display only)
    - Privacy Policy (placeholder link)
    - Terms of Service (placeholder link)
- **Navigation:** Back button to return to Vault

### [2026-04-09] Refactor ActiveTripScreen UI
- **Status:** Fixed in commit 56f6dd4
- **Changes:** Implemented Scaffold with bottomBar, refactored item cards with proper text wrapping, added visual state handling with lerp transitions, and updated summary card state alignment

1. **Bug Fixes** (Priority: High)
   - [x] Fix Active Shopping persistence
   - [x] Fix Add new item functionality

2. **Changes** (Priority: Medium)
   - [x] Remove non-functional icons from Vault

3. **New Features** (Priority: Medium)
   - [x] Settings menu infrastructure
   - [x] Dark/Light mode toggle (follow system)
   - [x] Currency selection (PHP, USD)
   - [x] Category templates update (migrate existing)

---

## 🎯 Milestones

- [x] All bugs fixed
- [x] Vault cleanup (remove non-functional UI)
- [x] Category templates implemented
- [x] Settings screen with theme toggle (follow system)
- [x] Currency selection (PHP, USD)
- [ ] Beta testing
- [ ] Play Store submission
- [ ] v1.0 release