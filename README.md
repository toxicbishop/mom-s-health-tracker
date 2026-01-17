# Mom's Health Tracker 🏥

A lightweight, premium mobile application designed for easy health tracking. This project demonstrates a **3-Tier Application Architecture** using React Native, Google Apps Script, and Google Sheets.

## 🏗️ Architecture & Educational Objectives

This project is a practical demonstration of a **3-Tier Application Architecture**. Each layer has a specific responsibility:

### 1. Presentation Layer (Front-End)
- **Role**: The Mobile App.
- **Responsibility**: User interaction, data capture, and visual feedback. It communicates with the backend via HTTP POST requests using JSON payloads.
- **Technologies**: React Native, Expo, Lucide Icons.

### 2. Application Layer (API)
- **Role**: Google Apps Script (Web App).
- **Responsibility**: Receives data from the mobile app, validates it, applies business logic (formatting dates, handling nulls), and acts as a bridge to the data layer.
- **Technologies**: JavaScript (Google Apps Script), JSON.

### 3. Data Layer (Persistence)
- **Role**: Google Spreadsheet.
- **Responsibility**: Reliable storage of the data. It acts as a lightweight database where data is persisted in a structured format.
- **Technologies**: Google Sheets.

---

## 📂 Project Structure

```text
Mom-health-tracker/
├── backend/            # Google Apps Script Source
│   └── Code.gs         # Backend logic for Sheets integration
├── mobile/             # React Native (Expo) Application
│   ├── assets/         # App icons and splash screens
│   ├── App.js          # Main application logic & UI
│   ├── Constants.js    # API configuration & URLs
│   ├── app.json        # Expo configuration
│   └── package.json    # Mobile dependencies
├── Dockerfile          # Container configuration for web version
├── .dockerignore       # Docker exclusion rules
├── .gitignore          # Git exclusion rules
├── LICENSE             # MIT License
└── README.md           # Project documentation
```

---

## 🚀 Setup Instructions

### 1. Persistence Layer (Google Sheets)
1.  Create a new [Google Sheet](https://sheets.new).
2.  Note the sheet name (default is `Sheet1`).

### 2. API Layer (Google Apps Script)
1.  In your Google Sheet, go to **Extensions** > **Apps Script**.
2.  Delete any existing code and paste the content from `backend/Code.gs` found in this project.
3.  Click the **Deploy** button (top right) > **New deployment**.
4.  Select type: **Web App**.
5.  Description: `Mom Health Tracker API`.
6.  Execute as: **Me**.
7.  Who has access: **Anyone** (This is required for the mobile app to communicate without OAuth complexity).
8.  Click **Deploy**, authorize the permissions, and **copy the Web App URL**.

### 3. Mobile App Configuration
1.  Open `mobile/Constants.js` in this project.
2.  Replace `'YOUR_GOOGLE_APPS_SCRIPT_URL_HERE'` with the URL you copied in the previous step.

---

## 📱 Running the App

1.  Open a terminal in the `mobile` directory.
2.  Install dependencies (if not already done):
    ```bash
    npm install
    ```
3.  Start the Expo server:
    ```bash
    npx expo start
    ```
4.  Use the **Expo Go** app on your phone (scan the QR code) or an emulator to run the application.

---

## 🐳 Docker Deployment (Web)

To run the web version in a container:
```bash
docker build -t mom-health-tracker .
docker run -p 8080:80 mom-health-tracker
```
Visit `http://localhost:8080` to see the app.

---

## ✨ Features
- **Modern UI**: Dark mode background with glowing neon accents.
- **Glassmorphism**: Elegant semi-transparent cards for a premium feel.
- **Micro-interactions**: Subtle feedback on button presses and transitions.
- **Validation**: Ensures data integrity before sending to the cloud.
- **Error Handling**: Graceful handling of network issues or configuration errors.

---

## 📜 License

Distributed under the MIT License. See [MIT License](./LICENSE) for more information.
