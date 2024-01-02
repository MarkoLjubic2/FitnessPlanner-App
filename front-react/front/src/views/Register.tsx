import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Register() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [dateOfBirth, setDateOfBirth] = useState('');
    const [licenceID, setLicenceID] = useState(Math.floor(Math.random() * (999999999999 - 100000000000 + 1) + 100000000000).toString());
    const [totalSessions, setTotalSessions] = useState(0);
    const navigate = useNavigate();

    const handleSubmit = (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        fetch(process.env.REACT_USER_SERVICE_URL + '/user/addUser', {
            'method': 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                "username": username,
                "password": password,
                "email": email,
                "firstName": firstName,
                "lastName": lastName,
                "dateOfBirth": dateOfBirth,
                "licenceID": licenceID,
                "totalSessions": totalSessions
            })
        }).then(res => res.json())
            .then(res => {
                console.log(res);
                if(res.statusCode !== 200) {
                    alert(res.message)
                }
                else {
                    navigate('/')
                }
            })
            .catch(error => console.error('Error:', error));
    }

    return (
        <div>
            <h1>Register</h1>
            <form onSubmit = {handleSubmit}>
                <label>
                    Username:
                    <input type="text" name="username" required value={username}
                           onChange={(e) => setUsername(e.target.value)}/>
                </label>
                <label>
                    Email:
                    <input type="email" name="email" required value={email}
                           onChange={(e) => setEmail(e.target.value)}/>
                </label>
                <label>
                    Password:
                    <input type="password" name="password" required value={password}
                           onChange={(e) => setPassword(e.target.value)}/>
                </label>
                <label>
                    First Name:
                    <input type="text" name="firstName" required value={firstName}
                           onChange={(e) => setFirstName(e.target.value)}/>
                </label>
                <label>
                    Last Name:
                    <input type="text" name="lastName" required value={lastName}
                           onChange={(e) => setLastName(e.target.value)}/>
                </label>
                <label>
                    Date of Birth:
                    <input type="date" name="dateOfBirth" required value={dateOfBirth}
                           onChange={(e) => setDateOfBirth(e.target.value)}/>
                </label>
                <input type="submit" value="Register" />
            </form>
        </div>
    );
}

export default Register;