// utils/auth.js
export function getValidToken() {
    const tokenStr = localStorage.getItem('token')
    if (!tokenStr) return null

    try {
        const tokenObj = JSON.parse(tokenStr)
        if (tokenObj.expire > Date.now()) {
            return tokenObj.value
        } else {
            localStorage.removeItem('token')
            localStorage.removeItem('userName')
            localStorage.removeItem('userId')
            return null
        }
    } catch (e) {
        localStorage.removeItem('token')
        return null
    }
}
