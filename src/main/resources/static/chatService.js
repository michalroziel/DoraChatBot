import * as api from './api.js';
import * as ui from './ui.js';

let currentConversationId = null;

/**
 * Loads the chat history from the API and renders it in the UI.
 * @param {HTMLElement} historyListElement - The UL element for the history.
 * @param {HTMLElement} messageAreaElement - The main message area.
 */
export async function loadAndDisplayHistory(historyListElement, messageAreaElement) {
    try {
        const history = await api.fetchChatHistory();
        ui.renderHistory(history, historyListElement, (conversationId) => {
            loadAndDisplayConversation(conversationId, messageAreaElement);
        });
    } catch (error) {
        console.error("Could not load chat history:", error);
    }
}

/**
 * Loads a specific conversation and displays its messages.
 * @param {number} conversationId - The ID of the conversation to load.
 * @param {HTMLElement} messageAreaElement - The message container.
 */
export async function loadAndDisplayConversation(conversationId, messageAreaElement) {
    ui.clearContainer(messageAreaElement);
    try {
        const messages = await api.fetchConversation(conversationId);
        messages.forEach(message => {
            const displayFn = message.role.toLowerCase() === 'user' ? ui.displayUserMessage : ui.displayBotMessage;
            displayFn(message.content, messageAreaElement);
        });
        currentConversationId = conversationId;
    } catch (error) {
        console.error(`Could not load conversation ${conversationId}:`, error);
        ui.displayBotMessage("Error: Could not load chat.", messageAreaElement);
    }
}

/**
 * Handles the entire process of sending a message and displaying the response.
 * @param {HTMLInputElement} inputElement - The input field.
 * @param {HTMLElement} messageAreaElement - The message container.
 * @param {HTMLElement} historyListElement - The history list container.
 */
export async function handleSendMessage(inputElement, messageAreaElement, historyListElement) {
    const messageText = inputElement.value.trim();
    if (!messageText) return;

    ui.displayUserMessage(messageText, messageAreaElement);
    const messageToSend = inputElement.value;
    inputElement.value = "";
    ui.showTypingIndicator(messageAreaElement);

    try {
        const response = await api.postMessage(messageToSend, currentConversationId);

        if (!currentConversationId) {
            currentConversationId = response.conversationId;
            await loadAndDisplayHistory(historyListElement, messageAreaElement);
        }

        ui.removeTypingIndicator(messageAreaElement);
        ui.displayBotMessage(response.content, messageAreaElement);
    } catch (error) {
        console.error("Error:", error);
        ui.removeTypingIndicator(messageAreaElement);
        ui.displayBotMessage("Error: Could not connect to the server.", messageAreaElement);
    }
}

/**
 * Resets the chat to a new conversation state.
 * @param {HTMLElement} messageAreaElement - The message container.
 * @param {HTMLInputElement} inputElement - The input field.
 */
export function startNewChat(messageAreaElement, inputElement) {
    currentConversationId = null;
    ui.clearContainer(messageAreaElement);
    inputElement.value = '';
    console.log("Started new chat session.");
}