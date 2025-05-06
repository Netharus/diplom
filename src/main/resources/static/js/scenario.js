// ✅ Глобальные константы сообщений
const MSG_LIMIT = "Нельзя добавить больше 5 жестов в сценарий.";
const MSG_EMPTY_SCENARIO = "Нельзя запустить пустой сценарий!";
const MSG_EMPTY_EXPORT = "Нельзя экспортировать пустой сценарий!";
const MSG_SCENARIO_RUN_ERROR = "Ошибка при выполнении сценария.";
const MSG_EXPORT_ERROR = "Не удалось экспортировать сценарий. Попробуйте позже.";
const MSG_IMPORT_ERROR = "Ошибка при загрузке сценария.";
const MSG_TOOLTIP_REMOVE = "Удалить жест?";

document.addEventListener('DOMContentLoaded', () => {
    const gestureButtons = document.querySelectorAll('.gesture-button');
    const scenarioBoxes = document.querySelectorAll('.gesture-scenario-box');
    const exportButton = document.getElementById('exportButton');
    const importButton = document.getElementById('importButton');
    const inputButton = document.getElementById('inputButton');
    const scenarioInput = document.getElementById('scenarioInput');
    const scenarioForm = document.getElementById('scenarioForm');
    const gestureMap = new Map();

    const updateBox = (box, src, alt) => {
        const clonedImg = document.createElement('img');
        clonedImg.src = src;
        clonedImg.alt = alt;

        box.innerHTML = '';
        box.classList.add('selected-gesture-box');
        box.appendChild(clonedImg);

        box.setAttribute('data-bs-toggle', 'tooltip');
        box.setAttribute('data-bs-title', MSG_TOOLTIP_REMOVE);
        new bootstrap.Tooltip(box);
    };

    gestureButtons.forEach((button) => {
        button.addEventListener('click', () => {
            const usedBoxes = Array.from(scenarioBoxes).filter(box => box.children.length > 0);
            if (usedBoxes.length >= 5) {
                showExceptionToast(MSG_LIMIT);
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
            showExceptionToast(MSG_EMPTY_SCENARIO);
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
            showExceptionToast(MSG_EMPTY_SCENARIO);
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
                showExceptionToast(errorText || MSG_SCENARIO_RUN_ERROR);
                return;
            }

            const successMessage = await response.text();
            showSuccessToast(successMessage);
        } catch (error) {
            showExceptionToast(MSG_SCENARIO_RUN_ERROR);
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
            showExceptionToast(MSG_EMPTY_EXPORT);
            return;
        }

        try {
            const response = await fetch(`/exportScenario?scenario=${encodeURIComponent(scenarioValue)}`);

            if (!response.ok) {
                const errorResponse = await response.json();
                showExceptionToast(errorResponse.message || MSG_EXPORT_ERROR);
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
            showExceptionToast(MSG_EXPORT_ERROR);
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
                    showExceptionToast(errorResponse.message || MSG_IMPORT_ERROR);
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
                showExceptionToast(MSG_IMPORT_ERROR);
                console.error('Ошибка при импорте сценария:', error);
            }
        };
        fileInput.click();
    });
});
function startIntro() {
    introJs().setOptions({
        steps: [
            {
                element: document.querySelector('.gestures'),
                title: "Жесты",
                intro: "Нажмите на нужный жест, чтобы добавить его в сценарий."
            },
            {
                element: document.querySelector('.scenario-gesture-button-box'),
                title: "Сценарий",
                intro: "Здесь отображаются добавленные жесты. Нажмите на иконку жеста, чтобы удалить его из сценария."
            },
            {
                title: "Ограничения",
                intro: "Сценарий может содержать не более 5 жестов одновременно."
            },
        ],
        disableInteraction: true,
        nextLabel: 'Далее',
        prevLabel: 'Назад',
        doneLabel: 'Готово'
    }).start();
}