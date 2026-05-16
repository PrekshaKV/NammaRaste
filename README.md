# NammaRaste 🛣️
### Every Road Has a Story. Be Its Auditor.

NammaRaste is a community-driven Android application designed 
to track, report, and monitor road conditions in local 
communities. It empowers citizens to become active auditors 
of their neighborhood roads by reporting damage, tracking 
repair status, and holding authorities accountable for 
road maintenance.

---

## 🚨 Problem Statement

Poor road conditions cause accidents, vehicle damage, and 
delays in daily commute. Local communities often have no 
easy way to report road damage or track whether complaints 
are being addressed. NammaRaste bridges this gap by giving 
every citizen a voice to report and monitor road conditions 
in real time.

---

## 💡 Vision

To build a transparent, community-powered road monitoring 
system where every pothole, crack, and damaged road surface 
is documented, reported, and tracked until fixed — making 
roads safer for everyone.

---

## ✨ Features

### 🗺️ Road Directory
- Browse all roads in your local area
- View road health scores (Excellent / Good / Fair / Poor)
- See total reports submitted per road
- Filter roads by condition category

### 📋 Dashboard
- View total roads monitored
- See average health score across all roads
- Track total damage reports submitted
- Identify critical roads needing urgent attention
- Road Condition Summary with color-coded categories

### 📝 Damage Report
- Submit detailed road damage reports
- Add location, damage type and severity
- Reports tagged as High / Medium / Low priority
- Track report status (Pending / In Progress / Resolved)

### 📅 Recent Reports
- View latest damage reports from the community
- See who reported and when
- Monitor repair progress in real time

### 👷 Contractor Directory
- View list of road contractors in the area
- Track which contractor is assigned to which road
- Monitor contractor performance

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin + Java |
| Architecture | MVVM (Model-View-ViewModel) |
| UI | Material Design 3 |
| Navigation | Jetpack Navigation Component |
| State Management | LiveData + ViewModel |
| Storage | JSON (local assets) |
| Build System | Gradle with Kotlin DSL |

---

## 📁 Project Structure

app/src/main/
├── java/com/nammaraste/
│   ├── MainActivity.kt
│   ├── adapters/       → RecyclerView Adapters
│   ├── fragments/      → All Screen Fragments
│   ├── models/         → Data Models
│   ├── utils/          → Helper Classes
│   └── viewmodels/     → ViewModels for each screen
├── assets/
│   ├── roads.json          → Road data
│   ├── damage_reports.json → Report data
│   └── contractors.json    → Contractor data
└── res/
├── layout/     → XML layouts
├── navigation/ → Nav graph
├── drawable/   → Icons & backgrounds
└── values/     → Colors, strings, themes
---

## 🚀 How to Run

1. Clone this repository
```bash
git clone https://github.com/PrekshaKV/NammaRaste.git
```
2. Open **NammaRaste** folder in Android Studio
3. Wait for Gradle sync to complete
4. Connect Android device or start emulator (min SDK 24)
5. Click ▶ Run button

---

## 📊 Road Health Score System

| Score | Category | Color |
|-------|----------|-------|
| 80-100 | Excellent | 🟢 Green |
| 60-79 | Good | 🟡 Yellow |
| 40-59 | Fair | 🟠 Orange |
| Below 40 | Poor/Critical | 🔴 Red |

---

## 🎯 Impact Goals

- 🛡️ **Road Safety** — Reduce accidents by identifying 
  dangerous road conditions early
- 👥 **Community Empowerment** — Give citizens a platform 
  to report and track road issues
- 📢 **Accountability** — Create transparency between 
  citizens, contractors and authorities
- 🏘️ **Better Infrastructure** — Contribute to improved 
  road quality in local communities

---

## 👩‍💻 Developed By
Preksha K V
