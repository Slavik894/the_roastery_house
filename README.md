# Barista Assistant — Hybrid POS System for Android Tablets

A native Android application designed to optimize the ordering process in coffee shops. This system offers a hybrid approach, combining a high-efficiency **Standard Mode** for staff and an interactive **Special Mode (Drink Creator)** for customers to personalize their orders.

## Key Features

* **Dual-Mode Interface:**
 *Standard Mode:* Optimized for baristas to quickly process typical orders.
 *Special Mode:* An interactive "Drink Creator" that allows customers to customize ingredients (coffee/tea base, milk types, syrups, etc.).
* **Role-Based Access Control:** Secure authentication and management for Administrators and Baristas.
* **Dynamic Cost Calculation:** Real-time price updates based on selected components and modifiers.
* **Robust Data Management:** Local persistence using SQLite with a "Snapshot" mechanism to preserve the state of orders at the moment of creation.
* **Modern UI/UX:** Fully compliant with **Material Design 3**, featuring adaptive layouts for tablets and organic shapes for better accessibility.

## Tech Stack

* **Language:** Java
* **IDE:** Android Studio
* **Database:** SQLite
* **UI/Layout:** XML with Material Design 3 components
* **Background Processing:** WorkManager for asynchronous tasks and data synchronization
* **Architecture:** Clean architecture principles with a focus on modularity and scalability

## Technical Highlights

* **Asynchronous Operations:** Utilizing modern background task scheduling to ensure a smooth UI experience without blocking the main thread.
* **Efficient Persistence:** Optimized database schema for handling complex, multi-component orders and user history.
* **Responsiveness:** Specifically designed for large tablet screens to maximize information density and ergonomics.

## Getting Started

1. Clone the repository:
   ```bash
   git clone [https://github.com/your-username/barista-assistant.git](https://github.com/your-username/barista-assistant.git)
   
2. Open the project in Android Studio.

3. Ensure you have JDK 11+ and the latest Android SDK installed.
4. Build and run the app on an Android Tablet emulator or a physical device (min SDK 24).
