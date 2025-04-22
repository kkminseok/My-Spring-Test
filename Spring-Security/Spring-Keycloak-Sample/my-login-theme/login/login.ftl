<!DOCTYPE html>
<html lang="en">
<head>
    <title>🚀 My Custom Login </title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${url.resourcesPath}/css/custom.css">
</head>
<body>
<div class="login-container">
    <img src="${url.resourcesPath}/img/logo.png" alt="Logo" class="logo"/>

    <h2>Welcome to My Awesome App ${url.resourcesPath}</h2>

    <#if message?has_content>
        <div class="alert ${message.type}">${message.summary}</div>
    </#if>

    <form id="kc-form-login" action="${url.loginAction}" method="post">
        <input type="text" id="username" name="username" placeholder="Username" value="${username!'guest'}" autofocus />
        <input type="password" id="password" name="password" placeholder="Password" />
        <input type="submit" value="Login" />
    </form>

    <p class="footer">Powered by Keycloak ❤️</p>
</div>
</body>
</html>