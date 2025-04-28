document.addEventListener('DOMContentLoaded', () => {
    const gestureButtons = document.querySelectorAll('.gesture-button');
    const scenarioBoxes = document.querySelectorAll('.gesture-scenario-box');
    const toast = new bootstrap.Toast(document.getElementById('toast'));
    const toastText = document.getElementById('toastText');
    const successToast = new bootstrap.Toast(document.getElementById('successToast'));
    const successToastText = document.getElementById('successToastText');
    const exportButton = document.getElementById('exportButton');
    const importButton = document.getElementById('importButton');
    const inputButton = document.getElementById('inputButton');
    const scenarioInput = document.getElementById('scenarioInput');
    const scenarioForm = document.getElementById('scenarioForm');
    const gestureMap = new Map();

    const LIMIT_MESSAGE = "Нельзя добавить больше 5 жестов в сценарий.";
    const EMPTY_MESSAGE = "Нельзя запустить пустой сценарий!";
    const EMPTY_EXPORT_MESSAGE = "Нельзя экспортировать пустой сценарий!";
    const LOAD_EXCEPTION_MESSAGE = "Ошибка при загрузке сценария";
    const EXPORT_EXCEPTION_MESSAGE = 'Не удалось экспортировать сценарий. Попробуйте позже.';

    const updateBox = (box, src, alt) => {
        const clonedImg = document.createElement('img');
        clonedImg.src = src;
        clonedImg.alt = alt;

        box.innerHTML = '';
        box.classList.add('selected-gesture-box');
        box.appendChild(clonedImg);

        box.setAttribute('data-bs-toggle', 'tooltip');
        box.setAttribute('data-bs-title', 'Удалить жест?');
        new bootstrap.Tooltip(box);
    };

    gestureButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const usedBoxes = Array.from(scenarioBoxes).filter(box => box.children.length > 0);
            if (usedBoxes.length >= 5) {
                toastText.textContent = LIMIT_MESSAGE;
                toast.show();
                return;
            }

            const img = button.querySelector('img');
            const src = img.getAttribute('src');
            const alt = img.getAttribute('alt');
            const id = button.getAttribute('value');

            const emptyBoxIndex = Array.from(scenarioBoxes).findIndex(box => box.children.length === 0);
            if (emptyBoxIndex === -1) return;

            const emptyBox = scenarioBoxes[emptyBoxIndex];
            updateBox(emptyBox, src, alt);

            gestureMap.set(emptyBoxIndex, id);
        });
    });

    scenarioBoxes.forEach((box, index) => {
        box.addEventListener('click', () => {
            box.innerHTML = '';
            box.classList.remove('selected-gesture-box');
            gestureMap.delete(index);

            box.removeAttribute('data-bs-toggle');
            box.removeAttribute('data-bs-title');
            const tooltipInstance = bootstrap.Tooltip.getInstance(box);
            if (tooltipInstance) tooltipInstance.dispose();
        });
    });

    inputButton.addEventListener('click', (e) => {
        const scenarioValue = Array.from(gestureMap.entries())
            .sort((a, b) => a[0] - b[0])
            .map(([_, id]) => id)
            .join(' ')
            .trim();

        if (scenarioValue === "") {
            toastText.textContent = EMPTY_MESSAGE;
            toast.show();
            e.preventDefault();
            return;
        }

        scenarioInput.value = scenarioValue;
    });

    scenarioForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const scenarioValue = Array.from(gestureMap.entries())
            .sort((a, b) => a[0] - b[0])
            .map(([_, id]) => id)
            .join(' ')
            .trim();

        if (scenarioValue === "") {
            toastText.textContent = EMPTY_MESSAGE;
            toast.show();
            return;
        }

        try {
            const response = await fetch('/scenario', {
                method: 'POST',
                body: new URLSearchParams({ scenario: scenarioValue }),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            });

            if (!response.ok) {
                const errorText = await response.text();
                toastText.textContent = errorText || "Ошибка при выполнении сценария.";
                toast.show();
                return;
            }

            const successMessage = await response.text();
            successToastText.textContent = successMessage;
            successToast.show();
        } catch (error) {
            toastText.textContent = "Ошибка при выполнении сценария.";
            toast.show();
            console.error('Ошибка отправки сценария:', error);
        }
    });

    exportButton.addEventListener('click', async () => {
        const scenarioValue = Array.from(gestureMap.entries())
            .sort((a, b) => a[0] - b[0])
            .map(([_, id]) => id)
            .join(' ')
            .trim();

        if (!scenarioValue) {
            toastText.textContent = EMPTY_EXPORT_MESSAGE;
            toast.show();
            return;
        }

        try {
            const response = await fetch(`/exportScenario?scenario=${encodeURIComponent(scenarioValue)}`);

            if (!response.ok) {
                const errorResponse = await response.json();
                toastText.textContent = errorResponse.message || EXPORT_EXCEPTION_MESSAGE;
                toast.show();
                return;
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);

            const a = document.createElement('a');
            a.href = url;
            a.download = 'scenario.json';
            document.body.appendChild(a);
            a.click();
            a.remove();
            window.URL.revokeObjectURL(url);

        } catch (error) {
            toastText.textContent = EXPORT_EXCEPTION_MESSAGE;
            toast.show();
            console.error('Ошибка при экспорте сценария:', error);
        }
    });

    importButton.addEventListener('click', () => {
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = '.json';
        fileInput.onchange = async (event) => {
            const file = event.target.files[0];
            const formData = new FormData();
            formData.append('file', file);

            try {
                const response = await fetch('/importScenario', {
                    method: 'POST',
                    body: formData
                });

                if (!response.ok) {
                    const errorResponse = await response.json();
                    toastText.textContent = errorResponse.message || LOAD_EXCEPTION_MESSAGE;
                    toast.show();
                    return;
                }

                const scenarioArray = await response.json();

                gestureMap.clear();
                scenarioBoxes.forEach(box => {
                    box.innerHTML = '';
                    box.classList.remove('selected-gesture-box');
                    box.removeAttribute('data-bs-toggle');
                    box.removeAttribute('data-bs-title');
                    const tooltipInstance = bootstrap.Tooltip.getInstance(box);
                    if (tooltipInstance) tooltipInstance.dispose();
                });

                scenarioArray.forEach((gestureId, index) => {
                    if (index < scenarioBoxes.length) {
                        const box = scenarioBoxes[index];
                        const button = Array.from(gestureButtons).find(b => b.getAttribute('value') === gestureId.toString());
                        if (button) {
                            const img = button.querySelector('img');
                            const src = img.getAttribute('src');
                            const alt = img.getAttribute('alt');
                            updateBox(box, src, alt);
                            gestureMap.set(index, gestureId);
                        }
                    }
                });

                scenarioInput.value = scenarioArray.join(' ');

            } catch (error) {
                toastText.textContent = LOAD_EXCEPTION_MESSAGE;
                toast.show();
                console.error('Ошибка при импорте сценария:', error);
            }
        };
        fileInput.click();
    });
});
