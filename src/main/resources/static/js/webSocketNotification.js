document.addEventListener('DOMContentLoaded', () => {
    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/notifications', function (notificationDto) {
            const body = JSON.parse(notificationDto.body);

            if (body.status === 'SUCCESS') {
                showSuccessToast(body.message);
            } else if (body.status === 'ERROR') {
                showExceptionToast(body.message);
            } else {
                console.warn('Неизвестный статус уведомления:', body.status);
            }
        });
    });
});