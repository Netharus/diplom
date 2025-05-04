document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('logInfoModal');
    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const userId = button.getAttribute('data-user-id');
        const username = button.getAttribute('data-username');
        const message = button.getAttribute('data-message');
        const status = button.getAttribute('data-status');

        document.getElementById('modalUserId').textContent = userId;
        document.getElementById('modalUsername').textContent = username;
        document.getElementById('modalMessage').textContent = message;

        const messageContainer = document.getElementById('modalMessageContainer');
        messageContainer.classList.remove('alert-info', 'alert-success', 'alert-danger'); // Clear existing classes

        // Apply new class based on status
        if (status === 'Успешно') {
            messageContainer.classList.add('alert-success');
        } else if (status === 'Ошибка') {
            messageContainer.classList.add('alert-danger');
        } else {
            messageContainer.classList.add('alert-info'); // Fallback/default
        }
    });
});

function searchLogs() {
    const keyword = document.getElementById('searchInput').value.trim();
    const queryString = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';

    fetch(`/api/admin/logs${queryString}`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при загрузке логов");
            return response.json();
        })
        .then(data => updateLogsTable(data.list))
        .catch(error => console.error("Ошибка при поиске логов:", error));
}
function clearSearch() {
    document.getElementById('searchInput').value = '';
    toggleClearButton();
    searchLogs();
}
function updateLogsTable(logs) {
    const tbody = document.querySelector('table tbody');
    tbody.innerHTML = '';

    if (!logs || logs.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center text-muted">Логи не найдены</td></tr>`;
        return;
    }

    logs.forEach(log => {
        const statusClass = log.status === 'Успешно' ? 'bg-success' : 'bg-danger';

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${log.id}</td>
            <td>${log.dateTime}</td>
            <td>
                <span class="badge ${statusClass} active-badge">
                    ${log.status}
                </span>
            </td>
            <td>
                <button
                    class="btn btn-outline-primary"
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
