document.addEventListener('DOMContentLoaded', () => {
    
     // 1. 診断画面のカード選択の動き（既存）
    const cards = document.querySelectorAll('.concern-card');

    // それぞれのカードにクリックされたときの動きを追加
    cards.forEach(card => {
        card.addEventListener('click', () => {
            // 他のカードの選択を解除して、クリックしたものだけ選択状態にする
            cards.forEach(c => c.classList.remove('active'));
            card.classList.add('active');
        });
    });
    // 2. 診断画面からバックエンドへデータを送る処理
    const continueBtn = document.querySelector('.btn-primary');
    if (continueBtn && window.location.pathname.includes('diagonostic')) {
        continueBtn.removeAttribute('onclick');
        continueBtn.addEventListener('click', async (e) => {
            e.preventDefault(); // デフォルトのボタン動作を止める

            const originalText = continueBtn.textContent;
            continueBtn.textContent = "診断中...";
            continueBtn.disabled = true;
            continueBtn.style.opacity = '0.7';
            // 入力値の取得
            const activeCard = document.querySelector('.concern-card.active');
            const skinType = activeCard ? activeCard.getAttribute('data-concern') : 'unknown';
            const budget = document.getElementById('input-budget') ? document.getElementById('input-budget').value : 0;
            const allergies = document.getElementById('input-allergies') ? document.getElementById('input-allergies').value : "";
            const moisture = document.getElementById('input-moisture') ? document.getElementById('input-moisture').value : 0;

            const budgetNum = parseInt(budget) || 0;
            const moistureNum = parseInt(moisture) || 0;

            if(budgetNum <= 0 || moistureNum < 0 || moistureNum > 100) {
                alert("予算は0以上の数値を、頭皮の水分量は0から100の範囲で入力してください。");
                continueBtn.textContent = originalText;
                continueBtn.disabled = false;
                continueBtn.style.opacity = '1';
                return;
            }
            // バックエンドに渡すJSONデータの作成
            const requestData = {
                monthlyBudget: parseInt(budget) || 0,
                allergies: allergies || "なし",
                skinType: skinType,
                moistureLevel: parseInt(moisture) || 0
            };

            console.log("バックエンドへ送信するデータ:", requestData);

            // --- ここからバックエンド通信（Fetch API） ---
            try {
                // ※ URLはバックエンド側の設定に合わせて後で山田さん達が修正してくれます
                const response = await fetch('/api/recommendations', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(requestData)
                });

                if (response.ok) {
                    const responseData = await response.json();
                    // 受け取った結果をブラウザに一時保存して詳細画面へ渡す
                    if(Array.isArray(responseData) && responseData.length > 0) {
                        localStorage.setItem('recommendResult', JSON.stringify(responseData));
                        window.location.href = 'detail.html';
                    } else {
                        alert("条件に合うシャンプーが0件でした！予算やアレルギーの条件を緩めてみてください。");
                        //window.location.href = 'detail.html';
                        continueBtn.textContent = originalText;
                        continueBtn.disabled = false;
                        continueBtn.style.opacity = '1';
                    }
                }else{
                    alert("バックエンドでエラーが起こりました。"+"ステータス"+response.status);
                    continueBtn.textContent = originalText;
                    continueBtn.disabled = false;
                    continueBtn.style.opacity = '1';
                    }
                
            } catch (error) {
                console.error("通信エラー:", error);
                // バックエンドが立ち上がっていない時のための仮遷移
                continueBtn.textContent = originalText;
                continueBtn.disabled = false;
                continueBtn.style.opacity = '1';
            }
        });
    }

    // 3. 詳細画面でバックエンドからのデータを表示する処理
    if (window.location.pathname.includes('detail')) {
        const savedData = localStorage.getItem('recommendResult');
        if (savedData) {
            const dataArray = JSON.parse(savedData);
            const data = dataArray[0];
            
            // HTMLのIDを指定して、バックエンドから来た文字を流し込む
            if(document.getElementById('display-name')) {
                document.getElementById('display-name').textContent = data.name || "推奨アイテム";
            }
            if(document.getElementById('display-price')) {
                document.getElementById('display-price').textContent = data.price || "---";
            }
            if(document.getElementById('display-ingredients')) {
                document.getElementById('display-ingredients').textContent = data.ingredients || "成分情報なし";
            }
            if(document.getElementById('display-months')) {
                document.getElementById('display-months').textContent = data.replacementIntervalMonths || "1";
            }
        }
    }
});