import React, { useState, useEffect } from 'react';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import { profileInfo } from '../atoms/loggedAtom';

interface User {
    id: number;
    username: string;
}

const BanTable = () => {
    const [users, setUsers] = useState<User[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const profile = useRecoilValue(profileInfo);
    const setProfile = useSetRecoilState(profileInfo);

    useEffect(() => {
        const storedJwt = localStorage.getItem('jwt');
        if (storedJwt) {
            setProfile(prevProfile => ({ ...prevProfile, jwt: storedJwt }));
        }
        setIsLoading(false);
    }, [setProfile]);

    useEffect(() => {
        if (!isLoading && profile.jwt) {
            localStorage.setItem('jwt', profile.jwt);

            fetch(process.env.REACT_APP_USER_SERVICE_URL + '', {
                headers: {
                    'Authorization': profile.jwt
                }
            })
                .then(response => response.json())
                .then(data => {
                    setUsers(data.data.content);
                })
                .catch(error => console.error('There was an error!', error));
        }
    }, [profile, isLoading]);

    const banUser = (username: string) => {
        fetch(process.env.REACT_APP_USER_SERVICE_URL + '/banUser/' + username, {
            method: 'PUT',
            headers: {
                'Authorization': profile.jwt
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
                'Authorization': profile.jwt
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

    if (isLoading) {
        return <div>Loading...</div>;
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