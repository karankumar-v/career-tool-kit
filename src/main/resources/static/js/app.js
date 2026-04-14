// ===== API BASE =====
const API_BASE = '/api';

let sectionChart = null;
let selectedResumeFile = null;


// ===== DOM READY =====
document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    initDynamicButtons();
    initAutoSave();
    initFormSubmit();
    initPreview();
    initAnalyzer();
    loadSavedData();
});


// ===== NAVIGATION =====
function initNavigation() {
    const navLinks = document.querySelectorAll('.nav-link');
    const tabContents = document.querySelectorAll('.tab-content');

    navLinks.forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();

            navLinks.forEach(l => l.classList.remove('active'));
            link.classList.add('active');

            tabContents.forEach(tab => {
                tab.classList.remove('active');
                if (tab.id === link.dataset.tab) {
                    tab.classList.add('active');
                }
            });
        });
    });
}


// ===== ADD BUTTONS FIX =====
function initDynamicButtons() {
    window.addEducation = function () {
    const container = document.getElementById('educationContainer');

    const div = document.createElement('div');
    div.className = 'education-entry';

    div.innerHTML = `
        <button type="button" class="entry-remove" onclick="this.parentElement.remove()">×</button>

        <div class="form-grid">
            <div class="form-group">
                <label>Degree</label>
                <input type="text" name="edu_degree[]" placeholder="Degree">
            </div>

            <div class="form-group">
                <label>Institution</label>
                <input type="text" name="edu_institution[]" placeholder="Institution">
            </div>

            <div class="form-group">
                <label>Year</label>
                <input type="text" name="edu_year[]" placeholder="2022 - 2024">
            </div>

            <div class="form-group">
                <label>GPA</label>
                <input type="text" name="edu_gpa[]" placeholder="8.5">
            </div>
        </div>
    `;

    container.appendChild(div);
};

    window.addExperience = function () {
    const container = document.getElementById('experienceContainer');

    const div = document.createElement('div');
    div.className = 'experience-entry';

    div.innerHTML = `
        <button type="button" class="entry-remove" onclick="this.parentElement.remove()">×</button>

        <div class="form-grid">
            <div class="form-group">
                <label>Job Title</label>
                <input type="text" name="exp_title[]" placeholder="Developer">
            </div>

            <div class="form-group">
                <label>Company</label>
                <input type="text" name="exp_company[]" placeholder="Company Name">
            </div>

            <div class="form-group">
                <label>Duration</label>
                <input type="text" name="exp_duration[]" placeholder="Jan 2023 - Present">
            </div>
        </div>

        <div class="form-group full-width">
            <label>Responsibilities</label>
            <textarea name="exp_responsibilities[]" rows="3"></textarea>
        </div>
    `;

    container.appendChild(div);
};
  window.addProject = function () {
    const container = document.getElementById('projectsContainer');

    const div = document.createElement('div');
    div.className = 'project-entry';

    div.innerHTML = `
        <button type="button" class="entry-remove" onclick="this.parentElement.remove()">×</button>

        <div class="form-grid">
            <div class="form-group">
                <label>Project Name</label>
                <input type="text" name="proj_name[]" placeholder="Project Name">
            </div>

            <div class="form-group">
                <label>Technologies</label>
                <input type="text" name="proj_tech[]" placeholder="Java, Spring Boot">
            </div>
        </div>

        <div class="form-group full-width">
            <label>Description</label>
            <textarea name="proj_description[]" rows="2"></textarea>
        </div>
    `;

    container.appendChild(div);
}
}


// ===== AUTO SAVE =====
function initAutoSave() {
    const form = document.getElementById('resumeForm');
    if (!form) return;

    form.addEventListener('input', () => {
        collectFormData();
    });
}


// ===== FORM SUBMIT =====
function initFormSubmit() {
    const form = document.getElementById('resumeForm');

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const data = collectFormData();

        if (!data.fullName || !data.email) {
            showToast('Fill required fields', 'error');
            return;
        }

        showLoading();

        try {
            const response = await fetch(`${API_BASE}/resume/generate`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error();

            const blob = await response.blob();
            const url = URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = `${data.fullName}_Resume.pdf`;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);

            showToast('Resume downloaded!', 'success');

        } catch {
            showToast('Download failed', 'error');
        } finally {
            hideLoading();
        }
    });
}


// ===== PREVIEW =====
function initPreview() {
    window.previewResume = async function () {
        const data = collectFormData();

        showLoading();

        try {
            const response = await fetch(`${API_BASE}/resume/preview`, {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(data)
            });

            if (!response.ok) throw new Error();

            const blob = await response.blob();
            window.open(URL.createObjectURL(blob));

        } catch {
            showToast('Preview failed', 'error');
        } finally {
            hideLoading();
        }
    };
}


