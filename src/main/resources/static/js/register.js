const usernameTag = document.getElementById('username')
const passwordTag = document.getElementById('password')
const nameTag = document.getElementById('name')
const confirmPasswordTag = document.getElementById('cpassword')
const btnRegistry = document.querySelector('.btn-register')

const requestRegister = async (data) => {
    const response = await fetch("http://localhost:8080/auth/register", {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })

    const jsonRes = await response.json();
    console.log(jsonRes)
    // if(jsonRes.success) {
    //     window.location.href="/login";
    // } else {
    //     alert("Failed")
    // }


}

const handleSubmit = () => {
    let password = passwordTag.value
    let confirmPassword = confirmPasswordTag.value
    console.log(password, confirmPassword)
    if(password === confirmPassword) {
        const data = {
            'username': usernameTag.value,
            'password': password,
            'name': nameTag.value
        }

        requestRegister(data)
    } else {
        alert("Confirm password does not match")
    }



}



btnRegistry.addEventListener('click', handleSubmit)


