class MessageService {
    async sendMessage(content) {
        const response = await fetch(config.api.messages.endpoint, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ content })
        });
        return response.json();
    }
}

const messageService = new MessageService();

async function handleSubmit(event) {
    event.preventDefault();
    const messageInput = document.getElementById('messageInput');
    const message = messageInput.value.trim();
    
    if (!message) return;

    try {
        const result = await messageService.sendMessage(message);
        if (result.success) {
            messageInput.value = '';
            feedbackManager.show(config.feedback.messages.success, true);
        } else {
            feedbackManager.show(result.error, false);
        }
    } catch (error) {
        console.error('Error:', error);
        feedbackManager.show(config.feedback.messages.error, false);
    }
}

function showFeedback(message, isSuccess) {
    const feedback = document.getElementById('feedback');
    const feedbackText = document.getElementById('feedbackText');
    
    feedback.className = 'feedback ' + (isSuccess ? 'success' : 'error');
    feedbackText.textContent = message;
    feedback.style.display = 'block';
    
    // 3秒後にフィードバックを非表示にする
    setTimeout(() => {
        feedback.style.display = 'none';
    }, 3000);
} 