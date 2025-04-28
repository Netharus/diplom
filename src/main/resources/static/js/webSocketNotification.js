document.addEventListener('DOMContentLoaded', () => {
    const socket = new SockJS('/websocket');
    const stompClient = Stomp.over(socket);

    const toastElement = document.getElementById('toast');
    const toastText = document.getElementById('toastText');
    const successToastElement = document.getElementById('successToast');
    const successToastText = document.getElementById('successToastText');

    const toast = new bootstrap.Toast(toastElement);
    const successToast = new bootstrap.Toast(successToastElement);

    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/topic/notifications', function(notificationDto) {
            const body = JSON.parse(notificationDto.body);

            if (body.status === 'SUCCESS') {
                successToastText.textContent = body.message;
                successToast.show();
            } else if (body.status === 'ERROR') {
                toastText.textContent = body.message;
                toast.show();
            } else {
                console.warn('Неизвестный статус уведомления:', body.status);
            }
        });
    });
});
