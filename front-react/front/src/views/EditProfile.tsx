import React, { useEffect, useState } from 'react';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import { decodeJwt } from '../util/decoder';

function EditProfile() {
    const [username, setUsername] = useState('');
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [email, setEmail] = useState('');

    useEffect(() => {
        const jwt = localStorage.getItem('jwt');
        if (jwt) {
            const decoded = decodeJwt(jwt);
            const userId = decoded.id;

            fetch(process.env.REACT_APP_USER_SERVICE_URL + '/getUserData/' + userId, {
                headers: {
                    'Authorization': jwt
                }

            })
                .then(response => response.json())
                .then(data => {
                    const user = data.data;
                    setUsername(user.username);
                    setFirstName(user.firstName);
                    setLastName(user.lastName);
                    setEmail(user.email);
                });
        }
    }, []);

    const handleSubmit = (event: React.FormEvent) => {
        event.preventDefault();

        const jwt = localStorage.getItem('jwt');
        let userId = null;
        if (jwt) {
            const decoded = decodeJwt(jwt);
            userId = decoded.id;
        }

        const updateUserDto = {
            id: userId,
            username,
            firstName,
            lastName,
            email
        };

        fetch(process.env.REACT_APP_USER_SERVICE_URL + '/updateUser', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': jwt + ''
            },
            body: JSON.stringify(updateUserDto)
        })
            .then(r => r.json())
            .then(data => {
                if (data.statusCode === 200) {
                    alert("Profile updated successfully");
                } else {
                    alert(`Error updating profile: ${data.message}`);
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh' }}>
            <h1 style={{ color: 'white' }}>Edit Profile</h1>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formHorizontalUsername">
                    <Form.Label style={{ color: 'white' }}>Username</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="text" value={username} onChange={e => setUsername(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3" controlId="formHorizontalFirstName">
                    <Form.Label style={{ color: 'white' }}>First Name</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="text" value={firstName} onChange={e => setFirstName(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3" controlId="formHorizontalLastName">
                    <Form.Label style={{ color: 'white' }}>Last Name</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="text" value={lastName} onChange={e => setLastName(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3" controlId="formHorizontalEmail">
                    <Form.Label style={{ color: 'white' }}>Email</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="email" value={email} onChange={e => setEmail(e.target.value)} />
                </Form.Group>
                <Form.Group className="mb-3">
                    <Button type="submit" style={{ display: 'block', margin: 'auto' }}>Update Profile</Button>
                </Form.Group>
            </Form>
        </div>
    );
}

export default EditProfile;