/**
 * Fetches the entire chat history from the server.
 * @returns {Promise<object[]>} A promise that resolves with the chat history.
 * @throws {Error} If the network response is not ok.
 */
export async function fetchChatHistory() {
    const response = await fetch('/api/chat/history');
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
}

/**
 * Fetches a specific conversation by its ID.
 * @param {number} conversationId - The ID of the conversation to load.
 * @returns {Promise<object[]>} A promise that resolves with the messages of the conversation.
 * @throws {Error} If the network response is not ok.
 */
export async function fetchConversation(conversationId) {
    const response = await fetch(`/api/chat/conversation/${conversationId}`);
    if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
    }
    return response.json();
}

/**
 * Sends a message to the server.
 * @param {string} message - The message to send.
 * @param {string|null} conversationId - The ID of the current conversation, or null for a new one.
 * @returns {Promise<object>} A promise that resolves with the server's JSON response.
 * @throws {Error} If the network response is not ok.
 */
export async function postMessage(message, conversationId) {
    const response = await fetch('/api/chat/message', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ content: message, conversationId: conversationId }),
    });

    if (!response.ok) {
        throw new Error('Network response was not ok');
    }
    return response.json();
}