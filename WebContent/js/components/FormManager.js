class FormManager {
    constructor(config) {
        this.config = config; // { endpoint, formId, redirectPage }
        this.form = document.getElementById(config.formId);
        
        this.setupListener();
        this.checkEditMode();
    }

    setupListener() {
        this.form.addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.save();
        });
    }

    async checkEditMode() {
        // Verifica se tem ?id=1 na URL
        const urlParams = new URLSearchParams(window.location.search);
        const id = urlParams.get('id');

        if (id) {
            this.id = id;
            document.getElementById('pageTitle').innerText = 'Editar Registro';
            await this.loadData(id);
        }
    }

    async loadData(id) {
        try {
            const data = await Api.get(`${this.config.endpoint}/${id}`);
            // Preenche os campos automaticamente (input name="email" recebe data.email)
            Object.keys(data).forEach(key => {
                const input = this.form.elements[key];
                if (input) {
                    input.value = data[key];
                }
            });
            
            // Tratamento especial para selects ou campos complexos pode ser feito aqui
            if(this.config.onLoad) this.config.onLoad(data);

        } catch (error) {
            console.error("Erro ao carregar dados", error);
            alert("Erro ao carregar registro para edição.");
        }
    }

    async save() {
        // Transforma FormData em JSON
        const formData = new FormData(this.form);
        const payload = Object.fromEntries(formData.entries());

        // Se tiver ID, é PUT (Update), senão é POST (Create)
        const method = this.id ? 'put' : 'post';
        // Ajusta endpoint se for PUT (ex: /usuarios/1)
        const url = this.id ? `${this.config.endpoint}/${this.id}` : this.config.endpoint;

        try {
            if(method === 'put') {
                // Adiciona ID no payload para garantir
                payload.id = this.id;
                await Api.put(url, payload);
            } else {
                await Api.post(url, payload);
            }

            alert("Operação realizada com sucesso!");
            window.location.href = this.config.redirectPage;

        } catch (error) {
            alert(error.message || "Erro ao salvar.");
        }
    }
}