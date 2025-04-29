function changeUserStatus(element) {
    const userId = element.getAttribute("data-user-id");

    fetch(`/api/admin/users/changeStatus/${userId}`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при обновлении статуса");
            return response.text();
        })
        .then(result => {
            // Показываем уведомление об успешном обновлении
            document.getElementById('successToastText').textContent = result;
            const successToast = new bootstrap.Toast(document.getElementById('successToast'));
            successToast.show();

            // Обновляем таблицу пользователей после изменения статуса
            fetchUpdatedUserTable();
        })
        .catch(error => {
            // Показываем уведомление об ошибке
            document.getElementById('toastText').textContent = error.message;
            const errorToast = new bootstrap.Toast(document.getElementById('toast'));
            errorToast.show();
        });
}

function fetchUpdatedUserTable() {
    const urlParams = new URLSearchParams(window.location.search);

    const page = urlParams.get("page") || 0;
    const sortField = urlParams.get("sortField") || "active";
    const sortDir = urlParams.get("sortDir") || "asc";
    const keyword = urlParams.get("keyword") || "";

    // Формируем строку запроса
    const queryString = `?page=${page}&sortField=${sortField}&sortDir=${sortDir}${keyword ? `&keyword=${keyword}` : ''}`;

    fetch(`/api/admin/users${queryString}`, {
        headers: {
            "Accept": "application/json"
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при загрузке пользователей");
            return response.json(); // Возвращаем JSON
        })
        .then(data => {
            updateUsersTable(data.list);
        })
        .catch(error => {
            console.error("Ошибка при обновлении таблицы:", error);
        });
}

function updateUsersTable(users) {
    const tbody = document.querySelector('table tbody');
    tbody.innerHTML = ''; // Очищаем текущие строки таблицы

    if (!users || users.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = `<td colspan="5" class="text-center text-muted">Пользователи не найдены</td>`;
        tbody.appendChild(row);
        return;
    }

    users.forEach(user => {
        const row = document.createElement('tr');

        const isActive = user.active;
        const activeBadgeClass = isActive ? 'bg-success' : 'bg-warning text-dark';
        const activeText = isActive ? 'Активен' : 'Не активен';

        let roleHtml = '<span class="badge bg-secondary">Неизвестная роль</span>';
        if (user.role === 'Администратор') {
            roleHtml = '<span class="badge bg-success">Администратор</span>';
        } else if (user.role === 'Пользователь') {
            roleHtml = '<span class="badge bg-primary">Пользователь</span>';
        }

        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>
                <span 
                    role="button"
                    style="cursor: pointer;"
                    data-user-id="${user.id}"
                    class="badge ${activeBadgeClass}"
                    onclick="changeUserStatus(this)">
                    ${activeText}
                </span>
            </td>
            <td>${roleHtml}</td>
            <td>
                <button class="btn btn-outline-success">Редактировать</button>
                <button class="btn btn-outline-danger">Удалить</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}
function toggleClearButton() {
    const input = document.getElementById('searchInput');
    const button = document.getElementById('clearSearchButton');
    button.style.display = input.value.trim() ? 'inline' : 'none';
}

function clearSearch() {
    const input = document.getElementById('searchInput');
    input.value = '';
    toggleClearButton();
    searchUsers();
}
function searchUsers() {
    const keyword = document.getElementById('searchInput').value.trim();
    const queryString = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';

    fetch(`/api/admin/users${queryString}`, {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при загрузке пользователей");
            return response.json();
        })
        .then(data => {
            updateUsersTable(data.list);
        })
        .catch(error => {
            console.error("Ошибка при поиске пользователей:", error);
        });
}