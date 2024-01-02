import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { profileInfo } from '../atoms/loggedAtom';
import { useRecoilState } from 'recoil';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

function Login() {

    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [profile, setProfile] = useRecoilState(profileInfo);
    const navigate = useNavigate();

    const handleSubmit = (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        fetch(process.env.REACT_USER_SERVICE_URL + '/login', {
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
            setProfile({'loggedIn':'true', 'jwt': 'Bearer '+res.data.token, 'data': null})
            navigate('/')
          }
        })
        .catch(error => console.error('Error:', error));
    }

    return (
        <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3" controlId="formBasicEmail">
                <Form.Label>Username</Form.Label>
                <Form.Control placeholder="Enter username" onChange={e => setUsername(e.target.value)} />
            </Form.Group>
            <Form.Group className="mb-3" controlId="formBasicPassword">
                <Form.Label>Password</Form.Label>
                <Form.Control type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} />
            </Form.Group>
            <Button variant="primary" type="submit">Log in</Button>
        </Form>
    );
}

export default Login;