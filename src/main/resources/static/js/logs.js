// ✅ Константы
const STATUS_SUCCESS = 'Успешно';
const STATUS_ERROR = 'Ошибка';

const STATUS_DEFAULT_CLASS = 'alert-info';
const STATUS_SUCCESS_CLASS = 'alert-success';
const STATUS_ERROR_CLASS = 'alert-danger';

const MSG_LOGS_NOT_FOUND = 'Логи не найдены';
const MSG_FETCH_LOGS_ERROR = 'Ошибка при загрузке логов';
const MSG_SEARCH_LOGS_ERROR = 'Ошибка при поиске логов:';

function searchLogs() {
    const searchInput = document.getElementById('searchInput');
    currentQuery.keyword = searchInput.value.trim();

    fetch(`/api/admin/logs${buildQueryString()}`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    })
        .then(response => {
            if (!response.ok) throw new Error(MSG_FETCH_LOGS_ERROR);
            return response.json();
        })
        .then(data => {
            updateLogsTable(data.list);
            updatePaginationInfoAndButtons(data, "Логи");
            searchInput.value = data.keyword;
        })
        .catch(error => console.error(MSG_SEARCH_LOGS_ERROR, error));
}

function clearSearchLogs(button) {
    const sortField = button.dataset.sort;
    const sortDirection = button.dataset.sortDirection;
    currentQuery.keyword = '';
    document.getElementById('searchInput').value = '';
    toggleClearButton(); // не определена в коде — предполагается, что есть
    window.location = `/admin/logs?page=0&sort=${sortField},${sortDirection}`;
}

function updateLogsTable(logs) {
    const tbody = document.querySelector('table tbody');
    tbody.innerHTML = '';

    if (!logs || logs.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">${MSG_LOGS_NOT_FOUND}</td></tr>`;
        return;
    }

    logs.forEach(log => {
        const statusClass = log.status === STATUS_SUCCESS ? 'bg-success' : 'bg-danger';
        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${log.id}</td>
            <td>${log.dateTime}</td>
            <td>
                <span class="badge ${statusClass} active-badge">${log.status}</span>
            </td>
            <td>
                <button class="log-info-button btn btn-outline-primary"
                        data-bs-toggle="modal"
                        data-bs-target="#logInfoModal"
                        data-user-id="${log.userId}"
                        data-username="${log.username}"
                        data-message="${log.message}"
                        data-status="${log.status}">
                    Детали
                </button>
            </td>
        `;

        tbody.appendChild(row);
    });
}

document.addEventListener('DOMContentLoaded', function () {
    currentQuery.page = parseInt(document.getElementById('hiddenPage')?.value || '0');
    currentQuery.sortField = document.getElementById('hiddenSortField')?.value || 'id';
    currentQuery.sortDir = document.getElementById('hiddenSortDir')?.value || 'ASC';
    currentQuery.keyword = document.getElementById('hiddenKeyword')?.value || '';

    document.querySelectorAll('.sort-link').forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            const moduleURL = this.dataset.moduleUrl;
            const fieldName = this.dataset.fieldName;
            const sortDir = this.dataset.sortDir;
            sort(moduleURL, fieldName, sortDir);
        });
    });

    const modal = document.getElementById('logInfoModal');
    if (modal) {
        modal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            document.getElementById('modalUserId').textContent = button.getAttribute('data-user-id');
            document.getElementById('modalUsername').textContent = button.getAttribute('data-username');
            document.getElementById('modalMessage').textContent = button.getAttribute('data-message');

            const messageContainer = document.getElementById('modalMessageContainer');
            messageContainer.classList.remove(STATUS_DEFAULT_CLASS, STATUS_SUCCESS_CLASS, STATUS_ERROR_CLASS);

            const status = button.getAttribute('data-status');
            if (status === STATUS_SUCCESS) {
                messageContainer.classList.add(STATUS_SUCCESS_CLASS);
            } else if (status === STATUS_ERROR) {
                messageContainer.classList.add(STATUS_ERROR_CLASS);
            } else {
                messageContainer.classList.add(STATUS_DEFAULT_CLASS);
            }
        });
    }
});
function startIntro() {
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.logs-table-header'),
                title: "Сортировка",
                intro: "Нажимая на заголовки столбцов, вы можете сортировать записи по нужному параметру."
            },
            {
                element: document.querySelector('.log-info-button'),
                title: "Детали",
                intro: "Нажмите на кнопку, чтобы просмотреть подробную информацию по конкретному логу."
            },
        ],
        disableInteraction: true,
        nextLabel: 'Далее',
        prevLabel: 'Назад',
        doneLabel: 'Готово'
    }).start();
}