/**
 * Retrieves essential DOM elements from the page.
 * @returns {object} An object containing the DOM elements.
 */
export function getDOMElements() {
    return {
        messageForm: document.getElementById('message-form'),
        input: document.getElementById("message-input"),
        messageArea: document.getElementById("message-area"),
        newChatButton: document.getElementById("new-chat-button"),
        historyList: document.getElementById('chat-history')
    };
}

/**
 * Creates a new div element for a message.
 * @param {string} text - The text content of the message.
 * @param {string[]} [classes=[]] - An array of CSS classes to add to the message element.
 * @returns {HTMLDivElement} The created message element.
 */
function createMessageElement(text, classes = []) {
    const el = document.createElement("div");
    el.classList.add("message", ...classes);
    el.textContent = text;
    return el;
}

/**
 * Appends a message element to a container and scrolls the container to the bottom.
 * @param {HTMLElement} element - The message element to append.
 * @param {HTMLElement} container - The container to append the message to.
 */
export function appendMessage(element, container) {
    container.appendChild(element);
    container.scrollTop = container.scrollHeight;
}

/**
 * Displays a user message in the chat area.
 * @param {string} text - The user's message.
 * @param {HTMLElement} container - The message container.
 */
export function displayUserMessage(text, container) {
    const userMessage = createMessageElement(text, ["user-message"]);
    appendMessage(userMessage, container);
}

/**
 * Displays a bot message in the chat area.
 * @param {string} text - The bot's message.
 * @param {HTMLElement} container - The message container.
 */
export function displayBotMessage(text, container) {
    const botMessage = createMessageElement(text, ["bot-message"]);
    appendMessage(botMessage, container);
}

/**
 * Clears all child elements from a given container.
 * @param {HTMLElement} container - The container to clear.
 */
export function clearContainer(container) {
    container.innerHTML = '';
}

/**
 * Renders the chat history list.
 * @param {object[]} history - The chat history data.
 * @param {HTMLElement} historyListElement - The UL element to render the history into.
 * @param {function(number)} onConversationClick - Callback function to execute when a history item is clicked.
 */
export function renderHistory(history, historyListElement, onConversationClick) {
    clearContainer(historyListElement);
    history.forEach(conversation => {
        const listItem = document.createElement('li');
        const link = document.createElement('a');
        link.href = `#`;
        link.textContent = conversation.title;
        link.dataset.conversationId = conversation.id;

        link.onclick = (event) => {
            event.preventDefault();
            onConversationClick(conversation.id);
        };

        listItem.appendChild(link);
        historyListElement.appendChild(listItem);
    });
}

/**
 * Shows a typing indicator in the message container.
 * @param {HTMLElement} container - The message container.
 */
export function showTypingIndicator(container) {
    if (container.querySelector('.typing-indicator')) return;
    const indicator = createMessageElement('', ["bot-message", "typing-indicator"]);
    indicator.innerHTML = `<span></span><span></span><span></span>`;
    appendMessage(indicator, container);
}

/**
 * Removes the typing indicator from the message container.
 * @param {HTMLElement} container - The message container.
 */
export function removeTypingIndicator(container) {
    const indicator = container.querySelector('.typing-indicator');
    if (indicator) indicator.remove();
}