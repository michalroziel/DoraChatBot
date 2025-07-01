/**
 * Main entry point. Attaches an event listener to the DOMContentLoaded event.
 * Once the DOM is fully loaded, it finds the message form and attaches a submit event listener.
 */
document.addEventListener('DOMContentLoaded', () => {
    const messageForm = document.getElementById('message-form');
    const input = document.getElementById("message-input");
    const messageArea = document.getElementById("message-area");
    const newChatButton = document.getElementById("new-chat-button");

    if (messageForm && input && messageArea) {
        messageForm.addEventListener('submit', (event) => {
            event.preventDefault(); // Prevent the default form submission
            handleSendMessage(input, messageArea);
        });
    }


    if (newChatButton && messageArea) {
        newChatButton.addEventListener('click', () => {
            clearArea(messageArea);
        });
    }
});

/**
 * Clears all messages from the chat container.
 * @param {HTMLElement} container - The message container to clear.
 */
function clearArea(container) {
    container.innerHTML = '';
}

/**
 * Generates a random 128-bit UUID and returns it as a hex string.
 * @returns {string} A 32-character hex string representing the UUID.
 */
function generateLongUuid() {
    const array = new Uint8Array(16);
    window.crypto.getRandomValues(array);
    return Array.from(array, b => b.toString(16).padStart(2, '0')).join('');
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
function appendMessage(element, container) {
    container.appendChild(element);
    scrollToBottom(container);
}

/**
 * Scrolls a container element to its bottom.
 * @param {HTMLElement} container - The element to scroll.
 */
function scrollToBottom(container) {
    container.scrollTop = container.scrollHeight;
}

/**
 * Displays a typing indicator in the message container.
 * Does nothing if an indicator is already present.
 * @param {HTMLElement} container - The message container.
 */
function showTypingIndicator(container) {
    if (container.querySelector('.typing-indicator')) return;

    const indicator = document.createElement("div");
    indicator.classList.add("message", "bot-message", "typing-indicator");
    indicator.innerHTML = `<span></span><span></span><span></span>`;
    appendMessage(indicator, container);
}

/**
 * Removes the typing indicator from the message container.
 * @param {HTMLElement} container - The message container.
 */
function removeTypingIndicator(container) {
    const indicator = container.querySelector('.typing-indicator');
    if (indicator) indicator.remove();
}

/**
 * Sends a message to the server via a POST request.
 * @param {string} message - The message to send.
 * @returns {Promise<string>} A promise that resolves with the server's text response.
 * @throws {Error} If the network response is not ok.
 */
async function sendMessageToServer(message) {
    const response = await fetch('/message', {
        method: 'POST',
        headers: { 'Content-Type': 'text/plain' },
        body: message,
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    return await response.text();
}

/**
 * Handles the process of sending a message from the user.
 * It displays the user's message, sends it to the server,
 * and then displays the server's response.
 * @param {HTMLInputElement} inputEl - The input element containing the user's message.
 * @param {HTMLElement} messageAreaEl - The container where messages are displayed.
 */
async function handleSendMessage(inputEl, messageAreaEl) {
    const messageText = inputEl.value.trim();
    if (!messageText) return; // Don't send empty messages

    // 1. Display user's message immediately.
    const userMessage = createMessageElement(messageText, ["user-message"]);
    appendMessage(userMessage, messageAreaEl);

    // 2. Clear the input field.
    const messageToSend = inputEl.value;
    inputEl.value = "";

    // 3. Show a typing indicator while waiting for the server.
    showTypingIndicator(messageAreaEl);

    try {
        // 4. Send the message to the server and wait for the response.
        const botResponse = await sendMessageToServer(messageToSend);
        const botMessage = createMessageElement(botResponse, ["bot-message"]);

        // 5. Remove the typing indicator and display the bot's message.
        removeTypingIndicator(messageAreaEl);
        appendMessage(botMessage, messageAreaEl);
    } catch (error) {
        console.error("Error:", error);
        const errorMessage = createMessageElement("Error: Could not connect to the server.", ["bot-message"]);

        // 6. If an error occurs, remove the indicator and show an error message.
        removeTypingIndicator(messageAreaEl);
        appendMessage(errorMessage, messageAreaEl);
    }
}