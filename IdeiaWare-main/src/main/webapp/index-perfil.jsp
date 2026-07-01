<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="edu.unisc.lic.dao.UsuarioDAO"%>
<%@page import="edu.unisc.lic.domain.Usuario"%>

<%
    UsuarioDAO usuarioDAO = new UsuarioDAO();
    Usuario usuario = null;
    if (session.getAttribute("codigoUsuario") == null) {
        response.sendRedirect("login.jsp");
        return; // RET-13: encerra apos o redirect de login (evita 2o sendRedirect e render com usuario null)
    } else {
        usuario = usuarioDAO.buscar((Long) session.getAttribute("codigoUsuario"));
    }

    if (usuario != null) {
        request.setAttribute("nome", usuario.getNome());
        request.setAttribute("usuario", usuario.getUsuario());
        request.setAttribute("email", usuario.getEmail());
    }
    
    Boolean respostaSenhasDiferentes = (Boolean) request.getAttribute("respostaSenhasDiferentes");
    Boolean respostaSenhaInvalida  = (Boolean) request.getAttribute("respostaSenhaInvalida");
    Boolean respostaNomeEmail  = (Boolean) request.getAttribute("respostaNomeEmail");
    Boolean respostaEmailCadastrado  = (Boolean) request.getAttribute("respostaEmailCadastrado");
    Boolean erroAlteracao  = (Boolean) request.getAttribute("erroAlteracao");
    Boolean EnviouEmail  = (Boolean) request.getAttribute("EnviouEmail");
    Boolean ErroEnvioEmail  = (Boolean) request.getAttribute("ErroEnvioEmail");
%>

