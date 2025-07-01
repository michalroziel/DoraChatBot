document.addEventListener('DOMContentLoaded', () => {
    const uuid = generateLongUuid();
    const messageForm = document.getElementById('message-form');

    if (messageForm) {
        messageForm.addEventListener('submit', (event) => {
            event.preventDefault();
            send();
        });
    }
});

function generateLongUuid() {
    const array = new Uint8Array(16);
    window.crypto.getRandomValues(array);
    return Array.from(array, b => b.toString(16).padStart(2, '0')).join('');
}

function send() {
    const input = document.getElementById("message-input");
    const messageArea = document.getElementById("message-area");
    const messageText = input.value.trim();

    if (messageText === "") {
        return;
    }

    // Create and display user message
    const userMessage = document.createElement("div");
    userMessage.classList.add("message", "user-message");
    userMessage.textContent = messageText;
    messageArea.appendChild(userMessage);

    const messageToSend = input.value;
    input.value = ""; // Clear input field
    messageArea.scrollTop = messageArea.scrollHeight; // Scroll down

    // Show typing indicator
    showTypingIndicator();

    // Send message to the backend
    fetch('/message', {
        method: 'POST',
        headers: {
            'Content-Type': 'text/plain',
        },
        body: messageToSend,
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.text(); // Read the response body as text
        })
        .then(textData => {
            // Remove typing indicator
            removeTypingIndicator();
            // Create and display bot response
            const botMessage = document.createElement("div");
            botMessage.classList.add("message", "bot-message");
            botMessage.textContent = textData; // Use the text content from the response
            messageArea.appendChild(botMessage);
            messageArea.scrollTop = messageArea.scrollHeight; // Scroll to the bottom
        })
        .catch(error => {
            console.error('Error:', error);
            removeTypingIndicator();
            const errorMessage = document.createElement("div");
            errorMessage.classList.add("message", "bot-message");
            errorMessage.textContent = "Error: Could not connect to the server.";
            messageArea.appendChild(errorMessage);
            messageArea.scrollTop = messageArea.scrollHeight;
        });
}

function showTypingIndicator() {
    const messageArea = document.getElementById("message-area");
    if (document.querySelector('.typing-indicator')) return; // Don't add if already present

    const typingIndicator = document.createElement("div");
    typingIndicator.classList.add("message", "bot-message", "typing-indicator");
    typingIndicator.innerHTML = `<span></span><span></span><span></span>`;
    messageArea.appendChild(typingIndicator);
    messageArea.scrollTop = messageArea.scrollHeight;
}

function removeTypingIndicator() {
    const typingIndicator = document.querySelector('.typing-indicator');
    if (typingIndicator) {
        typingIndicator.remove();
    }
}