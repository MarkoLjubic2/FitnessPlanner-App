import React, { useState } from 'react';
import Button from 'react-bootstrap/Button';
import Form from 'react-bootstrap/Form';

function ChangePassword() {
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [repeatNewPassword, setRepeatNewPassword] = useState('');
    const [userId, setUserId] = useState('');
    let decoded: any;

    const handleSubmit = (e: { preventDefault: () => void; }) => {
        e.preventDefault();

        const jwt = localStorage.getItem('jwt');

        if (newPassword !== repeatNewPassword) {
            alert("New passwords do not match");
            return;
        }

        if (jwt) {
            const base64Url = jwt.split('.')[1];
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
                return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));

            decoded = JSON.parse(jsonPayload);
            setUserId(decoded.id);
        }

        fetch(process.env.REACT_APP_USER_SERVICE_URL + '/changePassword', {
            'method': 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': jwt + ''
            },
            body: JSON.stringify({
                "oldPassword": oldPassword,
                "newPassword": newPassword,
                "id": decoded.id
            })
        }).then(res => res.json())
            .then(res => {
                if(res.statusCode !== 200) {
                    alert(res.message)
                }
                else {
                    alert("Password changed successfully");
                }
            })
            .catch(error => console.error('Error:', error));
    }


    return (
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100vh' }}>
            <h1 style={{ color: 'white' }}>Change Password</h1>
            <Form onSubmit={handleSubmit}>
                <Form.Group className="mb-3" controlId="formOldPassword">
                    <Form.Label style={{ color: 'white' }}>Old Password</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="password" placeholder="Old Password" onChange={e => setOldPassword(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formNewPassword">
                    <Form.Label style={{ color: 'white' }}>New Password</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="password" placeholder="New Password" onChange={e => setNewPassword(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3" controlId="formRepeatNewPassword">
                    <Form.Label style={{ color: 'white' }}>Repeat New Password</Form.Label>
                    <Form.Control style={{ width: '300px' }} type="password" placeholder="Repeat New Password" onChange={e => setRepeatNewPassword(e.target.value)} />
                </Form.Group>

                <Form.Group className="mb-3">
                    <Button type="submit" style={{ display: 'block', margin: 'auto' }}>Change Password</Button>
                </Form.Group>
            </Form>
        </div>
    );
}

export default ChangePassword;