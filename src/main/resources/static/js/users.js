const MSG_STATUS_UPDATE_ERROR = "Ошибка при обновлении статуса";
const MSG_USERS_LOAD_ERROR = "Ошибка при загрузке пользователей";
const MSG_TABLE_UPDATE_ERROR = "Ошибка при обновлении таблицы: ";
const MSG_NO_USERS_FOUND = "Пользователи не найдены";
const MSG_UNKNOWN_ROLE = "Неизвестная роль";
const MSG_ADMIN_ROLE = "Администратор";
const MSG_USER_ROLE = "Пользователь";
const MSG_ALL_FIELDS_REQUIRED = "Все поля должны быть заполнены";
const MSG_USERNAME_EXISTS = "Имя пользователя уже занято";
const MSG_CREATE_USER_ERROR = "Ошибка при создании пользователя";
const MSG_CHECK_USERNAME_ERROR = "Ошибка при проверке имени пользователя";
const MSG_USER_DELETED_SUCCESS = "Пользователь удален успешно";
const MSG_DELETE_USER_ERROR = "Ошибка при удалении пользователя";
const MSG_USERNAME_AND_ROLE_REQUIRED = "Имя пользователя и роль обязательны";
const MSG_UPDATE_USER_ERROR = "Ошибка при обновлении пользователя";


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
});


function changeUserStatus(element) {
    const userId = element.getAttribute("data-user-id");

    fetch(`/api/admin/users/changeStatus/${userId}`, {
        method: "PATCH",
        headers: {"Content-Type": "application/json"}
    })
        .then(response => {
            if (!response.ok) throw new Error(MSG_STATUS_UPDATE_ERROR);
            return response.text();
        })
        .then(result => {
            showSuccessToast(result);
            fetchUpdatedUserTable();
        })
        .catch(error => {
            showExceptionToast(error.message);
        });
}

function fetchUpdatedUserTable() {
    fetch(`/api/admin/users${buildQueryString()}`, {
        headers: {"Accept": "application/json"}
    })
        .then(response => {
            if (!response.ok) throw new Error(MSG_USERS_LOAD_ERROR);
            return response.json();
        })
        .then(data => {
            updateUsersTable(data.list);
            updatePaginationInfoAndButtons(data, "Пользователи");
        })
        .catch(error => showExceptionToast(MSG_TABLE_UPDATE_ERROR + error.message));
}

function updateUsersTable(users) {
    const tbody = document.querySelector('table tbody');
    tbody.innerHTML = '';

    if (!users || users.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-muted">${MSG_NO_USERS_FOUND}</td></tr>`;
        return;
    }

    users.forEach(user => {
        const isActive = user.active;
        const activeBadgeClass = isActive ? 'bg-success' : 'bg-warning text-dark';
        const activeText = isActive ? 'Активен' : 'Не активен';

        let roleHtml = `<span class="badge bg-secondary">${MSG_UNKNOWN_ROLE}</span>`;
        if (user.role === MSG_ADMIN_ROLE) roleHtml = `<span class="badge bg-success">${MSG_ADMIN_ROLE}</span>`;
        else if (user.role === MSG_USER_ROLE) roleHtml = `<span class="badge bg-primary">${MSG_USER_ROLE}</span>`;

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
        headers: {'Accept': 'application/json'}
    })
        .then(response => {
            if (!response.ok) throw new Error(MSG_USERS_LOAD_ERROR);
            return response.json();
        })
        .then(data => {
            updateUsersTable(data.list);
            updatePaginationInfoAndButtons(data, "Пользователи");
        })
        .catch(error => showExceptionToast("Ошибка при поиске пользователей: " + error.message));
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
        showExceptionToast(MSG_ALL_FIELDS_REQUIRED);
        return;
    }

    $.ajax({
        url: '/api/admin/users/isExistUsernameCreate',
        type: 'GET',
        data: {username},
        success: function (exists) {
            if (exists) {
                showExceptionToast(MSG_USERNAME_EXISTS);
                return;
            }

            const userData = {username, password, role, active};

            $.ajax({
                url: '/api/admin/users',
                type: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(userData),
                success: function (result) {
                    showSuccessToast(result);
                    bootstrap.Modal.getInstance(document.getElementById('createUserModal')).hide();
                    $('#createUserForm')[0].reset();
                    fetchUpdatedUserTable();
                },
                error: function () {
                    showExceptionToast(MSG_CREATE_USER_ERROR);
                }
            });
        },
        error: function () {
            showExceptionToast(MSG_CHECK_USERNAME_ERROR);
        }
    });
}

function clearSearchUsers(button) {
    const sortField = button.dataset.sort;
    const sortDirection = button.dataset.sortDirection;
    currentQuery.keyword = '';
    document.getElementById('searchInput').value = '';
    toggleClearButton();
    window.location = `/admin/users?page=0&sort=${sortField},${sortDirection}`;
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
            showSuccessToast(MSG_USER_DELETED_SUCCESS);
            fetchUpdatedUserTable();
        },
        error: function () {
            showExceptionToast(MSG_DELETE_USER_ERROR);
        }
    });

    bootstrap.Modal.getInstance(document.getElementById('deleteConfirmationModal')).hide();
}

function mapRoleToEnum(roleString) {
    return roleString === MSG_ADMIN_ROLE ? "ADMIN" : "USER";
}

function reverseMapRole(roleEnum) {
    return roleEnum === "ADMIN" ? MSG_ADMIN_ROLE : MSG_USER_ROLE;
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
        showExceptionToast(MSG_USERNAME_AND_ROLE_REQUIRED);
        return;
    }

    const roleEnum = mapRoleToEnum(roleString);
    const data = {id: parseInt(id), username, password: password || "", role: roleEnum, active};

    $.ajax({
        url: '/api/admin/users',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            showSuccessToast(result);
            bootstrap.Modal.getInstance(document.getElementById('updateUserModal')).hide();
            fetchUpdatedUserTable();
        },
        error: function () {
            showExceptionToast(MSG_UPDATE_USER_ERROR);
        }
    });
}