// ===== ANALYZER =====
function initAnalyzer() {
    const analyzerForm = document.getElementById('analyzerForm');
    const uploadArea = document.getElementById('uploadArea');
    const fileInput = document.getElementById('resumeFile');
    const selectedFileDiv = document.getElementById('selectedFile');
    const fileNameSpan = document.getElementById('fileName');

    if (!analyzerForm) return;

    // CLICK
    uploadArea.addEventListener('click', () => fileInput.click());

    // DRAG
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('drop', (e) => {
    e.preventDefault();

    const file = e.dataTransfer.files[0];
    if (!file) return;

    handleFile(file); // ✅ SAME function
});

       fileInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) return;

    handleFile(file); // ✅ same function
});
    

    analyzerForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // ✅ USE STORED FILE
    if (!selectedResumeFile) {
        showToast('Upload your resume', 'error');
        return;
    }

    const formData = new FormData();
    formData.append('file', selectedResumeFile);

    const jobDesc = document.getElementById('jobDescription').value;
    formData.append('jobDescription', jobDesc);

    showLoading();

    try {
        const response = await fetch(`${API_BASE}/analyzer/analyze`, {
            method: 'POST',
            body: formData
        });

        if (!response.ok) throw new Error();

        const result = await response.json();
        displayResults(result);

        showToast('Analysis complete', 'success');

    } catch (err) {
        console.error(err);
        showToast('Analysis failed', 'error');
    } finally {
        hideLoading();
    }
});

   function handleFile(file) {

    // ✅ store file (MOST IMPORTANT LINE)
    selectedResumeFile = file;

    // UI update
    document.getElementById('fileName').textContent = file.name;
    document.getElementById('uploadArea').style.display = 'none';
    document.getElementById('selectedFile').style.display = 'flex';

    console.log("File stored:", selectedResumeFile);
}

    // ❗ MAIN FIX IS HERE
    analyzerForm.addEventListener('submit', async (e) => {
        e.preventDefault(); // 🔥 STOP PAGE REFRESH

        
        const jobDesc = document.getElementById('jobDescription').value;

        if (!file) {
            showToast('Please upload a resume', 'error');
            return;
        }

        const formData = new FormData();
        formData.append('file', file);
        formData.append('jobDescription', jobDesc);

        showLoading();

        try {
            const response = await fetch(`${API_BASE}/analyzer/analyze`, {
                method: 'POST',
                body: formData
            });

            if (!response.ok) throw new Error();

            const result = await response.json();

            displayResults(result);

            showToast('Analysis complete', 'success');

        } catch (err) {
            console.error(err);
            showToast('Analysis failed', 'error');
        } finally {
            hideLoading();
        }
    });

    // CLEAR FILE
    window.clearFile = function () {
    selectedResumeFile = null; // ✅ reset

    fileInput.value = '';
    uploadArea.style.display = 'block';
    selectedFileDiv.style.display = 'none';
};
}


// ===== COLLECT DATA =====
function collectFormData() {
    const form = document.getElementById('resumeForm');

    const data = {
        fullName: form.fullName.value,
        email: form.email.value,
        phone: form.phone.value,
        location: form.location.value,
        linkedIn: form.linkedIn.value,
        github: form.github.value,
        summary: form.summary.value,
        skills: form.skills.value.split(',').map(s => s.trim()).filter(Boolean),
        education: [],
        experience: [],
        projects: []
    };

    form.querySelectorAll('.education-entry').forEach(e => {
        data.education.push({
            degree: e.querySelector('[name="edu_degree[]"]').value,
            institution: e.querySelector('[name="edu_institution[]"]').value,
            year: e.querySelector('[name="edu_year[]"]').value,
            gpa: e.querySelector('[name="edu_gpa[]"]').value
        });
    });

    form.querySelectorAll('.experience-entry').forEach(e => {
        data.experience.push({
            jobTitle: e.querySelector('[name="exp_title[]"]').value,
            company: e.querySelector('[name="exp_company[]"]').value,
            duration: e.querySelector('[name="exp_duration[]"]').value,
            responsibilities: e.querySelector('[name="exp_responsibilities[]"]').value.split('\n')
        });
    });

    form.querySelectorAll('.project-entry').forEach(e => {
        data.projects.push({
            name: e.querySelector('[name="proj_name[]"]').value,
            technologies: e.querySelector('[name="proj_tech[]"]').value,
            description: e.querySelector('[name="proj_description[]"]').value
        });
    });

    localStorage.setItem("resumeData", JSON.stringify(data));

    return data;
}


