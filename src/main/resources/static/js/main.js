// GFTour JavaScript Functions

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Add fade-in animation to cards
    const cards = document.querySelectorAll('.card');
    cards.forEach((card, index) => {
        card.style.animationDelay = `${index * 0.1}s`;
        card.classList.add('fade-in');
    });

    // Search functionality
    const searchInputs = document.querySelectorAll('.search-input');
    searchInputs.forEach(input => {
        input.addEventListener('input', debounce(function(e) {
            const searchTerm = e.target.value.toLowerCase();
            const targetTable = document.querySelector(e.target.dataset.target);
            if (targetTable) {
                filterTable(targetTable, searchTerm);
            }
        }, 300));
    });

    // Mobile sidebar toggle
    const sidebarToggle = document.querySelector('.sidebar-toggle');
    const sidebar = document.querySelector('.sidebar');
    
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('show');
        });
    }

    // Auto-hide alerts
    const alerts = document.querySelectorAll('.alert[data-auto-dismiss]');
    alerts.forEach(alert => {
        const delay = parseInt(alert.dataset.autoDismiss) || 5000;
        setTimeout(() => {
            alert.classList.add('fade');
            setTimeout(() => alert.remove(), 150);
        }, delay);
    });
});

// Utility Functions
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function filterTable(table, searchTerm) {
    const rows = table.querySelectorAll('tbody tr');
    rows.forEach(row => {
        const text = row.textContent.toLowerCase();
        const shouldShow = text.includes(searchTerm);
        row.style.display = shouldShow ? '' : 'none';
    });

    // Show "no results" message if no rows are visible
    const visibleRows = Array.from(rows).filter(row => row.style.display !== 'none');
    let noResultsRow = table.querySelector('.no-results-row');
    
    if (visibleRows.length === 0 && searchTerm) {
        if (!noResultsRow) {
            const colCount = table.querySelectorAll('thead th').length;
            noResultsRow = document.createElement('tr');
            noResultsRow.className = 'no-results-row';
            noResultsRow.innerHTML = `
                <td colspan="${colCount}" class="text-center text-muted py-4">
                    <i class="fas fa-search fa-2x mb-2"></i><br>
                    No results found for "${searchTerm}"
                </td>
            `;
            table.querySelector('tbody').appendChild(noResultsRow);
        }
    } else if (noResultsRow) {
        noResultsRow.remove();
    }
}

// Modal Functions
function showModal(modalId, title, content) {
    const modal = document.getElementById(modalId);
    if (modal) {
        const modalTitle = modal.querySelector('.modal-title');
        const modalBody = modal.querySelector('.modal-body');
        
        if (modalTitle) modalTitle.textContent = title;
        if (modalBody) modalBody.innerHTML = content;
        
        const modalInstance = new bootstrap.Modal(modal);
        modalInstance.show();
    }
}

function showConfirmDialog(message, onConfirm, onCancel) {
    const modalHtml = `
        <div class="modal fade" id="confirmModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-warning text-white">
                        <h5 class="modal-title">
                            <i class="fas fa-exclamation-triangle me-2"></i>Confirmation
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p class="mb-0">${message}</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                        <button type="button" class="btn btn-warning" id="confirmBtn">Confirm</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    const existingModal = document.getElementById('confirmModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // Add modal to body
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    const modal = new bootstrap.Modal(document.getElementById('confirmModal'));
    const confirmBtn = document.getElementById('confirmBtn');
    
    confirmBtn.addEventListener('click', function() {
        modal.hide();
        if (onConfirm) onConfirm();
    });
    
    document.getElementById('confirmModal').addEventListener('hidden.bs.modal', function() {
        if (onCancel) onCancel();
        this.remove();
    });
    
    modal.show();
}

// AJAX Helper Functions
function sendAjaxRequest(url, method, data, successCallback, errorCallback) {
    showLoading();
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        },
        body: data ? JSON.stringify(data) : null
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        hideLoading();
        if (successCallback) successCallback(data);
    })
    .catch(error => {
        hideLoading();
        console.error('Ajax request failed:', error);
        if (errorCallback) {
            errorCallback(error);
        } else {
            showAlert('An error occurred. Please try again.', 'danger');
        }
    });
}

// Loading Indicator
function showLoading() {
    let loader = document.getElementById('globalLoader');
    if (!loader) {
        loader = document.createElement('div');
        loader.id = 'globalLoader';
        loader.className = 'position-fixed top-0 start-0 w-100 h-100 d-flex justify-content-center align-items-center';
        loader.style.backgroundColor = 'rgba(255, 255, 255, 0.8)';
        loader.style.zIndex = '9999';
        loader.innerHTML = `
            <div class="text-center">
                <div class="spinner-custom mb-3"></div>
                <p class="mb-0">Loading...</p>
            </div>
        `;
        document.body.appendChild(loader);
    }
    loader.style.display = 'flex';
}

function hideLoading() {
    const loader = document.getElementById('globalLoader');
    if (loader) {
        loader.style.display = 'none';
    }
}

// Alert System
function showAlert(message, type = 'info', duration = 5000) {
    const alertContainer = document.getElementById('alertContainer') || createAlertContainer();
    
    const alertId = 'alert_' + Date.now();
    const alertHtml = `
        <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
            <i class="fas fa-${getAlertIcon(type)} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;
    
    alertContainer.insertAdjacentHTML('beforeend', alertHtml);
    
    // Auto dismiss
    if (duration > 0) {
        setTimeout(() => {
            const alert = document.getElementById(alertId);
            if (alert) {
                alert.classList.remove('show');
                setTimeout(() => alert.remove(), 150);
            }
        }, duration);
    }
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.id = 'alertContainer';
    container.className = 'position-fixed top-0 end-0 p-3';
    container.style.zIndex = '1060';
    document.body.appendChild(container);
    return container;
}

function getAlertIcon(type) {
    const icons = {
        'success': 'check-circle',
        'danger': 'exclamation-triangle',
        'warning': 'exclamation-triangle',
        'info': 'info-circle'
    };
    return icons[type] || 'info-circle';
}

// Form Validation
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    let isValid = true;
    const requiredFields = form.querySelectorAll('[required]');
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        }
    });
    
    return isValid;
}

// Date Formatting
function formatDate(dateString, format = 'YYYY-MM-DD HH:mm') {
    const date = new Date(dateString);
    return date.toLocaleString();
}

// Export Functions
window.GFTour = {
    showModal,
    showConfirmDialog,
    sendAjaxRequest,
    showLoading,
    hideLoading,
    showAlert,
    validateForm,
    formatDate,
    filterTable
};