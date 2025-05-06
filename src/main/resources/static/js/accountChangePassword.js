document.addEventListener("DOMContentLoaded", function () {
    // 💬 Константы сообщений
    const MSG_PASSWORDS_DO_NOT_MATCH = "Новый пароль и подтверждение пароля не совпадают.";
    const MSG_PASSWORDS_EQUAL = "Новый пароль не может быть таким же, как старый.";
    const MSG_INVALID_OLD_PASSWORD = "Неверный старый пароль!";
    const MSG_PASSWORD_UPDATED = "Пароль успешно обновлен!";
    const MSG_UPDATE_ERROR = "Произошла ошибка при обновлении пароля.";

    document.getElementById("savePasswordBtn").addEventListener("click", function () {
        const currentPassword = document.getElementById("currentPassword").value;
        const newPassword = document.getElementById("newPassword").value;
        const confirmPassword = document.getElementById("confirmPassword").value;
        const form = document.getElementById("passwordChangeForm");

        if (!form.checkValidity()) {
            form.reportValidity();
            return;
        }

        if (newPassword !== confirmPassword) {
            showExceptionToast(MSG_PASSWORDS_DO_NOT_MATCH);
            return;
        }

        if (currentPassword === newPassword) {
            showExceptionToast(MSG_PASSWORDS_EQUAL);
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
                    showExceptionToast(MSG_INVALID_OLD_PASSWORD);
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
                    showSuccessToast(MSG_PASSWORD_UPDATED);
                    closeModal();
                } else {
                    showExceptionToast(MSG_UPDATE_ERROR);
                }
            }
        };
        xhr.send(`password=${encodeURIComponent(newPassword)}`);
    }

    function closeModal() {
        $('#changePasswordModal').modal('hide');
    }
});