// ===== LOAD DATA =====
function loadSavedData() {
    const saved = localStorage.getItem("resumeData");
    if (!saved) return;

    const data = JSON.parse(saved);

    document.getElementById('fullName').value = data.fullName || '';
    document.getElementById('email').value = data.email || '';
    document.getElementById('phone').value = data.phone || '';
    document.getElementById('location').value = data.location || '';
    document.getElementById('linkedIn').value = data.linkedIn || '';
    document.getElementById('github').value = data.github || '';
    document.getElementById('summary').value = data.summary || '';
    document.getElementById('skills').value = (data.skills || []).join(', ');

    const eduC = document.getElementById('educationContainer');
    eduC.innerHTML = '';
    data.education.forEach(e => {
        addEducation();
        const last = eduC.lastElementChild;
        last.querySelector('[name="edu_degree[]"]').value = e.degree;
        last.querySelector('[name="edu_institution[]"]').value = e.institution;
        last.querySelector('[name="edu_year[]"]').value = e.year;
        last.querySelector('[name="edu_gpa[]"]').value = e.gpa;
    });

    const expC = document.getElementById('experienceContainer');
    expC.innerHTML = '';
    data.experience.forEach(e => {
        addExperience();
        const last = expC.lastElementChild;
        last.querySelector('[name="exp_title[]"]').value = e.jobTitle;
        last.querySelector('[name="exp_company[]"]').value = e.company;
        last.querySelector('[name="exp_duration[]"]').value = e.duration;
        last.querySelector('[name="exp_responsibilities[]"]').value = e.responsibilities.join('\n');
    });

    const projC = document.getElementById('projectsContainer');
    projC.innerHTML = '';
    data.projects.forEach(p => {
        addProject();
        const last = projC.lastElementChild;
        last.querySelector('[name="proj_name[]"]').value = p.name;
        last.querySelector('[name="proj_tech[]"]').value = p.technologies;
        last.querySelector('[name="proj_description[]"]').value = p.description;
    });
}


// ===== RESULT =====
function displayResults(result) {
    document.getElementById('analysisResults').style.display = 'block';

    // ===== SCORE =====
    const score = result.atsScore || 0;
    document.getElementById('scoreText').textContent = score + '%';

    // Circle animation
    const circle = document.getElementById('scoreCircle');
    circle.setAttribute("stroke-dasharray", `${score}, 100`);

    // Color based on score
    if (score >= 75) {
        circle.style.stroke = "#28a745"; // green
    } else if (score >= 50) {
        circle.style.stroke = "#ffc107"; // yellow
    } else {
        circle.style.stroke = "#dc3545"; // red
    }

    // ===== DETECTED SKILLS =====
    const detectedDiv = document.getElementById('detectedSkills');
    detectedDiv.innerHTML = '';

    (result.detectedSkills || []).forEach(skill => {
        const span = document.createElement('span');
        span.className = 'skill-tag';
        span.textContent = skill;
        detectedDiv.appendChild(span);
    });

    // ===== MISSING SKILLS =====
    const missingDiv = document.getElementById('missingSkills');
    missingDiv.innerHTML = '';

    (result.missingSkills || []).forEach(skill => {
        const span = document.createElement('span');
        span.className = 'skill-tag missing';
        span.textContent = skill;
        missingDiv.appendChild(span);
    });

    // ===== SUGGESTIONS =====
    const suggestionList = document.getElementById('suggestions');
    suggestionList.innerHTML = '';

    (result.suggestions || []).forEach(s => {
        const li = document.createElement('li');
        li.textContent = s;
        suggestionList.appendChild(li);
    });

    // ===== SECTION CHART =====
    const ctx = document.getElementById('sectionChart').getContext('2d');

    if (sectionChart) {
        sectionChart.destroy();
    }

    const sectionScores = result.sectionScores || {
        Skills: 70,
        Experience: 75,
        Education: 80,
        Projects: 65
    };

    sectionChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: Object.keys(sectionScores),
            datasets: [{
                label: 'Score',
                data: Object.values(sectionScores)
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100
                }
            }
        }
    });

    // ===== FEEDBACK TEXT =====
    const feedback = document.getElementById('overallFeedback');
    if (score >= 80) {
        feedback.textContent = "Excellent resume! You're ATS ready.";
    } else if (score >= 60) {
        feedback.textContent = "Good resume, but can be improved.";
    } else {
        feedback.textContent = "Needs improvement to pass ATS.";
    }
}


// ===== UI =====
function showLoading() {
    document.getElementById('loadingOverlay').style.display = 'flex';
}

function hideLoading() {
    document.getElementById('loadingOverlay').style.display = 'none';
}

function showToast(msg, type='success') {
    const t = document.getElementById('toast');
    t.textContent = msg;
    t.className = `toast ${type} show`;
    setTimeout(()=>t.classList.remove('show'),3000);

}