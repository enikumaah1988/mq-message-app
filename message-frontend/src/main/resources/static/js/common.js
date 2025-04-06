// 文字数カウント機能
function updateCharCount(textarea) {
    const maxLength = textarea.maxLength;
    const currentLength = textarea.value.length;
    const remaining = maxLength - currentLength;
    document.getElementById('charCount').textContent = remaining;
}

// ページ読み込み時に初期カウントを設定
document.addEventListener('DOMContentLoaded', function() {
    const textarea = document.getElementById('content');
    if (textarea) {
        updateCharCount(textarea);
    }
}); 