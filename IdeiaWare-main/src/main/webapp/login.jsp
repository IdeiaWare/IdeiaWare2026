<%
    Boolean resposta = (Boolean) request.getAttribute("resposta");
    Boolean respostaCadastro = (Boolean) request.getAttribute("respostaCadastro");
    Boolean respostaCadastro2 = (Boolean) request.getAttribute("respostaCadastro2");
    Boolean respostaCadastro3 = (Boolean) request.getAttribute("respostaCadastro3");
    Boolean respostaCadastro4 = (Boolean) request.getAttribute("respostaCadastro4"); // LucasFreitag 2024 
    Boolean resposta5 = (Boolean) request.getAttribute("resposta5");
    Boolean ErroRedefinicaoSenha = (Boolean) request.getAttribute("ErroRedefinicaoSenha");
    Boolean SucessoRedefinicaoSenha = (Boolean) request.getAttribute("SucessoRedefinicaoSenha");

%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<html lang="pt-BR">
    <head>
        <title>IdeiaWare - Login</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/meyer-reset/2.0/reset.min.css">
        <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Roboto:400,100,300,500,700,900'>
        <link rel='stylesheet prefetch' href='https://fonts.googleapis.com/css?family=Montserrat:400,700'>
        <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <link href='https://fonts.googleapis.com/css?family=Condiment' rel='stylesheet'>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
        <link rel="stylesheet" href="css/style.css">
        <link type="text/css" rel="stylesheet" href="css/materialize.min.css"  media="screen,projection"/>
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
        /*// LucasFreitag 2024*/
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
        
        .modal-content h4{
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 10px;
            margin-bottom: 20px;
            margin-top: 20px;
            font-size: 24px;
        }
        .modal-content ol{
            list-style: decimal inside;
            padding-left: 0;
        }
        .modal-content li{
            margin: 5px 5px 10px 5px;
        }
        .modal-content p{
            margin: 5px 5px 10px 5px;
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
                <h5 class="text-darken-2 blue-grey-text">Login</h5>
            </div>
        </div>
        <div class="form">
            <div class="thumbnail">
                <img src="imagens/idea.png" alt="IdeiaWare"/>
            </div>
            <c:if test="${resposta}">
                <div class="erro">
                    <h6>Login e/ou senha inválido(s)!</h6>
                </div> 
            </c:if>
            <c:if test="${respostaCadastro}">
                <div class="erro">
                    <h6>Usuário já existente!</h6>
                </div> 
            </c:if>
            <c:if test="${respostaCadastro3}">
                <div class="erro">
                    <h6>Preencha todos os campos para criar a conta.</h6>
                </div>
            </c:if>
            <!--// LucasFreitag 2024-->
            <c:if test="${respostaCadastro4}">
                <div class="erro">
                    <h6>E-mail já cadastrado!</h6>
                </div> 
            </c:if>
            <c:if test="${resposta5}">
                <div class="erro">
                    <h6>E-mail não cadastrado para redefinição de senha!</h6>
                </div> 
            </c:if>
            <c:if test="${ErroRedefinicaoSenha}">
                <div class="erro">
                    <h6>Houve um problema ao enviar e-mail de redefinição de senha!</h6>
                </div> 
            </c:if>
            <c:if test="${SucessoRedefinicaoSenha}">
                <div class="sucesso">
                    <h6>Enviado e-mail para redefinição de senha!</h6>
                </div> 
            </c:if>

            <form id="login-form" class="login-form" action="LogInServlet" method="POST" >
                <input name="usuario" type="text" placeholder="usuário" aria-label="usuário" pattern=".{4,32}" required title="O campo nome de usuario deve conter entre 4 e 32 caracteres"/>
                <!--// LucasFreitag 2024-->
                <!--<input name="senha" type="password" placeholder="senha" aria-label="senha" pattern=".{8,32}" required title="O campo senha deve conter entre 8 e 32 caracteres"/>-->
                <%-- UX: "olhinho" pra revelar a senha digitada. --%>
                <div style="position:relative;">
                    <input name="senha" id="login-senha" type="password" placeholder="senha" aria-label="senha"/>
                    <button type="button" id="toggle-login-senha" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('login-senha','toggle-login-senha')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                </div>
                <p class="forgot-password"><a href="#" id="forgot-password-link">Esqueceu a senha?</a></p>

                <button class="blue accent-1" type="submit">login</button>
                <p class="message">Não é cadastrado? <a href="#" id="show-register-form">Crie uma conta</a></p>
            </form>

            <form id="register-form" class="register-form" action="CadastroUsuarioServlet" method="POST" style="display:none;">
                <label for="reg-nome" class="perfil-label">Nome</label>
                <input id="reg-nome" name="nome" type="text" placeholder="nome" aria-label="nome" pattern=".{4,64}" required title="O campo nome deve conter entre 6 e 64 caracteres" />
                <label for="reg-usuario" class="perfil-label">Usuário</label>
                <input id="reg-usuario" name="usuario" type="text" placeholder="usuário" aria-label="usuário" pattern=".{4,32}" required title="O campo nome de usuario deve conter entre 4 e 32 caracteres" />
                <label for="reg-email" class="perfil-label">E-mail</label>
                <input id="reg-email" name="email" type="email" placeholder="email" aria-label="email" pattern="\w+(\+?\w+)@\w+(\.\w+)+" required title="email@exemplo.com" maxlength="100"/> <!--// LucasFreitag 2024-->
                <label for="senha" class="perfil-label">Senha</label>
                <!-- SENHA-#: o pattern antigo só aceitava os especiais @$!%*?& e rejeitava
                     senhas com '#' (e outros). Agora aceita qualquer caractere especial. -->
                <div style="position:relative;">
                    <input name="senha" id="senha" type="password" placeholder="senha" aria-label="senha" pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9])\S{8,32}$" required title="O campo senha deve conter entre 8 e 32 caracteres. E os 5 requisitos abaixo." maxlength="32" oninput="checkPasswordStrength()"/>
                    <button type="button" id="toggle-reg-senha" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('senha','toggle-reg-senha')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                </div>

                <label for="reg-senha2" class="perfil-label">Confirmar senha</label>
                <div style="position:relative;">
                    <input id="reg-senha2" name="senha2" type="password" placeholder="digite a senha novamente" aria-label="digite a senha novamente" pattern=".{4,32}" required title="O campo repetir senha deve conter entre 4 e 32 caracteres" />
                    <button type="button" id="toggle-reg-senha2" aria-label="Mostrar senha" onclick="toggleSenhaVisibility('reg-senha2','toggle-reg-senha2')" style="position:absolute; right:6px; top:50%; transform:translateY(-50%); width:32px; height:32px; min-width:0; background:none; border:0; padding:0; margin:0; cursor:pointer; display:flex; align-items:center; justify-content:center;"><i class="material-icons" style="color:#9e9e9e;">visibility_off</i></button>
                </div>

                <%-- UX: forca da senha depois de Senha+Confirmar (antes ficava encaixada
                     entre os dois campos, cortando o fluxo de preenchimento). --%>
                <!--// LucasFreitag 2024-->
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

                <c:if test="${respostaCadastro2}"><div class="erro"><h6>A senha e a confirmação não coincidem.</h6></div> </c:if>
                <!--<input disabled="true" type="text" placeholder="e-mail " aria-label="e-mail "/>-->
                <!--// LucasFreitag 2024-->
                <div style="text-align: left; margin-bottom: 10px">
                    <input type="checkbox" id="termos" name="termos" value="termos" required title="Necessário aceitar termos de uso para realizar cadastro." />
                    <label class="message2" for="termos">Termos de uso. <a href="#modal1" class="modal-trigger terms-link">Para ler clique aqui.</a></label>
                </div>
                <button class="blue accent-1" type="submit">cadastrar</button>
                <p class="message">Já é cadastrado? <a href="#" id="show-login-form">Entre</a></p>
            </form>

            <!--// LucasFreitag 2024-->
            <form id="reset-password-form" class="reset-password-form" action="ResetPasswordServlet" method="POST" style="display:none;">
                <input name="email" type="email" placeholder="email" aria-label="email" pattern="\w+(\+?\w+)@\w+(\.\w+)+" required title="email@exemplo.com" maxlength="100"/>
                <button class="blue accent-1" type="submit" >Enviar</button>
                <p class="message"><a href="#" id="back-to-login">Voltar ao login</a></p>
            </form>
        </div>    

        <!--// LucasFreitag 2024-->
        <div id="modal1" class="modal">
            <div class="modal-content">
                <h4>Termos de Uso</h4>
                <ol>
                    <b><li>Aceitação dos Termos</li></b>
                    <p>Ao se registrar na plataforma, o usuário concorda com os presentes Termos de Uso. Estes termos regem o acesso e uso da plataforma colaborativa.</p>
                    <b><li>Uso da Plataforma</li></b>
                    <p>A plataforma é destinada a colaboração entre usuários. Qualquer atividade na plataforma deve estar de acordo com as políticas da organização. O uso indevido ou fora do propósito pode resultar na suspensão ou cancelamento do acesso.</p>
                    <b><li>Propriedade dos Dados</li></b>
                    <p>Os dados inseridos na plataforma são de propriedade da organização. Esses dados não devem ser compartilhados ou utilizados fora da plataforma sem autorização expressa.</p>
                    <b><li>Alterações nos Termos</li></b>
                    <p>A organização pode alterar os Termos de Uso. Caso isso ocorra, o usuário será notificado por e-mail.</p>
                    <b><li>Responsabilidades do Usuário</li></b>
                    <p>O usuário se compromete a não violar leis aplicáveis ao utilizar a plataforma, e a manter as informações de cadastro corretas e atualizadas.</p>
                </ol>

                <h4>Política de Privacidade</h4>
                <ol>
                    <b><li>Dados Coletados</li></b>
                    <p>Durante o cadastro, coletamos apenas nome, usuário e e-mail. O e-mail é usado para redefinição de senha e para envio dos dados pessoais registrados na ferramenta.</p>
                    <b><li>Uso dos Dados</li></b>
                    <p>Os dados coletados têm como única finalidade o funcionamento da plataforma. Não há compartilhamento de informações com terceiros.</p>
                    <b><li>Direitos do Usuário</li></b>
                    <p>O usuário pode solicitar a anonimização de seus dados pessoais, assim como pode solicitar, a qualquer momento, a lista dos dados pessoais que a plataforma possui sobre ele.</p>
                </ol>
            </div>
            <div class="modal-footer">
                <a href="#" class="modal-close waves-effect waves-green btn-flat">Fechar</a>
            </div>
        </div>

        <!--// LucasFreitag 2024-->
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                var elems = document.querySelectorAll('.modal');
                var instances = M.Modal.init(elems);
            });

            // UX: "olhinho" pra revelar/ocultar a senha digitada (login e cadastro).
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

        <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
        <script>
            $(document).ready(function () {
                $('#show-register-form').click(function (e) {
                    console.log('Cadastrar link clicado');
                    e.preventDefault();
                    $('#login-form').hide();
                    $('#reset-password-form').hide();
                    $('#register-form').fadeIn();
                });

                $('#show-login-form').click(function (e) {
                    e.preventDefault();
                    console.log('Login link clicado');
                    $('#register-form').hide();
                    $('#reset-password-form').hide();
                    $('#login-form').fadeIn();
                });

                $('#forgot-password-link').click(function (e) {
                    e.preventDefault();
                    console.log('Esqueceu a senha link clicado');
                    $('#login-form').hide();
                    $('#register-form').hide();
                    $('#reset-password-form').fadeIn();
                });

                $('#back-to-login').click(function (e) {
                    e.preventDefault();
                    console.log('Voltar ao login clicado');
                    $('#reset-password-form').hide();
                    $('#register-form').hide();
                    $('#login-form').fadeIn();
                });

                // UX: quando o servidor retorna um erro de cadastro ou de redefinição,
                // reabre o formulário correspondente — senão a mensagem aparece mas o
                // usuário fica olhando para o formulário de login (contexto perdido).
                var erroCadastro = ${respostaCadastro or respostaCadastro2 or respostaCadastro3 or respostaCadastro4};
                var erroRedefinicao = ${resposta5 or ErroRedefinicaoSenha};
                if (erroCadastro) {
                    $('#login-form').hide();
                    $('#reset-password-form').hide();
                    $('#register-form').show();
                } else if (erroRedefinicao) {
                    $('#login-form').hide();
                    $('#register-form').hide();
                    $('#reset-password-form').show();
                }
            });
        </script>
    <script src="js/csrf.js"></script>
  </body>
</html>

