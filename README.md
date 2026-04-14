# 🚀 Career Toolkit - Resume Builder & Analyzer

A full-stack web application that helps users **create professional resumes** and **analyze them for ATS (Applicant Tracking System) compatibility**.

---

## 📌 Features

### 📝 Resume Builder

* Create professional resumes بسهولة (easy to use form)
* Dynamic sections:

  * Education
  * Work Experience
  * Projects
* Auto-save form data (localStorage)
* Preview resume before download
* Download resume as PDF

---

### 🔍 Resume Analyzer

* Upload resume (PDF, DOCX, TXT)
* Drag & Drop support + file picker
* ATS Score calculation
* Detects:

  * Skills present in resume
  * Missing skills (based on job description)
* Section-wise analysis:

  * Experience
  * Education
  * Projects
  * Skills
* Smart suggestions for improvement
* Visual charts using Chart.js

---

## 🛠️ Tech Stack

### Frontend

* HTML
* CSS
* JavaScript (Vanilla)
* Chart.js

### Backend

* Java (Spring Boot)
* REST APIs

### Libraries Used

* Apache PDFBox (PDF parsing)
* Apache POI (DOCX parsing)

---

## ⚙️ How It Works

### Resume Builder Flow

1. User fills form
2. Data auto-saved in browser
3. On submit → sent to backend
4. Backend generates PDF
5. File is downloaded

---

### Resume Analyzer Flow

1. User uploads resume
2. File sent to backend API
3. Backend extracts text
4. Analyzer processes:

   * Skill detection
   * ATS scoring
   * Suggestions
5. Results returned and displayed

---

## 📂 Project Structure

```
src/
 ├── main/
 │   ├── java/com/careertoolkit/
 │   │   ├── controller/
 │   │   ├── service/
 │   │   ├── model/
 │   │   └── config/
 │   └── resources/
 │       ├── static/
 │       │   ├── css/
 │       │   ├── js/
 │       │   └── index.html
 │       └── application.properties
```

---



## 🧪 Testing Checklist

* ✅ Resume Builder working
* ✅ PDF download working
* ✅ Drag & Drop upload working
* ✅ File select upload working
* ✅ Analyzer API working
* ✅ ATS score generated
* ✅ Charts & suggestions displayed

---

## ⚠️ Known Limitations

* ATS scoring is rule-based (not AI-based)
* Skill detection depends on keyword matching
* Free hosting may have cold start delay

---

## 💡 Future Improvements

* AI-based resume analysis
* Login & user profiles
* Save multiple resumes
* Better UI/UX
* Real job matching system

---

## 🙌 Author

**Karan**

---

## ⭐ If you like this project

Give it a star on GitHub ⭐