<!DOCTYPE html>
<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Perfil</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
        <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900'>
        <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Montserrat:400,700'>
        <link rel='stylesheet prefetch' href='https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css'>
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/style.css">
        <link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
        <script src="https://cdn.jsdelivr.net/npm/vue@2"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/1.0.0/js/materialize.min.js"></script>
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
        .card .icon {
            font-size:50px;
        }
        .card-content .card-title {
            color:#fff !important;
        }
        .card a {
            color:#fff;
        }
        .icon .fa-circle {
            color:#fff;
        }
        .new-idea .icon .fa-comments {
            color:#4db6ac;
        }
        .storytelling .icon .fa-pencil {
            color:#5c6bc0
        }
        .toolkit .icon .fa-briefcase {
            color:#c62828;
        }
        .knowledge .icon .fa-database {
            color:#0288d1;
        }
        .fa-hand-o-right {
            border-radius:50%;
            box-shadow: 0 0 0 rgba(255,255,255, 0.4);
            animation: pulse 2s infinite;
        }
        .fa-hand-o-right:hover {
            animation: none;
        }
        .form button{
            margin-top: 10px;
        }
        .perfil-titulo {
            color: #455a64;
            font-weight: 500;
            margin: 10px 0 24px 0;
        }
        .perfil-label {
            display: block;
            text-align: left;
            font-size: 13px;
            color: #607d8b;
            font-weight: 500;
            margin: 14px 0 2px 0;
        }

        @-webkit-keyframes pulse {
            0% {
                -webkit-box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
            }
            70% {
                -webkit-box-shadow: 0 0 0 10px rgba(255,255,255, 0);
            }
            100% {
                -webkit-box-shadow: 0 0 0 0 rgba(255,255,255, 0);
            }
        }
        @keyframes pulse {
            0% {
                -moz-box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
                box-shadow: 0 0 0 0 rgba(255,255,255, 0.4);
            }
            70% {
                -moz-box-shadow: 0 0 0 10px rgba(255,255,255, 0);
                box-shadow: 0 0 0 10px rgba(255,255,255, 0);
            }
            100% {
                -moz-box-shadow: 0 0 0 0 rgba(255,255,255, 0);
                box-shadow: 0 0 0 0 rgba(255,255,255, 0);
            }
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
    </style>

    <body class="blue-grey lighten-5">
        <nav>
            <div class="nav-wrapper blue-grey lighten-1 z-depth-2">
                <a class="brand-logo" href="index.jsp" style="left: 50px">
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
                <ul class="right" style="margin-right: 20px;">
                    <li><a href="LogOutServlet" title="Sair">Sair<i class="fa fa-sign-out" style="margin-left: 8px;"></i></a></li>
                </ul>
            </div>
        </nav>
        <div class="form">
            <h4 class="center-align perfil-titulo">Meu Perfil</h4>
            <c:if test="${respostaSenhasDiferentes}"><div class="erro"><h6>Senhas não batem!</h6></div> </c:if>
            <c:if test="${respostaSenhaInvalida}"><div class="erro"><h6>Senha inválida!</h6></div> </c:if>
            <c:if test="${respostaNomeEmail}"><div class="erro"><h6>Nome ou e-mail inválidos!</h6></div> </c:if>
            <c:if test="${respostaEmailCadastrado}"><div class="erro"><h6>E-mail já utilizado!</h6></div> </c:if>
            <c:if test="${erroAlteracao}"><div class="erro"><h6>Erro ao alterar dados!</h6></div> </c:if>
            <c:if test="${ErroEnvioEmail}"><div class="erro"><h6>Erro ao enviar e-mail!</h6></div> </c:if>
            <c:if test="${EnviouEmail}"><div class="sucesso"><h6>E-mail enviado!</h6></div> </c:if>
            
            <form id="userForm" class="userForm" method="POST">
                <label for="perfilUsuario" class="perfil-label">Usuário</label>
                <input id="perfilUsuario" name="usuario" type="text" placeholder="usuário" aria-label="usuário" value="${usuario}" pattern=".{4.32}" required title="O campo nome de usuario deve conter entre 4 e 32 caracteres" disabled />

                <label for="perfilNome" class="perfil-label">Nome</label>
                <input id="perfilNome" name="nome" type="text" placeholder="nome" aria-label="nome" value="${nome}" pattern=".{4,64}" required title="O campo nome deve conter entre 6 e 64 caracteres" />

                <label for="perfilEmail" class="perfil-label">E-mail</label>
                <input id="perfilEmail" name="email" type="text" placeholder="email" aria-label="email" value="${email}" pattern="\w+(\+?\w+)@\w+(\.\w+)+" required title="email@exemplo.com" maxlength="100"/>
                
                <button class="blue accent-1" href="#" id="show-password-form">Alterar senha</button>
                <button class="blue accent-1" type="submit" onclick="submitForm('EnviaDadosPessoaisServlet','userForm')">Enviar dados pessoais por e-mail</button>
                <button class="blue accent-1" href="#" id="show-anonimiza-form">Anonimizar dados</button>
                <button class="blue accent-1" type="submit" onclick="submitForm('AlteraUsuarioServlet','userForm')">Salvar</button>
            </form>
                
            <form id="passwordForm" class="userForm" method="POST" style="display: none;">
                <input name="usuario" type="text" placeholder="usuário" aria-label="usuário" value="${usuario}" pattern=".{4.32}" required title="O campo nome de usuario deve conter entre 4 e 32 caracteres" disabled />
                
                <input name="senhaAtual" id="senhaAtual" type="password" placeholder="senha atual" aria-label="senha atual" title="O campo senha deve conter entre 8 e 32 caracteres." maxlength="32" onchange="setRequiredPassword()"/>
                <input name="senhaNova" id="senhaNova" type="password" placeholder="senha nova" aria-label="senha nova" pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,32}|^$" title="O campo senha deve conter entre 8 e 32 caracteres. E os 5 requisitos abaixo." maxlength="32" oninput="checkPasswordStrength()" onchange="setRequiredPassword()"/>
                
                <div class="password-strength">
                    <div class="strength-bar" id="strength-bar"></div>
                </div>
                <div class="strength-text" id="strength-text">Muito fraca</div>
                <ul class="criterio">
                    <li id="length-criterio">Pelo menos 8 caracteres</li>
                    <li id="uppercase-criterio">Pelo menos uma letra maiúscula</li>
                    <li id="lowercase-criterio">Pelo menos uma letra minúscula</li>
                    <li id="number-criterio">Pelo menos um número</li>
                    <li id="special-criterio">Pelo menos um caractere especial</li>
                </ul>
                
                <input name="senha2" id="senha2" type="password" placeholder="digite a senha novamente" aria-label="digite a senha novamente" pattern=".{4,32}|^$" title="O campo repetir senha deve conter entre 4 e 32 caracteres" onchange="setRequiredPassword()"/>
                
                <button class="blue accent-1" type="submit" onclick="submitForm('AlteraUsuarioServlet','passwordForm')">Salvar</button>
                <p class="message"><a href="#" id="show-user-form">Voltar ao perfil</a></p>
            </form>
                
            <form id="AnonimizaForm" class="userForm" method="POST" style="display: none;">
                <input name="usuario" type="text" placeholder="usuário" aria-label="usuário" value="${usuario}" pattern=".{4.32}" required title="O campo nome de usuario deve conter entre 4 e 32 caracteres" disabled />
                <input name="senhaAtual" id="senhaAtual" type="password" placeholder="senha atual" aria-label="senha atual" required title="O campo senha deve conter entre 8 e 32 caracteres." maxlength="32"/>
                
                <div style="text-align: left; margin-bottom: 10px">
                   <input type="checkbox" id="termos" name="termos" value="termos" required title="Necessário confirmar que os dados serão anonimizados." />
                   <label class="message2" for="termos">Esta ação é irreversível, tem certeza que deseja continuar com a anonimização?</label>
                </div>
                
                <button class="red accent-1" type="submit" onclick="submitForm('AnonimizaUsuarioServlet','AnonimizaForm')">Anonimizar</button>
                <p class="message"><a href="#" id="show-user-form">Voltar ao perfil</a></p>
            </form>
        </div>
                
        <script>
            function submitForm(action, IdForm) {
                const form = document.getElementById(IdForm);
                form.action = action;
                form.submit();
            }
            
            function setRequiredPassword() {
                const senhaAtual = document.getElementById('senhaAtual');
                const senhaNova = document.getElementById('senhaNova');
                const senha2 = document.getElementById('senha2');
                
                const SenhaRequired = ((senhaAtual.value.length > 0) || (senhaNova.value.length > 0) || (senha2.value.length > 0));
                senhaAtual.required = SenhaRequired;
                senhaNova.required = SenhaRequired;
                senha2.required = SenhaRequired;
            }  
        </script>
        
        <script src='https://cdnjs.cloudflare.com/ajax/libs/jquery/2.1.3/jquery.min.js'></script>
        <script>            
            $(document).ready(function(){
                $('#show-password-form').click(function(e){
                    e.preventDefault();
                    $('#userForm').hide();
                    $('#AnonimizaForm').hide();
                    $('#passwordForm').fadeIn();
                });
                
                $('#show-user-form').click(function(e){
                    e.preventDefault();
                    $('#passwordForm').hide();
                    $('#AnonimizaForm').hide();
                    $('#userForm').fadeIn();
                });
                
                $('#show-anonimiza-form').click(function(e){
                    e.preventDefault();
                    $('#passwordForm').hide();
                    $('#userForm').hide();
                    $('#AnonimizaForm').fadeIn();
                });
            });
            
            function checkPasswordStrength() {
                const password = document.getElementById('senhaNova').value;
                const strengthBar = document.getElementById('strength-bar');
                const strengthText = document.getElementById('strength-text');
                let strength = 0;

                // Definindo critérios
                const lengthcriterio = document.getElementById('length-criterio');
                const uppercasecriterio = document.getElementById('uppercase-criterio');
                const lowercasecriterio = document.getElementById('lowercase-criterio');
                const numbercriterio = document.getElementById('number-criterio');
                const specialcriterio = document.getElementById('special-criterio');

                // Resetando classes
                lengthcriterio.classList.remove('valid');
                uppercasecriterio.classList.remove('valid');
                lowercasecriterio.classList.remove('valid');
                numbercriterio.classList.remove('valid');
                specialcriterio.classList.remove('valid');

                // Verificando critérios
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

                switch(strength) {
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
  </body>
</html>
