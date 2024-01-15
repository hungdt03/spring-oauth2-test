
const BASE_URL = "http://localhost:8080"
class UserService {
     async getAllOnlineUsers() {
        const response = await fetch(BASE_URL + "/users/online");
        return await response.json();
    }
}

const userService = new UserService();
export default userService