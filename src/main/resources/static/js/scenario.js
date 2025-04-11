document.addEventListener('DOMContentLoaded', () => {
    const gestureButtons = document.querySelectorAll('.gesture-button');
    const scenarioBoxes = document.querySelectorAll('.gesture-scenario-box');
    const toast = new bootstrap.Toast(document.getElementById('toast'));
    const toastText = document.getElementById('toastText');
    const exportButton = document.getElementById('exportButton');
    const importButton = document.getElementById('importButton');
    const inputButton = document.getElementById('inputButton');
    const scenarioInput = document.getElementById('scenarioInput');
    const gestureMap = new Map();

    const LIMIT_MESSAGE = "Нельзя добавить больше 5 жестов в сценарий.";
    const EMPTY_MESSAGE = "Нельзя запустить пустой сценарий!";
    const EMPTY_EXPORT_MESSAGE = "Нельзя экспортировать пустой сценарий!";
    const LOAD_EXCEPTION_MESSAGE = "Ошибка при загрузке сценария";

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
        }

        scenarioInput.value = scenarioValue;
    });

    exportButton.addEventListener('click', () => {
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

        window.location.href = `/exportScenario?scenario=${encodeURIComponent(scenarioValue)}`;
    });

    importButton.addEventListener('click', () => {
        const fileInput = document.createElement('input');
        fileInput.type = 'file';
        fileInput.accept = '.json';
        fileInput.onchange = async (event) => {
            const file = event.target.files[0];
            const formData = new FormData();
            formData.append('file', file);

            const response = await fetch('/importScenario', {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
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

            } else {
                toastText.textContent=LOAD_EXCEPTION_MESSAGE;
                toast.show();
            }
        };
        fileInput.click();
    });
});
