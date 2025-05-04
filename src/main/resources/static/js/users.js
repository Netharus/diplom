function changeUserStatus(element) {
    const userId = element.getAttribute("data-user-id");

    fetch(`/api/admin/users/changeStatus/${userId}`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при обновлении статуса");
            return response.text();
        })
        .then(result => {
            document.getElementById('successToastText').textContent = result;
            new bootstrap.Toast(document.getElementById('successToast')).show();
            fetchUpdatedUserTable();
        })
        .catch(error => {
            showErrorToast(error.message);
        });
}

function fetchUpdatedUserTable() {
    const urlParams = new URLSearchParams(window.location.search);
    const page = urlParams.get("page") || 0;
    const sortField = urlParams.get("sortField") || "active";
    const sortDir = urlParams.get("sortDir") || "asc";
    const keyword = urlParams.get("keyword") || "";

    const queryString = `?page=${page}&sortField=${sortField}&sortDir=${sortDir}${keyword ? `&keyword=${keyword}` : ''}`;

    fetch(`/api/admin/users${queryString}`, {
        headers: { "Accept": "application/json" }
    })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при загрузке пользователей");
            return response.json();
        })
        .then(data => updateUsersTable(data.list))
        .catch(error => console.error("Ошибка при обновлении таблицы:", error));
}

function updateUsersTable(users) {
    const tbody = document.querySelector('table tbody');
    tbody.innerHTML = '';

    if (!users || users.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">Пользователи не найдены</td></tr>`;
        return;
    }

    users.forEach(user => {
        const isActive = user.active;
        const activeBadgeClass = isActive ? 'bg-success' : 'bg-warning text-dark';
        const activeText = isActive ? 'Активен' : 'Не активен';

        let roleHtml = '<span class="badge bg-secondary">Неизвестная роль</span>';
        if (user.role === 'Администратор') roleHtml = '<span class="badge bg-success">Администратор</span>';
        else if (user.role === 'Пользователь') roleHtml = '<span class="badge bg-primary">Пользователь</span>';

        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${user.id}</td>
            <td>${user.username}</td>
            <td>
                <span role="button" style="cursor: pointer;" data-user-id="${user.id}" class="badge ${activeBadgeClass} active-badge" onclick="changeUserStatus(this)">
                    ${activeText}
                </span>
            </td>
            <td>${roleHtml}</td>
            <td>
                <button class="btn btn-outline-success update-button" data-id="${user.id}" data-username="${user.username}" data-role="${user.role}" data-active="${user.active}">
                    Редактировать
                </button>
                <button class="btn btn-outline-danger" onclick="showDeleteConfirmationModal(${user.id})">Удалить</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

function searchUsers() {
    const keyword = document.getElementById('searchInput').value.trim();
    const queryString = keyword ? `?keyword=${encodeURIComponent(keyword)}` : '';

    fetch(`/api/admin/users${queryString}`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    })
        .then(response => {
            if (!response.ok) throw new Error("Ошибка при загрузке пользователей");
            return response.json();
        })
        .then(data => updateUsersTable(data.list))
        .catch(error => console.error("Ошибка при поиске пользователей:", error));
}

function checkUsernameUniqueAndSubmit() {
    const form = document.getElementById("createUserForm");
    const username = $("#username").val().trim();
    const password = $("#password").val();
    const role = $("#role").val();
    const active = $("#active").is(":checked");

    if (!form.checkValidity()) {
        form.reportValidity();
        return;
    }
    if (!username || !password || !role) {
        showErrorToast("Все поля должны быть заполнены");
        return;
    }

    $.ajax({
        url: '/api/admin/users/isExistUsernameCreate',
        type: 'GET',
        data: { username },
        success: function (exists) {
            if (exists) {
                showErrorToast("Имя пользователя уже занято");
                return;
            }

            const userData = { username, password, role, active };

            $.ajax({
                url: '/api/admin/users',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(userData),
                success: function (result) {
                    $('#successToastText').text(result);
                    new bootstrap.Toast(document.getElementById('successToast')).show();

                    bootstrap.Modal.getInstance(document.getElementById('createUserModal')).hide();
                    $('#createUserForm')[0].reset();
                    fetchUpdatedUserTable();
                },
                error: function (xhr) {
                    handleAjaxError(xhr, "Ошибка при создании пользователя");
                }
            });
        },
        error: function (xhr) {
            handleAjaxError(xhr, "Ошибка при проверке имени пользователя");
        }
    });
}

function togglePassword(fieldId) {
    const input = document.getElementById(fieldId);
    const buttonText = document.getElementById(`show-button-text-${fieldId}`);
    const isPassword = input.type === "password";
    input.type = isPassword ? "text" : "password";
    buttonText.textContent = isPassword ? "Скрыть" : "Показать";
}
function clearSearch() {
    document.getElementById('searchInput').value = '';
    toggleClearButton();
    searchUsers();
}
let userIdToDelete = null;

function showDeleteConfirmationModal(userId) {
    userIdToDelete = userId;
    new bootstrap.Modal(document.getElementById('deleteConfirmationModal')).show();
}

function deleteUser() {
    if (!userIdToDelete) return;

    $.ajax({
        url: `/api/admin/users/${userIdToDelete}`,
        type: 'DELETE',
        success: function () {
            $('#successToastText').text("Пользователь удален успешно");
            new bootstrap.Toast(document.getElementById('successToast')).show();
            fetchUpdatedUserTable();
        },
        error: function (xhr) {
            handleAjaxError(xhr, "Ошибка при удалении пользователя");
        }
    });

    bootstrap.Modal.getInstance(document.getElementById('deleteConfirmationModal')).hide();
}

function mapRoleToEnum(roleString) {
    return roleString === "Администратор" ? "ADMIN" : "USER";
}

function reverseMapRole(roleEnum) {
    return roleEnum === "ADMIN" ? "Администратор" : "Пользователь";
}

$(document).on("click", ".update-button", function () {
    const button = $(this);
    openUpdateModal({
        id: button.data("id"),
        username: button.data("username"),
        role: button.data("role"),
        active: button.data("active")
    });
});

function openUpdateModal(user) {
    $("#updateUserId").val(user.id);
    $("#updateUsername").val(user.username);
    $("#updateRole").val(reverseMapRole(user.role));
    $("#updateActive").prop("checked", user.active);
    $("#updatePassword").val("");

    new bootstrap.Modal(document.getElementById('updateUserModal')).show();
}

function submitUserUpdate() {
    const id = $("#updateUserId").val();
    const username = $("#updateUsername").val().trim();
    const password = $("#updatePassword").val();
    const roleString = $("#updateRole").val();
    const active = $("#updateActive").is(":checked");

    if (!username || !roleString) {
        showErrorToast("Имя пользователя и роль обязательны");
        return;
    }

    const roleEnum = mapRoleToEnum(roleString);
    const data = { id: parseInt(id), username, password: password || "", role: roleEnum, active };

    $.ajax({
        url: '/api/admin/users',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            $('#successToastText').text(result);
            new bootstrap.Toast(document.getElementById('successToast')).show();

            bootstrap.Modal.getInstance(document.getElementById('updateUserModal')).hide();
            fetchUpdatedUserTable();
        },
        error: function (xhr) {
            handleAjaxError(xhr, "Ошибка при обновлении пользователя");
        }
    });
}
