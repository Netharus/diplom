document.addEventListener('DOMContentLoaded', function () {
    // 💬 Константы сообщений
    const MSG_CONFIRM_GESTURE_PREFIX = "Подтвердите жест ";
    const MSG_UNKNOWN_ERROR = "Произошла неизвестная ошибка.";
    const MSG_EVENTS_EMPTY = "Список событий пуст";

    const gestureModal = document.getElementById('gestureModal');
    const gestureForm = document.getElementById('gestureForm');

    if (gestureModal) {
        gestureModal.addEventListener('show.bs.modal', function (event) {
            const button = event.relatedTarget;
            const gestureId = button.getAttribute('data-id');
            const gestureTitle = button.getAttribute('data-title');
            const gestureName = button.getAttribute('data-name');

            document.getElementById('gestureIdInput').value = gestureId;
            document.getElementById('gestureNameLabel').textContent = MSG_CONFIRM_GESTURE_PREFIX + gestureTitle;

            const gifPath = `/images/${gestureName}.gif`;
            document.getElementById('gesturePreview').src = gifPath;
            document.getElementById('gesturePreview').alt = gestureName;
        });
    }

    if (gestureForm) {
        gestureForm.addEventListener('submit', function (event) {
            event.preventDefault();

            const formData = new FormData(gestureForm);
            const gestureId = formData.get('gestureId');

            fetch('/gestures', {
                method: 'POST',
                body: new URLSearchParams({gestureId}),
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
                    showSuccessToast(result);

                    const gestureModalInstance = bootstrap.Modal.getInstance(gestureModal);
                    if (gestureModalInstance) {
                        gestureModalInstance.hide();
                    }
                })
                .catch(error => {
                    if (error.message) {
                        showExceptionToast(error.message);
                    } else {
                        showExceptionToast(MSG_UNKNOWN_ERROR);
                    }
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
            row.innerHTML = `<td colspan="2" style="text-align: center;">${MSG_EVENTS_EMPTY}</td>`;
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

    fetch('/account/isUserViewTutor', {
        method: 'GET',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка запроса');
            }
            return response.json();
        })
        .then(data => {
            if (data === false) {
                startIntro();
                guideViewed();
            }
        })
        .catch(error => {
            console.error('Ошибка при проверке показа обучающего тура:', error);
        });
});


function startIntro() {
    introJs().setOptions({
        steps: [
            {
                title: "Добро пожаловать!",
                intro: "Пройдите короткий тур по основным функциям.<br>" +
                    "Позже вы всегда можете вернуться к нему через кнопку <b>«Помощь»</b> в меню. Для каждой страницы предусмотрена своя подсказка."
            },
            {
                element: document.querySelector('.menu-container'),
                title: "Навигация",
                intro: "Используйте кнопки на панели, чтобы перейти в нужный раздел."
            },
            {
                element: document.querySelector('.header-button'),
                title: "Смена пароля",
                intro: "Здесь вы можете при необходимости изменить свой пароль."
            },
            {
                element: document.querySelector('.gestures'),
                title: "Быстрые жесты",
                intro: "Нажмите на нужную кнопку, чтобы выполнить жест или действие."
            },
            {
                element: document.querySelector('.event-table'),
                title: "История действий",
                intro: "Здесь отображаются последние 5 использованных жестов или сценариев."
            },
            {
                title: "Готово!",
                intro: "Вы успешно прошли вводный тур. Приятной работы!"
            },
        ],
        disableInteraction: true,
        nextLabel: 'Далее',
        prevLabel: 'Назад',
        doneLabel: 'Готово'
    }).start();
}

function guideViewed() {
    fetch('/account/guideViewed', {
        method: 'PATCH',
        headers: {
            'Accept': 'application/json'
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Не удалось отметить обучение как пройденное');
            }
            console.log('Обучающий тур отмечен как пройденный');
        })
        .catch(error => {
            console.error('Ошибка при отметке обучения:', error);
        });
}