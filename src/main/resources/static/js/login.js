const loginForm = document.getElementById("login-form");
const loginButton = document.getElementById("login-button");
const resultElement = document.getElementById("result");
const inputPassSalt = document.querySelector(".login-card").dataset.inputPassSalt;

function showResult(message, type) {
    resultElement.textContent = message;
    resultElement.className = "result " + type;
    resultElement.style.display = "block";
}

loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const mobile = document.getElementById("mobile").value.trim();
    const password = document.getElementById("password").value;

    if (!mobile || !password) {
        showResult("请输入手机号和密码。", "error");
        return;
    }

    loginButton.disabled = true;
    loginButton.textContent = "提交中...";

    try {
        if (!inputPassSalt) {
            throw new Error("未获取到固定盐值");
        }

        const formPassword = inputPassToFormPass(password, inputPassSalt);
        const response = await fetch("/login/doLogin", {
            method: "POST",
            headers: {
                "Accept": "application/json",
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ mobile: mobile, password: formPassword })
        });
        const result = await response.json();

        if (!response.ok || result.code !== 200) {
            showResult(result.message || "参数提交失败。", "error");
            return;
        }

        window.location.assign("/goods/toList");
    } catch (error) {
        showResult("请求失败，请确认应用已启动。", "error");
    } finally {
        loginButton.disabled = false;
        loginButton.textContent = "登录";
    }
});

function inputPassToFormPass(inputPass, salt) {
    const source = "" +salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
    return md5(source);
}
