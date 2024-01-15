
const BASE_URL = "http://localhost:8080"
const connectedUsers = document.getElementById('connectedUsers')
const currentUserId = document.querySelector('.user-id')
const messageForm = document.querySelector('#messageForm');
const chatArea = document.querySelector('#chat-messages');
const messageInput = document.querySelector('#message1');
const btnSend = document.getElementById('btn-send')
const btnLogout = document.getElementById('logout')

let stompClient = null
let selectedUserId = null;


function connect(event) {
    const socket = new SockJS('/ws')
        stompClient = Stomp.over(socket)
        stompClient.connect({}, onConnected, () => {});

}

const onConnected = () => {
    console.log(currentUserId.id)
    stompClient.subscribe(`/user/${currentUserId.id}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/topic/connected`, fetchOnlineUsers);
    stompClient.subscribe(`/topic/disconnected`, fetchOnlineUsers);
    fetchOnlineUsers().then()
}

const onMessageReceived = (payload) => {
    console.log("Có được gọi không")
    console.log(payload)
    const message = JSON.parse(payload.body)
    console.log(message)
    console.log(selectedUserId, message.senderId)
    if(selectedUserId && Number.parseInt(selectedUserId) === Number.parseInt(message.senderId)) {
        console.log("Vô đây nè")
        displayMessages(message.senderId, message.content)
    }
}

const fetchOnlineUsers = async () => {
    const response = await fetch(BASE_URL + "/users/online");
    const json = await response.json();
    displayConnectedUsers(json)
}

const displayConnectedUsers = (users) => {
    const html = users.filter(user => user.id !== Number.parseInt(currentUserId.id))
        .map((user, index) => {
            return `
                <li onclick="userItemClick(event)" class="user-item" id="${user.id}">
                    <img src="../img/user_icon.png"  alt=""/>
                    <span>${user.name}</span>
                </li>
                <li class="separator"></li>
            `
        }).join('')

    connectedUsers.innerHTML = html;

}

const sendMessage = () => {
    const content = messageInput.value.trim();
    if(content && stompClient) {
        const payload = {
            'content': content,
            'sender': currentUserId.id,
            'recipient': selectedUserId,
            'timestamp': new Date()
        }

        stompClient.send('/app/chat', {}, JSON.stringify(payload))
        displayMessages(Number.parseInt(currentUserId.id), content)
        messageInput.value = ''
    }

}

const displayMessages = (senderId, content) => {
    const messageContainer = document.createElement('div')
    messageContainer.classList.add('message')
    if(senderId === Number.parseInt(currentUserId.id)) {
        messageContainer.classList.add('sender')
    } else {
        messageContainer.classList.add('receiver')
    }
    const message = document.createElement('p')
    message.textContent = content;
    messageContainer.appendChild(message)
    chatArea.appendChild(messageContainer)
}

const userItemClick = (event) => {
    selectedUserId = event.currentTarget.id
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    fetchMessages(currentUserId.id, selectedUserId)
}

const fetchMessages = async (sender, recipient) => {
    const response = await fetch(BASE_URL + "/messages/" + sender + "/" + recipient);
    const json = await response.json();
    chatArea.innerHTML = '';
    json.forEach(chat => displayMessages(chat.senderId, chat.content))
}

const onLogout = async () => {
    await fetch ("http://localhost:8080/doLogout")
    window.location.reload();
}

window.onbeforeunload = () => {
    onLogout()
}

window.onload = () => {
    connect()
}

btnLogout.addEventListener('click', onLogout)

btnSend.addEventListener('click', sendMessage)

