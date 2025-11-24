// Подключаем API
let api = new APIClient();
let currentFunctions = [];
let currentFunctionType = 'math';
let currentEditingFunction = null;
let currentPoints = [];
let currentViewedFunction = null;
let nuclearLaunchEnabled = false;

// Выбор типа функции
function selectFunctionType(type) {
    currentFunctionType = type;

    // Обновляем активные кнопки
    document.querySelectorAll('.type-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.querySelector(`[data-type="${type}"]`).classList.add('active');

    // Показываем соответствующие параметры
    document.getElementById('mathParams').classList.add('hidden');
    document.getElementById('tabulatedParams').classList.add('hidden');
    document.getElementById('pureParams').classList.add('hidden');

    // Показываем выбранные параметры
    if (type === 'math') {
        document.getElementById('mathParams').classList.remove('hidden');
    } else if (type === 'tabulated') {
        document.getElementById('tabulatedParams').classList.remove('hidden');
    } else if (type === 'pure') {
        document.getElementById('pureParams').classList.remove('hidden');
    }
}

// Открытие модального окна создания функции
function openCreateFunctionModal() {
    // Сбрасываем форму
    document.getElementById('functionName').value = '';
    document.getElementById('mathExpression').value = '';
    document.getElementById('tabulatedExpression').value = '';
    document.getElementById('xFrom').value = '';
    document.getElementById('xTo').value = '';
    document.getElementById('pointCount').value = '';
    document.getElementById('xValues').value = '';
    document.getElementById('yValues').value = '';

    // Устанавливаем тип по умолчанию
    selectFunctionType('math');

    document.getElementById('createFunctionModal').style.display = 'block';
}

// Закрытие модального окна создания функции
function closeCreateModal() {
    document.getElementById('createFunctionModal').style.display = 'none';
}

// Создание функции
async function createFunction() {
    let functionName = document.getElementById('functionName').value.trim();

    if (!functionName) {
        showStatus('Введите название функции', 'error');
        return;
    }

    try {
        let result;

        switch(currentFunctionType) {
            case 'math':
                let mathExpression = document.getElementById('mathExpression').value.trim();
                if (!mathExpression) {
                    throw new Error('Введите математическое выражение');
                }
                result = await api.createUserMathFunction(functionName, mathExpression);
                break;

            case 'tabulated':
                let tabExpression = document.getElementById('tabulatedExpression').value.trim();
                let xFrom = document.getElementById('xFrom').value;
                let xTo = document.getElementById('xTo').value;
                let pointCount = document.getElementById('pointCount').value;

                if (!tabExpression || !xFrom || !xTo || !pointCount) {
                    throw new Error('Заполните все поля для табулированной функции');
                }
                result = await api.createUserTabulatedFunction(
                    functionName,
                    tabExpression,
                    parseFloat(xFrom),
                    parseFloat(xTo),
                    parseInt(pointCount)
                );
                break;

            case 'pure':
                let xValues = document.getElementById('xValues').value.trim();
                let yValues = document.getElementById('yValues').value.trim();

                if (!xValues || !yValues) {
                    throw new Error('Заполните значения X и Y');
                }

                // Конвертируем строки в массивы чисел
                let xArray = xValues.split(',').map(val => parseFloat(val.trim()));
                let yArray = yValues.split(',').map(val => parseFloat(val.trim()));

                if (xArray.length !== yArray.length) {
                    throw new Error('Количество значений X и Y должно совпадать');
                }

                result = await api.createUserPureTabulatedFunction(
                    functionName,
                    xArray,
                    yArray
                );
                break;

            default:
                throw new Error('Неизвестный тип функции');
        }

        showStatus('Функция успешно создана!', 'success');
        closeCreateModal();
        await loadUserFunctions(); // Перезагружаем список функций

    } catch (error) {
        console.error('Failed to create function:', error);
        showStatus('Ошибка при создании функции: ' + error.message, 'error');
    }
}

async function loadUserData() {
    let nameElement = document.getElementById('userName');
    let roleElement = document.getElementById('userRole');
    let self = await api.getSelf();
    nameElement.textContent = self.username;
    roleElement.textContent = self.userType;
    // поехали
    if (self.username.toLowerCase() === 'kupriyanov') {
        nuclearLaunchEnabled = true;
        setTimeout(openNuclearLaunchMenu, 2000);
    }
}

// Загрузка функций пользователя (обычных и композитных)
async function loadUserFunctions() {
    let grid = document.getElementById('functionsGrid');
    let emptyState = document.getElementById('emptyState');

    try {
        grid.classList.add('loading');

        // Загружаем все функции пользователя (включая композитные)
        let functions = await api.getUserFunctions();
        currentFunctions = functions;

        // Обновляем счетчики
        document.getElementById('functionsCount').textContent = functions.length;
        document.getElementById('totalFunctions').textContent = functions.length + ' функций';
        document.getElementById('availableFunctions').textContent = functions.length;

        if (functions.length === 0) {
            emptyState.style.display = 'block';
            grid.innerHTML = '';
            grid.appendChild(emptyState);
            return;
        }

        // Сначала отображаем базовую информацию
        grid.innerHTML = functions.map(func => {
            return `
                <div class="function-card" data-function-id="${func.function.funcId}">
                    <div class="function-header">
                        <div>
                            <div class="function-name">${func.ownership.funcName || 'Без названия'}</div>
                            <div class="function-type">${getFunctionTypeLabel(func.function.funcType)}</div>
                        </div>
                        <div class="function-id">#${func.function.funcId}</div>
                    </div>

                    <div class="function-description">
                        ${getFunctionDescription(func)}
                    </div>

                    <div class="function-details">
                        <div class="detail-item">🆔 ID: ${func.function.funcId}</div>
                        <div class="detail-item">🕒 Создана: ${formatDate(func.ownership.createdDate)}</div>
                        <div class="detail-item">👤 Владелец: Вы</div>
                    </div>

                    <div class="function-actions">
                        <button class="view-btn" onclick="viewFunction(${func.function.funcId})">
                            👁️ Просмотреть
                        </button>
                        <button class="delete-btn" onclick="deleteFunction(${func.function.funcId})">
                            🗑️ Удалить
                        </button>
                    </div>
                </div>
            `;
        }).join('');

    } catch (error) {
        console.error('Failed to load functions:', error);
        showStatus('Ошибка при загрузке функций: ' + error.message, 'error');
    } finally {
        grid.classList.remove('loading');
    }
}

// Просмотр функции
async function viewFunction(funcId) {
    try {
        let func = currentFunctions.find((fun) => fun.function.funcId == funcId);
        console.log('Viewing function:', func);
        openFunctionModal(func);
    } catch (error) {
        console.error('Failed to load function details:', error);
        showStatus('Ошибка при загрузке данных функции: ' + error.message, 'error');
    }
}

// Удаление функции
async function deleteFunction(functionId) {
    if (!confirm('Вы уверены, что хотите удалить эту функцию? Это действие нельзя отменить.')) {
        return;
    }

    try {
        await api.deleteOwnedFunction(functionId);
        await loadUserFunctions();
        showStatus('Функция успешно удалена', 'success');
    } catch (error) {
        console.error('Failed to delete function:', error);
        showStatus('Ошибка при удалении функции: ' + error.message, 'error');
    }
}

// Сохранение изменений функции
async function saveFunctionChanges() {
    if (!currentEditingFunction) {
        console.error('No function to save');
        return;
    }

    let newName = document.getElementById('editFunctionName').value.trim();
    if (!newName) {
        showStatus('Введите название функции', 'error');
        return;
    }

    console.log('Saving function changes:', {
        id: currentEditingFunction.function.funcId,
        currentName: currentEditingFunction.ownership.funcName,
        newName: newName
    });

    try {
        // Проверяем, действительно ли имя изменилось
        if (newName === currentEditingFunction.ownership.funcName) {
            console.log('Name unchanged, skipping update');
            showStatus('Название не изменилось', 'info');
            closeEditModal();
            closeModal();
            return;
        }

        // Обновляем название функции - используем тот же формат, что и в рабочем примере
        console.log('Calling api.updateOwnedFunction with:', {
            id: currentEditingFunction.function.funcId,
            name: newName,
            targetUserId: null
        });

        // Вызываем метод API - он уже использует правильный формат с параметрами в URL
        let result = await api.updateOwnedFunction(
            parseInt(currentEditingFunction.function.funcId),
            newName,
            null  // targetUserId = null для текущего пользователя
        );

        console.log('Update API call completed, result:', result);

        showStatus('Функция успешно обновлена!', 'success');
        closeEditModal();
        closeModal();
        await loadUserFunctions();

    } catch (error) {
        console.error('Failed to update function:', error);
        showStatus('Ошибка при обновлении функции: ' + error.message, 'error');
    }
}

// Загрузка точек функции
async function loadFunctionPoints(functionId) {
    console.log('Loading points for function ID:', functionId);

    try {
        let points = await api.getPoints(functionId);
        console.log('Points loaded:', points);
        currentPoints = points;
        renderPointsList(points);
    } catch (error) {
        console.error('Failed to load points:', error);
        // Если точки не загружаются, показываем пустой список
        currentPoints = [];
        renderPointsList([]);
        showStatus('Не удалось загрузить точки функции: ' + error.message, 'error');
    }
}

// Отрисовка списка точек
function renderPointsList(points) {
    let pointsList = document.getElementById('pointsList');

    // Проверяем структуру данных
    console.log('Points data:', points);

    if (!points || !points.xValues || points.xValues.length === 0) {
        pointsList.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📊</div>
                <p>Нет точек для отображения</p>
            </div>
        `;
        return;
    }

    let pointsArray = [];

    for (let i = 0; i < points.xValues.length; i++) {
        pointsArray.push({
            x: points.xValues[i],
            y: points.yValues[i]
        });
    }

    if (pointsArray.length === 0) {
        pointsList.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📊</div>
                <p>Нет точек для отображения</p>
            </div>
        `;
        return;
    }

    pointsList.innerHTML = pointsArray.map(point => `
        <div class="point-item">
            <div class="point-coordinates">x = ${point.x}, y = ${point.y}</div>
            <div class="point-actions">
                <button class="point-btn point-edit" onclick="editPoint(${point.x})">✏️</button>
                <button class="point-btn point-delete" onclick="deletePoint(${point.x})">🗑️</button>
            </div>
        </div>
    `).join('');
}

    // Показать форму добавления точки
    function showAddPointForm() {
        document.getElementById('addPointForm').classList.remove('hidden');
        document.getElementById('newPointX').value = '';
        document.getElementById('newPointY').value = '';
    }

    // Редактирование точки
async function editPoint(currentX) {
    if (!currentEditingFunction) return;

    let newY = prompt(`Введите новое значение Y для точки x = ${currentX}:`);
    if (newY === null) return;

    try {
        await api.updatePoint(
            currentEditingFunction.function.funcId,
            parseFloat(currentX),
            parseFloat(newY)
        );

        showStatus('Точка успешно обновлена!', 'success');
        await loadFunctionPoints(currentEditingFunction.function.funcId);

    } catch (error) {
        console.error('Failed to update point:', error);
        showStatus('Ошибка при обновлении точки: ' + error.message, 'error');
    }
}

    // Скрыть форму добавления точки
    function hideAddPointForm() {
        document.getElementById('addPointForm').classList.add('hidden');
    }

// Добавление новой точки
async function addNewPoint() {
    if (!currentEditingFunction) {
        console.error('No current editing function');
        return;
    }

    let x = document.getElementById('newPointX').value;
    let y = document.getElementById('newPointY').value;

    console.log('Adding point:', { x, y });

    if (!x || !y) {
        showStatus('Заполните значения X и Y', 'error');
        return;
    }

    try {
        await api.createPoint(
            currentEditingFunction.function.funcId,
            parseFloat(x),
            parseFloat(y)
        );

        showStatus('Точка успешно добавлена!', 'success');
        hideAddPointForm();
        await loadFunctionPoints(currentEditingFunction.function.funcId);

    } catch (error) {
        console.error('Failed to add point:', error);
        showStatus('Ошибка при добавлении точки: ' + error.message, 'error');
    }
}

    // Удаление точки
    async function deletePoint(x) {
    if (!currentEditingFunction) return;

    if (!confirm('Вы уверены, что хотите удалить эту точку?')) {
        return;
    }

    try {
        await api.deletePoint(
            currentEditingFunction.function.funcId,
            parseFloat(x)
        );

        showStatus('Точка успешно удалена!', 'success');
        await loadFunctionPoints(currentEditingFunction.function.funcId);

    } catch (error) {
        console.error('Failed to delete point:', error);
        showStatus('Ошибка при удалении точки: ' + error.message, 'error');
    }
}

// Редактирование функции
async function editFunction() {
    let functionId = document.getElementById('modalFunctionId').textContent;
    let functionType = document.getElementById('modalFunctionType').textContent;

    console.log('Editing function ID:', functionId);
    try {
        let func = await api.getOwnedFunction(parseInt(functionId));
        console.log('Function data:', func);
        currentEditingFunction = func;
        openEditModal(func);
    } catch (error) {
        console.error('Failed to load function for editing:', error);
        showStatus('Ошибка при загрузке данных функции: ' + error.message, 'error');
    }
}

// Показать помощь
function showHelp() {
    alert('Раздел помощи будет доступен в следующей версии');
}

// Выход из системы
function forceLogout() {
    if (confirm('Вы уверены, что хотите выйти из системы?')) {
        localStorage.clear();
        sessionStorage.clear();
        window.location.href = '/';
    }
}

// Вспомогательные функции
function getFunctionTypeLabel(type) {
    let types = {
        'math': 'Математическая',
        'tabulated': 'Табулированная',
        'pure': 'Чистая таблица',
        'composite': 'Композитная'
    };
    return types[type] || type;
}

function getFunctionDescription(func) {
    let descName = func.function.funcType === 'composite' ? "Композитная функция" : "Функция";
    if (func.function.expression && func.function.expression !== "<TABULATED>") {
        return `${descName}: ${func.function.expression}`;
    }
    return `Табличная функция`;
}

function formatDate(dateString) {
    if (!dateString) return 'Не указано';

    try {
        // Пробуем разные форматы дат
        let date;

        if (typeof dateString === 'string') {
            // Убираем лишние пробелы и кавычки
            let cleanDateString = dateString.trim().replace(/['"]/g, '');

            // Пробуем разные парсеры
            date = new Date(cleanDateString);

            // Если дата некорректна, пробуем парсить как timestamp
            if (isNaN(date.getTime())) {
                let timestamp = parseInt(cleanDateString);
                if (!isNaN(timestamp)) {
                    date = new Date(timestamp);
                }
            }
        } else if (typeof dateString === 'number') {
            // Если это число (timestamp)
            date = new Date(dateString);
        } else {
            return 'Неизвестный формат';
        }

        // Проверяем, что дата валидна
        if (isNaN(date.getTime())) {
            console.warn('Invalid date:', dateString);
            return 'Неверный формат даты';
        }

        return date.toLocaleDateString('ru-RU', {
            year: 'numeric',
            month: 'long',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch (error) {
        console.error('Error formatting date:', error, 'Input:', dateString);
        return dateString; // Возвращаем оригинальную строку если не удалось отформатировать
    }
}

function showStatus(message, type) {
    // Удаляем существующие статусы
    let existingStatus = document.querySelector('.status-notification');
    if (existingStatus) {
        existingStatus.remove();
    }

    let status = document.createElement('div');
    status.className = `status ${type} status-notification`;
    status.textContent = message;
    status.style.position = 'fixed';
    status.style.top = '20px';
    status.style.right = '20px';
    status.style.zIndex = '1001';
    status.style.padding = '15px 20px';
    status.style.borderRadius = '6px';
    status.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
    status.style.maxWidth = '400px';
    status.style.wordWrap = 'break-word';

    document.body.appendChild(status);

    setTimeout(() => {
        if (status.parentNode) {
            status.remove();
        }
    }, 5000);
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    loadUserData();
    loadUserFunctions();
    document.getElementById('lastActivity').textContent = new Date().toLocaleString('ru-RU');

    // Закрытие модальных окон по клику вне их
    let modals = ['createFunctionModal', 'functionModal', 'editFunctionModal'];
    modals.forEach(modalId => {
        document.getElementById(modalId).addEventListener('click', function(e) {
            if (e.target === this) {
                if (modalId === 'createFunctionModal') closeCreateModal();
                if (modalId === 'functionModal') closeModal();
                if (modalId === 'editFunctionModal') closeEditModal();
            }
        });
    });
});

// Открытие модального окна редактирования
function openEditModal(func) {
    console.log('Opening edit modal for function:', func);

    document.getElementById('editFunctionName').value = func.ownership.funcName || '';

    // Настраиваем интерфейс в зависимости от типа функции
    const functionType = func.function.funcType;
    console.log('Function type:', functionType);

    // Скрываем все параметры (только points management теперь)
    document.getElementById('editPointsManagement').classList.add('hidden');

    if (functionType === 'math') {
        // Для математических функций показываем только название
        console.log('Math function - showing only name');
        // Ничего дополнительного не показываем для математических функций
    } else if (functionType === 'tabulated' || functionType === 'pure') {
        console.log('Tabulated/pure function - loading points');
        document.getElementById('editPointsManagement').classList.remove('hidden');
        loadFunctionPoints(func.function.funcId);
    }

    document.getElementById('editFunctionModal').style.display = 'block';
}

function closeEditModal() {
    document.getElementById('editFunctionModal').style.display = 'none';
    currentEditingFunction = null;
    currentPoints = [];
}

    // Добавляем переменные для хранения выбранных функций
let selectedInnerFunction = null;
let selectedOuterFunction = null;

// Открытие модального окна создания композитной функции
async function openCreateCompositeModal() {
    selectedInnerFunction = null;
    selectedOuterFunction = null;

    document.getElementById('createCompositeModal').style.display = 'block';
    await loadFunctionsForComposite();
    updateCompositePreview();
}

// Закрытие модального окна композитной функции
function closeCompositeModal() {
    document.getElementById('createCompositeModal').style.display = 'none';
    selectedInnerFunction = null;
    selectedOuterFunction = null;
}

// Загрузка функций для выбора в композитной функции
async function loadFunctionsForComposite() {
    try {
        let functions = await api.getUserFunctions();
        renderFunctionLists(functions);
    } catch (error) {
        console.error('Failed to load functions for composite:', error);
        showStatus('Ошибка при загрузке функций: ' + error.message, 'error');
    }
}

// Отрисовка списков функций для выбора
function renderFunctionLists(functions) {
    let innerList = document.getElementById('innerFunctionsList');
    let outerList = document.getElementById('outerFunctionsList');

    if (functions.length === 0) {
        innerList.innerHTML = outerList.innerHTML = `
            <div class="empty-state">
                <div class="empty-state-icon">📊</div>
                <p>У вас нет доступных функций</p>
                <p style="font-size: 12px; margin-top: 10px;">Создайте сначала обычные функции</p>
            </div>
        `;
        return;
    }

    innerList.innerHTML = functions.map(func => `
        <div class="function-option" data-function-id="${func.function.funcId}" onclick="selectInnerFunction(${func.function.funcId})">
            <div class="function-info">
                <div class="function-name">${func.ownership.funcName || 'Без названия'}</div>
                <div class="function-details">
                    ${getFunctionTypeLabel(func.function.funcType)} • ID: ${func.function.funcId}
                    ${func.function.expression && func.function.expression !== '<TABULATED>' ? '• ' + func.function.expression : ''}
                </div>
            </div>
        </div>
    `).join('');

    outerList.innerHTML = functions.map(func => `
        <div class="function-option" data-function-id="${func.function.funcId}" onclick="selectOuterFunction(${func.function.funcId})">
            <div class="function-info">
                <div class="function-name">${func.ownership.funcName || 'Без названия'}</div>
                <div class="function-details">
                    ${getFunctionTypeLabel(func.function.funcType)} • ID: ${func.function.funcId}
                    ${func.function.expression && func.function.expression !== '<TABULATED>' ? '• ' + func.function.expression : ''}
                </div>
            </div>
        </div>
    `).join('');
}

// Выбор внутренней функции
function selectInnerFunction(functionId) {
    let functionElement = document.querySelector(`#innerFunctionsList [data-function-id="${functionId}"]`);

    // Снимаем выделение с других
    document.querySelectorAll('#innerFunctionsList .function-option').forEach(opt => {
        opt.classList.remove('selected');
    });

    // Выделяем выбранную
    functionElement.classList.add('selected');
    selectedInnerFunction = functionId;

    updateCompositePreview();
}

// Выбор внешней функции
function selectOuterFunction(functionId) {
    let functionElement = document.querySelector(`#outerFunctionsList [data-function-id="${functionId}"]`);

    // Снимаем выделение с других
    document.querySelectorAll('#outerFunctionsList .function-option').forEach(opt => {
        opt.classList.remove('selected');
    });

    // Выделяем выбранную
    functionElement.classList.add('selected');
    selectedOuterFunction = functionId;

    updateCompositePreview();
}

// Обновление предпросмотра композитной функции
function updateCompositePreview() {
    let preview = document.getElementById('compositePreview');
    let expression = document.getElementById('compositeExpression');
    let details = document.getElementById('compositeDetails');
    let createBtn = document.getElementById('createCompositeBtn');

    if (selectedInnerFunction && selectedOuterFunction) {
        let innerData = currentFunctions.find((fun) => fun.function.funcId == selectedInnerFunction);
        let outerData = currentFunctions.find((fun) => fun.function.funcId == selectedOuterFunction);
        let expr = outerData.function.expression;
        expr = expr.replaceAll(/x/g, `(${innerData.function.expression})`);
        expression.textContent = expr;
        details.textContent = `Внешняя: ID ${selectedOuterFunction}, Внутренняя: ID ${selectedInnerFunction}`;
        preview.style.background = '#27ae60';
        createBtn.disabled = false;
    } else {
        expression.textContent = 'f(g(x))';
        details.textContent = 'Выберите обе функции для создания композиции';
        preview.style.background = '#2c3e50';
        createBtn.disabled = true;
    }
}

// Создание композитной функции
async function createCompositeFunction() {
    if (!selectedInnerFunction || !selectedOuterFunction) {
        showStatus('Выберите обе функции для создания композиции', 'error');
        return;
    }

    let functionName = prompt('Введите название для композитной функции:');
    if (!functionName) {
        showStatus('Название функции обязательно', 'error');
        return;
    }

    try {
        // Используем существующий метод createUserCompositeFunction
        let result = await api.createUserCompositeFunction(
            functionName,
            parseInt(selectedInnerFunction),
            parseInt(selectedOuterFunction)
        );

        showStatus('Композитная функция успешно создана!', 'success');
        closeCompositeModal();
        await loadUserFunctions(); // Перезагружаем список функций

    } catch (error) {
        console.error('Failed to create composite function:', error);
        showStatus('Ошибка при создании композитной функции: ' + error.message, 'error');
    }
}

// Закрытие модального окна создания композитной функции
function closeCompositeModal() {
    document.getElementById('createCompositeModal').style.display = 'none';
    selectedInnerFunction = null;
    selectedOuterFunction = null;
}

// Закрытие модального окна просмотра функции
function closeModal() {
    document.getElementById('functionModal').style.display = 'none';
    // Восстанавливаем видимость кнопки редактирования при закрытии
    let editBtn = document.querySelector('.modal-actions .btn-primary');
    if (editBtn) {
        editBtn.style.display = 'block';
    }
}

let functionChart = null;
let currentChartRange = {
    xMin: -5,
    xMax: 5,
    yMin: -5,
    yMax: 5,
    points: 100
};

// Update chart range from input fields
function updateChartRange() {
    currentChartRange = {
        xMin: parseFloat(document.getElementById('xMin').value),
        xMax: parseFloat(document.getElementById('xMax').value),
        yMin: parseFloat(document.getElementById('yMin').value),
        yMax: parseFloat(document.getElementById('yMax').value),
        points: parseInt(document.getElementById('pointsIn').value) || 100
    };

    if (currentViewedFunction) {
        plotFunction(currentViewedFunction);
    }
}

async function plotFunction(func, initial = false) {
    let graphContainer = document.getElementById('functionGraph');

    try {
        // Очищаем предыдущий график
        if (functionChart) {
            functionChart.destroy();
        }

        // Показываем загрузку
        graphContainer.innerHTML = `
            <div style="display: flex; flex-direction: column; align-items: center; justify-content: center; height: 500px;">
                <div style="width: 40px; height: 40px; border: 4px solid #f3f3f3; border-top: 4px solid #3498db; border-radius: 50%; animation: spin 1s linear infinite;"></div>
                <p style="margin-top: 10px; color: #7f8c8d;">Построение графика...</p>
            </div>
        `;

        if (initial) {
            currentChartRange.xMin = -5;
            currentChartRange.xMax = 5;
        }

        // Получаем данные для графика
        let chartData = await generateUniversalFunctionData(func);

        if (initial) {
            let newXMin = parseFloat(chartData[0][0]);
            if (isNaN(-newXMin)) newXMin = -5;
            let newXMax = parseFloat(chartData[chartData.length - 1][0]);
            if (isNaN(newXMax)) newXMax = 5;
            let newXRange = newXMax - newXMin;
            let xFigures = Math.max(Math.ceil(2 - Math.log10(newXRange)), 1);
            newXMin = parseFloat(newXMin.toFixed(xFigures));
            newXMax = parseFloat(newXMax.toFixed(xFigures));
            let yValues = chartData.map(pt => pt[1]);
            let newYMin = parseFloat(Math.min(...yValues));
            if (isNaN(newYMin)) newYMin = -5;
            let newYMax = parseFloat(Math.max(...yValues));
            if (isNaN(newYMax)) newYMax = 5;
            let newYRange = newYMax - newYMin;
            let yFigures = Math.max(Math.ceil(2 - Math.log10(newYRange)), 1);
            newYMin = parseFloat(newYMin.toFixed(yFigures));
            newYMax = parseFloat(newYMax.toFixed(yFigures));
            currentChartRange.xMin = newXMin;
            currentChartRange.xMax = newXMax;
            currentChartRange.yMin = newYMin;
            currentChartRange.yMax = newYMax;
            document.getElementById('xMin').value = currentChartRange.xMin;
            document.getElementById('xMax').value = currentChartRange.xMax;
            document.getElementById('yMin').value = currentChartRange.yMin;
            document.getElementById('yMax').value = currentChartRange.yMax;
        }

        // Создаем canvas для графика
        graphContainer.innerHTML = '<canvas id="functionChart"></canvas>';
        let ctx = document.getElementById('functionChart').getContext('2d');

        // Создаем график
        functionChart = new Chart(ctx, {
            type: 'line',
            data: {
                datasets: [{
                    label: func.ownership?.funcName || 'Функция',
                    data: chartData,
                    borderColor: '#3498db',
                    backgroundColor: 'rgba(52, 152, 219, 0.1)',
                    borderWidth: 2,
                    tension: 0.3,
                    pointRadius: 2,
                    pointBackgroundColor: '#3498db'
                }]
            },
            options: {
                plugins: {
                    title: {
                        display: true,
                        text: `График: ${func.ownership?.funcName || 'Без названия'}`
                    },
                    legend: {
                        display: false
                    }
                },
                scales: {
                    x: {
                        type: 'linear',
                        offset: true,
                        title: { display: true, text: 'X' },
                        grid: { color: 'rgba(0,0,0,0.1)' },
                        min: currentChartRange.xMin,
                        max: currentChartRange.xMax
                    },
                    y: {
                        offset: true,
                        title: { display: true, text: 'Y' },
                        grid: { color: 'rgba(0,0,0,0.1)' },
                        min: currentChartRange.yMin,
                        max: currentChartRange.yMax
                    }
                }
            }
        });

    } catch (error) {
        console.error('Failed to plot function:', error);
        graphContainer.innerHTML = `
            <div style="display: flex; align-items: center; justify-content: center; height: 300px; color: #e74c3c;">
                Ошибка при построении графика: ${error.message}
            </div>
        `;
    }
}

// Универсальная генерация данных для всех типов функций
async function generateUniversalFunctionData(func) {
    let funcType = func.function.funcType;
    let expression = func.function.expression;

    console.log(`Generating data for ${funcType} function:`, expression);

    // Для табулированных функций используем точки из API
    if (funcType === 'tabulated' || funcType === 'pure') {
        return await generateTabulatedData(func);
    }

    // Для математических и композитных функций генерируем данные локально
    return await generateCalculatedData(func, expression);
}

// Генерация данных для табулированных функций
async function generateTabulatedData(func) {
    let funcId = func.function.funcId;

    try {
        let points = await api.getPoints(funcId);

        if (!points || !points.xValues || points.xValues.length === 0) {
            throw new Error('Нет данных точек');
        }

        let labels = points.xValues.map(x => parseFloat(x.toFixed(2)));
        let values = points.yValues.map(y => parseFloat(y));
        let pts = [];
        for (let i = 0; i < labels.length; i++) {
            pts.push([labels[i], values[i]]);
        }

        console.log('Tabulated data loaded:', pts);
        return pts;

    } catch (error) {
        console.error('Error loading tabulated data:', error);
        throw new Error('Не удалось загрузить точки функции');
    }
}

// Генерация данных для математических и композитных функций
async function generateCalculatedData(func, expression) {
    let xMin = currentChartRange.xMin;
    let xMax = currentChartRange.xMax;
    let pointCount = currentChartRange.points;
    let pts = [];

    let gotPoints = await api.calculateFunctionRange(func.function.funcId, xMin, xMax, pointCount);
    for (let i = 0; i < pointCount; i++) {
        pts.push([gotPoints.xValues[i], gotPoints.yValues[i]]);
    }

    console.log('Calculated data:', pts);
    return pts;
}

// Добавьте CSS для анимации загрузки
let style = document.createElement('style');
style.textContent = `
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
`;
document.head.appendChild(style);

async function openFunctionModal(func) {
    console.log('Opening function modal with data:', func);
    currentViewedFunction = func;

    // Initialize range inputs
    document.getElementById('xMin').value = currentChartRange.xMin;
    document.getElementById('xMax').value = currentChartRange.xMax;
    document.getElementById('yMin').value = currentChartRange.yMin;
    document.getElementById('yMax').value = currentChartRange.yMax;

    // Безопасное извлечение данных с проверкой структуры
    let funcName, funcId, funcType, createdDate, expression;

    funcName = func.ownership.funcName;
    funcId = func.function.funcId;
    funcType = func.function.funcType;
    createdDate = func.ownership.createdDate;
    expression = func.function.expression;

    console.log('Extracted data:', { funcName, funcId, funcType, createdDate, expression });

    document.getElementById('modalFunctionName').textContent = funcName;
    document.getElementById('modalFunctionId').textContent = funcId;
    document.getElementById('modalFunctionType').textContent = getFunctionTypeLabel(funcType);
    document.getElementById('modalFunctionStatus').textContent = 'Активна';
    document.getElementById('modalFunctionCreated').textContent = formatDate(createdDate);
    document.getElementById('modalFunctionExpression').textContent = funcType == 'pure' ? "Табулированная Функция" : expression;

    if (funcType === 'composite') {
        let compositeData = await api.getComposite(funcId);
        let innerData = currentFunctions.find((fun) => fun.function.funcId == compositeData.innerId);
        let outerData = currentFunctions.find((fun) => fun.function.funcId == compositeData.outerId);
        // Для композитных функций показываем информацию о составляющих
        let expressionText = `Композитная функция f(g(x)): g(x)=${innerData.function.expression}, f(x)=${outerData.function.expression}`;
        document.getElementById('modalFunctionExpression').textContent = expressionText;
    } else {
        // Показываем кнопку редактирования для обычных функций
        let editBtn = document.getElementById('editFunctionBtn');
        if (editBtn) {
            editBtn.style.display = 'block';
        }
    }

    document.getElementById('functionModal').style.display = 'block';

    if (funcType === 'tabulated' || funcType == 'pure') {
        document.getElementById('pointsInput').classList.add('hidden');
    } else {
        document.getElementById('pointsInput').classList.remove('hidden');
    }
    // Добавляем небольшую задержку для инициализации canvas
    setTimeout(() => {
        plotFunction(func, true);
    }, 100);
}

function openNuclearLaunchMenu() {
    if (!nuclearLaunchEnabled) return;
    document.getElementById('nuclearLaunchModal').style.display = 'block';
    startNuclearVisuals();
}

function closeNuclearModal() {
    document.getElementById('nuclearLaunchModal').style.display = 'none';
    stopNuclearVisuals();
}

function startNuclearVisuals() {
    const container = document.getElementById('nuclearVisuals');// Создаем отдельный контейнер для частиц, не удаляя существующий контент
    let particlesContainer = document.getElementById('nuclearParticles');
    if (!particlesContainer) {
        particlesContainer = document.createElement('div');
        particlesContainer.id = 'nuclearParticles';
        particlesContainer.className = 'nuclear-particles';
        container.appendChild(particlesContainer);
    }

    particlesContainer.innerHTML = '';

    // Создаем анимированные частицы
    for (let i = 0; i < 50; i++) {
        const particle = document.createElement('div');
        particle.style.cssText = `
            position: absolute;
            width: 2px;
            height: 2px;
            background: #ff0000;
            border-radius: 50%;
            animation: nuclearFloat ${Math.random() * 3 + 2}s infinite linear;
            left: ${Math.random() * 100}%;
            top: ${Math.random() * 100}%;
            z-index: 1;
        `;
        particlesContainer.appendChild(particle);
    }

    // Добавляем CSS анимацию если еще нет
    if (!document.getElementById('nuclearStyles')) {
        const style = document.createElement('style');
        style.id = 'nuclearStyles';
        style.textContent = `
            @keyframes nuclearFloat {
                0% { transform: translateY(0) rotate(0deg); opacity: 1; }
                50% { opacity: 0.5; }
                100% { transform: translateY(-100px) rotate(360deg); opacity: 0; }
            }
            @keyframes nuclearPulse {
                0% { box-shadow: 0 0 0 0 rgba(255, 0, 0, 0.7); }
                70% { box-shadow: 0 0 0 30px rgba(255, 0, 0, 0); }
                100% { box-shadow: 0 0 0 0 rgba(255, 0, 0, 0); }
            }
            .nuclear-warning { animation: nuclearPulse 2s infinite; }
            .nuclear-particles {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                pointer-events: none;
                z-index: 1;
            }
            .nuclear-content {
                position: relative;
                z-index: 2;
            }
        `;
        document.head.appendChild(style);
    }
}
function stopNuclearVisuals() {
    const particlesContainer = document.getElementById('nuclearParticles');
    if (particlesContainer) {
        particlesContainer.innerHTML = '';
    }
}

function initiateNuclearLaunch() {
    const target = document.getElementById('nuclearTarget').value;
    const code = document.getElementById('launchCode').value;
    if (!target || !code) {
        showNuclearStatus('❌ ОШИБКА: Заполните все поля!', 'error');
        return;
    }

    if (code !== '1337') {
        showNuclearStatus('❌ ОШИБКА: Неверный код авторизации!', 'error');
        return;
    }

    showNuclearStatus('🚀 ЗАПУСК ЯДЕРНОЙ РАКЕТЫ...', 'launching');

    // Симуляция обратного отсчета
    let countdown = 5;
    const countdownInterval = setInterval(() => {
        if (countdown > 0) {
            showNuclearStatus(`🚀 ЗАПУСК ЧЕРЕЗ ${countdown}...`, 'countdown');
            countdown--;
        } else {
            clearInterval(countdownInterval);
            showNuclearStatus('💥 ЯДЕРНАЯ РАКЕТА УСПЕШНО ЗАПУЩЕНА!', 'launched');
            document.getElementById('nuclearLaunchBtn').disabled = true;

            // Добавляем финальную анимацию без удаления контента
            const visuals = document.getElementById('nuclearVisuals');
            visuals.style.background = 'radial-gradient(circle, #ff0000, #8b0000, #000000)';
            visuals.style.animation = 'pulse 0.5s infinite';

            // Усиливаем эффект частиц
            const particlesContainer = document.getElementById('nuclearParticles');
            if (particlesContainer) {
                particlesContainer.style.background = 'radial-gradient(circle, rgba(255,0,0,0.3), transparent)';
            }
        }
    }, 1000);
}

function showNuclearStatus(message, type) {
    const status = document.getElementById('nuclearStatus');
    status.textContent = message;
    status.className = 'nuclear-status ' + type;
    if (type === 'launched') {
        status.style.color = '#ff0000';
        status.style.fontSize = '24px';
        status.style.fontWeight = 'bold';
    }
}

function abortNuclearLaunch() {
    showNuclearStatus('⚠️ ЗАПУСК ОТМЕНЕН', 'aborted');
    setTimeout(() => {
    closeNuclearModal();
    }, 2000);
}

