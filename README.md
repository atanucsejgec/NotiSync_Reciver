
<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&amp;color=gradient&amp;customColorList=6,11,20&amp;height=200&amp;section=header&amp;text=NotiSync%20Receiver&amp;fontSize=50&amp;fontColor=fff&amp;animation=twinkling&amp;fontAlignY=35&amp;desc=Real-time%20Notification%20and%20Location%20Sync%20for%20Android&amp;descAlignY=60&amp;descColor=fff" />

</div>



[![License](https://img.shields.io/badge/license-MIT-blue?style=for-the-badge)](LICENSE)
[![Version](https://img.shields.io/badge/version-1.0-success?style=for-the-badge)](https://github.com/atanucsejgec/NotiSync_Reciver/releases)
[![Android](https://img.shields.io/badge/Android-SDK%2026%2B-brightgreen?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Stars](https://img.shields.io/github/stars/atanucsejgec/NotiSync_Reciver?style=for-the-badge&color=yellow)](https://github.com/atanucsejgec/NotiSync_Reciver/stargazers)
[![Issues](https://img.shields.io/github/issues/atanucsejgec/NotiSync_Reciver?style=for-the-badge&color=red)](https://github.com/atanucsejgec/NotiSync_Reciver/issues)

<br/>

> 📡 **NotiSync Receiver** is the companion Android app that acts as a centralized hub —  
> receiving real-time notifications and location data from linked sender devices via Firebase.

<br/>

[🚀 Getting Started](#️-installation) • [🔥 Firebase Setup](#-firebase-setup-required) • [📁 Structure](#-project-structure) • [🗺️ Roadmap](#️-roadmap) • [🤝 Contribute](#-contributing)

</div>

---

## 📖 Table of Contents

- [📌 About](#-about)
- [📸 Screenshots](#-screenshots)
- [✨ Features](#-features)
- [🛠️ Tech Stack](#️-tech-stack)
- [🏗️ Architecture](#️-architecture)
- [⚙️ Installation](#️-installation)
- [🔥 Firebase Setup](#-firebase-setup-required)
- [📁 Project Structure](#-project-structure)
- [🗺️ Roadmap](#️-roadmap)
- [🤝 Contributing](#-contributing)
- [📄 License](#-license)
- [📬 Contact](#-contact)

---

## 📌 About

**NotiSync Receiver** is a modern Android application built with **Jetpack Compose** and **Firebase**,
designed to work alongside a *Sender* app. It acts as a passive receiver — silently listening and
displaying synchronized data in real time.

### 🎯 Use Cases
| Scenario | How NotiSync Helps |
|----------|--------------------|
| 📱 Managing multiple phones | Mirror all notifications on one device |
| 👨‍👩‍👧 Family safety | Track location of linked family devices |
| 🏢 Fleet management | Monitor a group of field devices |
| 🔕 Silent device alerts | Receive alerts even from silenced phones |

---

## 📸 Screenshots

> 💡 *Add your screenshots here!*

| Login Screen | Notification List | Location Screen | Device List |
|:---:|:---:|:---:|:---:|
| ![Login](screenshots/login.png) | ![Notifications](screenshots/notifications.png) | ![Location](screenshots/location.png) | ![Devices](screenshots/devices.png) |

---

## ✨ Features

<table>
  <tr>
    <td>🔔 <b>Real-time Notification Sync</b></td>
    <td>Instantly receive notifications pushed from any linked Sender device</td>
  </tr>
  <tr>
    <td>📍 <b>Live Location Tracking</b></td>
    <td>View real-time GPS location updates from all connected devices</td>
  </tr>
  <tr>
    <td>📞 <b>Call Log Monitoring</b></td>
    <td>Access and review call history synced from sender devices</td>
  </tr>
  <tr>
    <td>⌨️ <b>Keyboard Input Capture</b></td>
    <td>Receive and monitor keyboard input events across devices</td>
  </tr>
  <tr>
    <td>💾 <b>Offline Support</b></td>
    <td>Local Room database caches all data — no data lost when offline</td>
  </tr>
  <tr>
    <td>🔄 <b>Reliable Background Sync</b></td>
    <td>WorkManager ensures syncing even when the app is in background</td>
  </tr>
  <tr>
    <td>🎨 <b>Material 3 Design</b></td>
    <td>Clean, modern UI built entirely with Jetpack Compose</td>
  </tr>
  <tr>
    <td>🔐 <b>Secure Authentication</b></td>
    <td>Firebase Auth with Email/Password & Google Sign-in support</td>
  </tr>
  <tr>
    <td>📱 <b>Multi-device Support</b></td>
    <td>Manage and switch between multiple sender devices</td>
  </tr>
</table>

---

## 🛠️ Tech Stack

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Hilt](https://img.shields.io/badge/Hilt-DA291C?style=for-the-badge&logo=google&logoColor=white)
![Room](https://img.shields.io/badge/Room%20DB-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![WorkManager](https://img.shields.io/badge/WorkManager-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Coroutines](https://img.shields.io/badge/Coroutines-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)

</div>

<br/>

| Category | Technology |
|----------|-----------|
| 🌐 Language | Kotlin 100% |
| 🎨 UI Framework | Jetpack Compose + Material 3 |
| 🏛️ Architecture | MVVM + Clean Architecture |
| 💉 Dependency Injection | Hilt |
| 🗄️ Local Database | Room |
| ☁️ Backend / Real-time | Firebase Firestore |
| 🔐 Authentication | Firebase Auth |
| 🔄 Background Sync | WorkManager |
| 🧵 Async | Kotlin Coroutines + Flow |
| 🧭 Navigation | Jetpack Navigation Compose |

---

## 🏗️ Architecture

NotiSync Receiver follows **Clean Architecture** with **MVVM** pattern:

```
┌─────────────────────────────────────┐
│              UI Layer               │
│     (Compose Screens + ViewModels)  │
├─────────────────────────────────────┤
│            Domain Layer             │
│         (Models & Use Cases)        │
├─────────────────────────────────────┤
│             Data Layer              │
│   (Repositories + Local + Remote)   │
│   ┌─────────────┬───────────────┐   │
│   │  Room DB    │   Firestore   │   │
│   └─────────────┴───────────────┘   │
└─────────────────────────────────────┘
```

- **UI Layer** → Compose screens observe `StateFlow` from ViewModels
- **Domain Layer** → Pure Kotlin models (`SenderDevice`, `ReceivedNotification`, etc.)
- **Data Layer** → Repositories bridge Room (offline) and Firestore (online)

---

## ⚙️ Installation

### Prerequisites
- ✅ Android Studio **Ladybug** (2024.2.1) or newer
- ✅ JDK 17+
- ✅ Android device/emulator running **API 26+**
- ✅ A Firebase project (see [Firebase Setup](#-firebase-setup-required))

### Steps

**1. Clone the repository**
```bash
git clone https://github.com/atanucsejgec/NotiSync_Reciver.git
cd NotiSyncReceiver
```

**2. Open in Android Studio**
```
File → Open → Select the NotiSyncReceiver folder
```

**3. Add Firebase config**
> ⚠️ The app won't build without `google-services.json`  
> See [🔥 Firebase Setup](#-firebase-setup-required) below

**4. Sync & Run**
```
Build → Make Project  (or press Ctrl+F9)
Run → Run 'app'       (or press Shift+F10)
```

---

## 🔥 Firebase Setup (Required)

> The app requires Firebase to function. Follow these steps carefully.

**Step 1 — Create Firebase Project**
- Go to [Firebase Console](https://console.firebase.google.com/)
- Click **"Add Project"** → Follow setup wizard

**Step 2 — Register Android App**
```
Package name: com.app.notisync_receiver
App nickname: NotiSync Receiver (optional)
```

**Step 3 — Download & Place Config File**
```bash
# Place the downloaded file here:
NotiSyncReceiver/
└── app/
    └── google-services.json   ← HERE
```

**Step 4 — Enable Firebase Services**

| Service | Steps |
|---------|-------|
| 🔐 **Authentication** | Build → Authentication → Sign-in method → Enable *Email/Password* & *Google* |
| 🗄️ **Firestore** | Build → Firestore Database → Create database → Start in **Test Mode** |

**Step 5 — Sync Gradle**
```
Android Studio → File → Sync Project with Gradle Files
```

<details>
<summary>⚠️ Common Firebase Issues & Fixes</summary>

| Problem | Fix |
|---------|-----|
| `google-services.json` not found | Make sure it's inside `/app` folder, not root |
| SHA-1 mismatch for Google Sign-In | Add your debug SHA-1 in Firebase console |
| Firestore permission denied | Set Firestore rules to Test Mode or configure proper rules |
| Build fails after adding JSON | Sync Gradle again and Invalidate Caches |

</details>

---

## 📁 Project Structure

```text
NotiSyncReceiver/
├── app/
│   ├── src/main/java/com/app/notisync_receiver/
│   │   │
│   │   ├── 📂 data/
│   │   │   ├── local/
│   │   │   │   ├── ReceivedNotificationDao.kt      # DAO interface for notifications
│   │   │   │   ├── ReceivedNotificationEntity.kt   # Room entity/table
│   │   │   │   └── ReceiverDatabase.kt             # Room database instance
│   │   │   │
│   │   │   ├── remote/
│   │   │   │   └── FirestoreDataSource.kt          # All Firestore operations
│   │   │   │
│   │   │   └── repository/
│   │   │       ├── AuthRepository.kt               # Login, register, session
│   │   │       ├── CallLogViewRepository.kt        # Call log sync logic
│   │   │       ├── DeviceRepository.kt             # Linked device management
│   │   │       ├── FeatureFlagRepository.kt        # Feature toggle control
│   │   │       ├── KeyboardRepository.kt           # Keyboard input data
│   │   │       ├── LocationRequestRepository.kt    # Location sync logic
│   │   │       └── NotificationRepository.kt       # Notification sync logic
│   │   │
│   │   ├── 📂 di/
│   │   │   ├── AppModule.kt                        # App-wide Hilt bindings
│   │   │   └── DatabaseModule.kt                   # Room & DAO providers
│   │   │
│   │   ├── 📂 domain/
│   │   │   └── model/
│   │   │       ├── CallLogBatch.kt                 # Call log data model
│   │   │       ├── CallRecord.kt                   # Single call record
│   │   │       ├── CapturedSentence.kt             # Keyboard captured text
│   │   │       ├── DeviceLocation.kt               # GPS location model
│   │   │       ├── NotificationBatch.kt            # Batch of notifications
│   │   │       ├── ReceivedNotification.kt         # Single notification model
│   │   │       ├── SenderDevice.kt                 # Linked device model
│   │   │       └── SentenceBatch.kt                # Batch of captured text
│   │   │
│   │   ├── 📂 ui/
│   │   │   ├── navigation/
│   │   │   │   └── ReceiverNavGraph.kt             # Navigation routes & graph
│   │   │   │
│   │   │   ├── screens/
│   │   │   │   ├── CallLogScreen.kt                # Call log viewer UI
│   │   │   │   ├── DeviceListScreen.kt             # Linked devices manager
│   │   │   │   ├── KeyboardScreen.kt               # Keyboard input viewer
│   │   │   │   ├── LocationScreen.kt               # Map & location UI
│   │   │   │   ├── LoginScreen.kt                  # Auth / Login UI
│   │   │   │   ├── NotificationListScreen.kt       # Notification feed UI
│   │   │   │   └── SettingsScreen.kt               # App settings UI
│   │   │   │
│   │   │   └── theme/                              # Material 3 theme & colors
│   │   │
│   │   ├── 📂 viewmodel/
│   │   │   ├── AuthViewModel.kt                    # Login/auth state
│   │   │   ├── CallLogViewModel.kt                 # Call log state
│   │   │   ├── DeviceListViewModel.kt              # Device list state
│   │   │   ├── KeyboardViewModel.kt                # Keyboard data state
│   │   │   ├── LocationViewModel.kt                # Location state
│   │   │   └── NotificationListViewModel.kt        # Notification feed state
│   │   │
│   │   ├── MainActivity.kt                         # Single activity entry point
│   │   └── NotiSyncReceiverApp.kt                  # Hilt Application class
│   │
│   └── build.gradle.kts                            # App-level Gradle config
│
├── build.gradle.kts                                # Project-level Gradle config
├── settings.gradle.kts                             # Module settings
├── google-services.json                            # 🔥 Firebase config (not committed)
└── README.md
```

---

## 🗺️ Roadmap

| Status | Feature |
|--------|---------|
| ✅ Done | Firebase Firestore real-time sync |
| ✅ Done | Local notification history (Room) |
| ✅ Done | Authentication (Email + Google) |
| ✅ Done | Call log monitoring |
| ✅ Done | Keyboard input sync |
| ✅ Done | Location tracking |
| 🔄 In Progress | Multi-device grouping & labels |
| 📋 Planned | Advanced notification filtering rules |
| 📋 Planned | Dark mode optimization |
| 📋 Planned | Desktop/Web companion dashboard |
| 📋 Planned | Notification categories & priorities |
| 💡 Idea | Wear OS / smartwatch support |

---

## 🤝 Contributing

Contributions are what make the open-source community an amazing place to learn and grow! 🎉

**Ways to Contribute:**
- 🐛 Report bugs via [Issues](https://github.com/atanucsejgec/NotiSync_Reciver/issues)
- 💡 Suggest features
- 🔧 Submit Pull Requests
- ⭐ Star the repo to show support

**How to Contribute:**

1. **Fork** the repository
2. **Create** your feature branch
   ```bash
   git checkout -b feature/YourAmazingFeature
   ```
3. **Commit** your changes
   ```bash
   git commit -m "feat: add YourAmazingFeature"
   ```
4. **Push** to your branch
   ```bash
   git push origin feature/YourAmazingFeature
   ```
5. **Open** a Pull Request 🚀

> 💡 Please follow [Conventional Commits](https://www.conventionalcommits.org/) for commit messages.

---

## 📄 License

```
MIT License

Copyright (c) 2025 Atanu Majumder

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for full details.

---

## 📬 Contact

<div align="center">

**Atanu Majumder**

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/atanucsejgec)

**Project Link:** [https://github.com/atanucsejgec/NotiSync_Reciver](https://github.com/atanucsejgec/NotiSync_Reciver)

</div>

---

<div align="center">

<img src="https://capsule-render.vercel.app/api?type=waving&color=gradient&customColorList=6,11,20&height=100&section=footer"/>

**⭐ If NotiSync helped you, please consider giving it a star — it means a lot! ⭐**

Made with ❤️ using Kotlin & Jetpack Compose

</div>
