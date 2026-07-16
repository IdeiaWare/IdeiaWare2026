<%
    Boolean tokenInvalido = (Boolean) request.getAttribute("tokenInvalido");
    Boolean senhasNaoConferem = (Boolean) request.getAttribute("senhasNaoConferem");
    String token = (String) request.getAttribute("token");
%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Redefinir senha</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
        <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900'>
        <%-- TITULO-MODULO-POPPINS: Montserrat nunca era usada em lugar nenhum -- trocada pela Poppins (mesma fonte dos titulos de modulo). --%>
        <link href='https://fonts.googleapis.com/css2?family=Poppins:wght@600' rel='stylesheet'>
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/style.css">
        <link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
        <script src="js/materialize.min.js"></script>
    </head>
    <style>
        .title-app {
            color:#fff;
            font-family: 'Condiment';
            font-size: 42px;
            line-height: 25px
        }
        .title-app2 {
            color:#fff;
            font-family: 'Condiment';
            font-size: 100px;
        }
        .password-strength {
            height: 10px;
            border-radius: 5px;
            margin-top: 10px;
            background-color: #e0e0e0;
            overflow: hidden;
        }
        .strength-bar {
            height: 100%;
            width: 0;
            background-color: red;
            transition: width 0.3s;
        }
        .strength-text {
            margin-top: 10px;
            text-align: center;
            font-weight: bold;
        }
        .criterio {
            list-style: none;
            padding: 0;
            margin: 10px 0 0 0;
            font-size: 11px;
        }
        .criterio li {
            color: red;
        }
        .criterio li.valid {
            color: green;
        }
        .perfil-label {
            display: block;
            text-align: left;
            font-size: 13px;
            color: #607d8b;
            font-weight: 500;
            margin: 14px 0 2px 0;
        }
    </style>

    <body class="blue-grey lighten-5" id="app">
    <main>
        <nav>
            <div class="nav-wrapper blue-grey lighten-1 z-depth-2">
                <a  class="brand-logo" style="left: 50px">
                    <ul style="width:300px" id="nav-mobile" class="left hide-on-med-and-down">
                        <div class="row" style="padding-left: 10px">
                            <div class="col s1 blue-grey lighten-3" style=" width: 50px;  height: 50px;
                                 margin-top: 5px;  padding: 6px 6px;
                                 border-radius: 100%;  box-sizing: border-box;">
                                <img style="display: block;  width: 62%;  margin-left: 7px; margin-top: -1px" src="imagens/idea.png" alt="IdeiaWare"/>
                            </div>
                            <h1 class=" col s4 center-align title-app">IdeiaWare</h1>
                        </div>
                    </ul>
                </a>

            </div>
        </nav>
        <div class="container">
            <div class="info">
                <h4 class=" blue-grey-text text-darken-2 title-app2">IdeiaWare</h4>
                <h5 class="text-darken-2 blue-grey-text" style="font-family:'Poppins',sans-serif; font-weight:600;">Redefinir senha</h5>
            </div>
        </div>
        <div class="form">
            <div class="thumbnail">
                <img src="imagens/idea.png" alt="IdeiaWare"/>
            </div>

            <c:if test="${tokenInvalido}">
                <div class="erro">
                    <h6>Este link de redefinição de senha é inválido ou já expirou.</h6>
                </div>
                <p class="message"><a href="login.jsp">Voltar ao login</a></p>
            </c:if>

            <c:if test="${not tokenInvalido}">
                <c:if test="${senhasNaoConferem}">
                    <div class="erro">
                        <h6>A senha e a confirmação não coincidem.</h6>
                    </div>
                </c:if>

                <form id="redefinir-senha-form" action="RedefinirSenhaServlet" method="POST">
                    <input type="hidden" name="token" value="<%= token %>"/>

                    <%-- UX: mesmo "olhinho" pra revelar senha usado no login/cadastro. --%>
                    <div style="position:relative;">
                        <input name="senha" id="senha" type="password" placeholder="nova senha" aria-label="nova senha"
                               pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])\S{8,32}$" required
                               title="O campo senha deve conter entre 8 e 32 caracteres. E os 5 requisitos abaixo." maxlength="32" oninput="checkPasswordStrength()"/>
                        <button type="button" id="toggle-nova-senha" tabindex="-1" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('senha','toggle-nova-senha')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                    </div>

                    <label for="reg-senha2" class="perfil-label">Confirmar senha</label>
                    <div style="position:relative;">
                        <input id="reg-senha2" name="senha2" type="password" placeholder="digite a senha novamente" aria-label="digite a senha novamente" pattern=".{4,32}" required title="O campo repetir senha deve conter entre 4 e 32 caracteres" />
                        <button type="button" id="toggle-confirma-senha" tabindex="-1" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('reg-senha2','toggle-confirma-senha')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                    </div>

                    <%-- SENHA-FORCA-PRISTINE: barra/texto so aparecem depois que o usuario comeca a digitar (senao nasce em vermelho "Muito fraca" sem culpa do usuario). --%>
                    <div id="strength-meter" style="display:none;">
                        <div class="password-strength">
                            <div class="strength-bar" id="strength-bar"></div>
                        </div>
                        <div class="strength-text" id="strength-text"></div>
                    </div>
                    <ul class="criterio">
                        <li id="length-criterio">Pelo menos 8 caracteres</li>
                        <li id="uppercase-criterio">Pelo menos uma letra maiúscula</li>
                        <li id="lowercase-criterio">Pelo menos uma letra minúscula</li>
                        <li id="number-criterio">Pelo menos um número</li>
                        <li id="special-criterio">Pelo menos um caractere especial</li>
                    </ul>

                    <button class="blue accent-1" type="submit" style="margin-top:20px;">Redefinir senha</button>
                    <p class="message"><a href="login.jsp">Voltar ao login</a></p>
                </form>
            </c:if>
        </div>

        <script>
            // UX: "olhinho" pra revelar/ocultar a senha digitada (mesma funcao do login/cadastro).
            function toggleSenhaVisibility(inputId, buttonId) {
                var input = document.getElementById(inputId);
                var icon = document.getElementById(buttonId).querySelector('i');
                if (input.type === 'password') {
                    input.type = 'text';
                    icon.textContent = 'visibility';
                } else {
                    input.type = 'password';
                    icon.textContent = 'visibility_off';
                }
            }

            function checkPasswordStrength() {
                const password = document.getElementById('senha').value;
                const strengthBar = document.getElementById('strength-bar');
                const strengthText = document.getElementById('strength-text');
                document.getElementById('strength-meter').style.display = password.length > 0 ? 'block' : 'none';
                let strength = 0;

                const lengthcriterio = document.getElementById('length-criterio');
                const uppercasecriterio = document.getElementById('uppercase-criterio');
                const lowercasecriterio = document.getElementById('lowercase-criterio');
                const numbercriterio = document.getElementById('number-criterio');
                const specialcriterio = document.getElementById('special-criterio');

                lengthcriterio.classList.remove('valid');
                uppercasecriterio.classList.remove('valid');
                lowercasecriterio.classList.remove('valid');
                numbercriterio.classList.remove('valid');
                specialcriterio.classList.remove('valid');

                if (password.length >= 8) {
                    strength += 1;
                    lengthcriterio.classList.add('valid');
                }
                if (/[A-Z]/.test(password)) {
                    strength += 1;
                    uppercasecriterio.classList.add('valid');
                }
                if (/[a-z]/.test(password)) {
                    strength += 1;
                    lowercasecriterio.classList.add('valid');
                }
                if (/[0-9]/.test(password)) {
                    strength += 1;
                    numbercriterio.classList.add('valid');
                }
                if (/[^A-Za-z0-9]/.test(password)) {
                    strength += 1;
                    specialcriterio.classList.add('valid');
                }

                let strengthPercentage = (strength / 5) * 100;
                let strengthColor = '';
                let strengthMsg = '';

                switch (strength) {
                    case 0:
                    case 1:
                        strengthColor = 'red';
                        strengthMsg = 'Muito Fraca';
                        break;
                    case 2:
                        strengthColor = 'orange';
                        strengthMsg = 'Fraca';
                        break;
                    case 3:
                        strengthColor = 'yellow';
                        strengthMsg = 'Média';
                        break;
                    case 4:
                        strengthColor = 'lightgreen';
                        strengthMsg = 'Forte';
                        break;
                    case 5:
                        strengthColor = 'green';
                        strengthMsg = 'Muito Forte';
                        break;
                }

                strengthBar.style.width = strengthPercentage + '%';
                strengthBar.style.backgroundColor = strengthColor;
                strengthText.textContent = strengthMsg;
            }
        </script>
        <script src="js/csrf.js"></script>
    </main>
    </body>
</html>
