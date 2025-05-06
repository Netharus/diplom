function togglePassword(fieldId) {

    const input = document.getElementById(fieldId);
    const buttonText = document.getElementById(`show-button-text-${fieldId}`);
    const isPassword = input.type === "password";
    input.type = isPassword ? "text" : "password";
    buttonText.textContent = isPassword ? "Скрыть" : "Показать";
}

window.addEventListener('DOMContentLoaded', () => {
    toggleClearButton(); // Проверка сразу после загрузки
});

function toggleClearButton() {
    const input = document.getElementById('searchInput');
    const button = document.getElementById('clearSearchButton');
    button.style.display = input.value.trim() ? 'inline' : 'none';
}
const currentQuery = {
    page: 0,
    sortField: 'id',
    sortDir: 'ASC',
    keyword: ''
};
function sort(url, sortField, sortDir) {
    currentQuery.sortField = sortField;
    currentQuery.sortDir = sortDir;
    window.location.href = url + buildQueryString();
}
function updatePaginationInfoAndButtons(pageContainer, entityName) {
    const paginationText = document.getElementById('pagination-text');
    const paginationLinks = document.getElementById('pagination-links');
    const moduleURL = window.location.pathname;

    Object.assign(currentQuery, {
        page: pageContainer.currentPage,
        sortField: pageContainer.sortField,
        sortDir: pageContainer.sortDir,
        keyword: pageContainer.keyword || ''
    });

    paginationText.innerHTML = pageContainer.totalItems > 0
        ? `<span class="text-muted">Показаны ${entityName} # ${pageContainer.startCount} по ${pageContainer.endCount} из ${pageContainer.totalItems}</span>`
        : `<span class="text-warning">${entityName} не найдены</span>`;

    if (pageContainer.totalPages === 0) {
        paginationLinks.innerHTML = '';
        return;
    }

    const createPageLink = (pageNum, label, disabled, active = false) => {
        const url = moduleURL + buildQueryString({page: pageNum});
        return `
            <li class="page-item ${disabled ? 'disabled' : ''} ${active ? 'active' : ''}">
                <a class="page-link" href="${url}">${label}</a>
            </li>
        `;
    };

    const currentPage = pageContainer.currentPage;
    let html = '';

    html += createPageLink(0, 'Первая', currentPage <= 0);
    html += createPageLink(currentPage - 1, 'Предыдущая', currentPage <= 0);

    for (let i = 1; i <= pageContainer.totalPages; i++) {
        html += createPageLink(i - 1, i, false, currentPage === (i - 1));
    }

    html += createPageLink(currentPage + 1, 'Следующая', currentPage >= pageContainer.totalPages - 1);
    html += createPageLink(pageContainer.totalPages - 1, 'Последняя', currentPage >= pageContainer.totalPages - 1);

    paginationLinks.innerHTML = html;
}
function buildQueryString(overrides = {}) {
    const params = new URLSearchParams();
    params.set('page', overrides.page ?? currentQuery.page);
    params.set('sort', `${overrides.sortField ?? currentQuery.sortField},${overrides.sortDir ?? currentQuery.sortDir}`);
    const keyword = overrides.keyword ?? currentQuery.keyword;
    if (keyword) params.set('keyword', keyword);
    return `?${params.toString()}`;
}

document.addEventListener("DOMContentLoaded", function () {
    const successMsgContainer = document.getElementById("success-message");
    if (successMsgContainer) {
        const message = successMsgContainer.getAttribute("data-message");
        showSuccessToast(message);
    }

    const exceptionMsgContainer = document.getElementById("exception-message");
    if (exceptionMsgContainer) {
        const message = exceptionMsgContainer.getAttribute("data-message");
        showExceptionToast(message);
    }
});

function showSuccessToast(message) {
    createAndShowToast(message, 'bg-success', 'text-white');
}

function showExceptionToast(message) {
    createAndShowToast(message, 'bg-danger', 'text-white');
}

function createAndShowToast(message, bgClass, textClass) {
    const toastContainer = document.querySelector('.toast-container');

    // Создание элементов
    const toast = document.createElement('div');
    toast.className = `toast align-items-center ${bgClass} ${textClass} border-0 p-2`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    const toastInner = document.createElement('div');
    toastInner.className = 'd-flex';

    const toastBody = document.createElement('div');
    toastBody.className = 'toast-body';
    toastBody.textContent = message;

    const closeBtn = document.createElement('button');
    closeBtn.type = 'button';
    closeBtn.className = 'btn-close btn-close-white me-2 m-auto';
    closeBtn.setAttribute('data-bs-dismiss', 'toast');
    closeBtn.setAttribute('aria-label', 'Закрыть');

    toastInner.appendChild(toastBody);
    toastInner.appendChild(closeBtn);
    toast.appendChild(toastInner);
    toastContainer.appendChild(toast);

    // Инициализация Bootstrap Toast
    const bsToast = new bootstrap.Toast(toast, {delay: 7000});
    bsToast.show();

    // Удаление из DOM после скрытия
    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}