import * as ui from './ui.js';
import * as chatService from './chatService.js';

/**
 * Main entry point. Attaches event listeners after the DOM is fully loaded.
 */
document.addEventListener('DOMContentLoaded', () => {
    const { messageForm, input, messageArea, newChatButton, historyList } = ui.getDOMElements();

    messageForm.addEventListener('submit', (event) => {
        event.preventDefault();
        chatService.handleSendMessage(input, messageArea, historyList);
    });

    newChatButton.addEventListener('click', () => {
        chatService.startNewChat(messageArea, input);
    });

    // Initial load of the chat history
    chatService.loadAndDisplayHistory(historyList, messageArea);
});