<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <jsp:include page="../components/head.jsp" />
    <title>Cadastro de Profissional - Chronos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cadastro.css">
</head>
<body>
    <jsp:include page="../components/navbar.jsp" />

    <div class="container mt-5">
        <div class="row justify-content-center">
            <div class="col-md-8 col-lg-6">
                
                <div class="d-flex align-items-center gap-3 mb-4">
                    <a href="gestaoProfissional.jsp" class="btn btn-outline-secondary btn-sm">
                        <i data-lucide="arrow-left"></i>
                    </a>
                    <h2 class="fw-bold mb-0" id="pageTitle">Novo Profissional</h2>
                </div>

                <div class="card card-cadastro">
                    <div class="card-body p-4">
                        
                        <form id="formProfissional">
                            <div class="mb-3">
                                <label class="form-label">Usuário Vinculado</label>
                                <p class="text-muted small mb-2">Selecione um usuário com perfil "Profissional"</p>
                                
                                <select class="form-select form-control-dark" name="usuarioId" id="selectUsuario" required>
                                    <option value="" disabled selected>Carregando usuários...</option>
                                </select>

                                <div id="areaAlerta" class="mt-2 p-3 bg-dark border border-secondary rounded" style="display: none;">
                                    <span class="text-danger d-block mb-2">
                                        <i data-lucide="alert-circle" style="width: 16px; vertical-align: middle;"></i> 
                                        Nenhum usuário "Profissional" disponível.
                                    </span>
                                    <a href="cadastroUsuario.jsp" class="btn btn-sm btn-outline-light w-100">
                                        Cadastrar Novo Usuário
                                    </a>
                                </div>
                            </div>

                            <div class="mb-4">
                                <label class="form-label">Especialidades</label>
                                <textarea class="form-control form-control-dark" name="especialidades" rows="3" placeholder="Ex: Corte, Barba, Pigmentação..." required></textarea>
                            </div>

                            <div class="d-grid">
                                <button type="submit" class="btn btn-primary">Salvar</button>
                            </div>
                        </form>

                    </div>
                </div>

            </div>
        </div>
    </div>

    <script src="../js/components/FormManager.js"></script>

    <script>
        async function carregarCandidatos() {
            try {
                // 1. Pega ID da URL para saber se é Edição
                const urlParams = new URLSearchParams(window.location.search);
                const idEdicao = urlParams.get('id'); // ID do Profissional sendo editado

                // 2. Busca Usuários e Profissionais (para ver quem já está vinculado)
                const [usuarios, profissionais] = await Promise.all([
                    Api.get('/usuarios'),
                    Api.get('/profissionais')
                ]);

                // 3. Descobre qual usuário está vinculado a ESTE profissional (se for edição)
                let usuarioAtualId = null;
                if (idEdicao) {
                    const profAtual = profissionais.find(p => p.id == idEdicao);
                    if (profAtual) usuarioAtualId = profAtual.usuarioId;
                }

                // 4. Cria lista de IDs já usados (exceto o atual)
                const usuariosVinculados = profissionais
                    .map(p => p.usuarioId)
                    .filter(id => id !== usuarioAtualId); // Não remove o usuário da própria edição

                // 5. Filtra: Perfil Profissional (2) AND Não está na lista de vinculados
                const candidatos = usuarios.filter(u => 
                    u.perfilId === 2 && 
                    u.ativo === true && 
                    !usuariosVinculados.includes(u.id)
                );

                // 6. Renderiza no Select
                const select = document.getElementById('selectUsuario');
                const areaAlerta = document.getElementById('areaAlerta');
                
                select.innerHTML = '<option value="" disabled selected>Selecione um usuário...</option>';

                if (candidatos.length === 0) {
                    select.disabled = true; // Trava o select
                    areaAlerta.style.display = 'block'; // Mostra o link
                } else {
                    select.disabled = false;
                    areaAlerta.style.display = 'none';
                    
                    candidatos.forEach(u => {
                        const option = document.createElement('option');
                        option.value = u.id;
                        option.textContent = u.nome + ' (' + u.email + ')';
                        select.appendChild(option);
                    });
                }
                
                lucide.createIcons(); // Atualiza ícone do alerta

            } catch (error) {
                console.error("Erro ao carregar lista", error);
                alert("Erro ao verificar usuários disponíveis.");
            }
        }

        document.addEventListener("DOMContentLoaded", async () => {
            // Primeiro carrega o select com a lógica de filtro
            await carregarCandidatos();

            // Depois inicia o gerenciador do formulário
            new FormManager({
                endpoint: "/profissionais",
                formId: "formProfissional",
                redirectPage: "gestaoProfissional.jsp"
            });
            
            lucide.createIcons();
        });
    </script>
</body>
</html>