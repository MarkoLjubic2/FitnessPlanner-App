import React, { useState, useEffect } from 'react';
import Cookies from "js-cookie";

interface User {
    id: number;
    username: string;
}

const jwt = Cookies.get('jwt');

const BanTable = () => {
    const [users, setUsers] = useState<User[]>([]);

    useEffect(() => {
        if (!jwt) return;
        fetch(process.env.REACT_APP_USER_SERVICE_URL + '', {
            headers: {
                'Authorization': 'Bearer ' + jwt
            }
        })
            .then(response => response.json())
            .then(data => {
                setUsers(data.data.content);
            })
            .catch(error => console.error('There was an error!', error));
    }, []);

    const banUser = (username: string) => {
        fetch(process.env.REACT_APP_USER_SERVICE_URL + '/banUser/' + username, {
            method: 'PUT',
            headers: {
                'Authorization': 'Bearer ' + jwt
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    setUsers(users.filter(user => user.username !== username));
                } else {
                    console.error('Failed to ban user');
                }
            })
            .catch(error => console.error('There was an error!', error));
    }

    const unbanUser = (username: string) => {
        fetch(process.env.REACT_APP_USER_SERVICE_URL + '/unbanUser/' + username, {
            method: 'PUT',
            headers: {
                'Authorization': 'Bearer ' + jwt
            }
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    setUsers(users.filter(user => user.username !== username));
                } else {
                    console.error('Failed to unban user');
                }
            })
            .catch(error => console.error('There was an error!', error));
    }

    return (
        <div>
            <h1 style={{ color: 'white', textAlign: 'center' }}>Ban/Unban User</h1>
            <table className="table rounded-table">
                <thead>
                <tr>
                    <th>Username</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                {users.map((user: User) => (
                    <tr key={user.id}>
                        <td>{user.username}</td>
                        <td>
                            <button style={{ marginRight: '10px' }} onClick={() => banUser(user.username)}>Ban</button>
                            <button onClick={() => unbanUser(user.username)}>Unban</button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
}

export default BanTable;