// 相対時間のフォーマット
function formatRelativeTime(dateStr) {
    const date = new Date(dateStr);
    const now = new Date();
    const diff = Math.floor((now - date) / 1000);

    if (diff < 60) return '今';
    if (diff < 3600) return Math.floor(diff / 60) + '分前';
    if (diff < 86400) return Math.floor(diff / 3600) + '時間前';
    if (diff < 604800) return Math.floor(diff / 86400) + '日前';
    return new Date(dateStr).toLocaleDateString('ja-JP');
}

// 相対時間の更新
function updateRelativeTimes() {
    document.querySelectorAll('.relative-time').forEach(element => {
        const dateStr = element.getAttribute('data-time');
        element.textContent = formatRelativeTime(dateStr);
    });
}

// 削除確認モーダルの表示
function showDeleteConfirmation(messageId, content) {
    const modal = document.getElementById('deleteConfirmModal');
    const messageContent = document.getElementById('deleteMessageContent');
    const form = document.getElementById('deleteForm');
    
    messageContent.textContent = content;
    form.querySelector('input[name="id"]').value = messageId;
    
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}

// Ctrl+Enterでフォーム送信
function handleKeyPress(event) {
    if (event.key === 'Enter' && (event.ctrlKey || event.metaKey)) {
        event.preventDefault();
        const form = event.target.closest('form');
        if (form) {
            form.submit();
        }
    }
}

// 全メッセージの選択/解除
function toggleAllMessages() {
    const checkboxes = document.querySelectorAll('.message-checkbox');
    const selectAllCheckbox = document.getElementById('selectAll');
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
    updateBulkDeleteButton();
}

// 一括削除ボタンの更新
function updateBulkDeleteButton() {
    const selectedCount = document.querySelectorAll('.message-checkbox:checked').length;
    const bulkDeleteBtn = document.getElementById('bulkDeleteBtn');
    if (selectedCount > 0) {
        bulkDeleteBtn.removeAttribute('disabled');
        bulkDeleteBtn.textContent = `選択したメッセージを削除 (${selectedCount}件)`;
    } else {
        bulkDeleteBtn.setAttribute('disabled', 'disabled');
        bulkDeleteBtn.textContent = '選択したメッセージを削除';
    }
}

// 一括削除確認モーダルの表示
function showBulkDeleteConfirmation() {
    const selectedMessages = document.querySelectorAll('.message-checkbox:checked');
    const messageIds = Array.from(selectedMessages).map(cb => cb.value);
    const messageContents = Array.from(selectedMessages).map(cb => 
        cb.closest('.list-group-item').querySelector('.message-content').textContent
    );

    const modal = document.getElementById('bulkDeleteConfirmModal');
    const messageList = document.getElementById('bulkDeleteMessageList');
    const form = document.getElementById('bulkDeleteForm');
    
    messageList.innerHTML = messageContents.map(content => 
        `<div class="alert alert-secondary mb-2">${content}</div>`
    ).join('');
    
    form.querySelector('input[name="ids"]').value = messageIds.join(',');
    
    const bsModal = new bootstrap.Modal(modal);
    bsModal.show();
}

// 文字数カウントの更新
function updateCharCount(textarea) {
    const maxLength = textarea.getAttribute('maxlength');
    const currentLength = textarea.value.length;
    const charCountElement = document.getElementById('charCount');
    if (charCountElement) {
        charCountElement.textContent = maxLength - currentLength;
    }
}

// 初期化処理
document.addEventListener('DOMContentLoaded', function() {
    // 相対時間の初期更新
    updateRelativeTimes();
    // 1分ごとに相対時間を更新
    setInterval(updateRelativeTimes, 60000);

    // テキストエリアの初期文字数カウント
    const textarea = document.querySelector('textarea[name="content"]');
    if (textarea) {
        updateCharCount(textarea);
        textarea.addEventListener('input', function() {
            updateCharCount(this);
        });
    }
}); 