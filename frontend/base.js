/* ============================================================
   UNIVERSITY SCHEDULING SYSTEM — Global JavaScript
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
   Call this at the top of every protected page
   Pass allowed roles as an array e.g. ['ADMIN', 'CLASS_HANDLER']
   ============================================================ */
function requireAuth(allowedRoles) {
  const token = getToken();
  const role = getRole();

  // No token at all — send to login
  if (!token || !role) {
    window.location.href = "login.html";
    return false;
  }

  // Role not allowed on this page — redirect to their dashboard
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
   FETCH HELPERS — all requests include JWT token
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
   UPDATE TOPBAR — show logged in user info
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
   RUN ON EVERY PAGE LOAD
   ============================================================ */
document.addEventListener("DOMContentLoaded", () => {
  setActiveSidebarLink();
  updateTopbar();
});
