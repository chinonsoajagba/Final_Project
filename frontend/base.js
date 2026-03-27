/* ============================================================
   UNIVERSITY SCHEDULING SYSTEM - Global JavaScript
   Updated with JWT authentication
   ============================================================ */

const API_BASE = "http://localhost:8080/api";

/* ============================================================
   TOKEN HELPERS
   ============================================================ */
function getToken() {
  return localStorage.getItem("token");
}

function getRole() {
  return localStorage.getItem("role");
}

function getUserEmail() {
  return localStorage.getItem("email");
}

function saveAuthData(data) {
  localStorage.setItem("token", data.token);
  localStorage.setItem("role", data.role);
  localStorage.setItem("email", data.email);
  localStorage.setItem("userId", data.userId);
  localStorage.setItem("linkedId", data.linkedId);
}

function clearAuthData() {
  localStorage.removeItem("token");
  localStorage.removeItem("role");
  localStorage.removeItem("email");
  localStorage.removeItem("userId");
  localStorage.removeItem("linkedId");
}

/* ============================================================
   AUTH GUARD
   ============================================================ */
function requireAuth(allowedRoles) {
  const token = getToken();
  const role = getRole();

  // No token at all - send to login
  if (!token || !role) {
    window.location.href = "login.html";
    return false;
  }

  // Role not allowed on this page - redirect to their dashboard
  if (allowedRoles && !allowedRoles.includes(role)) {
    redirectToDashboard(role);
    return false;
  }

  return true;
}

/* ============================================================
   REDIRECT TO CORRECT DASHBOARD BY ROLE
   ============================================================ */
function redirectToDashboard(role) {
  const dashboards = {
    ADMIN: "dashboard.html",
    ENROLLMENT_OFFICER: "dashboard.html",
    CLASS_HANDLER: "dashboard.html",
    STUDENT: "student-dashboard.html",
    TEACHER: "teacher-dashboard.html",
  };
  window.location.href = dashboards[role] || "login.html";
}

/* ============================================================
   LOGOUT
   ============================================================ */
function logout() {
  clearAuthData();
  window.location.href = "login.html";
}

/* ============================================================
   FETCH HELPERS - all requests include JWT token
   ============================================================ */
async function apiFetch(endpoint, options = {}) {
  const token = getToken();
  try {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      headers: {
        "Content-Type": "application/json",
        Authorization: token ? `Bearer ${token}` : "",
      },
      ...options,
    });

    // Token expired or invalid
    if (response.status === 401) {
      clearAuthData();
      window.location.href = "login.html";
      return;
    }

    // Not allowed
    if (response.status === 403) {
      throw new Error("You do not have permission to perform this action.");
    }

    if (!response.ok) {
      const err = await response.json();
      throw new Error(err.message || "Something went wrong");
    }

    if (response.status === 204) return null;
    return await response.json();
  } catch (error) {
    throw error;
  }
}

async function apiGet(endpoint) {
  return apiFetch(endpoint);
}

