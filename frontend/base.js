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
   ROLE-BASED SIDEBAR RENDERER
   Rewrites <nav class="sidebar-nav"> based on the stored role.
   ============================================================ */
function renderSidebar() {
  const nav = document.querySelector(".sidebar-nav");
  if (!nav) return;

  const role = getRole();

  // Define all possible nav items grouped by section
  const allLinks = {
    main: [
      { href: "dashboard.html", icon: "bi-grid-1x2-fill", label: "Dashboard" },
    ],
    resources: [
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
  };

  // Filter helper — item visible if no role restriction, or current role is included
  const visible = (item) => !item.roles || item.roles.includes(role);

  const resourceLinks = allLinks.resources.filter(visible);
  const schedulingLinks = allLinks.scheduling.filter(visible);

  // Build HTML
  let html = "";

  // Main section (always shown)
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
  // Inject sidebar overlay backdrop once (avoids editing every HTML page)
  if (!document.getElementById("sidebar-overlay")) {
    const overlay = document.createElement("div");
    overlay.id = "sidebar-overlay";
    overlay.className = "sidebar-overlay";
    overlay.addEventListener("click", toggleSidebar);
    document.body.appendChild(overlay);
  }

  renderSidebar(); // role-filtered nav links
  setActiveSidebarLink(); // highlight current page
  updateTopbar(); // show user email + role badge
});
