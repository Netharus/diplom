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
                <button
                        class="btn btn-outline-success update-button"
                        data-id="${user.id}"
                        data-username="${user.username}"
                        data-role="${user.role}"
                        data-active="${user.active}">
                    Редактировать
                </button>
                <button class="btn btn-outline-danger" onclick="showDeleteConfirmationModal(${user.id})">Удалить</button>
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
function checkUsernameUniqueAndSubmit() {
    const form = document.getElementById("createUserForm");
    const usernameInput = $("#username");
    const passwordInput = $("#password");
    const roleInput = $("#role");
    const activeInput = $("#active");

    const username = usernameInput.val().trim();
    const password = passwordInput.val();
    const role = roleInput.val();
    const active = activeInput.is(":checked");

    if (!username || !password || !role) {
        showErrorToast("Все поля должны быть заполнены");
        return;
    }

    $.ajax({
        url: '/api/admin/users/isExistUsernameCreate',
        type: 'GET',
        data: { username: username },
        success: function (exists) {
            if (exists) {
                usernameInput[0].setCustomValidity("Имя пользователя уже занято");
                usernameInput[0].reportValidity();
            } else {
                usernameInput[0].setCustomValidity("");
                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                const userData = {
                    username: username,
                    password: password,
                    role: role,
                    active: active
                };

                $.ajax({
                    url: '/api/admin/users',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(userData),
                    success: function (result) {
                        $('#successToastText').text(result);
                        new bootstrap.Toast(document.getElementById('successToast')).show();

                        const modal = bootstrap.Modal.getInstance(document.getElementById('createUserModal'));
                        modal.hide();

                        $('#createUserForm')[0].reset();
                        fetchUpdatedUserTable(); // Обновление таблицы пользователей
                    },
                    error: function () {
                        showErrorToast("Ошибка при создании пользователя");
                    }
                });
            }
        },
        error: function () {
            showErrorToast("Ошибка при проверке имени пользователя");
        }
    });
}

function showErrorToast(message) {
    document.getElementById('toastText').textContent = message;
    new bootstrap.Toast(document.getElementById('toast')).show();
}

function togglePassword(fieldId) {
    const input = document.getElementById(fieldId);
    const buttonText = document.getElementById(`show-button-text-${fieldId}`);
    const isPassword = input.type === "password";
    input.type = isPassword ? "text" : "password";
    buttonText.textContent = isPassword ? "Скрыть" : "Показать";
}

let userIdToDelete = null;

function showDeleteConfirmationModal(userId) {
    userIdToDelete = userId;
    const modal = new bootstrap.Modal(document.getElementById('deleteConfirmationModal'));
    modal.show();
}

function deleteUser() {
    if (userIdToDelete) {
        // Отправка запроса на удаление пользователя
        $.ajax({
            url: `/api/admin/users/${userIdToDelete}`,
            type: 'DELETE',
            success: function(response) {
                $('#successToastText').text("Пользователь удален успешно");
                new bootstrap.Toast(document.getElementById('successToast')).show();

                fetchUpdatedUserTable(); // Обновление таблицы пользователей (если нужно)
            },
            error: function(error) {
                showErrorToast(error.message);
            }
        });

        // Закрываем модальное окно
        const modal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmationModal'));
        modal.hide();
    }
}

function mapRoleToEnum(roleString) {
    switch (roleString) {
        case "Администратор":
            return "ADMIN";
        case "Пользователь":
            return "USER";
        default:
            return "USER"; // значение по умолчанию
    }
}

function reverseMapRole(roleEnum) {
    switch (roleEnum) {
        case "ADMIN":
            return "Администратор";
        case "USER":
            return "Пользователь";
        default:
            return "Пользователь";
    }
}

$(document).on("click", ".update-button", function () {
    const button = $(this);
    const user = {
        id: button.data("id"),
        username: button.data("username"),
        role: button.data("role"),
        active: button.data("active")
    };
    openUpdateModal(user);
});

function openUpdateModal(user) {
    $("#updateUserId").val(user.id);
    $("#updateUsername").val(user.username);
    $("#updateRole").val(reverseMapRole(user.role));
    $("#updateActive").prop("checked", user.active);
    $("#updatePassword").val("");

    const modal = new bootstrap.Modal(document.getElementById('updateUserModal'));
    modal.show();
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

    const data = {
        id: parseInt(id),
        username: username,
        password: password || "",
        role: roleEnum,
        active: active
    };

    $.ajax({
        url: '/api/admin/users',
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            $('#successToastText').text(result);
            new bootstrap.Toast(document.getElementById('successToast')).show();

            const modal = bootstrap.Modal.getInstance(document.getElementById('updateUserModal'));
            modal.hide();
            $('#updateUserId').val('');
            $('#updateUsername').val('');
            $('#updatePassword').val('');
            $('#updateRole').val('Пользователь');
            $('#updateActive').prop('checked', false);
            fetchUpdatedUserTable();
        },
        error: function (xhr) {
            let errorText = "Ошибка при обновлении пользователя";
            try {
                const response = JSON.parse(xhr.responseText);
                if (response.message) {
                    errorText = response.message;
                }
            } catch (e) {
                console.warn("Ошибка парсинга ответа:", e);
            }
            showErrorToast(errorText);
        }
    });
}
