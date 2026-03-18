const API_BASE = "http://localhost:8080/api";

async function apiFetch(endpoint, options = {}) {
  try {
    const response = await fetch(`${API_BASE}${endpoint}`, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
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
        position: fixed; bottom: 24px; right: 24px; z-index: 9999;
        background: ${c.bg}; color: ${c.color};
        border: 1.5px solid ${c.color}33;
        padding: 14px 20px; border-radius: 10px;
        font-size: 13.5px; font-weight: 600;
        display: flex; align-items: center; gap: 10px;
        box-shadow: 0 8px 24px rgba(0,0,0,0.12);
        max-width: 380px; animation: slideIn 0.3s ease;
        font-family: 'DM Sans', sans-serif;
    `;
  toast.innerHTML = `<i class="bi ${c.icon}" style="font-size:16px"></i><span>${message}</span>`;

  const style = document.createElement("style");
  style.textContent = `@keyframes slideIn {
        from { transform: translateX(100px); opacity: 0; }
        to   { transform: translateX(0);    opacity: 1; }
    }`;
  document.head.appendChild(style);
  document.body.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = "0";
    toast.style.transition = "opacity 0.3s";
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

function confirmDelete(
  message = "Are you sure you want to delete this record?",
) {
  return confirm(message);
}

function setActiveSidebarLink() {
  const currentPage = window.location.pathname.split("/").pop();
  document.querySelectorAll(".sidebar-link").forEach((link) => {
    const href = link.getAttribute("href");
    if (href === currentPage) {
      link.classList.add("active");
    }
  });
}

function updateTopbarDate() {
  const el = document.getElementById("topbar-date");
  if (el) {
    const now = new Date();
    el.textContent = now.toLocaleDateString("en-GB", {
      weekday: "short",
      day: "numeric",
      month: "short",
      year: "numeric",
    });
  }
}

document.addEventListener("DOMContentLoaded", () => {
  setActiveSidebarLink();
  updateTopbarDate();
});
