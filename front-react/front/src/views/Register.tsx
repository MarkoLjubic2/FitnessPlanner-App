import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Button from 'react-bootstrap/Button';
import Col from 'react-bootstrap/Col';
import Form from 'react-bootstrap/Form';
import Row from 'react-bootstrap/Row';

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
                "dateOfBirth": dateOfBirth
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
        <Form onSubmit={handleSubmit}>
            <Form.Group as={Row} className="mb-3" controlId="formHorizontalEmail">
                <Form.Label column sm={2}>Username</Form.Label>
                <Col sm={10}><Form.Control type="text" placeholder="Username" onChange={e => setUsername(e.target.value)} /></Col>
            </Form.Group>

            <Form.Group as={Row} className="mb-3" controlId="formHorizontalPassword">
                <Form.Label column sm={2}>Password</Form.Label>
                <Col sm={10}><Form.Control type="password" placeholder="Password" onChange={e => setPassword(e.target.value)} /></Col>
            </Form.Group>

            <Form.Group as={Row} className="mb-3" controlId="formHorizontalEmail">
                <Form.Label column sm={2}>Email</Form.Label>
                <Col sm={10}><Form.Control type="email" placeholder="Email" onChange={e => setEmail(e.target.value)} /></Col>
            </Form.Group>

            <Form.Group as={Row} className="mb-3" controlId="formHorizontalFirstName">
                <Form.Label column sm={2}>First name</Form.Label>
                <Col sm={10}><Form.Control type="text" placeholder="First Name" onChange={e => setFirstName(e.target.value)} /></Col>
            </Form.Group>

            <Form.Group as={Row} className="mb-3" controlId="formHorizontalLastName">
                <Form.Label column sm={2}>Last name</Form.Label>
                <Col sm={10}><Form.Control type="text" placeholder="Last Name" onChange={e => setLastName(e.target.value)} /></Col>
            </Form.Group>

            <Form.Group as={Row} className="mb-3" controlId="formHorizontalDateOfBirth">
                <Form.Label column sm={2}>Date of Birth</Form.Label>
                <Col sm={10}><Form.Control type="date" placeholder="Date of Birth" onChange={e => setDateOfBirth(e.target.value)} /></Col>
            </Form.Group>

            <Form.Group as={Row} className="mb-3">
                <Col sm={{ span: 10, offset: 2 }}><Button type="submit">Register</Button></Col>
            </Form.Group>
        </Form>
    );
}

export default Register;