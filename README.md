# Velstrack - Enterprise Call Analytics & Marketing Dashboard

Welcome to the Velstrack monorepo. This repository contains both the **Node.js/Express Backend System** and the **Android Native Application**.

## 🚀 Backend Deployment
This backend is fully configured for zero-downtime deployment on Render via Infrastructure-as-Code.
1. Connect this GitHub repository to your Render dashboard.
2. Render will automatically detect the `render.yaml` Blueprint.
3. Supply the secure environment variables (`MONGO_URI`, `JWT_SECRET`, `META_ACCESS_TOKEN`) when prompted by the Render dashboard.
4. Render will auto-deploy the API on every push to the `main` branch!

## 📱 Android Development
To compile the Android app locally:
1. Open **Android Studio**.
2. Select **File -> Open** and select the `velstrack/android/` directory (not the root directory).
3. Allow Gradle to sync dependencies (Hilt, Compose, Room, etc.).
4. Run the app on your emulator or physical device.

## ✨ Core Features Designed
* **Automated Data Processing**: Node-cron jobs syncing Meta Ads APIs at midnight.
* **Offline-First Resilience**: Android WorkManager & Room database syncing call durations silently in the background.
* **Security First**: JWT endpoints, bcryptjs hashing, and role-based access control.
* **Premium UX/UI**: Scalable Clean Architecture, M3 Dark Themes, and fluid Compose Navigations.
