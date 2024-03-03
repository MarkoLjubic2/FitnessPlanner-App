import React, { useState } from 'react';
import { profileInfo } from '../atoms/loggedAtom';
import { useRecoilState } from 'recoil';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

function Login() {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [profile, setProfile] = useRecoilState(profileInfo);

    const handleSubmit = (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        fetch(process.env.REACT_APP_USER_SERVICE_URL + '/login', {
          'method': 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            "username": username,
            "password": password
          })
        }).then(res => res.json())
            .then(res => {
                console.log(res);
                if(res.statusCode !== 200) {
                    alert(res.message)
                }
                else {
                    setProfile({'loggedIn':'true', 'username': username, 'jwt': 'Bearer '+res.data.token, 'data': null})
                    localStorage.setItem('jwt', 'Bearer '+res.data.token);
                    localStorage.setItem('username', username);
                    document.cookie = `jwt=${res.data.token}; path=/`;
                    window.location.href = '/';
                }
            })
        .catch(error => console.error('Error:', error));
    }

    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh' }}>
            <h1 style={{ color: 'white' }}>Login</h1>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formBasicEmail">
                    <Form.Label style={{ color: 'white' }}>Username</Form.Label>
                    <Form.Control style={{ width: '300px' }} placeholder="Enter username" onChange={e => setUsername(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3" controlId="formBasicPassword">
                    <Form.Label style={{ color: 'white' }}>Password</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} />
                </Form.Group>
                <Button variant="primary" type="submit" style={{ display: 'block', marginLeft: 'auto', marginRight: 'auto' }}>Log in</Button>
            </Form>
        </div>
    );
}

export default Login;