async function apiPost(endpoint, data) {
  return apiFetch(endpoint, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

async function apiPut(endpoint, data) {
  return apiFetch(endpoint, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

async function apiDelete(endpoint) {
  return apiFetch(endpoint, { method: "DELETE" });
}

/* ============================================================
   TOAST NOTIFICATIONS
   ============================================================ */
function showToast(message, type = "success") {
  const existing = document.getElementById("toast-notification");
  if (existing) existing.remove();

  const colors = {
    success: { bg: "#dcfce7", color: "#16a34a", icon: "bi-check-circle-fill" },
    danger: { bg: "#fee2e2", color: "#dc2626", icon: "bi-x-circle-fill" },
    warning: {
      bg: "#fef3c7",
      color: "#d97706",
      icon: "bi-exclamation-triangle-fill",
    },
    info: { bg: "#e0f2fe", color: "#0891b2", icon: "bi-info-circle-fill" },
  };
  const c = colors[type] || colors.success;

  const toast = document.createElement("div");
  toast.id = "toast-notification";
  toast.style.cssText = `
        position:fixed;bottom:24px;right:24px;z-index:9999;
        background:${c.bg};color:${c.color};
        border:1.5px solid ${c.color}33;
        padding:14px 20px;border-radius:10px;
        font-size:13.5px;font-weight:600;
        display:flex;align-items:center;gap:10px;
        box-shadow:0 8px 24px rgba(0,0,0,0.12);
        max-width:380px;animation:slideIn 0.3s ease;
        font-family:'DM Sans',sans-serif;
    `;
  toast.innerHTML = `
        <i class="bi ${c.icon}" style="font-size:16px"></i>
        <span>${message}</span>`;

  const style = document.createElement("style");
  style.textContent = `@keyframes slideIn {
        from { transform:translateX(100px);opacity:0; }
        to   { transform:translateX(0);opacity:1; }
    }`;
  document.head.appendChild(style);
  document.body.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transition = "opacity 0.3s";
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

/* ============================================================
   CONFIRMATION DIALOG
   ============================================================ */
function confirmDelete(
  message = "Are you sure you want to delete this record?",
) {
  return confirm(message);
}

/* ============================================================
   ROLE-BASED SIDEBAR RENDERER
   ============================================================ */
function renderSidebar() {
  const nav = document.querySelector(".sidebar-nav");
  if (!nav) return;

  const role = getRole();

  const allLinks = {
    main: [
      { href: "dashboard.html", icon: "bi-grid-1x2-fill", label: "Dashboard" },
    ],
    resources: [
      {
        href: "users.html",
        icon: "bi-person-gear",
        label: "Users",
        roles: ["ADMIN"],
      },
      {
        href: "rooms.html",
        icon: "bi-building",
        label: "Rooms",
        roles: ["ADMIN"],
      },
      {
        href: "teachers.html",
        icon: "bi-person-badge",
        label: "Teachers",
        roles: ["ADMIN"],
      },
      {
        href: "students.html",
        icon: "bi-people",
        label: "Students",
        roles: ["ADMIN", "ENROLLMENT_OFFICER"],
      },
      {
        href: "courses.html",
        icon: "bi-book",
        label: "Courses",
        roles: ["ADMIN"],
      },
    ],
    scheduling: [
      {
        href: "classes.html",
        icon: "bi-journals",
        label: "Classes",
        roles: ["ADMIN", "CLASS_HANDLER"],
      },
      {
        href: "schedules.html",
        icon: "bi-calendar3",
        label: "Schedules",
        roles: ["ADMIN", "CLASS_HANDLER"],
      },
      {
        href: "enrolments.html",
        icon: "bi-person-check",
        label: "Enrolments",
        roles: ["ADMIN", "ENROLLMENT_OFFICER"],
      },
    ],
    settings: [
      {
        href: "#",
        icon: "bi-key",
        label: "Change Password",
        action: "openChangePasswordModal()",
        roles: ["ENROLLMENT_OFFICER", "CLASS_HANDLER", "STUDENT", "TEACHER"],
      },
    ],
  };

  const visible = (item) => !item.roles || item.roles.includes(role);

  const resourceLinks = allLinks.resources.filter(visible);
  const schedulingLinks = allLinks.scheduling.filter(visible);

  // Build HTML
  let html = "";

  html += `<div class="sidebar-section-label">Main</div>`;
  for (const link of allLinks.main) {
    html += `<a href="${link.href}" class="sidebar-link"><i class="bi ${link.icon}"></i> ${link.label}</a>`;
  }

  // Resources section (only if at least one link is visible)
  if (resourceLinks.length > 0) {
    html += `<div class="sidebar-section-label">Resources</div>`;
    for (const link of resourceLinks) {
      html += `<a href="${link.href}" class="sidebar-link"><i class="bi ${link.icon}"></i> ${link.label}</a>`;
    }
  }

  // Scheduling section (only if at least one link is visible)
  if (schedulingLinks.length > 0) {
    html += `<div class="sidebar-section-label">Scheduling</div>`;
    for (const link of schedulingLinks) {
      html += `<a href="${link.href}" class="sidebar-link"><i class="bi ${link.icon}"></i> ${link.label}</a>`;
    }
  }

  // Settings section
  const settingsLinks = allLinks.settings ? allLinks.settings.filter(visible) : [];
  if (settingsLinks.length > 0) {
    html += `<div class="sidebar-section-label">Settings</div>`;
    for (const link of settingsLinks) {
      if (link.action) {
        html += `<a href="#" onclick="${link.action}; return false;" class="sidebar-link"><i class="bi ${link.icon}"></i> ${link.label}</a>`;
      } else {
        html += `<a href="${link.href}" class="sidebar-link"><i class="bi ${link.icon}"></i> ${link.label}</a>`;
      }
    }
  }

  nav.innerHTML = html;
}

/* ============================================================
   SET ACTIVE SIDEBAR LINK
   ============================================================ */
function setActiveSidebarLink() {
  const currentPage = window.location.pathname.split("/").pop();
  document.querySelectorAll(".sidebar-link").forEach((link) => {
    if (link.getAttribute("href") === currentPage) {
      link.classList.add("active");
    }
  });
}

/* ============================================================
   UPDATE TOPBAR - show logged in user info
   ============================================================ */
function updateTopbar() {
  const dateEl = document.getElementById("topbar-date");
  const userEl = document.getElementById("topbar-user");
  const roleBadgeEl = document.getElementById("topbar-role");

  if (dateEl) {
    dateEl.textContent = new Date().toLocaleDateString("en-GB", {
      weekday: "short",
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  }

  if (userEl) {
    userEl.textContent = getUserEmail() || "";
  }

  if (roleBadgeEl) {
    const role = getRole() || "";
    const roleLabels = {
      ADMIN: "Administrator",
      ENROLLMENT_OFFICER: "Enrollment Officer",
      CLASS_HANDLER: "Class Handler",
      STUDENT: "Student",
      TEACHER: "Teacher",
    };
    roleBadgeEl.textContent = roleLabels[role] || role;
  }
}

/* ============================================================
   SIDEBAR TOGGLE (mobile hamburger)
   ============================================================ */
function toggleSidebar() {
  const sidebar = document.querySelector(".sidebar");
  const overlay = document.getElementById("sidebar-overlay");
  if (!sidebar) return;

  const isOpen = sidebar.classList.toggle("sidebar-open");
  if (overlay) {
    overlay.classList.toggle("active", isOpen);
  }
}

/* ============================================================
   RUN ON EVERY PAGE LOAD
   ============================================================ */
document.addEventListener("DOMContentLoaded", () => {
  if (!document.getElementById("sidebar-overlay")) {
    const overlay = document.createElement("div");
    overlay.id = "sidebar-overlay";
    overlay.className = "sidebar-overlay";
    overlay.addEventListener("click", toggleSidebar);
    document.body.appendChild(overlay);
  }

  renderSidebar();
  setActiveSidebarLink();
  updateTopbar();
});

/* ============================================================
   PAGINATION HELPERS
   ============================================================ */
function paginateData(dataArray, currentPage, pageSize) {
  const start = (currentPage - 1) * pageSize;
  return dataArray.slice(start, start + pageSize);
}

function renderPagination(
  totalItems,
  currentPage,
  pageSize,
  containerId,
  onPageChangeFuncName,
) {
  const totalPages = Math.ceil(totalItems / pageSize);
  const container = document.getElementById(containerId);
  if (!container) return;

  if (totalItems <= pageSize) {
    container.innerHTML = "";
    return;
  }

  let html = '<div class="pagination-container">';

  html += `<button class="btn-page" ${currentPage === 1 ? "disabled" : ""} onclick="window.${onPageChangeFuncName}(${currentPage - 1})">
             <i class="bi bi-chevron-left"></i>
           </button>`;

  // Show max 5 pages around the current page to avoid clutter
  let startPage = Math.max(1, currentPage - 2);
  let endPage = Math.min(totalPages, currentPage + 2);

  if (startPage > 1) {
    html += `<button class="btn-page" onclick="window.${onPageChangeFuncName}(1)">1</button>`;
    if (startPage > 2) html += `<span class="pagination-dots">...</span>`;
  }

  for (let i = startPage; i <= endPage; i++) {
    html += `<button class="btn-page ${i === currentPage ? "active" : ""}" onclick="window.${onPageChangeFuncName}(${i})">${i}</button>`;
  }

  if (endPage < totalPages) {
    if (endPage < totalPages - 1)
      html += `<span class="pagination-dots">...</span>`;
    html += `<button class="btn-page" onclick="window.${onPageChangeFuncName}(${totalPages})">${totalPages}</button>`;
  }

  html += `<button class="btn-page" ${currentPage === totalPages ? "disabled" : ""} onclick="window.${onPageChangeFuncName}(${currentPage + 1})">
             <i class="bi bi-chevron-right"></i>
           </button>`;

  html += "</div>";
  html += "</div>";
  container.innerHTML = html;
}

/* ============================================================
   CHANGE PASSWORD MODAL (Injected dynamically)
   ============================================================ */
function getUserId() {
  return localStorage.getItem("userId");
}

function injectChangePasswordModal() {
  if (document.getElementById("changePasswordModal")) return;
  const modalHtml = `
  <div class="modal fade" id="changePasswordModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content" style="border-radius: var(--radius-lg); border: none;">
        <div class="modal-header-custom">
          <h5 id="cp-modal-title">Change Password</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body" style="padding: 24px">
          <div id="cp-modal-alert"></div>
          <div class="mb-3">
            <label class="form-label-custom">New Password</label>
            <input type="password" id="cp-newPassword" class="form-control-custom" placeholder="Enter new password" />
          </div>
          <div class="mb-3">
            <label class="form-label-custom">Confirm Password</label>
            <input type="password" id="cp-confirmPassword" class="form-control-custom" placeholder="Confirm new password" />
          </div>
        </div>
        <div class="modal-footer" style="padding: 16px 24px; border-top: 1px solid var(--border)">
          <button type="button" class="btn-danger-custom" data-bs-dismiss="modal">Cancel</button>
          <button type="button" class="btn-primary-custom" onclick="submitChangePassword()">
            <i class="bi bi-check-lg"></i> Update Password
          </button>
        </div>
      </div>
    </div>
  </div>`;
  document.body.insertAdjacentHTML("beforeend", modalHtml);
}

function openChangePasswordModal() {
  injectChangePasswordModal();
  document.getElementById("cp-newPassword").value = "";
  document.getElementById("cp-confirmPassword").value = "";
  document.getElementById("cp-modal-alert").innerHTML = "";
  const modal = new bootstrap.Modal(document.getElementById("changePasswordModal"));
  modal.show();
}

async function submitChangePassword() {
  const newPass = document.getElementById("cp-newPassword").value;
  const confPass = document.getElementById("cp-confirmPassword").value;
  const alertDiv = document.getElementById("cp-modal-alert");
  
  if (!newPass || !confPass) {
    alertDiv.innerHTML = `<div class="alert-custom alert-danger-custom"><i class="bi bi-x-circle-fill"></i> Please fill both fields.</div>`;
    return;
  }
  if (newPass !== confPass) {
    alertDiv.innerHTML = `<div class="alert-custom alert-danger-custom"><i class="bi bi-x-circle-fill"></i> Passwords do not match.</div>`;
    return;
  }
  
  const uid = getUserId();
  if (!uid) {
    alertDiv.innerHTML = `<div class="alert-custom alert-danger-custom"><i class="bi bi-x-circle-fill"></i> Could not identify user. Please log in again.</div>`;
    return;
  }
  
  try {
    await apiPut(`/users/${uid}/reset-password`, { newPassword: newPass });
    const modalEl = document.getElementById("changePasswordModal");
    const modal = bootstrap.Modal.getInstance(modalEl);
    modal.hide();
    showToast("Password updated successfully!", "success");
  } catch (err) {
    alertDiv.innerHTML = `<div class="alert-custom alert-danger-custom"><i class="bi bi-x-circle-fill"></i> ${err.message}</div>`;
  }
}

// Inject on load if user is logged in
document.addEventListener("DOMContentLoaded", () => {
    if (localStorage.getItem("token")) {
        injectChangePasswordModal();
    }
});
