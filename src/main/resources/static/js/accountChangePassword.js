document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("savePasswordBtn").addEventListener("click", function () {
        const currentPassword = document.getElementById("currentPassword").value;
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;

        if (newPassword !== confirmPassword) {
            showToast("Новый пароль и подтверждение пароля не совпадают.");
            return;
        }

        if (currentPassword === newPassword) {
            showToast("Новый пароль не может быть таким же, как старый.");
            return;
        }

        const xhr = new XMLHttpRequest();
        xhr.open("GET", `/account/isPasswordValid?password=${encodeURIComponent(currentPassword)}`, true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                const isValid = JSON.parse(xhr.responseText);
                if (isValid) {
                    updatePassword(newPassword);
                } else {
                    showToast("Неверный старый пароль!");
                }
            }
        };
        xhr.send();
    });

    function updatePassword(newPassword) {
        const xhr = new XMLHttpRequest();
        xhr.open("PUT", "/account/updatePassword", true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    showSuccessToast("Пароль успешно обновлен!");
                    closeModal(); // Закрытие модального окна
                } else {
                    showErrorToast("Произошла ошибка при обновлении пароля.");
                }
            }
        };
        xhr.send(`password=${encodeURIComponent(newPassword)}`);
    }

    function showToast(message) {
        const toastText = document.getElementById('toastText');
        toastText.textContent = message;
        const toast = new bootstrap.Toast(document.getElementById('toast'));
        toast.show();
    }

    function showSuccessToast(message) {
        const successToastText = document.getElementById('successToastText');
        successToastText.textContent = message;
        const successToast = new bootstrap.Toast(document.getElementById('successToast'));
        successToast.show();
    }

    function showErrorToast(message) {
        const errorToastText = document.getElementById('errorToastText');
        errorToastText.textContent = message;
        const errorToast = new bootstrap.Toast(document.getElementById('errorToast'));
        errorToast.show();
    }

    // Функция для закрытия модального окна
    function closeModal() {
        $('#changePasswordModal').modal('hide');// Закрываем модальное окно
    }
});
function togglePassword(fieldId) {
    const input = document.getElementById(fieldId);
    const icon = document.getElementById('show-button-text-' + fieldId);

    const isPassword = input.type === "password";
    input.type = isPassword ? "text" : "password";
    icon.textContent = isPassword ? "Скрыть" : "Показать";
}