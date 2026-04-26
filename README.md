# BudgetBuddy – Personal Budget Tracker

BudgetBuddy is an Android budgeting app designed to make expense tracking, goal setting, and financial management simple and engaging. Developed in Kotlin using AndroidX, Room Database, and Material Design, the app runs on devices with API 24 (Android 7.0) and above.

## Table of Contents
- [Features](#features)
- [Demo Video](#demo-video)
- [Screenshots](#screenshots)
- [Own Additional Features](#own-additional-features)
- [Technical Stack](#technical-stack)
- [Build Instructions](#build-instructions)
- [Testing & Continuous Integration](#testing--continuous-integration)
- [Project Structure](#project-structure)
- [Author](#author)

---

## Features

- **User Authentication**  
  Register and login with username/password. Passwords are stored securely (SHA-256 hashed). Remember Me option via SharedPreferences.

- **Category Management**  
  Create, edit, and delete custom expense categories.  
  Five beautiful built‑in categories (Transport, Rent, Groceries, Entertainment, Dining), each with its own colour and icon.

- **Expense Logging**  
  Add an expense entry with amount, date, start/end time, description, category, and an optional receipt photo (camera or gallery).  
  Foreign currency conversion built‑in for overseas purchases.

- **Monthly Goal Setting**  
  Set a minimum and maximum spending goal per month.  
  Visual dashboard shows progress against these goals.

- **Interactive Dashboard**  
  Circular progress indicator with percentage overlay for overall monthly spending.  
  Custom pie chart breaking down spending by category.  
  Coloured progress bars for each category.

- **Filtered Expense List**  
  View all expenses within a custom date range.  
  Long‑press an expense to view attached receipt photo.

- **Category Totals (Reports)**  
  Select a date range and see total spending per category, plus overall total spent in that period.

- **PDF Export**  
  Export the filtered expense list to a PDF file and share/print it.

- **Currency Converter**  
  Offline hard‑coded exchange rates supporting ZAR, USD, EUR, GBP, JPY.

- **Gamification (Foundation)**  
  Placeholder badges and a “Coming Soon” quiz button, ready for future enhancements.

---

## Demo Video

🎥 Watch the full demonstration:  
[BudgetBuddy Demo](https://youtu.be/your_video_link_here)

---

## Screenshots

| Login | Dashboard | Add Expense | Reports |
|-------|-----------|-------------|---------|
| ![Login](screenshots/login.png) | ![Dashboard](screenshots/dashboard.png) | ![Add](screenshots/add_expense.png) | ![Reports](screenshots/reports.png) |

*(Add screenshots to a `screenshots/` folder in your repo if desired.)*

---

## Own Additional Features (Part 2)

1. **Offline Currency Converter**  
   Hard‑coded exchange rates (updated as needed) supporting five currencies. The converter is available from the “More” menu and is also integrated into the Add Expense screen for foreign purchases.

2. **PDF Export**  
   Exports the currently displayed expense list to a well‑formatted PDF saved in the device’s Downloads folder, ready for printing or sharing.

---

## Technical Stack

- **Language:** Kotlin
- **Minimum SDK:** 24 (Android 7.0 Nougat)
- **Target SDK:** 35
- **Architecture:** MVVM (ViewModels + LiveData)
- **Database:** Room (SQLite abstraction)
- **UI:** Material Components, ViewBinding, BottomNavigationView, custom PieChartView
- **Image Capture:** Camera/Gallery via ActivityResultContracts + FileProvider
- **PDF:** Android PdfDocument API
- **Testing:** JUnit, Room in‑memory tests
- **CI/CD:** GitHub Actions (build on every push)

---

## Build Instructions

1. **Clone the repository**  
   ```bash
   git clone https://github.com/YourUsername/BudgetBuddy.git
