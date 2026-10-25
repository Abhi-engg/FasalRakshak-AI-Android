# FasalRakshak AI (फसल रक्षक) — Native Android Application

[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![UI](https://img.shields.io/badge/UI-Jetpack%20Compose%20%7C%20Material%203-blue.svg)](https://developer.android.com/jetpack/compose)
[![ML](https://img.shields.io/badge/ML-TensorFlow%20Lite%20Edge%20AI-orange.svg)](https://www.tensorflow.org/lite)
[![Architecture](https://img.shields.io/badge/Architecture-Clean%20%2B%20MVI%2FMVVM-emerald.svg)](https://developer.android.com/topic/architecture)

> **Empowering Indian Farmers with 100% Offline, On-Device Crop Disease & Pest Diagnostics powered by Edge AI.**

---

## 🌾 Overview

**FasalRakshak AI** is an agronomic edge-AI diagnostic assistant designed specifically for Indian agriculture and low-connectivity rural environments. Running completely offline on-device without requiring continuous internet connectivity, the application enables farmers and field extension workers to photograph affected plant leaves and instantly receive:

1. **Dual-Head Edge Diagnosis**: Simultaneous foliar disease classification across 83 pathological categories and pest identification across 113 entomological classes.
2. **Pathological Crop Masking Prior**: Context-aware Bayesian prior that dynamically masks biologically impossible disease candidates based on the selected crop.
3. **Visual Explainability (Grad-CAM)**: Real-time class activation heatmap overlays highlighting the exact lesion regions that influenced the model's decision.
4. **ICAR-Aligned Agronomic Advisory**: Instant chemical, biological, and cultural management recommendations adhering to the Indian Council of Agricultural Research (ICAR) guidelines.
5. **Bilingual Hindi & English UI**: Field-optimized accessibility crafted for direct farmer usability under harsh outdoor sunlight.

---

## 🚀 Key Features

- **⚡ 100% Offline TFLite Inference**: Zero latency, zero cloud dependency. Complete inference cycle executes in < 180ms on modern ARM64 devices using hardware acceleration (GPU / NNAPI delegates).
- **📸 CameraX Smart Viewfinder**:
  - Precision tap-to-focus with spring-animated reticle and tactile haptic feedback.
  - Natural daylight heuristic guidance and real-time blur/lighting detection.
  - Interactive crop-selection dropdown prior.
- **🎨 Material 3 Expressive UI**:
  - Custom floating navigation dock with organic center crest and elevated Scan FAB.
  - Spring-animated pill morphing on tab transitions.
  - Tactile micro-interactions across cards, chips, and accordions.
- **📜 Diagnostic History & Analytics**:
  - Local SQLite / Room database caching previous field scans.
  - Historical breakdown by crop, severity alert level, and detected pests.

---

## 🧠 Model Architecture & Edge Pipeline

The core machine learning engine utilizes a custom **Dual-Head EfficientNet-B3** backbone:

```
[Leaf Image (300x300x3)]
         │
         ▼
[EfficientNet-B3 Backbone (Weights Quantized / FP16)]
         │
    ┌────┴────────────────────────┐
    ▼                             ▼
[Head 1: Disease Pathology]   [Head 2: Pest Entomology]
  - 83 Foliar Disease Classes   - 113 Insect / Pest Classes
  - Softmax Probability         - Calibrated Softmax Detection
    │                             │
    └──────────────┬──────────────┘
                   ▼
    [Pathological Crop Prior Filter]
                   ▼
   [Top Predictions + Grad-CAM Heatmap]
                   ▼
       [ICAR Advisory Resolution]
```

- **Model File**: `app/src/main/assets/models/crop_teacher_efficientnet_b3.tflite`
- **Taxonomy & Knowledge**: `app/src/main/assets/taxonomy/` and `app/src/main/assets/knowledge/`

---

## 🛠 Tech Stack & Architecture

- **Language**: Kotlin 2.0+
- **UI Toolkit**: Jetpack Compose, Material 3 Expressive Design System
- **Camera Pipeline**: AndroidX CameraX (Preview, ImageCapture, FocusMeteringAction)
- **Dependency Injection**: Dagger Hilt
- **State Management**: Unidirectional Data Flow (UDF), Kotlin Coroutines & StateFlow
- **Edge ML Runtime**: TensorFlow Lite Android Support Library (`org.tensorflow:tensorflow-lite`, `org.tensorflow:tensorflow-lite-support`, `org.tensorflow:tensorflow-lite-gpu`)
- **Image Processing**: Android Graphics / ExifInterface orientation auto-correction, Coil
- **Local Persistence**: Room Database / Preferences DataStore

---

## 📂 Project Structure

```
android/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── assets/
│   │   │   │   ├── knowledge/       # ICAR agronomic treatment database (JSON)
│   │   │   │   ├── models/          # Quantized Dual-Head EfficientNet-B3 TFLite model
│   │   │   │   └── taxonomy/        # Disease & pest taxonomy labels
│   │   │   ├── java/com/agrovision/ai/
│   │   │   │   ├── app/             # Application class, MainActivity, Navigation
│   │   │   │   ├── camera/          # CameraX manager, lighting & blur analyzers
│   │   │   │   ├── data/            # Room database, repositories, scan entities
│   │   │   │   ├── domain/          # Models, business use-cases, crop taxonomy
│   │   │   │   ├── ml/              # CropDiseaseClassifier, GradCAM generator, prior masking
│   │   │   │   └── presentation/    # Compose UI (Home, Camera, Diagnosis, History, Guide)
│   │   │   └── res/                 # Vector drawables, mipmaps, strings, colors
│   │   └── build.gradle.kts
│   ├── build.gradle.kts
│   └── settings.gradle.kts
```

---

## ⚙️ Build & Installation

### Prerequisites
- **Android Studio Ladybug (2024.2+)** or later
- **JDK 17** or higher
- **Android SDK**: `compileSdk = 34`, `minSdk = 24`, `targetSdk = 34`
- Physical device running Android 7.0+ (API 24+) recommended for CameraX and TFLite hardware delegate testing.

### Steps to Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/Abhi-engg/FasalRakshak-AI-Android.git
   cd FasalRakshak-AI-Android
   ```

2. **Open in Android Studio** or build directly via Gradle:
   ```bash
   # Linux / macOS
   ./gradlew assembleDebug

   # Windows PowerShell
   .\gradlew.bat assembleDebug
   ```

3. **Install on connected device**:
   ```bash
   .\gradlew.bat installDebug
   ```

4. **Launch the application**:
   ```bash
   adb shell am start -n com.agrovision.ai/.app.MainActivity
   ```

---

## 📄 License & Attribution

Developed as part of the Major Engineering Capstone Project on **Edge AI for Indian Agriculture**.
Advisory datasets and pathological thresholds follow **ICAR** and **State Agricultural University (SAU)** guidelines.
