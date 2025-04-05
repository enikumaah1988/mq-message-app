class FeedbackManager {
    constructor(feedbackId, feedbackTextId) {
        this.feedback = document.getElementById(feedbackId);
        this.feedbackText = document.getElementById(feedbackTextId);
    }

    show(message, isSuccess) {
        this.feedback.className = 'feedback ' + (isSuccess ? 'success' : 'error');
        this.feedbackText.textContent = message;
        this.feedback.style.display = 'block';
        
        setTimeout(() => {
            this.hide();
        }, config.feedback.timeout);
    }

    hide() {
        this.feedback.style.display = 'none';
    }
}

const feedbackManager = new FeedbackManager('feedback', 'feedbackText'); 