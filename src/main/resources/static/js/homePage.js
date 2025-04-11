document.addEventListener('DOMContentLoaded', function () {
    const gestureModal = document.getElementById('gestureModal');

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
});