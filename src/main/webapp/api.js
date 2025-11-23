class APIClient {
    constructor(baseURL = '') {
        this.baseURL = baseURL;
        this.auth = {
            username: null,
            password: null
        };
    }

    setCredentials(username, password) {
        this.auth.username = username;
        this.auth.password = password;
    }

    getAuthHeader() {
        if (this.auth.username && this.auth.password) {
            const credentials = btoa(`${this.auth.username}:${this.auth.password}`);
            return `Basic ${credentials}`;
        }
        return null;
    }

    async request(endpoint, options = {}, expectJson = true) {
        const url = `${this.baseURL}${endpoint}`;
        const headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };

        const authHeader = this.getAuthHeader();
        if (authHeader) {
            headers['Authorization'] = authHeader;
        }

        const config = {
            ...options,
            headers
        };

        try {
            const response = await fetch(url, config);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}\nResponse: ${await response.text()}`);
            }

            if (response.status === 204) {
                return null;
            }

            return expectJson ? await response.json() : true;
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    // Heartbeat endpoints
    async checkHeartbeat() {
        return this.request('/heartbeat', { method: 'GET' }, false);
    }

    // User endpoints
    async getUsers() {
        return this.request('/users', { method: 'GET' });
    }

    async getUser(id) {
        return this.request(`/users/user?id=${id}`, { method: 'GET' });
    }

    async getSelf() {
        return this.request('/users/self', { method: 'GET' });
    }

    async createUser(userData) {
        return this.request('/users', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    async register(username, password) {
        return this.request(`/users/register?username=${encodeURIComponent(username)}&password=${encodeURIComponent(password)}`, {
            method: 'POST'
        });
    }

    async updateUser(id, userData) {
        return this.request(`/users?id=${id}`, {
            method: 'PUT',
            body: JSON.stringify(userData)
        }, false);
    }

    async deleteUser(id) {
        return this.request(`/users?id=${id}`, { method: 'DELETE' }, false);
    }

    // Function endpoints (Admin only)
    async getAllFunctions() {
        return this.request('/functions', { method: 'GET' });
    }

    async getFunction(id) {
        return this.request(`/functions?id=${id}`, { method: 'GET' });
    }

    async getComposite(id) {
        return this.request(`/functions/composite?id=${id}`, { method: 'GET' });
    }

    async calculateFunction(id, x) {
        return this.request(`/functions/calculate?id=${id}&x=${x}`, { method: 'GET' });
    }

    async createMathFunction(expression) {
        return this.request('/functions/math', {
            method: 'POST',
            body: JSON.stringify({ type: 'math', expression })
        });
    }

    async createTabulatedFunction(expression, xFrom, xTo, pointCount) {
        return this.request('/functions/tabulated', {
            method: 'POST',
            body: JSON.stringify({ type: 'tabulated', expression, xFrom, xTo, pointCount })
        });
    }

    async createPureTabulatedFunction(xValues, yValues) {
        return this.request('/functions/pure-tabulated', {
            method: 'POST',
            body: JSON.stringify({ type: 'pure', xValues, yValues })
        });
    }

    async createCompositeFunction(innerId, outerId) {
        return this.request('/functions/composite', {
            method: 'POST',
            body: JSON.stringify({ type: 'composite', innerId, outerId })
        });
    }

    // Owned Function endpoints
    async getOwnedFunction(id, targetUserId = null) {
        let url = `/owned-functions?id=${id}`;
        if (targetUserId) {
            url += `&user=${targetUserId}`;
        }
        return this.request(url, { method: 'GET' });
    }

    async getOwnedComposite(id, targetUserId = null) {
        let url = `/owned-functions/composite?id=${id}`;
        if (targetUserId) {
            url += `&user=${targetUserId}`;
        }
        return this.request(url, { method: 'GET' });
    }

    async getUserFunctions(targetUserId = null) {
        let url = '/owned-functions/user';
        if (targetUserId) {
            url += `?user=${targetUserId}`;
        }
        return this.request(url, { method: 'GET' });
    }

    async createUserMathFunction(name, expression, targetUserId = null) {
        let url = '/owned-functions/math';
        if (targetUserId) {
            url += `?userId=${targetUserId}`;
        }
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify({
                type: "owned",
                name,
                funcParams: { type: 'math', expression }
            })
        });
    }

    async createUserTabulatedFunction(name, expression, xFrom, xTo, pointCount, targetUserId = null) {
        let url = '/owned-functions/tabulated';
        if (targetUserId) {
            url += `?userId=${targetUserId}`;
        }
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify({
                type: "owned",
                name,
                funcParams: { type: 'tabulated', expression, xFrom, xTo, pointCount }
            })
        });
    }

    async createUserPureTabulatedFunction(name, xValues, yValues, targetUserId = null) {
        let url = '/owned-functions/pure-tabulated';
        if (targetUserId) {
            url += `?userId=${targetUserId}`;
        }
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify({
                type: "owned",
                name,
                funcParams: { type: 'pure', xValues, yValues }
            })
        });
    }

    async createUserCompositeFunction(name, innerId, outerId, targetUserId = null) {
        let url = '/owned-functions/composite';
        if (targetUserId) {
            url += `?userId=${targetUserId}`;
        }
        return this.request(url, {
            method: 'POST',
            body: JSON.stringify({
                type: "owned",
                name,
                funcParams: { type: 'composite', innerId, outerId }
            })
        });
    }

    async assignOwnership(functionId, name, targetUserId) {
        return this.request(`/owned-functions/own?id=${functionId}&name=${encodeURIComponent(name)}`, {
            method: 'POST'
        });
    }

    async updateOwnedFunction(id, name, targetUserId = null) {
        let url = `/owned-functions?id=${id}&name=${encodeURIComponent(name)}`;
        if (targetUserId) {
            url += `&user=${targetUserId}`;
        }
        return this.request(url, { method: 'PUT' }, false);
    }

    async deleteOwnedFunction(id, targetUserId = null) {
        let url = `/owned-functions?id=${id}`;
        if (targetUserId) {
            url += `&user=${targetUserId}`;
        }
        return this.request(url, { method: 'DELETE' }, false);
    }

    // Points endpoints
    async getPoints(functionId) {
        return this.request(`/points?id=${functionId}`, { method: 'GET' });
    }

    async createPoint(functionId, x, y) {
        return this.request(`/points?id=${functionId}&x=${x}&y=${y}`, { method: 'POST' });
    }

    async updatePoint(functionId, x, y) {
        return this.request(`/points?id=${functionId}&x=${x}&y=${y}`, { method: 'PUT' }, false);
    }

    async deletePoint(functionId, x) {
        return this.request(`/points?id=${functionId}&x=${x}`, { method: 'DELETE' }, false);
    }
}