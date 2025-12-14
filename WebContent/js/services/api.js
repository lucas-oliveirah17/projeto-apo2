/**
 * Configuração central da API
 * Substitui a configuração do Axios que você tinha no React.
 */

// Define a URL base da API. 
const API_BASE_URL = "/projeto-apo2/api";

const Api = {
    /**
     * Wrapper genérico para o fetch
     */
    request: async (endpoint, options = {}) => {
        const url = `${API_BASE_URL}${endpoint}`;
        
        // Configurações padrão (Headers JSON)
        const defaultHeaders = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        };

        const config = {
            ...options,
            headers: { ...defaultHeaders, ...options.headers }
        };

        try {
            const response = await fetch(url, config);
            
            // Tenta ler o JSON da resposta
            let data = null;
            try {
                data = await response.json();
            } catch (e) {
            }

            if (!response.ok) {
                throw {
                    status: response.status,
                    message: (data && data.message) ? data.message : "Erro na requisição"
                };
            }

            return data;
        } catch (error) {
            console.error("Erro na API:", error);
            throw error;
        }
    },

    // Métodos (GET, POST, PUT, DELETE
    
    get: (endpoint) => Api.request(endpoint, { method: "GET" }),

    post: (endpoint, body) => Api.request(endpoint, { 
        method: "POST", 
        body: JSON.stringify(body) 
    }),

    put: (endpoint, body) => Api.request(endpoint, { 
        method: "PUT", 
        body: JSON.stringify(body) 
    }),

    delete: (endpoint) => Api.request(endpoint, { method: "DELETE" })
};