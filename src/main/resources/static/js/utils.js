function handleAjaxError(xhr, defaultMessage = "Произошла ошибка") {
    let errorText = defaultMessage;
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

function showErrorToast(message) {
    document.getElementById('toastText').textContent = message;
    new bootstrap.Toast(document.getElementById('toast')).show();
}
