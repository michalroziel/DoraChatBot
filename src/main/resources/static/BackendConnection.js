let currentConversationId = null;
/**
 * Main entry point. Attaches an event listener to the DOMContentLoaded event.
 * Once the DOM is fully loaded, it finds the message form and attaches a submit event listener.
 */
document.addEventListener('DOMContentLoaded', () => {
    const messageForm = document.getElementById('message-form');
    const input = document.getElementById("message-input");
    const messageArea = document.getElementById("message-area");
    const newChatButton = document.getElementById("new-chat-button");

        messageForm.addEventListener('submit', (event) => {
            event.preventDefault(); // Prevent the default form submission
            handleSendMessage(input, messageArea);
        });

        newChatButton.addEventListener('click', () => {
            startNewChat();
        });
        loadChatHistory();
});
/**
 * Resets the chat interface for a new conversation.
 */
function startNewChat() {
    currentConversationId = null;
    const messageArea = document.getElementById("message-area");
    const input = document.getElementById("message-input");
    clearArea(messageArea);
    input.value = '';
    console.log("Started new chat session.");
}
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

async function loadChatHistory() {
    try {
        const response = await fetch('/api/chat/history');
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const history = await response.json();
        const historyList = document.getElementById('chat-history');
        historyList.innerHTML = ''; // Clear previous list

        history.forEach(conversation => {
            const listItem = document.createElement('li');
            const link = document.createElement('a');
            link.href = `#`;
            link.textContent = conversation.title;
            link.dataset.conversationId = conversation.id;

            link.onclick = (event) => {
                event.preventDefault();
                loadChatConversation(conversation.id); // Call the new function
            };

            listItem.appendChild(link);
            historyList.appendChild(listItem);
        });
    } catch (error) {
        console.error("Could not load chat history:", error);
    }
}

/**
 * Fetches a specific conversation from the server and displays its messages.
 * @param {number} conversationId - The ID of the conversation to load.
 */
async function loadChatConversation(conversationId) {
    const messageArea = document.getElementById("message-area");
    clearArea(messageArea); // Clear current messages

    try {
        const response = await fetch(`/api/chat/conversation/${conversationId}`);
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        const messages = await response.json();

        messages.forEach(message => {
            // Use the role from the server to apply the correct CSS class
            const messageClass = message.role.toLocaleLowerCase() === 'user' ? 'user-message' : 'bot-message';
            const messageElement = createMessageElement(message.content, [messageClass]);
            appendMessage(messageElement, messageArea);
        });
        currentConversationId = conversationId; // Set the active conversation

    } catch (error) {
        console.error(`Could not load conversation ${conversationId}:`, error);
        const errorMessage = createMessageElement("Error: Could not load chat.", ["bot-message"]);
        appendMessage(errorMessage, messageArea);
    }
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
 * @param {string|null} conversationId - The ID of the current conversation, or null for a new one.
 * @returns {Promise<object>} A promise that resolves with the server's JSON response.
 * @throws {Error} If the network response is not ok.
 */
async function sendMessageToServer(message, conversationId) {
    const response = await fetch('/api/chat/message', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: message, conversationId: conversationId }),
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }

    return await response.json();
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
        // 4. Send the message and conversation ID to the server.
        const response = await sendMessageToServer(messageToSend, currentConversationId);
        const botMessage = createMessageElement(response.content, ["bot-message"]);

        // 5. Update conversation ID and reload history if it's a new chat.
        if (!currentConversationId) {
            currentConversationId = response.conversationId;
            await loadChatHistory();
        }

        // 6. Remove the typing indicator and display the bot's message.
        removeTypingIndicator(messageAreaEl);
        appendMessage(botMessage, messageAreaEl);
    } catch (error) {
        console.error("Error:", error);
        const errorMessage = createMessageElement("Error: Could not connect to the server.", ["bot-message"]);

        // 7. If an error occurs, remove the indicator and show an error message.
        removeTypingIndicator(messageAreaEl);
        appendMessage(errorMessage, messageAreaEl);
    }
}