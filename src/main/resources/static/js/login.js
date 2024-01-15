// const usernameTag = document.getElementById('username')
// const passwordTag = document.getElementById('password')
// const btnLogin = document.querySelector('.btn-login')
// const requestLogin = async (data) => {
//     const response = await fetch("http://localhost:8080/auth/login", {
//         method: 'POST',
//         headers: {
//             'Content-Type': 'application/json',
//         },
//         body: JSON.stringify(data),
//     })
//
//     const jsonRes = await response.json();
//     console.log(jsonRes)
//     if(jsonRes.success) {
//         console.log('hihi')
//         window.location.href="/";
//     } else {
//         alert("Failed")
//         window.location.href="/login";
//     }
//
//
// }
//
// const handleSubmit = () => {
//     const data = {
//         'username': usernameTag.value,
//         'password': passwordTag.value,
//     }
//
//     requestLogin(data)
// }
//
//
// btnLogin.addEventListener('click', handleSubmit)
//
//
