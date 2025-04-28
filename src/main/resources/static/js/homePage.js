document.addEventListener('DOMContentLoaded', function () {
    const gestureModal = document.getElementById('gestureModal');
    const gestureForm = document.getElementById('gestureForm');

    if (gestureModal) {
        gestureModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const gestureId = button.getAttribute('data-id');
            const gestureTitle = button.getAttribute('data-title');
            const gestureName = button.getAttribute('data-name');

            document.getElementById('gestureIdInput').value = gestureId;
            document.getElementById('gestureNameLabel').textContent = "Подтвердите жест " + gestureTitle;

            const gifPath = `/images/${gestureName}.gif`;
            document.getElementById('gesturePreview').src = gifPath;
            document.getElementById('gesturePreview').alt = gestureName;
        });
    }

    if (gestureForm) {
        gestureForm.addEventListener('submit', function (event) {
            event.preventDefault(); // отменяем стандартную отправку формы

            const formData = new FormData(gestureForm);
            const gestureId = formData.get('gestureId');

            fetch('/gestures', {
                method: 'POST',
                body: new URLSearchParams({ gestureId }),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        return response.json().then(errorData => {
                            throw new Error(errorData.message);
                        });
                    }
                    return response.text();
                })
                .then(result => {
                    // Успех
                    document.getElementById('successToastText').textContent = result;
                    const successToast = new bootstrap.Toast(document.getElementById('successToast'));
                    successToast.show();

                    const gestureModalInstance = bootstrap.Modal.getInstance(gestureModal);
                    if (gestureModalInstance) {
                        gestureModalInstance.hide(); // закрыть модалку
                    }
                })
                .catch(error => {
                    // Ошибка - обработка ErrorResponseDto
                    if (error.message) {
                        // Выводим сообщение из поля message
                        document.getElementById('toastText').textContent = error.message;
                    } else {
                        // Если сообщение не найдено, выводим стандартную ошибку
                        document.getElementById('toastText').textContent = 'Произошла неизвестная ошибка.';
                    }
                    const errorToast = new bootstrap.Toast(document.getElementById('toast'));
                    errorToast.show();
                });
        });
    }

    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/recent', function (message) {
            const events = JSON.parse(message.body);
            updateEventsTable(events);
        });
    });

    function updateEventsTable(events) {
        const tbody = document.querySelector('.event-table tbody');
        tbody.innerHTML = '';

        if (events.length === 0) {
            const row = document.createElement('tr');
            row.innerHTML = '<td colspan="2" style="text-align: center;">Список событий пуст</td>';
            tbody.appendChild(row);
        } else {
            events.forEach(event => {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${event.dateTime}</td>
                        <td>${event.gesture}</td>
                    `;
                tbody.appendChild(row);
            });
        }
    }
});
