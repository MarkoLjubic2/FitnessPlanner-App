import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

function Register() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [dateOfBirth, setDateOfBirth] = useState('');
    const navigate = useNavigate();

    const handleSubmit = (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        const licenseID = Math.floor(1000000000 + Math.random() * 9000000000).toString();

        fetch(process.env.REACT_APP_USER_SERVICE_URL + '/addUser', {
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
                "licenseID": licenseID
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
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh' }}>
            <h1 style={{ color: 'white' }}>Register</h1>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formHorizontalEmail">
                    <Form.Label style={{ color: 'white' }}>Username</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="text" placeholder="Username" onChange={e => setUsername(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formHorizontalPassword">
                    <Form.Label style={{ color: 'white' }}>Password</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formHorizontalEmail">
                    <Form.Label style={{ color: 'white' }}>Email</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="email" placeholder="Email" onChange={e => setEmail(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formHorizontalFirstName">
                    <Form.Label style={{ color: 'white' }}>First name</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="text" placeholder="First Name" onChange={e => setFirstName(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formHorizontalLastName">
                    <Form.Label style={{ color: 'white' }}>Last name</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="text" placeholder="Last Name" onChange={e => setLastName(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formHorizontalDateOfBirth">
                    <Form.Label style={{ color: 'white' }}>Date of Birth</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="date" placeholder="Date of Birth" onChange={e => setDateOfBirth(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Button type="submit" style={{ display: 'block', margin: 'auto' }}>Register</Button>
                </Form.Group>
            </Form>
        </div>
    );
}

export default Register;