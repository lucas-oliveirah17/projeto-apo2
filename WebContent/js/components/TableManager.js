/**
 * Componente Reutilizável para Listagem de Dados (DataTable)
 */
class TableManager {
    /**
     * @param {Object} config
     * @param {string} config.entityName - Nome da entidade para exibição (ex: "Usuário")
     * @param {string} config.endpoint - URL da API (ex: "/usuarios")
     * @param {Array} config.columns - Definição das colunas [{ label: "Nome", key: "nome" }]
     * @param {string} config.containerId - ID da div onde a tabela será renderizada
     * @param {Function} [config.renderActions] - (Opcional) Função que retorna o HTML dos botões de ação
     */
    constructor(config) {
        this.config = config;
        this.container = document.getElementById(config.containerId);
    }

    async init() {
        this.renderLoading();
        try {
            const data = await Api.get(this.config.endpoint);
            this.renderTable(data);
        } catch (error) {
            this.container.innerHTML = `
                <div class="alert alert-danger">
                    Erro ao carregar ${this.config.entityName}s: ${error.message}
                </div>`;
        }
    }

    renderLoading() {
        this.container.innerHTML = `
            <div class="text-center py-5">
                <div class="spinner-border text-primary" role="status"></div>
                <p class="mt-2 text-muted">Carregando dados...</p>
            </div>`;
    }

    renderTable(data) {
        if (!data || data.length === 0) {
            this.container.innerHTML = `
                <div class="text-center py-5 p-4" style="background: rgba(30, 41, 59, 0.5); border-radius: 10px;">
                    <i data-lucide="inbox" size="48" class="text-muted mb-3"></i>
                    <h4 class="text-light">Nenhum registro encontrado</h4>
                    <p class="text-muted">Cadastre um novo ${this.config.entityName} para começar.</p>
                </div>`;
            lucide.createIcons();
            return;
        }

        // Cabeçalho
        let html = `
            <div class="table-responsive">
                <table class="table table-dark table-hover align-middle custom-table">
                    <thead>
                        <tr>
                            ${this.config.columns.map(col => `<th>${col.label}</th>`).join('')}
                            <th class="text-end">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        // Linhas
        html += data.map(item => `
                    <tr>
                        ${this.config.columns.map(col => `
                            <td>${this.formatValue(item, col)}</td>
                        `).join('')}
                        <td class="text-end">
                            ${this.renderActionButtons(item)}
                        </td>
                    </tr>
                `).join('');

        html += `</tbody></table></div>`;
        
        this.container.innerHTML = html;
        lucide.createIcons();
    }
    
    // Decide quais botões mostrar
    renderActionButtons(item) {
        // Se a página passou uma função customizada de ações, usa ela
        if (this.config.renderActions) {
            return this.config.renderActions(item);
        }

        // Caso contrário, usa o padrão (Editar + Excluir)
        return `
            <button class="btn btn-sm btn-outline-info me-2" onclick="editItem(${item.id})" title="Editar">
                <i data-lucide="edit-2" size="16"></i>
            </button>
            <button class="btn btn-sm btn-outline-danger" onclick="deleteItem(${item.id})" title="Excluir">
                <i data-lucide="trash-2" size="16"></i>
            </button>
        `;
    }

    // Função auxiliar para acessar propriedades aninhadas (ex: perfil.descricao)
    formatValue(item, col) {
        if (col.format) return col.format(item[col.key]); // Formatação customizada
        
        const keys = col.key.split('.');
        let value = item;
        keys.forEach(k => { value = (value && value[k] !== undefined) ? value[k] : '' });
        return value;
    }
}

// Funções globais para os botões (serão sobrescritas na página específica se necessário)
window.deleteItem = async (id) => {
    if(!confirm("Tem certeza que deseja excluir este registro?")) return;
    try {
        // Assume que a instância atual está salva em window.currentTableManager
        await Api.delete(`${window.currentTableManager.config.endpoint}/${id}`);
        window.currentTableManager.init(); // Recarrega
        alert("Registro excluído com sucesso!");
    } catch (e) {
        alert("Erro ao excluir: " + e.message);
    }
};

window.editItem = (id) => {
    // Redireciona para página de cadastro com ID na URL
    const currentPage = window.location.pathname;
    const cadastroPage = currentPage.replace('gestao', 'cadastro');
    window.location.href = `${cadastroPage}?id=${id}`;
};